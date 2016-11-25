package com.example.administrator.five_in_a_row.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;
import com.example.administrator.five_in_a_row.view.ChessPanel;

/**
 * Created by Administrator on 2016/11/23.
 */

public class SettingActivity extends AppCompatActivity {
    private Button btn_back;
    private ToggleButton tbtn_first;
    private ToggleButton tbtn_audio;




    private boolean isFirstStart ;//是否第一次启动

    //以下变量用来存储游戏设置各个按钮状态
    private static boolean isGameAudio;
    private static boolean isGameWhoFirst;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.setting);
        ExitApplication.getInstance().addActivity(this);
        initWidget();
        setWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initWidget() {
        btn_back = (Button) findViewById(R.id.btn_setting_back);
        tbtn_first = (ToggleButton) findViewById(R.id.tbtn_first);
        tbtn_audio = (ToggleButton) findViewById(R.id.tbtn_audio);
    }

    private void setWidget() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        tbtn_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("isGameWhoFirst", String.valueOf(isChecked));
                isGameWhoFirst = isChecked;
                ChessPanel.setWhite(isGameWhoFirst);

            }
        });
        tbtn_audio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("isGameAudio", String.valueOf(isChecked));
                isGameAudio = isChecked;
                ChessPanel.setPlayAudio(isGameAudio);
            }
        });
    }

    private void initData() {
        isFirstStart = MenuActivity.getSharedPreferences().getBoolean("isFirstStart", true);
        //判断是不是第一次运行，默认为第一次运行
        if (isFirstStart) {
            //如果是第一次运行，那么直接获取控件当前的默认状态
            isGameWhoFirst = tbtn_first.isChecked();
            isGameAudio = tbtn_audio.isChecked();
        } else {
            //如果不是那么，就从之前写入的设置文件中读取
            isGameWhoFirst = MenuActivity.getSharedPreferences().getBoolean("isGameWhoFirst",false);
            isGameAudio = MenuActivity.getSharedPreferences().getBoolean("isGameAudio",false);
            //再将控件设为与文件相符的状态
            tbtn_first.setChecked(isGameWhoFirst);
            tbtn_audio.setChecked(isGameAudio);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFirstStart){
            //启动后再将isFirstStart改为false
            MenuActivity.getEditor().putBoolean("isFirstStart",false);
        }
        //在关闭之前写入文件
        MenuActivity.getEditor().putBoolean("isGameWhoFirst",isGameWhoFirst);
        MenuActivity.getEditor().putBoolean("isGameAudio",isGameAudio);
        MenuActivity.getEditor().commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.fade,R.anim.hold);
        }
        return super.onKeyDown(keyCode, event);
    }
}
