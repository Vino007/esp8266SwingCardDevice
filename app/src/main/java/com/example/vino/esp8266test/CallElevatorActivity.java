package com.example.vino.esp8266test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.vino.utils.MessageHandler;
import com.example.vino.utils.MyApplication;
import com.example.vino.utils.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 0x10: 呼梯成功
 * 0x01: 发送命令成功
 */
public class CallElevatorActivity extends Activity {
    private GridView gv;
    private ListView parameter_lv;
    private List<Integer> message = new ArrayList<Integer>();//报文存储
    private Handler handler;
    private SocketClient client;
    private String[] parameterNames = {"刷卡器时间:", "刷卡时段:", "工作模式"};
    private String[] parameterContents = {"无", "无", "无"};
    private List<Map<String, Object>> items_lv;
    private MyApplication application;
    SimpleAdapter simpleAdapter_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_elevator);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new MyHandler();
        //初始化报文
        message= MessageHandler.initMessage();

        gv = (GridView) findViewById(R.id.call_elevator_gridview);
   //     parameter_lv = (ListView) findViewById(R.id.call_elevator_listview);
   //     initListView();
        initGridView();
    }

    /**
     * 初始化listview
     */
    public void initListView() {
        items_lv = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("parameterName", parameterNames[i]);
            item.put("parameterContent", parameterContents[i]);
            items_lv.add(item);
        }

        simpleAdapter_listview = new SimpleAdapter(this, items_lv, R.layout.read_parameter_item,
                new String[]{"parameterName", "parameterContent"}, new int[]{R.id.read_parameter_name, R.id.read_parameter_content});
        parameter_lv.setAdapter(simpleAdapter_listview);
    }

    /**
     * 初始化gridview
     */
    public void initGridView() {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 64; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("textItem", i + 1 + "层");
            items.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, items, R.layout.call_elevator_button_item,
                new String[]{"textItem"}, new int[]{R.id.floor_btn});
        gv.setAdapter(simpleAdapter);
        /**
         * 点击楼层按钮，实现呼梯与获取模块实时参数
         */
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//positiion从0开始
                message.set(0, 0xf1);
                message.set(1, 0x04);
                message.set(2, MyUtils.decToBcd(position + 1));
                //在设置crc校验
                //获取全局的client实例
                application = (MyApplication) CallElevatorActivity.this.getApplication();
                client = application.getClient();
                if (client != null && !client.isClose()) { //判断socket连接是否还存在
                    SendMessageThread sendMessageThread = new SendMessageThread();
                    Thread thread = new Thread(sendMessageThread);
                    thread.start();
                    Log.i("position", position + "");
                    Log.i("id", id + "");

                } else
                    Toast.makeText(CallElevatorActivity.this, "连接已断开，请返回上层进行连接", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 发送并接收报文
     */
    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            String result = null;
            client.sendMessage(message);//无返回值，不读取模块返回的信息
            Message msg = handler.obtainMessage();
            msg.what = 0x10;//呼梯成功
            handler.sendMessage(msg);
            //读取参数信息

            /**
             *
             */
         /*   ReadParameterSetting readParameterSetting = new ReadParameterSetting(message);
            message = readParameterSetting.readAll();
            result = client.readMessage(message);
            msg=handler.obtainMessage();
           if(result!=null) {
               msg.what = 0x01;//接收成功
               msg.obj = result;
               handler.sendMessage(msg);
           }*/
        }

    }

    /**
     * *******************************handler********************************************
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {//成功接收到数据
                if (msg.obj != null) {
                    Toast.makeText(CallElevatorActivity.this, "接收到模块参数", Toast.LENGTH_SHORT).show();
                    items_lv.clear();
                    String parameterContents = (String) msg.obj;
                    for (int i = 0; i < 3; i++) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("parameterName", parameterNames[i]);
                        item.put("parameterContent", parameterContents);
                        items_lv.add(item);
                    }
                    simpleAdapter_listview.notifyDataSetChanged();
                }
            }
            if (msg.what == 0x10) {
                Toast.makeText(CallElevatorActivity.this, "呼梯成功", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
