package com.example.vino.esp8266test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vino.utils.MessageHandler;
import com.example.vino.utils.MyApplication;
import com.example.vino.utils.MyUtils;
import com.example.vino.utils.ReadParameterSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 0x01：发送命令成功
 * 0x02: 建立连接成功
 * 0x03: 建立连接失败
 */
public class MainActivity extends ActionBarActivity {

    private Button connect_btn;

    private Button readParameter_btn;
    private Button setting_btn;
    private Handler handler;
    private TextView receive_data_textview;
    private ListView parameter_lv;
    private boolean connectStatus = false;
    private List<Integer> message = new ArrayList<>();//报文存储

    private SocketClient client = null;
    private MyApplication application;
    private String[] parameterNames = {"刷卡器时间:", "刷卡时段:", "工作模式:"};
    private String[] parameterContents = {"无", "无", "无"};
    private List<Map<String, Object>> items_lv;
    private SimpleAdapter simpleAdapter_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect_btn = (Button) findViewById(R.id.connect_btn);

        readParameter_btn = (Button) findViewById(R.id.readParameter_btn);
        setting_btn= (Button) findViewById(R.id.setting_btn);


        receive_data_textview = (TextView) findViewById(R.id.receive_data_textview);
        Log.d("start", "正常启动");
        handler = new MyHandler();
        //初始化下行报文
        for (int i = 0; i < 9; i++)
            message.add(0x00);
        application = (MyApplication) MainActivity.this.getApplication();
        parameter_lv = (ListView) findViewById(R.id.main_listview);
        initListView();

        /******************************监听器************************************/

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        readParameter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.isWifiConnect(MainActivity.this)&&connectStatus) {
                    ReadParameterSetting readParameterSetting = new ReadParameterSetting(message);
                    message = readParameterSetting.readAll();
                    ReadMessageThread readMessageThread = new ReadMessageThread();
                    Thread mythread = new Thread(readMessageThread);
                    mythread.start();
                } else
                    Toast.makeText(MainActivity.this, "请先连接wifi", Toast.LENGTH_SHORT).show();
            }
        });



        connect_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyUtils.isWifiConnect(MainActivity.this)) {
                    if (!connectStatus) {
                        ConnectThread connectThread = new ConnectThread();
                        Thread thread = new Thread(connectThread);
                        thread.start();
                    } else {
                        client.close();
                        connectStatus = false;
                        connect_btn.setText("connect");
                    }
                } else
                    Toast.makeText(MainActivity.this, "请先连接对应的wifi", Toast.LENGTH_SHORT).show();
            }
        });



    }


    /**
     * 初始化listView
     */
    public void initListView() {
        items_lv = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("parameterName", parameterNames[i]);
            item.put("parameterContent", parameterContents[i]);
            items_lv.add(item);
        }

        simpleAdapter_lv = new SimpleAdapter(this, items_lv, R.layout.read_parameter_item,
                new String[]{"parameterName", "parameterContent"}, new int[]{R.id.read_parameter_name, R.id.read_parameter_content});
        parameter_lv.setAdapter(simpleAdapter_lv);
    }

    /**
     * *******************************handler********************************************
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0X01) {
                Toast.makeText(MainActivity.this, "发送命令成功", Toast.LENGTH_SHORT).show();
                if (msg.obj != null) {
                    List<Integer> resultMessage = (List<Integer>) msg.obj;
                    parameterContents= MessageHandler.messageHandle(resultMessage);
                    receive_data_textview.setText((Arrays.toString(resultMessage.toArray())));
                    Toast.makeText(MainActivity.this, "接收到模块参数", Toast.LENGTH_SHORT).show();
                    items_lv.clear();

                    Log.i("接收到的报文",Arrays.toString(resultMessage.toArray()));
                    for (int i = 0; i < 3; i++) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("parameterName", parameterNames[i]);
                        item.put("parameterContent", parameterContents[i]);
                        items_lv.add(item);
                    }
                    simpleAdapter_lv.notifyDataSetChanged();
                }

            } else if (msg.what == 0x02) {//连接成功
                connect_btn.setText("disconnect");
                connectStatus = true;
                application.setConnectStatus(true);
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();

            } else if (msg.what == 0x03) {//连接失败
                connect_btn.setText("connect");
                connectStatus = false;
                application.setConnectStatus(false);
                Toast.makeText(MainActivity.this, "创建连接失败，请确认是否连接对正确的wifi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * ******************************线程********************************************
     */

    class ConnectThread implements Runnable {
        @Override
        public void run() {
            try {
                client = new SocketClient("192.168.4.1", 8080);

                application.setClient(client);
                application.setConnectStatus(true);
                Message msg = handler.obtainMessage();
                msg.what = 0x02;//成功
                handler.sendMessage(msg);

            } catch (RuntimeException e) {
                e.printStackTrace();
                application.setConnectStatus(false);
                Log.e("error", "SocketClient构造器出错");
                Message msg = handler.obtainMessage();
                msg.what = 0x03;//创建连接失败
                handler.sendMessage(msg);
            }
        }
    }

    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            String result = "";
            // String result = client.sendMessage(send_data_textview.getText().toString().trim());
            client.sendMessage(message);//无返回值，不读取模块返回的信息

            Message msg = handler.obtainMessage();
            msg.what = 0X01;//发送报文成功
            msg.obj = result;

            handler.sendMessage(msg);
        }

    }

    class ReadMessageThread implements Runnable {
        @Override
        public void run() {
            List<Integer> result = client.readMessage(message);
            Message msg = handler.obtainMessage();
            msg.what = 0X01;//发送报文成功
            msg.obj = result;
            handler.sendMessage(msg);
        }

    }


}
