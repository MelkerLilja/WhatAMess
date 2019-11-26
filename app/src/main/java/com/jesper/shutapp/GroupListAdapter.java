package com.jesper.shutapp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.jesper.shutapp.Activities.GroupInChatActivity;
import com.jesper.shutapp.model.GroupChat;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

public class GroupListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<GroupChat> groupList;
    FirebaseUser fuser;
    DatabaseReference reference;
    private ArrayList<String> groupUsers = new ArrayList<>();
    private ArrayList<User> groupMembers = new ArrayList<>();
    private ArrayList<String> userPics = new ArrayList<>();


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
        ImageView currentUserPic;
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        ImageView imageView4;
        //Add more if we want
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GroupChat groupChat = groupList.get(position);
        ViewHolder holder = null;


        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_messagefragment_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.group_messagefragment_name);
            holder.currentUserPic = convertView.findViewById(R.id.group_image_5);
            holder.imageView1 = convertView.findViewById(R.id.group_image_1);
            holder.imageView2 = convertView.findViewById(R.id.group_image_2);
            holder.imageView3 = convertView.findViewById(R.id.group_image_3);
            holder.imageView4 = convertView.findViewById(R.id.group_image_4);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        generateGroupUsers(groupList.get(position));


        getUserPictures(groupChat.getGroupName(), holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupInChatActivity.class);
                intent.putExtra("groupname", groupChat.getGroupName());
                intent.putStringArrayListExtra("grouplist", groupUsers);
                context.startActivity(intent);
            }
        });

        GroupChat groupChat_pos = groupList.get(position);
        holder.userName.setText(groupChat_pos.getGroupName());


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

    private void getUserPictures(final String groupname,final ViewHolder holder) {
        Log.d("ANTON", "getUserPictures: method running");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

       DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("groups").child(groupname).child("members");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userPics.add(user.getProfile_picture());
                }

                int a = userPics.size();
                if (a == 1){
                    Glide.with(context).load(userPics.get(0)).into(holder.currentUserPic);
                    holder.imageView1.setVisibility(View.GONE);
                    holder.imageView2.setVisibility(View.GONE);
                    holder.imageView3.setVisibility(View.GONE);
                    holder.imageView4.setVisibility(View.GONE);
                }
                if (a == 2){
                    Glide.with(context).load(userPics.get(0)).into(holder.currentUserPic);
                    Glide.with(context).load(userPics.get(1)).into(holder.imageView1);
                    holder.imageView2.setVisibility(View.GONE);
                    holder.imageView3.setVisibility(View.GONE);
                    holder.imageView4.setVisibility(View.GONE);
                }
                if (a == 3){
                    Glide.with(context).load(userPics.get(0)).into(holder.currentUserPic);
                    Glide.with(context).load(userPics.get(1)).into(holder.imageView1);
                    Glide.with(context).load(userPics.get(2)).into(holder.imageView2);
                    holder.imageView3.setVisibility(View.GONE);
                    holder.imageView4.setVisibility(View.GONE);

                }
                if (a == 4){
                    Glide.with(context).load(userPics.get(0)).into(holder.currentUserPic);
                    Glide.with(context).load(userPics.get(1)).into(holder.imageView1);
                    Glide.with(context).load(userPics.get(2)).into(holder.imageView2);
                    Glide.with(context).load(userPics.get(3)).into(holder.imageView3);
                    holder.imageView4.setVisibility(View.GONE);

                }
                if (a == 5){
                    Glide.with(context).load(userPics.get(0)).into(holder.currentUserPic);
                    Glide.with(context).load(userPics.get(1)).into(holder.imageView1);
                    Glide.with(context).load(userPics.get(2)).into(holder.imageView2);
                    Glide.with(context).load(userPics.get(3)).into(holder.imageView3);
                    Glide.with(context).load(userPics.get(4)).into(holder.imageView4);
                }







            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
