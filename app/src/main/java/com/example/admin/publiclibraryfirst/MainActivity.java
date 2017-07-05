package com.example.admin.publiclibraryfirst;

/*
 * Copyright (C) 2017 The Android Open Source Project
 * This app "Public Library First" is for people who want to check
 * if their desired book there is in public library first and then to buy it
 * Is created with android studio 2.3.1
 * as exercise for Android Basics by Google Nanodegree Program
 * "Book Listing " by Dimitra Christina Nikolaidou
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private EditText givenTitle;
    private String titleOfBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the text that the user gave
        givenTitle = (EditText) findViewById(R.id.search_title);
        // Find the button that the user have to click
        Button button = (Button) findViewById(R.id.search_button);

        // Set a click listener on that View
        button.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers category is clicked on.
            @Override
            public void onClick(View view) {
                titleOfBook = givenTitle.getText().toString();
                // Create a new intent to open the {@link RenewActivity}
                Intent renewIntent = new Intent(getApplicationContext(), RenewActivity.class);
                renewIntent.putExtra("TITLE", titleOfBook);

                // Start the new activity
                startActivity(renewIntent);
            }
        });
    }
}

