package com.example.vino.esp8266test;

import android.test.InstrumentationTestCase;

import com.example.vino.utils.MessageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joker on 2015/5/14.
 */
public class UtilsTest extends InstrumentationTestCase{
    public void testMessageHandler() throws Exception {
        MessageHandler messageHandler=new MessageHandler();
        List<Integer> msg=new ArrayList<>();
        msg.add(0x0d);
        msg.add(0x0d);
        msg.add(0x14);
        msg.add(0xcd);
        msg.add(0x15);
        String[] result=messageHandler.messageHandle(msg);
        assertEquals("15",result[0]);

    }
}
