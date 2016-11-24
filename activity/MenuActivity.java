package com.example.administrator.five_in_a_row.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;
import com.example.administrator.five_in_a_row.view.ChessPanel;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2016/11/17.
 */

public class MenuActivity extends AppCompatActivity {
    private int quitCount;//统计用户点击back键次数

    private TextView tv_startGame;
    private TextView tv_exit;
    private TextView tv_about;
    private TextView tv_setting;

    private ToggleButton tbtn_first, tbtn_audio;
    private boolean isFirstStart;//是否第一次启动

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }

    private static SharedPreferences.Editor editor;
    //以下变量用来存储游戏设置各个按钮状态
    private static boolean isGameAudio;

    public static boolean isGameWhoFirst() {
        return isGameWhoFirst;
    }

    public static boolean isGameAudio() {
        return isGameAudio;
    }

    private static boolean isGameWhoFirst;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        ExitApplication.getInstance().addActivity(this);
        initWidget();
        setWidget();
        initData();//由于用户下一次打开时并不会去设置界面，故需要在界面加载的时候。就需要获取的用户配置信息
    }

    private void initWidget() {
        tv_startGame = (TextView) findViewById(R.id.tv_gamestart);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        tv_about = (TextView) findViewById(R.id.tv_about);
        //setting界面的togglebutton
        View settingView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.setting, null);
        tbtn_first = (ToggleButton) settingView.findViewById(R.id.tbtn_first);
        tbtn_audio = (ToggleButton) settingView.findViewById(R.id.tbtn_audio);
    }

    private void setWidget() {
        tv_startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, GameActivity.class));
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog exitDialog = new AlertDialog.Builder(MenuActivity.this).create();
                exitDialog.show();
                Window window = exitDialog.getWindow();//获取弹出框的句柄
                //这里仍然用的系统自带，只是修改了布局文件
                window.setContentView(R.layout.exit_dialog);//实例化
                Button btn_comfirm;
                Button btn_cancel;
                btn_cancel = (Button) window.findViewById(R.id.btn_exit_cancel);
                btn_comfirm = (Button) window.findViewById(R.id.btn_exit_confirm);
                btn_comfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExitApplication.getInstance().exit();
                        exitDialog.dismiss();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitDialog.dismiss();
                    }
                });
            }
        });
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, AboutActivity.class));
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
    }

    private void initData() {
        sharedPreferences = getSharedPreferences("WuZiQiSetting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isFirstStart = sharedPreferences.getBoolean("isFirstStart", true);
        //判断是不是第一次运行，默认为第一次运行
        if (isFirstStart) {
            //如果是第一次运行，那么直接获取控件当前的默认状态
            isGameWhoFirst = tbtn_first.isChecked();
            isGameAudio = tbtn_audio.isChecked();
        } else {
            //如果不是那么，就从之前写入的设置文件中读取
            isGameWhoFirst =sharedPreferences.getBoolean("isGameWhoFirst", false);
            isGameAudio = sharedPreferences.getBoolean("isGameAudio", false);
            //再将控件设为与文件相符的状态
            tbtn_first.setChecked(isGameWhoFirst);
            tbtn_audio.setChecked(isGameAudio);
        }
        ChessPanel.setWhite(isGameWhoFirst);
        ChessPanel.setPlayAudio(isGameAudio);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (quitCount < 1) {
                Toast.makeText(MenuActivity.this, "再点一次，退出程序！", Toast.LENGTH_SHORT).show();
                quitCount++;
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        quitCount = 0;
                    }
                };
                timer.schedule(task, 1000);
            } else {
                ExitApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
