package com.example.vino.utils;

import java.util.List;

/**
 * Created by Joker on 2015/4/16.
 */
public class SwingCardSetting {
    private List<Integer> msg = null;

    public SwingCardSetting(List<Integer> msg) {
        this.msg = msg;
        msg.set(0, 0xF1);
        msg.set(1, 0x03);
    }

    public List<Integer> setMode(int workMode) {
        msg.set(1, 0x03);
        msg.set(3, workMode);
        return msg;
    }

    /**
     * 关闭或开启刷卡时段功能
     * @param isEnable
     * @return
     */
    public List<Integer> setEnableSwingCard(boolean isEnable){
        if(isEnable){
            msg.set(1,0x02);
            msg.set(2, 0xee);
            msg.set(3, 0xee);
            msg.set(4, 0xee);
            msg.set(5, 0xee);
        }else {
            msg.set(1, 0x02);
            msg.set(2, 0x00);
            msg.set(3, 0x00);
            msg.set(4, 0x00);
            msg.set(5, 0x00);
        }
        return msg;
    }
}
