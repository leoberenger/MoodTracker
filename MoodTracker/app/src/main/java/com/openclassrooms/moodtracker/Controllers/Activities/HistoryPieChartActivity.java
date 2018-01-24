package com.openclassrooms.moodtracker.Controllers.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.openclassrooms.moodtracker.Models.WeeklyMoods;
import com.openclassrooms.moodtracker.R;
import com.openclassrooms.moodtracker.Adapters.DecimalRemover;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryPieChartActivity extends AppCompatActivity {

    SharedPreferences mPreferences;
    WeeklyMoods mDailyMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pie_chart);

        PieChart pieChart = (PieChart)findViewById(R.id.activity_history_piechart);
        mPreferences = getSharedPreferences("dailyMoods", MODE_PRIVATE);
        mDailyMood = new WeeklyMoods();

        float [] moods = new float[5];

        for (int i = 0; i < 7; i++) {
            int mood = mDailyMood.getDailyMood(mPreferences, i);
            switch (mood) {
                case 0:
                    moods[0]++;
                    break;
                case 1:
                    moods[1]++;
                    break;
                case 2:
                    moods[2]++;
                    break;
                case 3:
                    moods[3]++;
                    break;
                case 4:
                    moods[4]++;
                    break;
            }
        }

        for (int i = 0; i<moods.length; i++){
            moods[i] = moods[i] / 7 * 100;
        }

        String [] moodLabels = {"Sad", "Disappointed", "Normal", "Happy", "SuperHappy"};
        int [] colors = getResources().getIntArray(R.array.colorPagesViewPager);
        List<Integer> moodColors = new ArrayList<Integer>();
        List<PieEntry> entries = new ArrayList<>();

        for(int i = 0; i<moods.length; i++){
            if (moods[i] != 0.0f){
                entries.add(new PieEntry(moods[i], moodLabels[i]));
                moodColors.add(colors[i]);
            }
        }

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.setDescription(null);


        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(moodColors);

        PieData data = new PieData(set);
        pieChart.setData(data);
        data.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        pieChart.invalidate(); // refresh
    }
}
