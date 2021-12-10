package com.example.foodies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CreateAccountActivity2 extends AppCompatActivity {

    //variables
    private String name, surname, gender, measurementSystem, email, birthday, password;
    private double height;
    private EditText editTextCurrentWeight, editTextTargetWeight, editTextCalorieGoal, editTextStepGoal;
    private double currentWeight, targetWeight, calorieGoal, stepGoal;
    private final String TAG = "CreateAccountActivity2";
    private TextView createAccountSystem1, createAccountSystem2;

    //Firebase variables
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    //User class to insert into database
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account2);

        GetIntentAttributes();

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("users");

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(CreateAccountActivity2.this);

        //getting views
        editTextCurrentWeight = findViewById(R.id.editTextCurrentWeight);
        editTextTargetWeight = findViewById(R.id.editTextTargetWeight);
        editTextCalorieGoal = findViewById(R.id.editTextCalorieGoal);
        editTextStepGoal = findViewById(R.id.editTextStepGoal);
        createAccountSystem1 = findViewById(R.id.createAccountSystem1);
        createAccountSystem2 = findViewById(R.id.createAccountSystem2);

        if (measurementSystem.equals("Imperial")) {
            createAccountSystem1.setText("lbs");
            createAccountSystem2.setText("lbs");
        } else {
            createAccountSystem1.setText("kg");
            createAccountSystem2.setText("kg");
        }

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });

    }

    //tests the user's details
    private void checkCredentials() {

        if (editTextCurrentWeight.getText().toString().isEmpty())
        {
            showError(editTextCurrentWeight, "Please enter your current weight");
        }
        else if (editTextTargetWeight.getText().toString().isEmpty())
        {
            showError(editTextTargetWeight, "Please enter your target weight");
        }
        else if (editTextCalorieGoal.getText().toString().isEmpty())
        {
            showError(editTextCalorieGoal, "Please enter your daily calorie intake goal");
        }
        else if (editTextStepGoal.getText().toString().isEmpty())
        {
            showError(editTextStepGoal, "Please enter your daily step goal");
        }
        else {
            currentWeight = Double.parseDouble(editTextCurrentWeight.getText().toString());
            targetWeight = Double.parseDouble(editTextTargetWeight.getText().toString());
            calorieGoal = Double.parseDouble(editTextCalorieGoal.getText().toString());
            stepGoal = Double.parseDouble(editTextStepGoal.getText().toString());

            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait while we check your credentials");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        mLoadingBar.dismiss();
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                    else
                    {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(CreateAccountActivity2.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                        Toast.makeText(CreateAccountActivity2.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //shows custom error message on selected inputs
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    //creates the user
    public void updateUI(FirebaseUser currentUser) {
        if (measurementSystem.equals("Imperial")) {
            currentWeight = currentWeight / 2.205;
            targetWeight = targetWeight / 2.205;
        }
        user = new User(currentUser.getUid(), name, surname, gender, measurementSystem, email, birthday, height, currentWeight, targetWeight, calorieGoal, stepGoal);
        mDatabase.child(currentUser.getUid()).setValue(user);
        Intent finishIntent = new Intent(CreateAccountActivity2.this, MainActivity.class);
        startActivity(finishIntent);
        finish();
    }

    //grabs values from previous page
    private void GetIntentAttributes() {
        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        gender = intent.getStringExtra("gender");
        measurementSystem = intent.getStringExtra("measurementSystem");
        email = intent.getStringExtra("email");
        birthday = intent.getStringExtra("birthday");
        height = intent.getDoubleExtra("height", 0);
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
    }

}