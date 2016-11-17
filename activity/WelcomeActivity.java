package com.example.administrator.five_in_a_row.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/17.
 */

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        ExitApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,MenuActivity.class));
                overridePendingTransition(R.anim.fade,R.anim.hold);
            }
        };
        timer.schedule(timerTask,2000);
    }
}
