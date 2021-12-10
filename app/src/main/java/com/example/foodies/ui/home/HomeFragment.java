package com.example.foodies.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.foodies.AddFoodActivity;
import com.example.foodies.LoginActivity;
import com.example.foodies.MainActivity;
import com.example.foodies.R;
import com.example.foodies.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    //Firebase variables
    private FirebaseUser user;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef, rootRef, caloriesRef;

    //variables
    private PieChart pieChart;
    private Button addCaloriesButton;
    private TextView textViewCalorieGoal;
    private String id;
    private LocalDate localDate;
    private DateTimeFormatter formatter;
    private String date;
    private double calorieGoal;

    //color array for pie chart template
    final int[] MY_COLORS = {
            Color.  rgb(51,171,255),
            Color. rgb(8,117,193),
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


        //reference to the database
        rootRef = database.getReference();

        //grab current user and save their id
        user = FirebaseAuth.getInstance().getCurrentUser();
        id = user.getUid();

        //grabbing the calorie goal of the user
        usersRef = rootRef.child("users").child(id);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    calorieGoal = snapshot.child("userCalorieGoal").getValue(Double.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        textViewCalorieGoal = getView().findViewById(R.id.textViewCalorieGoal);
        pieChart = getView().findViewById(R.id.pieChartCalories);

        //getting today's date
        formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
        localDate = LocalDate.now();
        date = localDate.format(formatter);

        //getting calorie information from the database and populating pie chart with it
        caloriesRef = rootRef.child("usercalories").child(id).child(date);
        caloriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double dCaloriesEaten = 0;
                for(DataSnapshot ds : snapshot.getChildren()) {
                    double calories = ds.getValue(Double.class);
                    dCaloriesEaten = dCaloriesEaten + calories;
                }
                double dCaloriesLeft = calorieGoal - dCaloriesEaten;
                float caloriesEaten = (float) dCaloriesEaten;
                float caloriesLeft = (float) dCaloriesLeft;
                CreatePieChart(caloriesLeft, caloriesEaten, calorieGoal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //button to go to page to add calories for today
        addCaloriesButton = getView().findViewById(R.id.addCalorieButton);
        addCaloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddFoodActivity.class));
            }
        });
    }

    //creates the pie chart
    private void CreatePieChart(float caloriesLeft, float caloriesEaten, double calorieGoal) {
        ArrayList<Integer> colors = new ArrayList<>();

        for(int c: MY_COLORS) colors.add(c);

        textViewCalorieGoal.setText("Calorie Goal: " + String.valueOf(calorieGoal));

        ArrayList<PieEntry> calories = new ArrayList<>();
        calories.add(new PieEntry(caloriesLeft, "Left"));
        calories.add(new PieEntry(caloriesEaten, "Eaten"));

        PieDataSet pieDataSet = new PieDataSet(calories, "Calories");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(20f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(30f);
        pieChart.setCenterText("Calories");
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000);
    }

}