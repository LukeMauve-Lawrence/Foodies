package com.example.foodies.ui.weight;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.foodies.R;
import com.example.foodies.WeightActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WeightFragment extends Fragment {

    private WeightViewModel weightViewModel;
    private BarChart barChart;
    private Button addWeightButton;

    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference weightRef;
    private DatabaseReference measurementRef;

    //variables
    private String id;

    private ArrayList<String> xAxisLabel = new ArrayList<>();
    private ArrayList<BarEntry> barEntries = new ArrayList<>();

    private LocalDate localDate;
    private DateTimeFormatter formatter;
    private String date;

    private String measurementSystem;
    private TextView weightInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        weightViewModel =
                ViewModelProviders.of(this).get(WeightViewModel.class);
        View root = inflater.inflate(R.layout.fragment_weight, container, false);
        final TextView textView = root.findViewById(R.id.text_weight);
        weightViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //button to go to page to add weight for the day
        addWeightButton = getView().findViewById(R.id.addWeightButton);
        addWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WeightActivity.class));
            }
        });

        //grabs the current signed in user's ID
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getUid();

        //formatting date
        formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

        weightInfo = getView().findViewById(R.id.weightInfo);

        //getting the user's measurement system
        measurementRef = database.getReference("users").child(id).child("userMeasurementSystem");
        measurementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                measurementSystem = snapshot.getValue(String.class);
                if (measurementSystem.equals("Imperial")) {
                    weightInfo.setText("Your weight over the last week in pounds");
                } else {
                    weightInfo.setText("Your weight over the last week in kilograms");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //grabbing data from database and creating barchart with it.
        //Data is based on the user's weight for the last week
        weightRef = database.getReference("userweight").child(id);
        weightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barEntries.clear();
                xAxisLabel.clear();
                localDate = LocalDate.now();
                long n = -6;
                for (int day = 0; day <= 6; day++) {
                    date = localDate.plusDays(n).format(formatter);
                    double dWeight;
                    if (snapshot.child(date).getValue(Double.class)!=null){
                        if (measurementSystem.equals("Imperial")) {
                            dWeight = snapshot.child(date).getValue(Double.class);
                            dWeight = dWeight * 2.205;
                        } else {
                            dWeight = snapshot.child(date).getValue(Double.class);
                        }


                    } else {
                        dWeight = 0;
                    }
                    float weight = (float) dWeight;
                    String date = String.valueOf(localDate.plusDays(n).getDayOfWeek());
                    String shortHandDays = date.substring(0, Math.min(date.length(), 3));
                    xAxisLabel.add(shortHandDays);
                    barEntries.add(new BarEntry(day, weight));
                    n++;
                }
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //creates the bar chart
    private void createBarChart() {
        barChart = (BarChart) getView().findViewById(R.id.barChartWeight);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Weight");
        barDataSet.setValueTextSize(20f);

        BarData barData = new BarData(barDataSet);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        barChart.animateY(1000);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);
    }
}