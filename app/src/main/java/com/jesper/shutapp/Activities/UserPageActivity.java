package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class UserPageActivity extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseUser fuser;

    ImageView imageView;
    ImageButton btnAddFriend, btnFriendAdded;
    TextView userName;
    TextView userBio;
    Intent intent;
    String name;
    String bio;
    String photo;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");
        photo = intent.getStringExtra("photo");
        uid = intent.getStringExtra("uid");

        init();
        checkIfFriends();


        Glide.with(this).load(photo).into(imageView);
        userName.setText(name);
        userBio.setText(bio);



    }

    private void init () {
        imageView = findViewById(R.id.image_user_page);
        userBio = findViewById(R.id.text_userpage_bio);
        userName = findViewById(R.id.text_username_userpage);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        btnFriendAdded = findViewById(R.id.btn_friend_added);
        reference = FirebaseDatabase.getInstance().getReference();
    }

    private void checkIfFriends() {

        reference.child("users").child(fuser.getUid()).child("friends").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnAddFriend.setVisibility(View.GONE);
                    btnFriendAdded.setVisibility(View.VISIBLE);
                } else {
                    btnAddFriend.setVisibility(View.VISIBLE);
                    btnFriendAdded.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Call the add friend method.
    public void btnAddFriend(View view) {
        Toast.makeText(this, "Friend added", Toast.LENGTH_SHORT).show();

            reference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    reference.child("users").child(fuser.getUid()).child("friends").child(uid).setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference.child("users").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    reference.child("users").child(uid).child("friends").child(fuser.getUid()).setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public void btnRemoveFriend(View view) {
        reference.child("users").child(fuser.getUid()).child("friends").child(uid).removeValue();
        reference.child("users").child(uid).child("friends").child(fuser.getUid()).removeValue();

        btnAddFriend.setVisibility(View.VISIBLE);
        btnFriendAdded.setVisibility(View.GONE);

        Toast.makeText(this, "Friend removed", Toast.LENGTH_SHORT).show();
    }
}
