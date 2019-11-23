package com.jesper.shutapp;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
import com.jesper.shutapp.model.User;
import java.util.ArrayList;

public class FriendRequestAdapter extends BaseAdapter {

    Context context;
    private ArrayList<User> friendsRequests;
    User currentUser;
    private DatabaseReference reference;
    FirebaseUser fuser;

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
        ImageButton confirm;
        ImageButton decline;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = friendsRequests.get(position);

        FriendRequestAdapter.ViewHolder holder = null;

        final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new FriendRequestAdapter.ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friendrequests_item, parent, false);
            holder.userName = (TextView) convertView.findViewById(R.id.text_friendrequests_username); //FIX
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.friendrequests_profile_image);
            holder.confirm = (ImageButton) convertView.findViewById(R.id.btn_confirm);
            holder.decline = (ImageButton) convertView.findViewById(R.id.btn_decline);

            convertView.setTag(holder);
        } else {
            holder = (FriendRequestAdapter.ViewHolder) convertView.getTag();
        }

        final User user_pos = friendsRequests.get(position);
        holder.userName.setText(user_pos.getName());
        Glide.with(context).load(user.getProfile_picture()).into(holder.profilePicture);

        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendConfirmed(user_pos);
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendDeclined(user_pos);
            }
        });

        return convertView;
    }

    //Method that handles the friendrequest confirmed functions
    public void friendConfirmed (final User user) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = new User();

        //Adds the other to our Friends tree
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friends");
        reference.child(user.getUid()).setValue(user);

        //Removes the user from our Friendrequests tree
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friendrequests").child(user.getUid());
        reference.removeValue();

        // Get our User profile
        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user2 = dataSnapshot.getValue(User.class);
                reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("friends");
                reference.child(fuser.getUid()).setValue(user2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Add our User profile to other users Friends tree
        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("friends");
        reference.child(fuser.getUid()).setValue(currentUser);
    }

    //Method to handle the friendrequest declined functions
    private void friendDeclined(User user) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friendrequests").child(user.getUid());
        reference.removeValue();
    }
}
