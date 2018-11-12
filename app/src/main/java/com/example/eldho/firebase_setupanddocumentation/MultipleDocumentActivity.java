package com.example.eldho.firebase_setupanddocumentation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

/**A collection can contain multiple documents , collection is just like an folder
 * Querying and order by is done in loadNotes()*/

public class MultipleDocumentActivity extends AppCompatActivity {
    private static final String TAG = "MultipleDocumentAct";

    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private TextView textViewData;

    NoteModel note;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteReference = db.collection("NoteBook");

    private ListenerRegistration noteListener; //For detaching while app is inactive
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_document);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.text_view_data);
        editTextPriority = findViewById(R.id.edit_text_priority);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteListener = noteReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) { /**checks whether there is any exceptions, Its an important check*/
                    Toast.makeText(MultipleDocumentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (queryDocumentSnapshots != null) {
                   loopingThroughDocuments(queryDocumentSnapshots);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteListener.remove();
    }

    public void addNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0){ // if the priority field is null put a default value as 0
            editTextPriority.setText("0");
        }

        int priority = Integer.parseInt(editTextPriority.getText().toString().trim());

        note = new NoteModel(title, description, priority);

        /**.add(object) is for saving data into firestore*/
        //NOTE: Must .addOnFailureListener() and .addOnSuccessListener()
        noteReference.add(note);
    }

    public void loadNotes(View view) {
   /*NOTE :1. we can apply multiple range operators with same fields
    *           But not across multiple fields
    *      2. we can apply multiple whereEqualTo() across multiple fields , there is no limitations
    *      3. We can combine a single range operators with a multiple whereEqualTo() with help of Indexing
    *      4. We can create orderBy() for multiple fields by using indexing , (orderBy must be equal to range operator)
    *      5. firestore doesn't have an "OR" and "NOT" operator, all the combine queries are of AND operator (We can merge it locally)
    */

        noteReference.whereGreaterThan("priority",2) // Check there are a lot of types of querying types

                .whereEqualTo("title","agsfg") // Need to create Index here, check logcat

                .orderBy("priority",Query.Direction.DESCENDING) // NOTE : orderBy must be equal to range operator
                .limit(3) // Limiting to first 3 results

                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                loopingThroughDocuments(queryDocumentSnapshots);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //NOTE : Check for toast message if the result is not loading up ,
                //       if its a crash that query combination don't work
                Toast.makeText(MultipleDocumentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.getLocalizedMessage()); //NOTE : Necessary for creating Indexing
            }
        });
    }

    //Query document snapshot contains snapshot of all the documents in that collection, here we are looping through every documents
    private void loopingThroughDocuments(QuerySnapshot queryDocumentSnapshots) {
        String data = "";
        //NOTE : use QueryDocumentSnapshot is guaranteed exists to reduce checks
        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){// Iterates through all documents in the collection

            note = documentSnapshot.toObject(NoteModel.class);
            //NOTE : getDocumentId in NoteModel Class is annotated with  @Exclude
            note.setDocumentId(documentSnapshot.getId()); // get the Id of the document, Its a uId for to manipulate the data in that doc
            // TODO : Do it in recyclerView
            String documentId = note.getDocumentId();
            String title = note.getTitle();
            String description = note.getDescription();
            int priority = note.getPriority();

            data += "ID: " + documentId +
                    "\nTitle: " + title +
                    "\nDescription: " + description +
                    "\nPriority: " + priority +"\n\n";
        }

        textViewData.setText(data);
    }
}
