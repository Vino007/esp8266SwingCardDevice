package com.example.vino.utils;

import java.util.List;

/**
 * Created by Joker on 2015/4/16.
 */
public class SwingCardSetting {
    private List<Integer> msg=null;
    public SwingCardSetting(List<Integer> msg){
        this.msg=msg;
        msg.set(0,0xF1);
        msg.set(1,0x03);
    }
    public List<Integer> setMode(int workMode){
        msg.set(3,workMode);
        return msg;
    }
}
