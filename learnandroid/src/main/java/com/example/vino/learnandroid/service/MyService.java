package com.example.vino.learnandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 服务启动后就和activity没有关系，通过在activity绑定服务可以进行通信，当进程被销毁后，服务也会跟着销毁
 */
public class MyService extends Service {
   private DownloadBinder mBinder=new DownloadBinder();
    @Override
    public IBinder onBind(Intent intent) {

       return mBinder;
    }

     public class DownloadBinder extends Binder{
        public  void startDownload(){
            Log.i("myService","startDownload");
        }
        public int getProgress(){
            Log.i("myService","getProgress");
            return 0;
        }
    }
    //当服务被被创建的时候执行
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("myService","onCreate");
    }
    //服务每次启动的都是都会执行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("myService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("myService","onDestroy");
    }
}
