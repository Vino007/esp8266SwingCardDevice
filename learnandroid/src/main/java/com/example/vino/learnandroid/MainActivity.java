package com.example.vino.learnandroid;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vino.learnandroid.adapter.FruitAdapter;
import com.example.vino.learnandroid.service.MyService;
import com.example.vino.learnandroid.webview.WebviewActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private ListView fruit_listview;
    private Button btnWebView;
    private Button return_btn;
    private  Button btnStartService;
    private  Button btnStopService;
    private  Button btnBindService;
    private  Button btnUnbindService;

    private MyService.DownloadBinder downloadBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fruit_listview= (ListView) findViewById(R.id.fruit_listview);
        btnWebView = (Button) findViewById(R.id.webview_btn);
        return_btn= (Button) findViewById(R.id.return_btn);
        btnStartService= (Button) findViewById(R.id.btnStartService);
        btnStopService= (Button) findViewById(R.id.btnStopService);
        btnBindService= (Button) findViewById(R.id.btnBindService);
        btnUnbindService= (Button) findViewById(R.id.btnUnbindService);
        //添加按钮的监听器，由于activity实现了onclicklistener接口，监听this就可以
        return_btn.setOnClickListener(this);
        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
        btnWebView.setOnClickListener(this);
        btnBindService.setOnClickListener(this);
        btnUnbindService.setOnClickListener(this);


        ArrayList list=new ArrayList();
        list.add("123");
        list.add("456");

        FruitAdapter adapter=new FruitAdapter(this,list);
        fruit_listview.setAdapter(adapter);
    }

    private ServiceConnection connection=new ServiceConnection() {

        //服务成功绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder= (MyService.DownloadBinder) service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }
        //服务解除绑定的时候调用
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 用来判断服务是否运行.
     * @param mContext
     * @param className 判断的服务名字 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    @Override
    public void onClick(View v) {
        Log.i("onclick",v.getId()+"");
        switch (v.getId()) {
            case R.id.webview_btn:
                Log.i("webview","webviewstart");
                startActivity(new Intent(this, WebviewActivity.class));
                break;

            case R.id.return_btn:
                Toast.makeText(this,"return",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnStartService:
                startService(new Intent(this, MyService.class));//启动服务
                break;
            case R.id.btnStopService:
                stopService(new Intent(this,MyService.class));
                break;
            case R.id.btnBindService:
                Intent bindIntent=new Intent(this,MyService.class);
                bindService(bindIntent,connection,BIND_AUTO_CREATE);//context类的方法,绑定服务，BIND_AUTO_CREATE表示绑定后自动创建服务

                break;
            case R.id.btnUnbindService:
                if(isServiceRunning(this,"com.example.vino.learnandroid.service.MyService"))
                unbindService(connection);//从activity上解绑服务
                else
                Toast.makeText(this,"未绑定服务",Toast.LENGTH_SHORT).show();

                break;
            default:
                break;

        }
    }
}
