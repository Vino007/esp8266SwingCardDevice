package com.example.vino.utils;

import android.app.Application;

import com.example.vino.esp8266test.SocketClient;

/**
 * Created by Joker on 2015/4/25.
 */
public class MyApplication extends Application {
    private SocketClient client;
    private boolean connectStatus;

    public boolean getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(boolean connectStatus) {
        this.connectStatus = connectStatus;
    }

    public SocketClient getClient() {
        return client;
    }

    public void setClient(SocketClient client) {
        this.client = client;
    }
}
