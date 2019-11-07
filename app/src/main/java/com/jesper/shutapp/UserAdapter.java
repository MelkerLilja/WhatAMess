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
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends BaseAdapter {


    Context context;
    private ArrayList<User> usersList;

    public UserAdapter(Context context,  ArrayList<User> usersList) { //Constructor for MessageAdapter with the Context and our chatList.
        this.context = context;
        this.usersList = usersList;
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

    private class ViewHolder {
        TextView userName;
        TextView userEmail;
        ImageView profilePicture;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = usersList.get(position);

        ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_userName);
            holder.userEmail = (TextView) convertView.findViewById(R.id.text_email_user);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
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

        User user_pos = usersList.get(position);
        holder.userName.setText(user_pos.getName());
        holder.userEmail.setText(user_pos.getEmail());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);


        return convertView;
    }


}
