package com.example.foodies;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddFoodActivity extends AppCompatActivity {

    //Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;

    //variables
    private EditText editTextCalories;
    private Button addFoodButton, cameraButton;
    private double calories;
    private LocalDate localDate;
    private DateTimeFormatter formatter;
    private String date;
    private String id;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

         //For reference
        formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

        //Firebase things
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("usercalories");
        user = FirebaseAuth.getInstance().getCurrentUser();

        editTextCalories = findViewById(R.id.editTextCalories);

        //lets the user use their camera to take a picture and store it to their phone
        cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
            }
        });

        //button to add the calories
        addFoodButton = findViewById(R.id.addFoodButton);
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCalories();
            }
        });
    }

    //adds calories to database
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCalories() {
        //test if there is input
        if (editTextCalories.getText().toString().isEmpty()) {
            showError(editTextCalories, "Please enter calorie amount");
        } else {
            //today's date
            localDate = LocalDate.now();
            date = localDate.format(formatter);

            //calorie amount
            calories = Double.parseDouble(editTextCalories.getText().toString());

            //current signed in user
            id = user.getUid();

            //insert into database
            reference.child(id).child(date).push().setValue(calories);

            //feedback to user
            Toast.makeText(AddFoodActivity.this, String.valueOf(calories) + " calories entered", Toast.LENGTH_LONG).show();

            //reset input
            editTextCalories.setText("");
        }
    }

    //shows custom error message on selected inputs
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}