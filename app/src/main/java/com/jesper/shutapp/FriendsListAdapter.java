package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jesper.shutapp.Activities.ChatActivity;
import com.jesper.shutapp.Activities.MainActivity;
import com.jesper.shutapp.Activities.UserPageActivity;
import com.jesper.shutapp.Fragments.ProfileFragment;
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
            convertView = mInflater.inflate(R.layout.friends_item_list, parent, false);
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

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, context.getText(R.string.long_click_toast), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //NEED TO ADD THOSE INTO STRINGS LATER ON !!!
                builder.setTitle(context.getString(R.string.delete_friend_txt));
                builder.setMessage(context.getText(R.string.confirm_delete_friend_part1_txt)+ user.getName() + context.getText(R.string.confirm_delete_friend_part2_txt));
                builder.setCancelable(true);

                builder.setPositiveButton(context.getText(R.string.yes_txt),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFriend(user);
                            }
                        });

                //Negative Button
                builder.setNegativeButton(context.getText(R.string.no_txt),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();


                return true;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserPageActivity.class);
                intent.putExtra("bio", user.getBio());
                intent.putExtra("photo", user.getProfile_picture());
                intent.putExtra("name", user.getName());
                intent.putExtra("uid", user.getUid());

                context.startActivity(intent);
            }
        });

        User user_pos = friendsList.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }

    //NEED TO ADD SO THAT USER REMOVES FROM LIST DIRECLY
    private void deleteFriend(User user){
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(fuser.getUid()).child("friends").child(user.getUid()).removeValue();
        reference.child("users").child(user.getUid()).child("friends").child(fuser.getUid()).removeValue();

        Toast.makeText(context, context.getText(R.string.friend_removed_toast), Toast.LENGTH_SHORT).show();
    }
}
