package com.example.eldho.firebase_setupanddocumentation;

import com.google.firebase.firestore.Exclude;

/**
 * Creating a model class
 */

public class NoteModel {
    private String title;
    private String description;
    private int priority;
    private String documentId; // For to get id of the document

    public NoteModel() {
        //public no-arg constructor needed
    }

    public NoteModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public NoteModel(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    @Exclude //Prevent it on showing to Firestore Db
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
