package com.example.vino.esp8266test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
 * 0x04: 连接已断开，即socketclient为null或close
 * if (MyUtils.isWifiConnect(SettingActivity.this) && connectStatus)
 * if (client != null && !client.isClose())
 * 两重判断，第一个判断wifi是否连接，第二个判断socket是否建立
 */
public class MainActivity extends ActionBarActivity {

    private Button readParameter_btn;
    private Button setting_btn;
    private Handler handler;
   // private TextView receive_data_textview;
    private ListView parameter_lv;

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

        readParameter_btn = (Button) findViewById(R.id.readParameter_btn);
        setting_btn = (Button) findViewById(R.id.setting_btn);
       // receive_data_textview = (TextView) findViewById(R.id.receive_data_textview);
        Log.d("start", "正常启动");
        handler = new MyHandler();
        //初始化下行报文
        message= MessageHandler.initMessage();
        application = (MyApplication) MainActivity.this.getApplication();
        parameter_lv = (ListView) findViewById(R.id.main_listview);
        initListView();

        /******************************监听器************************************/
        /**
         * 要求输入密码，由alertdialog实现
         */
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请输入密码");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.password_dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText password_edit = (EditText) view.findViewById(R.id.password_edit);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String password = password_edit.getText().toString().trim();
                        if (password.equals("1234")) {

                            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(MainActivity.this, " 密码错误", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }

        });

        readParameter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.isWifiConnect(MainActivity.this)) {
                    ReadParameterSetting readParameterSetting = new ReadParameterSetting(message);
                    message = readParameterSetting.readAll();
                    ReadMessageThread readMessageThread = new ReadMessageThread();
                    Thread mythread = new Thread(readMessageThread);
                    mythread.start();
                } else
                    Toast.makeText(MainActivity.this, "请先连接wifi", Toast.LENGTH_SHORT).show();
            }
        });


    }


    /**
     * 初始化listView
     */
    public void initListView() {
        items_lv = new ArrayList<>();
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
                    parameterContents = MessageHandler.messageHandle(resultMessage);
                 //   receive_data_textview.setText((Arrays.toString(resultMessage.toArray())));
                    Toast.makeText(MainActivity.this, "接收到模块参数", Toast.LENGTH_SHORT).show();
                    items_lv.clear();

                    Log.i("接收到的报文", Arrays.toString(resultMessage.toArray()));
                    for (int i = 0; i < 3; i++) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("parameterName", parameterNames[i]);
                        item.put("parameterContent", parameterContents[i]);
                        items_lv.add(item);
                    }
                    simpleAdapter_lv.notifyDataSetChanged();
                }

            } else if (msg.what == 0x04) {
                Toast.makeText(MainActivity.this, "连接已断开，请返回上层重新连接", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * ******************************线程********************************************
     */

    class ReadMessageThread implements Runnable {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            client=application.getClient();
            if (client != null && !client.isClose()) { //判断socket连接是否还存在
                client = application.getClient();
                List<Integer> result = client.readMessage(message);
                msg.what = 0X01;//发送报文成功
                msg.obj = result;
                handler.sendMessage(msg);
            } else {
                msg.what = 0x04;
                handler.sendMessage(msg);
            }
        }

    }


}
