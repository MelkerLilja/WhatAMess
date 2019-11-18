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

public class FriendsListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<User> friendsList;


    public FriendsListAdapter(Context context, ArrayList<User> friendsList) { //Constructor for InChatAdapter with the Context and our chatList.
        this.context = context;
        this.friendsList = friendsList;
    }

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView userName;
        ImageView profilePicture;
        ImageView imgOn;
        ImageView imgOff;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = friendsList.get(position);

        FriendsListAdapter.ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new FriendsListAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_friends_username);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.friendlist_profile_image);
            holder.imgOn = (ImageView) convertView.findViewById(R.id.status_on);
            holder.imgOff = (ImageView) convertView.findViewById(R.id.status_off);

            convertView.setTag(holder);
        } else {
            holder = (FriendsListAdapter.ViewHolder) convertView.getTag();
        }

        if (user.getStatus().equals("online")) {
            holder.imgOn.setVisibility(View.VISIBLE);
            holder.imgOff.setVisibility(View.GONE);
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userid", user.getUid());
                intent.putExtra("username", user.getName());
                intent.putExtra("userpic", user.getProfile_picture());
                context.startActivity(intent);
            }
        });

        User user_pos = friendsList.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);


        return convertView;
    }
}
