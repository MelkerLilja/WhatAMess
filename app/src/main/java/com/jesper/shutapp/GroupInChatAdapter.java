package com.jesper.shutapp;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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

    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public GroupInChatAdapter(Context context, ArrayList<GroupChat> groupList) { //Constructor for InChatAdapter with the Context and our chatList.
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

        } else {
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
}
