package com.example.eldho.firebase_setupanddocumentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void singleDocument(View view) {
        Intent intent = new Intent(getApplicationContext(),SingleDocumentActivity.class);
        startActivity(intent);
    }

    public void multipleDocument(View view) {
        Intent intent = new Intent(getApplicationContext(),MultipleDocumentActivity.class);
        startActivity(intent);
    }
}
