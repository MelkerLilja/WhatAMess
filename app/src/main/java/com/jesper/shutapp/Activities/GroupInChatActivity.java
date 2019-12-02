package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.GroupInChatAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.GroupChat;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInChatActivity extends AppCompatActivity {

    ImageButton btnSend;
    EditText txtSend;
    String message;
    TextView userNameChat, txtUserGroup;
    ImageView userImage, user1, user2, user3, user4, user5;
    DatabaseReference reference;
    // ArrayList<User> groupClassUsers;
    FirebaseUser fuser;
    String groupName;
    ArrayList<String> groupUsers;
    Intent intent;
    ArrayList<GroupChat> groupChatList;
    GroupInChatAdapter adapter;
    ListView listView;
    ArrayList<String> groupPictures;
    Toolbar mToolbar;

    private static String TAG = "JesperChat";
    private static int PICK_IMAGE = 100;
    private RequestManager imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_in_chat);

        init();
        readMessage2();
        generateGroupPictures();
    }

    //Initiate all variables and views.
    private void init() {
        btnSend = findViewById(R.id.group_btn_send);
        txtSend = findViewById(R.id.group_text_send);
        userNameChat = findViewById(R.id.text_userName_chat);
        txtUserGroup = findViewById(R.id.group_text_name);
        listView = findViewById(R.id.group_listview_message);
        user1 = findViewById(R.id.groupchat_image_5);
        user2 = findViewById(R.id.groupchat_image_2);
        user3 = findViewById(R.id.groupchat_image_3);
        user4 = findViewById(R.id.groupchat_image_4);
        user5 = findViewById(R.id.groupchat_image_1);

        groupPictures = new ArrayList<>();

        intent = getIntent();
        groupName = intent.getStringExtra("groupname");
        groupUsers = intent.getStringArrayListExtra("grouplist");
        groupPictures = intent.getStringArrayListExtra("pictures");

        txtUserGroup.setText(groupName);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mToolbar = findViewById(R.id.activity_group_chat_toolbar);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Method to send a message and push it to firebase.
    private void sendMessage(String sender, ArrayList<String> receiver, String message) {
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("groupname", groupName);

        reference.child("groups").child(groupName).child("chats").push().setValue(hashMap);
    }

    private void readMessage2() {
        groupChatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupChat chat = snapshot.getValue(GroupChat.class);
                    groupChatList.add(chat);
                }
                adapter = new GroupInChatAdapter(GroupInChatActivity.this, groupChatList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Method to read all the messages.
    private void readMessage(final String myid) {
       // groupChatList = new ArrayList<>(); //Arraylist to store our group-chats

        reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatList = new ArrayList<>();

                for (int i = 0; i < groupUsers.size(); i++) {

                    groupChatList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GroupChat chat = snapshot.getValue(GroupChat.class);

                        for (int j = 0; j < chat.getReceiver().size(); j++) {


                            if (chat.getReceiver().get(j).equals(myid) && chat.getSender().equals(groupUsers.get(i)) ||
                                    chat.getReceiver().get(j).equals(groupUsers.get(i)) && chat.getSender().equals(myid)) {

                                groupChatList.add(chat);
                                break;
                            }
                        }
                    }
                }
                  /*      adapter = new InChatAdapter(ChatActivity.this, chatList); //Creates our adapter with our ChatActivity and our chatList as constructor.
                        messagesList.setAdapter(adapter); //Set our listview to with our adapter*/

                adapter = new GroupInChatAdapter(GroupInChatActivity.this, groupChatList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here.
            }
        });
    }

    //Onclick button to call for sendmessage method.
    public void btnGroupSend(View view) {
        message = txtSend.getText().toString(); //Getting EditText value and adds it into sendMessage method.
        if (!message.equals("")) {
            sendMessage(fuser.getUid(), groupUsers, message);
        } else {
            Toast.makeText(GroupInChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        txtSend.setText("");
    }

    private void generateGroupPictures() {
        int a = groupPictures.size();
        if (a == 1){
            Glide.with(this).load(groupPictures.get(0)).into(user1);
            user2.setVisibility(View.INVISIBLE);
            user3.setVisibility(View.INVISIBLE);
            user4.setVisibility(View.INVISIBLE);
            user5.setVisibility(View.INVISIBLE);
        }
        if (a == 2){
            Glide.with(this).load(groupPictures.get(0)).into(user1);
            Glide.with(this).load(groupPictures.get(1)).into(user2);
            user3.setVisibility(View.INVISIBLE);
            user4.setVisibility(View.INVISIBLE);
            user5.setVisibility(View.INVISIBLE);
        }
        if (a == 3){
            Glide.with(this).load(groupPictures.get(0)).into(user1);
            Glide.with(this).load(groupPictures.get(1)).into(user2);
            Glide.with(this).load(groupPictures.get(2)).into(user3);
            user4.setVisibility(View.INVISIBLE);
            user5.setVisibility(View.INVISIBLE);

        }
        if (a == 4){
            Glide.with(this).load(groupPictures.get(0)).into(user1);
            Glide.with(this).load(groupPictures.get(1)).into(user2);
            Glide.with(this).load(groupPictures.get(2)).into(user3);
            Glide.with(this).load(groupPictures.get(3)).into(user4);
            user5.setVisibility(View.INVISIBLE);

        }
        if (a >= 5){
            Glide.with(this).load(groupPictures.get(0)).into(user1);
            Glide.with(this).load(groupPictures.get(1)).into(user2);
            Glide.with(this).load(groupPictures.get(2)).into(user3);
            Glide.with(this).load(groupPictures.get(3)).into(user4);
            Glide.with(this).load(groupPictures.get(4)).into(user5);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GroupInChatActivity.this, FragmentHolderActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
