package com.example.vino.esp8266test;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private MyThread myThread;//短连接使用的线程
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
        receive_data_textview= (TextView) findViewById(R.id.receive_data_textview);
        Log.d("start","正常启动");
        handler=new MyHandler();

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(isWifiConnect()) {
                    if (!connectStatus) {
                        ConnectThread connectThread = new ConnectThread();
                        Thread thread = new Thread(connectThread);
                        thread.start();


                    } else {
                        client.close();
                        connectStatus = false;
                        connect_btn.setText("connect");
                    }
                }else
                    Toast.makeText(MainActivity.this,"请先连接对应的wifi",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 判断是否连接上wifi
     * isConnected 为true表示当前手机有连接上网络
     * iswifi 为true 表示当前手机连接的网络是wifi
     * @return
     */
    public boolean isWifiConnect(){
        ConnectivityManager cm= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();//若当前没有任何网络连接则为null，因此使用之前要先判断！null
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        boolean isWifi=false;
        if(isConnected) {//
            isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            receive_data_textview.setText(activeNetwork.toString());
        }
        return isWifi;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.i("sendSuccess", "sendSuccess");

                if(msg.obj!=null)
                receive_data_textview.setText((String)msg.obj);
            }
            else if (msg.what==2) {//连接成功
                connect_btn.setText("disconnect");
                connectStatus=true;

            }
            else if (msg.what == 3){//连接失败
                connect_btn.setText("connect");
                connectStatus=false;
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
