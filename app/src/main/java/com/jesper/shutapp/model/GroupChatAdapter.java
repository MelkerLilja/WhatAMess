package com.jesper.shutapp.model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jesper.shutapp.Activities.UserPageActivity;
import com.jesper.shutapp.FriendsListAdapter;
import com.jesper.shutapp.R;

import java.util.ArrayList;

public class GroupChatAdapter extends BaseAdapter {
    Context context;
    private ArrayList<User> friendsList;


    public GroupChatAdapter(Context context, ArrayList<User> friendsList) { //Constructor for InChatAdapter with the Context and our chatList.
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
        CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = friendsList.get(position);

        GroupChatAdapter.ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new GroupChatAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grouplist_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_friends_username);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.friendlist_profile_image);
            holder.imgOn = (ImageView) convertView.findViewById(R.id.status_on);
            holder.imgOff = (ImageView) convertView.findViewById(R.id.status_off);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_group);

            convertView.setTag(holder);
        } else {
            holder = (GroupChatAdapter.ViewHolder) convertView.getTag();
        }

        if (user.getStatus().equals("online")) {
            holder.imgOn.setVisibility(View.VISIBLE);
            holder.imgOff.setVisibility(View.GONE);
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }

    /*    convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setSelected(true);
            }
        });*/


        User user_pos = friendsList.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }

}



