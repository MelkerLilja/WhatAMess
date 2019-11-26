package com.jesper.shutapp;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jesper.shutapp.model.GroupChat;

import java.util.ArrayList;

public class GroupListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<GroupChat> groupList;

    public GroupListAdapter(Context context, ArrayList<GroupChat> groupList) {
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
        TextView userName;
        //Add more if we want
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GroupChat groupChat = groupList.get(position);
        ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_groupName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GroupChat groupChat_pos = groupList.get(position);
        holder.userName.setText(groupChat_pos.getGroupName());
      //  Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }
}
