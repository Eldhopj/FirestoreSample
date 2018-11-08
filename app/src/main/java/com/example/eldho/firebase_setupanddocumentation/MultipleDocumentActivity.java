package com.example.eldho.firebase_setupanddocumentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MultipleDocumentActivity extends AppCompatActivity {
    private static final String TAG = "MultipleDocumentAct";

    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
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
                loopingThroughDocuments(queryDocumentSnapshots);

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

        note = new NoteModel(title, description);

        /**.add(object) is for saving data into firestore*/
        //NOTE: Must .addOnFailureListener() and .addOnSuccessListener()
        noteReference.add(note);
    }

    public void loadNotes(View view) {

        noteReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                loopingThroughDocuments(queryDocumentSnapshots);
            }

        });//NOTE: Must .addOnFailureListener()
    }

    private void loopingThroughDocuments(QuerySnapshot queryDocumentSnapshots) {
        String data = "";
        //NOTE : use QueryDocumentSnapshot is guaranteed exists to reduce checks
        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){// Iterates through all documents in the collection

            note = documentSnapshot.toObject(NoteModel.class);
            //NOTE : check getDocumentId in NoteModel Class
            note.setDocumentId(documentSnapshot.getId()); // get the Id of the document, Its a uId for to manipulate the data in that doc
            // TODO : Do it in recyclerView
            String documentId = note.getDocumentId();
            String title = note.getTitle();
            String description = note.getDescription();

            data += "ID: " + documentId + "\nTitle: " + title + "\nDescription: " + description + "\n\n";
        }

        textViewData.setText(data);
    }


}
