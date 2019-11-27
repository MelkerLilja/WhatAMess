package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

public class GroupInviteAdapter extends BaseAdapter {
    Context context;
    private ArrayList<User> friendsList;
    DatabaseReference reference;
    FirebaseUser fuser;

    public GroupInviteAdapter(Context context, ArrayList<User> friendsList) { //Constructor for InChatAdapter with the Context and our chatList.
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
        ImageButton btnAddGroup;
        ImageButton btnRemoveGroup;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final User user = friendsList.get(position);

        GroupInviteAdapter.ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new GroupInviteAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grouplist_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_friends_username);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.friendlist_profile_image);
            holder.imgOn = (ImageView) convertView.findViewById(R.id.status_on);
            holder.imgOff = (ImageView) convertView.findViewById(R.id.status_off);
            holder.btnAddGroup = (ImageButton) convertView.findViewById(R.id.btn_add_group);
            holder.btnRemoveGroup = (ImageButton) convertView.findViewById(R.id.btn_added_to_group);

            convertView.setTag(holder);
        } else {
            holder = (GroupInviteAdapter.ViewHolder) convertView.getTag();
        }

        if (user.getStatus().equals("online")) {
            holder.imgOn.setVisibility(View.VISIBLE);
            holder.imgOff.setVisibility(View.GONE);
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }

        final ViewHolder finalHolder = holder;
        final ViewHolder finalHolder1 = holder;
        holder.btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference();
                fuser = FirebaseAuth.getInstance().getCurrentUser();

                reference.child("groups").child(fuser.getUid()).child(friendsList.get(position).getUid()).setValue(friendsList.get(position).getUid());
                finalHolder.btnAddGroup.setVisibility(View.GONE);
                finalHolder1.btnRemoveGroup.setVisibility(View.VISIBLE);
            }
        });

        final ViewHolder finalHolder2 = holder;
        final ViewHolder finalHolder3 = holder;
        holder.btnRemoveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference();
                fuser = FirebaseAuth.getInstance().getCurrentUser();

                reference.child("groups").child(fuser.getUid()).child(friendsList.get(position).getUid()).removeValue();
                //reference.child("groups").child(fuser.getUid()).getRoot().child(friendsList.get(position).getUid()).removeValue();
                finalHolder2.btnRemoveGroup.setVisibility(View.GONE);
                finalHolder3.btnAddGroup.setVisibility(View.VISIBLE);
            }
        });

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(position, finalConvertView, parent);

            }
        });

        User user_pos = friendsList.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        return convertView;
    }

    private void hideKeyboard (final int position,final View convertView, final ViewGroup parent){
        final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView(position, convertView, parent).getWindowToken(), 0);
    }
}



