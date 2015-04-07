package com.example.vino.esp8266test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private Button send_btn;
    private Button connect_btn;
    private EditText send_data_textview;
    private Handler handler;
    private MyThread myThread;
    private TextView status_textview;
    private TextView receive_data_textview;
    private boolean connectStatus=false;

    private SocketClient client=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_btn= (Button) findViewById(R.id.send_btn);
        connect_btn= (Button) findViewById(R.id.connect_btn);
        send_data_textview= (EditText) findViewById(R.id.send_data_textview);
        status_textview= (TextView) findViewById(R.id.status_textview);
        receive_data_textview= (TextView) findViewById(R.id.receive_data_textview);
        Log.d("start","正常启动");
        handler=new MyHandler();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // myThread=new MyThread();
              //  Thread thread=new Thread(myThread);
                if(connectStatus) {
                    SendMessageThread sendMessageThread = new SendMessageThread();
                    Thread thread = new Thread(sendMessageThread);
                    thread.start();
                }else
                    Toast.makeText(MainActivity.this,"请先连接",Toast.LENGTH_SHORT).show();
            }
        });
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectStatus)
                {
                    ConnectThread  connectThread=new ConnectThread();
                    Thread thread=new Thread(connectThread);
                    thread.start();

                }
            }
        });

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.i("sendSuccess", "sendSuccess");
                status_textview.setText("发送成功");
                if(msg.obj!=null)
                receive_data_textview.setText((String)msg.obj);
            }
            else if (msg.what==2){//连接成功
                connect_btn.setText("disconnect");
            }
            else if (msg.what == 3){//连接失败
                connect_btn.setText("connect");
            }
        }
    }
    class MyThread implements Runnable{
        @Override
        public void run() {
            //socket连接
            Log.i("socket","建立socket");
            SocketClient client=new SocketClient("192.168.4.1",8080);
            String result=client.sendMessage(send_data_textview.getText().toString().trim());
            Message msg=handler.obtainMessage();
            msg.what=1;
            msg.obj=result;
            handler.sendMessage(msg);
        }
    }
    class ConnectThread implements Runnable{
        @Override
        public void run() {
            try {
                client = new SocketClient("192.168.4.1", 8080);
                Message msg=handler.obtainMessage();
                msg.what=2;
                handler.sendMessage(msg);

            }catch (RuntimeException e){
                e.printStackTrace();
                Log.e("error","SocketClient构造器出错");
                Message msg=handler.obtainMessage();
                msg.what=3;
                handler.sendMessage(msg);
            }
        }
    }

    class SendMessageThread implements  Runnable{
        @Override
        public void run(){
            String result = client.sendMessage(send_data_textview.getText().toString().trim());
            Message msg = handler.obtainMessage();
            msg.what = 1;
            msg.obj = result;
            handler.sendMessage(msg);
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
