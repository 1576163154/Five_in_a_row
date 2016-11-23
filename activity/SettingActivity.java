package com.example.administrator.five_in_a_row.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;

/**
 * Created by Administrator on 2016/11/23.
 */

public class SettingActivity extends AppCompatActivity {
    private Button btn_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        ExitApplication.getInstance().addActivity(this);
        initWidget();
        setWidget();
    }
    private void initWidget() {
        btn_back = (Button) findViewById(R.id.btn_setting_back);
    }
    private void setWidget() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,MenuActivity.class));
                overridePendingTransition(R.anim.fade,R.anim.hold);
            }
        });
    }
}
