package com.example.vino.learnandroid.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.vino.learnandroid.R;

/**
 * Created by Joker on 2015/5/25.
 */
public class MyLayout extends LinearLayout {

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_layout, this);

    }

}
