package com.example.administrator.five_in_a_row.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2016/11/17.
 */

public class MenuActivity extends AppCompatActivity{
    private int quitCount;//统计用户点击back键次数

    private TextView tv_startGame;
    private TextView tv_exit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        ExitApplication.getInstance().addActivity(this);
        initWidget();
        setWidget();
    }
    private void initWidget() {
        tv_startGame = (TextView) findViewById(R.id.tv_gamestart);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
    }

    private void setWidget() {
        tv_startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,GameActivity.class));
                overridePendingTransition(R.anim.fade,R.anim.hold);
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
                btn_comfirm= (Button) window.findViewById(R.id.btn_exit_confirm);
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
