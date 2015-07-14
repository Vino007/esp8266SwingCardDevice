package com.example.vino.learnjson;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 网络操作在子线程中执行
 */
public class HttpUtil {

    public static void sendRequest(final String url, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                HttpClient client=new DefaultHttpClient();
                HttpGet httpGet=new HttpGet(url);
                HttpResponse response=null;
                response=client.execute(httpGet);
                    if(response.getStatusLine().getStatusCode()==200){
                        Log.i("Get","success");
                        HttpEntity entity=response.getEntity();
                        String html= EntityUtils.toString(entity,"utf-8");
                        if(listener!=null)
                        listener.onFinish(html);//回调方法

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("Get","fail");
                    if(listener!=null)
                    listener.onError();
                }
            }
        }).start();
    }
}
