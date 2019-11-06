package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MessageAdapter extends BaseAdapter {

    public static final int MSG_TYPE_LEFT = 0; // Need to use later on with our user to check which chat layout we want
    public static final int MSG_TYPE_RIGHT = 1;

    Context context;
    private ArrayList<Chat> chatList;

    public MessageAdapter(Context context,  ArrayList<Chat> chatList) { //Constructor for MessageAdapter with the Context and our chatList.
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.chat_item_left, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.show_message);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Chat chat_pos = chatList.get(position);
        holder.message.setText(chat_pos.getMessage());

        return convertView;
    }
}
