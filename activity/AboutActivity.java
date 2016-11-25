package com.example.administrator.five_in_a_row.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;

/**
 * Created by Administrator on 2016/11/23.
 */

public class AboutActivity extends AppCompatActivity {
    private Button btn_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.about);
        ExitApplication.getInstance().addActivity(this);
        initWidget();
        setWidget();
    }

    private void initWidget() {
        btn_back = (Button) findViewById(R.id.btn_about_back);
    }
    private void setWidget() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,MenuActivity.class));
                overridePendingTransition(R.anim.fade,R.anim.hold);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            startActivity(new Intent(AboutActivity.this, MenuActivity.class));
            overridePendingTransition(R.anim.fade,R.anim.hold);
        }
        return super.onKeyDown(keyCode, event);
    }

}
