package com.example.eldho.firebase_setupanddocumentation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class SingleDocumentActivity extends AppCompatActivity {
    private static final String TAG = "SingleDocumentActivity";

    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;

    NoteModel note;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.collection("NoteBook").document("MyFirstNote"); //(There are also collection reference like document reference)

    private ListenerRegistration noteListener; //For detaching while app is inactive

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_document);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.text_view_data);
    }

    /**
     * Fetches data in Realtime
     */
    @Override
    protected void onStart() {
        super.onStart();
        noteListener = noteRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) { /**checks whether there is any exceptions, Its an important check*/
                    Toast.makeText(SingleDocumentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (documentSnapshot.exists()) { /**checks document is exist in firestore or not*/

                    /**Gets the values inside the key from firestore*/
                    note = documentSnapshot.toObject(NoteModel.class);
                    String title = note.getTitle();
                    String desc = note.getDescription();

                    textViewData.setText("Title: " + title + "\n" + "Description: " + desc);
                } else
                    Toast.makeText(SingleDocumentActivity.this, "Doc missing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteListener.remove();
    }

    /**
     * Saving data into a document
     */
    public void saveNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        note = new NoteModel(title, description);

        //saving Items
        // can be also done like this -> db.document("Notebook/MyFirstNote");
        /**.set(object) is for saving data into firestore */
        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SingleDocumentActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingleDocumentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * loading data
     */

    public void loadNote(View view) {
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) { /**checks document is exist in firestore or not*/

                            /**Gets the values inside the key from firestore*/
                            note = documentSnapshot.toObject(NoteModel.class);
                            String title = note.getTitle();
                            String desc = note.getDescription();

                            textViewData.setText("Title: " + title + "\n" + "Description: " + desc);
                        } else {
                            //if doc is empty
                            textViewData.setText("");
                            Log.d(TAG, "Missing Doc");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingleDocumentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * updating single field without overriding other fields
     */
    public void updateDescription(View view) {
        //NOTE :- must add OnSuccessListener and OnFailureListener to find whether the changes come into effect or not
        String description = editTextDescription.getText().toString();

        /**Merge is used to update/create single Field/Multiple , if we didn't use merge the other field will become null*/
//        Map<String, Object> note = new HashMap<>();
//        note.put(KEY_DESCRIPTION, description);
//        noteRef.set(note, SetOptions.merge());

        /**This will just update , if there is no documents wont create one*/
        noteRef.update(KEY_DESCRIPTION, description);
    }

    /**
     * Deletes a particular field from the document
     */
    public void deleteDescription(View view) {

        //Map<String, Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, FieldValue.delete());

        //noteRef.update(note);
        noteRef.update(KEY_DESCRIPTION, FieldValue.delete()); //KEY_DESCRIPTION holds the name which is same name as of Firebase DB name
    }

    /**
     * Delete the note document
     */
    public void deleteNote(View view) {
        noteRef.delete();
    }
}