package com.example.vino.esp8266test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.vino.utils.MessageHandler;
import com.example.vino.utils.MyApplication;
import com.example.vino.utils.MyUtils;
import com.example.vino.utils.SwingCardSetting;
import com.example.vino.utils.TimedialogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 0x01：发送命令成功
 * 0x02: 建立连接成功
 * 0x03: 建立连接失败
 * 0x04: 连接已断开，即socketclient为null或close
 *
 * if (MyUtils.isWifiConnect(SettingActivity.this) && connectStatus)
 *
 *  if (client != null && !client.isClose())
 *  两重判断，第一个判断wifi是否连接，第二个判断socket是否建立
 */
public class SettingActivity extends ActionBarActivity {
    private Button setBeginTime_btn;
    private Button setEndTime_btn;
    private Button send_btn;
    private ToggleButton time_toggle_btn;
    private Switch time_switch;
    private RadioGroup selectMode_group;
    private Button setMode_btn;
    private Button setDate_btn;
    private Button setTime_btn;
    private Button callElevator_btn;
    private List<Integer> message = new ArrayList<Integer>();//报文存储
    private int workMode = -1;//工作模式
    private SocketClient client = null;
    private MyApplication application;
    private Handler handler;
    private boolean connectStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        send_btn = (Button) findViewById(R.id.send_btn);
        setBeginTime_btn = (Button) findViewById(R.id.setBeginTime_btn);
        setEndTime_btn = (Button) findViewById(R.id.setEndTime_btn);
        setMode_btn = (Button) findViewById(R.id.setMode_btn);
        setDate_btn = (Button) findViewById(R.id.setDate_btn);
        setTime_btn = (Button) findViewById(R.id.setTime_btn);
        time_switch= (Switch) findViewById(R.id.time_switch);
        callElevator_btn = (Button) findViewById(R.id.call_elevator_btn);
        selectMode_group = (RadioGroup) findViewById(R.id.selectMode_group);
        application = (MyApplication) SettingActivity.this.getApplication();
        connectStatus = application.getConnectStatus();
        Log.d("start", "正常启动");
        handler = new MyHandler();
        //初始化下行报文
        message= MessageHandler.initMessage();
        time_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SwingCardSetting swingCardSetting=new SwingCardSetting(message);
                    swingCardSetting.setEnableSwingCard(isChecked);
            }
        });

        callElevator_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, CallElevatorActivity.class);
                startActivity(intent);
            }
        });
        setTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimedialogUtils timedialogUtils = new TimedialogUtils(SettingActivity.this, message);
                message = timedialogUtils.showTimepickerDialog("deviceTime");
            }
        });

        setDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimedialogUtils timedialogUtils = new TimedialogUtils(SettingActivity.this, message);
                message = timedialogUtils.showDatepickerDialog();
            }
        });
        /**
         * 当允许刷卡的时候才可以设置时间，默认时间为2200-800，禁止刷卡时，直接点发送命令即可
         */
        setBeginTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimedialogUtils timedialogUtils = new TimedialogUtils(SettingActivity.this, message);
                if(time_switch.isChecked()){
                    message = timedialogUtils.showTimepickerDialog("beginTime");
                    message.set(6,0xee);}
                else {

                    Toast.makeText(SettingActivity.this, "请先开启时段再设置时间", Toast.LENGTH_SHORT).show();

                }
            }
        });

        setEndTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimedialogUtils timedialogUtils = new TimedialogUtils(SettingActivity.this, message);
                if (time_switch.isChecked()) {
                    message = timedialogUtils.showTimepickerDialog("endTime");
                    message.set(6, 0xee);
                } else {

                    Toast.makeText(SettingActivity.this, "请先开启时段再设置时间", Toast.LENGTH_SHORT).show();

                }
            }
        });


        setMode_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int selectId = selectMode_group.getCheckedRadioButtonId();
                Log.i("selectId", selectId + "");
                switch (selectId) {
                    case R.id.mode1_rbtn:
                        workMode = 1;
                        break;
                    case R.id.mode2_rbtn:
                        workMode = 2;
                        break;
                    case R.id.mode3_rbtn:
                        workMode = 3;
                        break;
                    case R.id.mode4_rbtn:
                        workMode = 4;
                        break;
                    default:
                        workMode = -1;
                }
                Log.i("workMode", workMode + "");
                if (workMode != -1) {
                    message=MessageHandler.initMessage();
                    SwingCardSetting swingCardSetting = new SwingCardSetting(message);
                    message = swingCardSetting.setMode(workMode);
                }

            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("message", Arrays.toString(message.toArray()));

                /********************短连接********************/
                /**
                 * 0.使用send按钮进行建立连接以及发送数据
                 * 1.每次连接结束都关闭输入输出流以及socket
                 * 2.readline是阻塞方法，因此会等到接受到换行符后才结束
                 * 3.当接收到换行后立马就断开连接了 如果调用while(readLine！=null)可以接收过个数据才结束
                 */
                // myThread=new MyThread();
                //  Thread thread=new Thread(myThread);

                /********************长连接********************/
                /**
                 * 0.使用connet按钮进行连接，使用send按钮进行发送数据
                 * 1.不能关闭输入输出流
                 * 2.当不需要连接的时候手动关闭socket。close或者等到timeout自动关闭
                 * 3.
                 */
                if (MyUtils.isWifiConnect(SettingActivity.this) && connectStatus) {
                    SendMessageThread sendMessageThread = new SendMessageThread();
                    Thread thread = new Thread(sendMessageThread);
                    thread.start();
                } else
                    Toast.makeText(SettingActivity.this, "请先连接", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * *******************************handler********************************************
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0X01)
                Toast.makeText(SettingActivity.this, "发送命令成功", Toast.LENGTH_SHORT).show();
            else if (msg.what == 0x02) {//连接成功
               Toast.makeText(SettingActivity.this, "连接成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == 0x03) {//连接失败
                Toast.makeText(SettingActivity.this, "创建连接失败，请确认是否连接对正确的wifi", Toast.LENGTH_SHORT).show();
            }else if(msg.what==0x04)
                Toast.makeText(SettingActivity.this,"连接已断开，请返回上层重新连接",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ******************************线程********************************************
     */

    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            client=application.getClient();
            if (client != null && !client.isClose()) { //判断socket连接是否还存在
                client.sendMessage(message);//无返回值，不读取模块返回的信息
                Message msg = handler.obtainMessage();
                msg.what = 0X01;//发送报文成功
                handler.sendMessage(msg);
            }else
            {
                Message msg = handler.obtainMessage();
                msg.what = 0X04;//发送报文成功
                handler.sendMessage(msg);
            }

        }

    }

}

