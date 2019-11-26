package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageView userImage;
    DatabaseReference reference;
    // ArrayList<User> groupClassUsers;
    FirebaseUser fuser;
    String groupName;
    ArrayList<String> groupUsers;
    Intent intent;
    ArrayList<GroupChat> groupChatList;
    GroupInChatAdapter adapter;
    ListView listView;

    private static String TAG = "JesperChat";
    private static int PICK_IMAGE = 100;
    private RequestManager imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_in_chat);

        init();
    }

    //Initiate all variables and views.
    private void init() {
        btnSend = findViewById(R.id.group_btn_send);
        txtSend = findViewById(R.id.group_text_send);
        userNameChat = findViewById(R.id.text_userName_chat);
        userImage = findViewById(R.id.group_image_user);
        txtUserGroup = findViewById(R.id.group_text_name);
        listView = findViewById(R.id.group_listview_message);

        // groupClassUsers = new ArrayList<>();
        //  groupUsers = new ArrayList<>();
        intent = getIntent();
        groupName = intent.getStringExtra("groupname");
        groupUsers = intent.getStringArrayListExtra("grouplist");

        txtUserGroup.setText(groupName);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
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
        readMessage(fuser.getUid());
    }

    //Method to read all the messages.
    private void readMessage(final String myid) {
        groupChatList = new ArrayList<>(); //Arraylist to store our group-chats

        reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
}
