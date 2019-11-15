package com.jesper.shutapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
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
import com.jesper.shutapp.Activities.ChatActivity;
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

public class FriendsListAdapter extends BaseAdapter {


    Context context;
    private ArrayList<User> usersList;
    String theLastMessage;


    public FriendsListAdapter(Context context, ArrayList<User> usersList) { //Constructor for MessageListAdapter with the Context and our chatList.
        this.context = context;
        this.usersList = usersList;
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

    private class ViewHolder {
        TextView userName;
        TextView userEmail;
        ImageView profilePicture;
        TextView lastMessage;
        ImageView imgOn;
        ImageView imgOff;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = usersList.get(position);

        ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_item_list, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_userName);
          //  holder.userEmail = (TextView) convertView.findViewById(R.id.last_message);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
            holder.imgOn = (ImageView) convertView.findViewById(R.id.img_on);
            holder.imgOff = (ImageView) convertView.findViewById(R.id.img_off);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (user.getStatus().equals("online")){
            holder.imgOn.setVisibility(View.VISIBLE);
            holder.imgOff.setVisibility(View.GONE);
        }   else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, ChatActivity.class);
                intent.putExtra("userid", user.getUid());
                intent.putExtra("username", user.getName());
                intent.putExtra("userpic", user.getProfile_picture());
                context.startActivity(intent);
            }
        });

        User user_pos = usersList.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);
        lastMessageMethod(user.getUid(), holder.lastMessage);


        return convertView;
    }

    private void lastMessageMethod(final String userid, final TextView lastMessage) { //A method that loops the chat messages and checks what the last message sent
        theLastMessage = "default";                                                  //between the user and receiver then adds it to the user item.
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage) {
                    case "default":
                        lastMessage.setText("No Message");
                        break;

                        default:
                            lastMessage.setText(theLastMessage);
                            break;
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
