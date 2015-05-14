package com.example.vino.utils;

import java.util.List;

/**
 * Created by Joker on 2015/5/14.
 */
public class MessageHandler {

    /**
     * 处理接收到的报文信息，将处理后的信息回传给listview显示
     * @param msg 接收到的报文
     * @return parameterContents 返回listview显示用的数组
     */
    public static String[] messageHandle(List<Integer> msg){

        /**
         * 进行和校验
         */

        String[] parameterContents = {"无", "无", "无"};


        /**
         * 处理报文信息，转换成文本信息
         */
        String year=Integer.toHexString(msg.get(4));
        String month=Integer.toHexString(msg.get(5));
        String day=Integer.toHexString(msg.get(6));
        String hour,minute,startHour,startMinute,endHour,endMinute;
        if(msg.get(7)<10)
            hour="0"+Integer.toHexString(msg.get(7));
        else
            hour=Integer.toHexString(msg.get(7));
        if(msg.get(8)<10)
            minute="0"+Integer.toHexString(msg.get(8));
        else
            minute=Integer.toHexString(msg.get(8));
        if(msg.get(9)<10)
            startHour="0"+Integer.toHexString(msg.get(9));
        else
            startHour=Integer.toHexString(msg.get(9));
        if(msg.get(10)<10)
            startMinute="0"+Integer.toHexString(msg.get(10));
        else
            startMinute=Integer.toHexString(msg.get(10));
        if(msg.get(11)<10)
            endHour="0"+Integer.toHexString(msg.get(11));
        else
            endHour=Integer.toHexString(msg.get(11));
        if(msg.get(12)<10)
            endMinute="0"+Integer.toHexString(msg.get(12));
        else
            endMinute=Integer.toHexString(msg.get(12));

        String model=Integer.toHexString(msg.get(13));
        parameterContents[0]="20"+year+"年"+month+"月"+day+"日  "+hour+":"+minute;
        parameterContents[1]=startHour+":"+startMinute+"-"+endHour+":"+endMinute;
        parameterContents[2]=model;
    return parameterContents;
    }

}