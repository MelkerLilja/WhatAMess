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

public class GroupAdapter extends BaseAdapter {

    Context context;
    private ArrayList<String> usersList;
    DatabaseReference reference;
    FirebaseUser fuser;

    public GroupAdapter(Context context, ArrayList<String> usersList) {
        this.context = context;
        this.usersList = usersList;
    }


    private class ViewHolder {
        TextView userName;
        ImageView profilePicture;
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

         ArrayList<String> groupChat = usersList;
        getGroups();

        GroupAdapter.ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new GroupAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_userName);
        //    holder.profilePicture = (ImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }
        else {
            holder = (GroupAdapter.ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, GroupInChatActivity.class);

                context.startActivity(intent);
            }
        });


        String groupChat_pos = usersList.get(position);
        holder.userName.setText(groupChat_pos);
      //  Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }

    private void getGroups() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("groups"); //KAN BLI EN KRALL

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usersList.add(snapshot.getKey());
                    Log.d("ANTON", "onDataChange: "  + snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
