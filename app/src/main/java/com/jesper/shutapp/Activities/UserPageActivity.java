package com.jesper.shutapp.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.User;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class UserPageActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser fuser;
    private ImageView imageView, background;
    private ImageButton btnAddFriend, btnFriendChat;
    private TextView userName, userBio, friends;
    private Intent intent;
    private String name;
    private String bio;
    private String photo;
    private String uid;
    private User user;
    private int friendCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        init();
        setFriendCount();
        checkIfFriends();
    }

    //Initiate all variables and views
    private void init () {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        intent = getIntent();

        name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");
        photo = intent.getStringExtra("photo");
        uid = intent.getStringExtra("uid");

        imageView = findViewById(R.id.image_user_page);
        userBio = findViewById(R.id.text_userpage_bio);
        userName = findViewById(R.id.text_username_userpage);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        btnFriendChat = findViewById(R.id.button_chat_userpage);
        background = findViewById(R.id.userpage_background);
        friends = findViewById(R.id.userpage_friends);
        btnFriendChat = findViewById(R.id.button_chat_userpage);

        Glide.with(this).load(photo).into(imageView);
        setBlurryPhoto(photo);
        userName.setText(name);
        userBio.setText(bio);
    }

    //Method that checks if users is friends and changes views.
    private void checkIfFriends() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").child(fuser.getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(uid)) {
                        btnFriendChat.setVisibility(View.VISIBLE);
                        btnAddFriend.setVisibility(View.GONE);
                        Log.d("ANTON", "onDataChange: friend");
                    }   else {
                        btnFriendChat.setVisibility(View.GONE);
                        btnAddFriend.setVisibility(View.VISIBLE);
                        Log.d("ANTON", "onDataChange: not friend");
                    }
                }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Button for remove friend.
    public void btnRemoveFriend(View view) {
        reference.child("users").child(fuser.getUid()).child("friends").child(uid).removeValue();
        reference.child("users").child(uid).child("friends").child(fuser.getUid()).removeValue();

        btnAddFriend.setVisibility(View.VISIBLE);
        //btnFriendAdded.setVisibility(View.GONE);

        Toast.makeText(this, "Friend removed", Toast.LENGTH_SHORT).show();
    }

    //Button to get in chat with user.
    public void btnSendMessage(View view) {
        Intent intent = new Intent(UserPageActivity.this, ChatActivity.class);
        intent.putExtra("userid", uid);
        intent.putExtra("username", name);
        intent.putExtra("userpic", photo);
        intent.putExtra("bio", bio);
        startActivity(intent);
    }

    //Method to send a friend.request to a user.
    public void sendFriendRequest () {

        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("friendrequests").child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBlurryPhoto(String photo){
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        background.setColorFilter(filter);

        Glide.with(this).load(photo).transform(new BlurTransformation(7,10)).into(background);
    }

    private void setFriendCount() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        reference.child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendCount = (int) dataSnapshot.getChildrenCount();
                String s = Integer.toString(friendCount);
                friends.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Button send friend request
    public void btnAddFriend(View view) {
        Toast.makeText(this, "Friend request sent", Toast.LENGTH_SHORT).show();
        sendFriendRequest();
    }
}
