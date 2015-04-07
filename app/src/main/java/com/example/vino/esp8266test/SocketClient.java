package com.example.vino.esp8266test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Joker on 2015/3/31.
 */
public class SocketClient {
    private Socket client=null;
    public SocketClient(String host,int port)  throws RuntimeException{
        try {
            client=new Socket(host,port);//这种构造器会一直阻塞到直到连上服务器
           // client.setSoTimeout(6000);//设置超时时间
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("newSocketError", "newSocketError");
            throw new RuntimeException("socketclient构造器出错");
        }
    }

    /**
     * 发送信息给指定服务器
     * 返回服务器发回的信息，服务器的信息需已回车结尾才能立即收到，要不然只能等到连接断开后才能收到
     * AT+CIPSEND=0，6 回车也算一个字符，因此是五个数据+回车
     * @param msg
     * @return
     */
    public String sendMessage(String msg)  {
        OutputStream os=null;
        PrintWriter out=null;
        InputStream is=null;
        BufferedReader in=null;
        try{
            os=client.getOutputStream();
            out=new PrintWriter(os,true);
            is=client.getInputStream();
            in=new BufferedReader(new InputStreamReader(is));
            out.println(msg);

            return in.readLine();//读取响应的字符串
           /* if(in.hasNext()) {
                String str=in.next();
                Log.e("receive","receive");
                return str;
            }*/

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("sendMessageError","sendMessageError");
        }finally {

            if(out!=null)
                out.close();
            if(os!=null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("closeError","closeError");
                }
            if(in!=null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(is!=null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
       // return in.hasNext()?in.nextLine():"";
        return "";
    }


}
