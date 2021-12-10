package com.example.foodies.ui.goals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.foodies.MainActivity;
import com.example.foodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GoalsFragment extends Fragment {

    //Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    private GoalsViewModel goalsViewModel;

    //variables
    private EditText editTextNumber3, editTextNumber4, editTextNumber5;
    private Button updateGoalsButton;
    private String id;
    private TextView goalsSystem;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        goalsViewModel =
                ViewModelProviders.of(this).get(GoalsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_goals, container, false);
        final TextView textView = root.findViewById(R.id.textView5);
        goalsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        //grabs the current user and saves his ID
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getUid();

        //create a reference to the database using the id to identify the user
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users").child(id);

        //saving EditTexts into variables
        editTextNumber3 = getView().findViewById(R.id.editTextNumber3);
        editTextNumber4 = getView().findViewById(R.id.editTextNumber4);
        editTextNumber5 = getView().findViewById(R.id.editTextNumber5);
        goalsSystem = getView().findViewById(R.id.goalsSystem);

        //sets the measurement system type based on what is saved for the user in the database
        usersRef = database.getReference("users").child(id);

        //sets the inputs to values from the database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String measurementSystem = snapshot.child("userMeasurementSystem").getValue(String.class);
                double dWeight = snapshot.child("userWeightGoal").getValue(Double.class);
                if(measurementSystem.equals("Imperial")) {
                    goalsSystem.setText("lbs");
                    dWeight = dWeight * 2.205;
                } else if(measurementSystem.equals("Metric")) {
                    goalsSystem.setText("kg");
                }
                editTextNumber3.setText(String.valueOf(dWeight));
                editTextNumber4.setText(String.valueOf(snapshot.child("userCalorieGoal").getValue(Double.class)));
                editTextNumber5.setText(String.valueOf(snapshot.child("userStepGoal").getValue(Double.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //listener for update button
        updateGoalsButton = getView().findViewById(R.id.updateGoalsButton);
        updateGoalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testInputs();
            }
        });
    }

    //Catches any empty inputs and asks the user to fill them in
    private void testInputs() {
        String sWeightGoal = editTextNumber3.getText().toString().trim();
        String sCalorieGoal = editTextNumber4.getText().toString().trim();
        String sStepGoal = editTextNumber5.getText().toString().trim();

        if (sWeightGoal.isEmpty()) {
            showError(editTextNumber3, "Please enter a weight goal");
        } else if (sCalorieGoal.isEmpty()) {
            showError(editTextNumber3, "Please enter a daily calorie intake goal");
        } else if (sStepGoal.isEmpty()) {
            showError(editTextNumber3, "Please enter a daily steps goal");
        } else {
            double weightGoal = Double.parseDouble(sWeightGoal);
            double calorieGoal = Double.parseDouble(sCalorieGoal);
            double stepGoal = Double.parseDouble(sStepGoal);
            updateUserGoals(weightGoal, calorieGoal, stepGoal);
            Toast.makeText(getActivity(), "Goals updated!", Toast.LENGTH_LONG).show();
        }
    }

    //shows custom error message on selected inputs
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    //writes the new goals to the database
    private void updateUserGoals(double weightGoal, double calorieGoal, double stepGoal) {
        usersRef.child("userWeightGoal").setValue(weightGoal);
        usersRef.child("userCalorieGoal").setValue(calorieGoal);
        usersRef.child("userStepGoal").setValue(stepGoal);
    }
}