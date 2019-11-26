package com.jesper.shutapp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.ChatActivity;
import com.jesper.shutapp.Activities.GroupInChatActivity;
import com.jesper.shutapp.model.GroupChat;

import java.util.ArrayList;

public class GroupListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<GroupChat> groupList;
    FirebaseUser fuser;
    DatabaseReference reference;
    private ArrayList<String> groupUsers = new ArrayList<>();

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
        generateGroupUsers(groupList.get(position));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupInChatActivity.class);
                intent.putExtra("groupname", groupChat.getGroupName());
                intent.putStringArrayListExtra("grouplist", groupUsers);
             //   intent.putExtra("userpic", user.getProfile_picture());
                context.startActivity(intent);
            }
        });

        GroupChat groupChat_pos = groupList.get(position);
        holder.userName.setText(groupChat_pos.getGroupName());
      //  Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }

    private void generateGroupUsers(final GroupChat groupChat) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("groups");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (groupChat.getGroupName().equals(snapshot.getKey())){
                        groupUsers.add(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
