package com.openclassrooms.moodtracker.Controllers.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.openclassrooms.moodtracker.Adapters.PageAdapter;
import com.openclassrooms.moodtracker.Models.WeeklyMoods;
import com.openclassrooms.moodtracker.R;

import java.time.Year;
import java.util.Calendar;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private PopupWindow mPopupWindow;
    private FrameLayout mMainLayout;
    private VerticalViewPager viewPager;
    private Button mCommentButton;
    private Button mHistoryButton;
    private Button cancelButton;
    private Button validateButton;
    private EditText commentsText;

    private WeeklyMoods mTodayMood;
    private int intTodayMood;
    private SharedPreferences mPreferences;
    private int todayYear;
    private int todayMonth;
    private int todayDay;

    private double deviceWidth;
    private double deviceHeight;

    private final int[] smileys = {R.drawable.smiley_sad, R.drawable.smiley_disappointed, R.drawable.smiley_normal, R.drawable.smiley_happy, R.drawable.smiley_super_happy};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configure Comment and History Buttons
        mCommentButton = (Button) findViewById(R.id.activity_main_comment_button);
        mHistoryButton = (Button) findViewById(R.id.activity_main_history_button);
        mMainLayout = (FrameLayout) findViewById(R.id.activity_main_frame_layout);
        mContext = getApplicationContext();
        this.configureCommentAndHistoryButtons();

        mPreferences = getSharedPreferences("dailyMoods", MODE_PRIVATE);
        mTodayMood = new WeeklyMoods();

        //Get Current Date
        Calendar.getInstance();
        todayYear = YEAR;
        todayMonth = MONTH;
        todayDay = DAY_OF_MONTH;

        //If not same day, update WeeklyMoods according with the numbers of day since last opening
        int nbOfDaysSinceLastOpening = betweenDays(mPreferences);
        if(nbOfDaysSinceLastOpening != 0)
            mTodayMood.updateWeeklyMoods(mPreferences, nbOfDaysSinceLastOpening);

        //Configure the view pager according to TodayMood (if same day -> saved / if not -> default)
        intTodayMood = mTodayMood.getDailyMood(mPreferences, 0);
        this.configureViewPager(intTodayMood);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Save current date
        mPreferences.edit().putInt("Today", todayDay).apply();
        mPreferences.edit().putInt("Today", todayMonth).apply();
        mPreferences.edit().putInt("Today", todayYear).apply();

        //Save current mood as WeeklyMood[0]
        int position = viewPager.getCurrentItem();
        mTodayMood.setDailyMood(mPreferences, 0, position);
    }

    private int betweenDays (SharedPreferences prefsFile){

        int betweenDays;

        int savedYear = prefsFile.getInt("Today", todayYear);
        int savedMonth = prefsFile.getInt("Today", todayMonth);
        int savedDay = prefsFile.getInt("Today", todayDay);

        if(savedYear == todayYear && savedMonth == todayMonth) {
            betweenDays = todayDay - savedDay;
        }else if ((todayDay < 7) && (todayYear - savedYear <= 1)
                && ((todayMonth - savedMonth)==1) || (savedMonth==12 && todayMonth==1)) {
            int monthNbOfDays = 0;
            switch(savedMonth) {
                case 1: case 3 : case 5 : case 7 : case 8 : case 10 : case 12: monthNbOfDays = 31; break;
                case 4 : case 6 : case 9 : case 11 : monthNbOfDays = 30; break;
                case 2 : //February
                    if((savedYear % 4 == 0)&&((savedYear%100 !=0)||(savedYear %400 == 0)))
                        monthNbOfDays = 29;
                    else
                        monthNbOfDays = 28;
                    break;
            }
            betweenDays = todayDay + (monthNbOfDays-savedDay);
        }else{
            betweenDays = 7;
        }
        return betweenDays;
    }

    private void configureViewPager(int todayMood) {
        viewPager = (VerticalViewPager) findViewById(R.id.activity_main_viewpager);
        viewPager.setAdapter(new PageAdapter(
                getSupportFragmentManager(),
                getResources().getIntArray(R.array.colorPagesViewPager), smileys) {});
        viewPager.setCurrentItem(todayMood);
    }

    private void configureCommentAndHistoryButtons(){
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Activity History when History Button clicked
                Intent historyActivityIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(historyActivityIntent);
            }
        });

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show Popup Window when Comment Button clicked
                configureCommentPopupWindow();
            }
        });
    }

    private void configureCommentPopupWindow(){
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View commentPopupView = inflater.inflate(R.layout.activity_main_comment_popup,null);

        getDeviceMetrics();
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(commentPopupView, (int)(deviceWidth/1.2), (int)(deviceHeight/3), true);

        // Get a reference for the close and validate buttons
        cancelButton = (Button) commentPopupView.findViewById(R.id.activity_main_comment_popup_cancel_btn);
        validateButton = (Button) commentPopupView.findViewById(R.id.activity_main_comment_popup_validate_btn);
        commentsText = (EditText) commentPopupView.findViewById((R.id.activity_main_comment_popup_validate_edittext));

        configureCommentCancelButton();
        configureCommentValidateButton();

        //Positions popup window
        mPopupWindow.showAtLocation(mMainLayout, Gravity.TOP,0,0);
    }

    private void configureCommentCancelButton(){
        // Set a click listener for the popup window close button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
    }

    private void configureCommentValidateButton(){
        //Save Text in EditText as a comment when Validate clicked
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getComment from EditText
                String dailyComment = commentsText.getText().toString();

                if(!dailyComment.equals("")){
                    //Save the comment
                    mTodayMood.setDailyComment(mPreferences, 0, dailyComment);
                    mPopupWindow.dismiss();
                    Toast.makeText(MainActivity.this, "Commentaire enregistré", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Ecrivez votre commentaire", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDeviceMetrics(){
        //Get Device Width and Height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;
    }

}