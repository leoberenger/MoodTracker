package com.openclassrooms.moodtracker.Controllers.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openclassrooms.moodtracker.Models.WeeklyMoods;
import com.openclassrooms.moodtracker.R;

public class HistoryActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private Button mPieChartButton;

    private WeeklyMoods mWeeklyMoods = new WeeklyMoods();
    private double [] viewSizeDivider = {3, 2.5, 2, 1.5, 1};;
    private double deviceWidth;
    private double deviceHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Initialize Preferences
        mPreferences = getSharedPreferences("dailyMoods", MODE_PRIVATE);

        //Get Device Width and Height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        configureWeeklyMoods();
        configurePieChartButton();
    }

    private void configureWeeklyMoods(){
        //Inflate layout
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout container = (LinearLayout) findViewById(R.id.activity_history_linearLayout);

        //Declare and initializes our 7 textview frames
        View [] views = new View [7];

        //Create the 7 views of daily moods
        for(int i = 0; i < views.length; i++) {
            //Get Mood of the day [i]
            int dailyMood = mWeeklyMoods.getDailyMood(mPreferences, i+1);

            //Inflate framelayouts and create new textview and comment button
            views[i] = inflater.inflate(R.layout.activity_history_frame, null);

            //Serialize FrameLayouts, TextViews and Buttons
            FrameLayout frameLayout = (FrameLayout)views[i].findViewById(R.id.activity_history_framelayout);
            TextView textView = (TextView)views[i].findViewById(R.id.activity_history_textview);
            ImageButton commentButton = (ImageButton)views[i].findViewById(R.id.activity_history_comment_btn);

            //Define FrameLayout width and height with device width and height
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    (int) (deviceWidth/viewSizeDivider[dailyMood]),
                    (int) deviceHeight/9);

            //Set FrameLayout
            frameLayout.setLayoutParams(params);
            frameLayout.setBackgroundColor(getResources().getIntArray(R.array.colorPagesViewPager)[dailyMood]);

            //Set TextView according to Mood
            textView.setText(getResources().getStringArray(R.array.dayCount)[i]);

            //Set Comment button if comment exists
            if(!mWeeklyMoods.getDailyComment(mPreferences, i+1).equals("")){
                //Show button + if click on button, show Comment (Toast)
                commentButton.setVisibility(ImageButton.VISIBLE);
                final int finalI = i;
                commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = mWeeklyMoods.getDailyComment(mPreferences, finalI+1);
                        Toast.makeText(HistoryActivity.this, comment, Toast.LENGTH_LONG).show();
                    }   });
            }else{
                commentButton.setVisibility(ImageButton.INVISIBLE);
            }

            //Create new view and add it to the container (LinearLayout)
            container.addView(views[i]);
        }

        //Add PieChart Button
        View piechartView = inflater.inflate(R.layout.activity_history_piechart_btn, null);
        container.addView(piechartView);
    }

    private void configurePieChartButton() {
        //Serialize the button
        mPieChartButton = (Button) findViewById(R.id.activity_history_piechart_button);

        //Add a clickListener to go to PieChart Activity
        mPieChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pieChartActivityIntent = new Intent(HistoryActivity.this, HistoryPieChartActivity.class);
                startActivity(pieChartActivityIntent);
            }
        });
    }
}