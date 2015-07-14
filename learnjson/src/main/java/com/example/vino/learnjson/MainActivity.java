package com.example.vino.learnjson;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    private Button btnGet;
    private TextView tvHttpResponse;
    private HttpHandler handler;//主线程创建的handler就在主线程中运行，主线程默认开启了looper.loop()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler=new HttpHandler();
        btnGet= (Button) findViewById(R.id.btnGet);
        tvHttpResponse= (TextView) findViewById(R.id.tvHttpResponse);

        //使用回调方法来进行处理，实现解耦
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.sendRequest("http://10.0.2.2:8080/androidTest/person.text",new HttpCallbackListener() {
                    @Override
                    public void onFinish(String html) {
                        Log.i("get",html);
                        /**
                         * Gson进行json的解析
                         * fromJson,toJson
                         */
                        Gson gson=new Gson();
                        List<Person> person=gson.fromJson(html,new TypeToken<List<Person>>(){}.getType());
                        Message message=new Message();
                        message.obj=person;
                        message.what=0x01;
                        handler.sendMessage(message);

                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
    }
//handler来修改UI
    public class HttpHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x01) {
                List<Person> people = (List<Person>) msg.obj;
                StringBuffer sb=new StringBuffer();
                for(Person p:people)
                    sb.append(p.toString());
                tvHttpResponse.setText(sb.toString());
            }
            else
                tvHttpResponse.setText("error");
        }
    }


}
