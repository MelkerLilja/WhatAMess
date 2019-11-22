package com.jesper.shutapp;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

public class FriendRequestAdapter extends BaseAdapter {

    Context context;
    private ArrayList<User> friendsRequests;

    public FriendRequestAdapter(Context context, ArrayList<User> friendsRequests) { //Constructor for InChatAdapter with the Context and our chatList.
        this.context = context;
        this.friendsRequests = friendsRequests;
    }

    @Override
    public int getCount() {
        return friendsRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView userName;
        ImageView profilePicture;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = friendsRequests.get(position);

        FriendRequestAdapter.ViewHolder holder = null;


        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new FriendRequestAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_list_item, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_friends_username); //FIX
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.friendlist_profile_image);


            convertView.setTag(holder);
        } else {
            holder = (FriendRequestAdapter.ViewHolder) convertView.getTag();
        }




        User user_pos = friendsRequests.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }
}
