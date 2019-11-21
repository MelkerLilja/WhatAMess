package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jesper.shutapp.Activities.ChatActivity;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<User> usersList;

    public UserListAdapter(Context context, ArrayList<User> usersList) { //Constructor for InChatAdapter with the Context and our chatList.
        this.context = context;
        this.usersList = usersList;
    }

    private class ViewHolder {
        TextView userName;
        ImageView profilePicture;
        ImageView imgOn;
        ImageView imgOff;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = usersList.get(position);

        UserListAdapter.ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new UserListAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_userName);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.imgOn = (ImageView) convertView.findViewById(R.id.img_on);
            holder.imgOff = (ImageView) convertView.findViewById(R.id.img_off);

            convertView.setTag(holder);
        }
        else {
            holder = (UserListAdapter.ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, ChatActivity.class);
                intent.putExtra("userid", user.getUid());
                intent.putExtra("username", user.getName());
                intent.putExtra("userpic", user.getProfile_picture());
                context.startActivity(intent);
            }
        });
        holder.imgOn.setVisibility(View.GONE);
        holder.imgOff.setVisibility(View.GONE);

        User user_pos = usersList.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);
        
        return convertView;
    }
}
