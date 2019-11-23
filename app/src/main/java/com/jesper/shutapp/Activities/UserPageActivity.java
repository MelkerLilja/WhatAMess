package com.jesper.shutapp.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        init();
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
        btnFriendAdded = findViewById(R.id.btn_friend_added);

        Glide.with(this).load(photo).into(imageView);
        userName.setText(name);
        userBio.setText(bio);
    }

    //Method that checks that users i friends and changes views.
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

    //Call the send request method.
    public void btnAddFriend(View view) {
        Toast.makeText(this, "Friend request sent", Toast.LENGTH_SHORT).show();
        sendFriendRequest();
    }

    //Button for remove friend.
    public void btnRemoveFriend(View view) {
        reference.child("users").child(fuser.getUid()).child("friends").child(uid).removeValue();
        reference.child("users").child(uid).child("friends").child(fuser.getUid()).removeValue();

        btnAddFriend.setVisibility(View.VISIBLE);
        btnFriendAdded.setVisibility(View.GONE);

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
}
