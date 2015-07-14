package com.example.vino.learnboardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Joker on 2015/6/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    //接收到广播的时候执行该方法
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"receive broadcast",Toast.LENGTH_SHORT).show();
    }
}
