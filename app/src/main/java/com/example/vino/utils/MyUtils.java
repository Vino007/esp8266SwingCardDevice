package com.example.vino.utils;

import java.util.List;

/**
 * Created by Joker on 2015/4/16.
 */
public class MyUtils {
    //将list清空成全零数组
    public static void clearList(List list){
        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, 0);
            }
        }

    }
}
