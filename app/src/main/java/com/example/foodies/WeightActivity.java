package com.example.foodies;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeightActivity extends AppCompatActivity {

    //Firebase variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference weightRef = database.getReference("userweight");
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    //variables
    private Button submitButton;
    private EditText editTextWeight;
    private TextView textViewMeasurementSystem;
    private LocalDate localDate;
    private DateTimeFormatter formatter;
    private String date;
    private String id;
    private double weight;
    private String measurementSystem;
    private double convertToMetric = 2.205;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        //finding views
        textViewMeasurementSystem = findViewById(R.id.weightMeasurementSystem);
        editTextWeight = findViewById(R.id.editTextWeight);

        //gets current user's id
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getUid();

        //sets the measurement system type based on what is saved for the user in the database
        usersRef = database.getReference("users").child(id);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                measurementSystem = snapshot.child("userMeasurementSystem").getValue(String.class);
                if(measurementSystem.equals("Imperial")) {
                    textViewMeasurementSystem.setText("lbs");
                } else if(measurementSystem.equals("Metric")) {
                    textViewMeasurementSystem.setText("kg");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

        //submit the entered weight
        submitButton = findViewById(R.id.submitWeightButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputWeight();
            }
        });
    }

    //inserts weight into database
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void inputWeight() {
        String sWeight = editTextWeight.getText().toString();

        //tests if weight is empty and shows message if it is
        if (sWeight.isEmpty()) {
            showError(editTextWeight, "Please enter a value");
        } else {
            weight = Double.parseDouble(sWeight);

            //convert to save to database
            if (textViewMeasurementSystem.getText().equals("lbs")) {
                weight = weight / convertToMetric;
            }

            //today's date
            localDate = LocalDate.now();
            date = localDate.format(formatter);

            //save value into database for the current date
            weightRef.child(id).child(date).setValue(weight);

            //alert user
            Toast.makeText(WeightActivity.this, sWeight + " entered for today", Toast.LENGTH_LONG).show();

            //clear input
            editTextWeight.setText("");

            //takes user back to weight viewing page
            finish();
        }
    }

    //shows custom error message on selected inputs
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}