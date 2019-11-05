package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {


    private ArrayList<Chat> dataSource;
    private LayoutInflater inflater;

    public MessageAdapter(Activity activity,  ArrayList<Chat> chats) { //Constructor for MessageAdapter
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Gettin layoutinflater from activity
        dataSource = chats; //Adds our chatobject to our datasource.
    }


    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.chat_window_layout, parent, false);

        return v;
    }
}
