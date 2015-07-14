package com.example.vino.learnandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vino.learnandroid.R;

import java.util.ArrayList;

/**
 * Created by Joker on 2015/5/24.
 */
public class FruitAdapter extends BaseAdapter {
    private Context context;
    private ArrayList list;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    public FruitAdapter(Context context,ArrayList list){
        this.context=context;
        this.list=list;
        this.inflater=LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View view;
       if(convertView==null) {
           Log.i("converview","is null");
           viewHolder=new ViewHolder();
           view=inflater.inflate(R.layout.fruit_layout, null);
           viewHolder.image=(ImageView)view.findViewById(R.id.fruit);
           viewHolder.title=(TextView)view.findViewById(R.id.title);
           view.setTag(viewHolder);
       }else
       {
           Log.i("converview","not null");
           view=convertView;
           viewHolder= (ViewHolder) view.getTag();


       }
        viewHolder.title.setText(list.get(position).toString());
        viewHolder.image.setImageResource(R.mipmap.ic_launcher);
        return view;
    }
    private class ViewHolder{
        TextView title;
        ImageView image;

    }

}
