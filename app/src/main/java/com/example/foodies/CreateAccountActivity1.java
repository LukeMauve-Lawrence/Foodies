package com.example.foodies;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateAccountActivity1 extends AppCompatActivity {

    //variables
    private String name, surname, gender, measurementSystem = "Metric", email, password, confirmPassword, birthday;
    private double height;
    private EditText nameInput, surnameInput, heightInput, emailInput, passwordInput, confirmPasswordInput;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView textViewHeight, textViewDate;
    private ToggleButton toggleButton;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "CreateAccountActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account1);

        //finding views
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        textViewHeight = findViewById(R.id.textViewUnits);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        textViewDate = findViewById(R.id.selectDate);
        nameInput = findViewById(R.id.inputFirstName);
        surnameInput = findViewById(R.id.inputLastName);
        heightInput = findViewById(R.id.editTextHeight);
        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        confirmPasswordInput = findViewById(R.id.inputConfirmPassword);

        configureCalendar();
        configureMeasurementSystem();

        //button to go to next part of sign up
        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });
    }

    //tests inputs
    private void checkCredentials() {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        radioButton = (RadioButton) findViewById(selectedId);
        name = nameInput.getText().toString().trim();
        surname = surnameInput.getText().toString().trim();
        gender = radioButton.getText().toString().trim();
        measurementSystem = toggleButton.getText().toString().trim();
        birthday = textViewDate.getText().toString().trim();

        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (name.isEmpty())
        {
            showError(nameInput, "Please enter a name");
        }
        else if (surname.isEmpty())
        {
            showError(surnameInput, "Please enter a surname");
        }
        else if (birthday.isEmpty() || birthday.equals("Select a date"))
        {
            textViewDate.setError("Please select a date");
            textViewDate.requestFocus();
        }
        else if (heightInput.getText().toString().isEmpty())
        {
            showError(heightInput, "Please enter a height");
        }
        else if (email.isEmpty() || !email.contains("@"))
        {
            showError(emailInput, "Please enter a valid email");
        }
        else if (password.isEmpty() || password.length() < 7)
        {
            showError(passwordInput, "Password must be at least 7 characters");
        }
        else if (confirmPassword.isEmpty() || !confirmPassword.equals(password))
        {
            showError(confirmPasswordInput, "Passwords do not match");
        }
        else
        {
            height = Double.parseDouble(heightInput.getText().toString());
            goToNextPartOfSignUp();
        }
    }

    //shows custom error message on selected inputs
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    //save inputs onto the intent and send them to next activity
    private void goToNextPartOfSignUp() {
        Intent intent = new Intent(CreateAccountActivity1.this, CreateAccountActivity2.class);
        if (measurementSystem.equals("Imperial")) {
            height = height * 2.54;
        }

        intent.putExtra("name", name);
        intent.putExtra("surname", surname);
        intent.putExtra("gender", gender);
        intent.putExtra("measurementSystem", measurementSystem);
        intent.putExtra("email", email);
        intent.putExtra("birthday", birthday);
        intent.putExtra("height", height);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    //sets the measurement system based on togglebutton value
    private void configureMeasurementSystem(){
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    measurementSystem = "Imperial";
                    textViewHeight.setText("INCH");
                } else {
                    measurementSystem = "Metric";
                    textViewHeight.setText("CM");
                }
            }
        });
    }

    //sets up the calendar for the user to select their birthday
    private void configureCalendar() {
        mDisplayDate = (TextView) findViewById(R.id.selectDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateAccountActivity1.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = day + "-" + month + "-" + year;
                mDisplayDate.setText(date);
            }
        };
    }
}