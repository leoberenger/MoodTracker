package com.openclassrooms.moodtracker.Controllers.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.openclassrooms.moodtracker.Adapters.PageAdapter;
import com.openclassrooms.moodtracker.R;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private PopupWindow mPopupWindow;
    private FrameLayout mMainLayout;

    private Button mCommentButton;
    private Button mHistoryButton;
    private View mCommentPopup;
    int[] smileys = {R.drawable.smiley_sad, R.drawable.smiley_disappointed, R.drawable.smiley_normal, R.drawable.smiley_happy, R.drawable.smiley_super_happy};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get the application context
        mContext = getApplicationContext();

        // Get the activity
        mActivity = MainActivity.this;

        mMainLayout = (FrameLayout) findViewById(R.id.activity_main_frame_layout);
        mCommentButton = (Button) findViewById(R.id.activity_main_comment_button);

        mHistoryButton = (Button) findViewById(R.id.activity_main_history_button);

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyActivityIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(historyActivityIntent);
            }
        });

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Ajoutez un commentaire", Toast.LENGTH_SHORT).show();

                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.activity_main_comment_popup,null);

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(customView, 800, 600);

                // Get a reference for the custom view close button
                Button cancelButton = (Button) customView.findViewById(R.id.activity_main_comment_popup_cancel_btn);

                // Set a click listener for the popup window close button
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                //Positions popup window
                mPopupWindow.showAtLocation(mMainLayout, Gravity.TOP,0,0);
            }
        });

        this.configureViewPager();
    }

    private void configureViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        viewPager.setAdapter(new PageAdapter(
                getSupportFragmentManager(),
                getResources().getIntArray(R.array.colorPagesViewPager), smileys) {});
        viewPager.setCurrentItem(3);
    }
}