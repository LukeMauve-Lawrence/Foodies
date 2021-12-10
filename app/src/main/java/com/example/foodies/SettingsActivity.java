package com.example.foodies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    //Firebase variables
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference("users");
    private FirebaseUser currentUser;

    //variables
    private ToggleButton toggleButton;
    private String measurementSystem;
    private RadioGroup radioGroup;
    private RadioButton radioButtonMale, radioButtonFemale, radioButton;
    private TextView settingsBirthday, settingsMeasurement;
    private Button saveButton;
    private String id;
    private EditText settingsName, settingsSurname, settingsHeight;
    private String settingsGender;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //finding views
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupSettings);
        radioButtonMale = findViewById(R.id.radioButtonMaleSettings);
        radioButtonFemale = findViewById(R.id.radioButtonFemaleSettings);
        toggleButton = findViewById(R.id.settingsToggleButton);
        settingsName = findViewById(R.id.settingsName);
        settingsSurname = findViewById(R.id.settingsSurname);
        settingsBirthday = findViewById(R.id.settingsBirthday);
        settingsMeasurement = findViewById(R.id.settingsMeasurement);
        settingsHeight = findViewById(R.id.settingsHeight);

        //gets current user's id
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getUid();

        configureCalendar();
        configureInputs();

        //toggle button for metric or imperial
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (toggleButton.isChecked()) {
                    settingsMeasurement.setText("INCH");
                } else {
                    settingsMeasurement.setText("CM");
                }
            }
        });

        //save the changes button
        saveButton = findViewById(R.id.settingsSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUsers();
            }
        });
    }

    //updates the database with user inputs
    private void updateUsers() {
        //tests them first
        if (settingsName.getText().toString().isEmpty()) {
            showError(settingsName, "Please enter a name");
        } else if (settingsSurname.getText().toString().isEmpty()) {
            showError(settingsSurname, "Please enter a surname");
        } else if (settingsHeight.getText().toString().isEmpty()) {
            showError(settingsHeight, "Please enter a height");
        } else {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            radioButton = (RadioButton) findViewById(selectedId);

            double height = Double.parseDouble(settingsHeight.getText().toString());

            //converting data to correct system for saving
            String system;
            if (toggleButton.isChecked()) {
                height = height * 2.54;
                system = "Imperial";
            } else {
                system = "Metric";
            }

            mDatabase.child(id).child("userName").setValue(settingsName.getText().toString());
            mDatabase.child(id).child("userSurname").setValue(settingsSurname.getText().toString());
            mDatabase.child(id).child("userGender").setValue(radioButton.getText().toString());
            mDatabase.child(id).child("userBirthday").setValue(settingsBirthday.getText().toString());
            mDatabase.child(id).child("userMeasurementSystem").setValue(system);
            mDatabase.child(id).child("userHeight").setValue(height);
            Toast.makeText(SettingsActivity.this, "Changes saved!", Toast.LENGTH_LONG).show();
        }
    }

    //shows custom error message on selected inputs
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    //set up initial input values
    private void configureInputs(){

        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double dHeight = snapshot.child("userHeight").getValue(Double.class);
                measurementSystem = snapshot.child("userMeasurementSystem").getValue(String.class);
                if (measurementSystem.equals("Imperial")) {
                    dHeight = dHeight / 2.54;
                    toggleButton.setChecked(true);
                    settingsMeasurement.setText("INCH");

                } else if (measurementSystem.equals("Metric")) {
                    settingsMeasurement.setText("CM");
                    toggleButton.setChecked(false);
                }
                String height = String.valueOf(dHeight);
                settingsGender = snapshot.child("userGender").getValue(String.class);

                if (settingsGender.equals("Male")) {
                    radioButtonFemale.setChecked(false);
                    radioButtonMale.setChecked(true);
                } else {
                    radioButtonFemale.setChecked(true);
                    radioButtonMale.setChecked(false);
                }

                settingsName.setText(snapshot.child("userName").getValue(String.class));
                settingsSurname.setText(snapshot.child("userSurname").getValue(String.class));
                settingsBirthday.setText(snapshot.child("userBirthday").getValue(String.class));
                settingsHeight.setText(height);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    measurementSystem = "Imperial";
                } else {
                    measurementSystem = "Metric";
                }
            }
        });
    }

    //sets up calendar for user's birthday to be entered
    private void configureCalendar() {
        mDisplayDate = (TextView) findViewById(R.id.settingsBirthday);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SettingsActivity.this,
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