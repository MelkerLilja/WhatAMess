package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.GroupChat;
import com.jesper.shutapp.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jesper.shutapp.model.GroupChat;

import java.util.ArrayList;

public class GroupInChatAdapter extends BaseAdapter {

    public static final int MSG_TYPE_LEFT = 0; // Need to use later on with our user to check which chat layout we want
    public static final int MSG_TYPE_RIGHT = 1;

    Context context;
    public ArrayList<GroupChat> groupList;
    User user;

    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public GroupInChatAdapter(Context context, ArrayList<GroupChat> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView message;
        ImageView image;
        ImageView profilePicture;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        int type = getItemViewType(position);

        GroupInChatAdapter.ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            if (type == 0) {
                convertView = mInflater.inflate(R.layout.chat_item_left, parent, false);
                holder.message = (TextView) convertView.findViewById(R.id.show_message);
                holder.image = convertView.findViewById(R.id.image_received);
                holder.profilePicture = convertView.findViewById(R.id.profile_image_inchat);
                getSender(groupList.get(position),convertView,holder);
                convertView.setTag(holder);
            } else {
                convertView = mInflater.inflate(R.layout.chat_item_right, parent, false);
                holder.message = (TextView) convertView.findViewById(R.id.show_message);
                holder.image = convertView.findViewById(R.id.image_sent);
                convertView.setTag(holder);
            }
        } else {
            holder = (GroupInChatAdapter.ViewHolder) convertView.getTag();
        }

        final GroupChat group_pos = groupList.get(position);
        if (group_pos.getMessage().contains(".jpg") || group_pos.getMessage().contains(".png")) {
            Glide.with(context).load(group_pos.getMessage()).into(holder.image);
            Log.d("JesperChat", "getView: ");

        }else if(group_pos.getMessage().contains("http"))
        {

            holder.message.setText(group_pos.getMessage());
            holder.message.setLinksClickable(true);
            Linkify.addLinks(holder.message,Linkify.WEB_URLS);
        }
        else {
            holder.message.setText(group_pos.getMessage());
        }


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (groupList.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    private void getSender(final GroupChat groupChat, final View convertView, final ViewHolder holder) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (groupChat.getSender().equals(snapshot.getKey())){
                        user = snapshot.getValue(User.class);
                        Glide.with(convertView).load(user.getProfile_picture()).into(holder.profilePicture);
                    }
                }
                //     Glide.with(convertView).load(user.getProfile_picture()).into(holder.image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
