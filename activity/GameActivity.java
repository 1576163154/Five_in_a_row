package com.example.administrator.five_in_a_row.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.util.ExitApplication;
import com.example.administrator.five_in_a_row.view.ChessPanel;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private ChessPanel chessPanel;
    private TextView tv_who;//该谁落子
    private Button btn_back;//返回
    private Button btn_withdraw;//悔棋
    private Resources res;

    private boolean threadFlag = true;
    private Thread tv_Thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ExitApplication.getInstance().addActivity(this);
        res = this.getResources();
        initWidget();
        setWidget();
        tv_Thread.start();

    }

    private void initWidget() {
        chessPanel = (ChessPanel) findViewById(R.id.panel_main);
        tv_who = (TextView) findViewById(R.id.tv_who);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_withdraw = (Button) findViewById(R.id.btn_withdraw);
    }

    private void setWidget() {
        btn_back.setOnClickListener(this);
        btn_withdraw.setOnClickListener(this);
//异步任务，改变tv_who文本信息
        tv_Thread =  new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    if (chessPanel.isWhite()) {
                        message.what = 1;
                    } else {
                        message.what = 2;
                    }
                    handler.postDelayed(tv_Thread,200);//添加线程执行延迟
                    handler.sendMessage(message);

                }
            });
        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_withdraw:
                chessPanel.withDraw();
                break;
            case R.id.btn_back:
                if (chessPanel.isPanelEmpty()) {
                    startActivity(new Intent(GameActivity.this, MenuActivity.class));
                } else {
                    //弹窗询问
                }

        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_who.setText(res.getString(R.string.whitepiece));
                    break;
                case 2:
                    tv_who.setText(res.getString(R.string.blackpiece));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //新建AlertDialog
        return false;//不弹出默认的菜单弹出
    }

}
