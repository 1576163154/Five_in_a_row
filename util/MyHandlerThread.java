package com.example.administrator.five_in_a_row.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by Administrator on 2016/11/22.
 */

public class MyHandlerThread extends HandlerThread implements Handler.Callback{
    public MyHandlerThread(String name) {
        super(name);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
