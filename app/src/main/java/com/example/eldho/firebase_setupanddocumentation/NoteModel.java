package com.example.eldho.firebase_setupanddocumentation;

/**
 * Creating a model class
 */

public class NoteModel {
    private String title;
    private String description;

    public NoteModel() {
        //public no-arg constructor needed
    }

    public NoteModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
