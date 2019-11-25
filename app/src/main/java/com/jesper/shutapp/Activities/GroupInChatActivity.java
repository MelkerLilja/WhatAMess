package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jesper.shutapp.GroupChatClass;
import com.jesper.shutapp.GroupInChatAdapter;
import com.jesper.shutapp.InChatAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.GroupChat;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInChatActivity extends AppCompatActivity {

    ImageButton btnSend;
    EditText txtSend;
    String message;
    ListView messagesList;
    TextView userNameChat, txtUserGroup;
    ImageView userImage;
    Toolbar mToolbar;
    DatabaseReference reference;
    ArrayList<User> groupClassUsers;
    FirebaseUser fuser;
    String groupName;
    ArrayList<String> groupUsers;
    String userid;
    Intent intent;
    ArrayList<GroupChat> groupChatList;
    GroupInChatAdapter adapter;
    ListView listView;
    Boolean aBoolean;
    private static String TAG = "JesperChat";
    private static int PICK_IMAGE = 100;
    private RequestManager imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_in_chat);

        init();
        getUsers();
        //initUserViews();


    }

    private void init() {
        btnSend = findViewById(R.id.group_btn_send);
        txtSend = findViewById(R.id.group_text_send);
        messagesList = findViewById(R.id.group_listview_message);
        userNameChat = findViewById(R.id.text_userName_chat);
        userImage = findViewById(R.id.group_image_user);
        txtUserGroup = findViewById(R.id.group_text_name);
        listView = findViewById(R.id.group_listview_message);

        groupClassUsers = new ArrayList<>();
        groupUsers = new ArrayList<>();
        intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        groupUsers = intent.getStringArrayListExtra("groupChat");

//        groupUsers.set(0, fuser.getUid());
        txtUserGroup.setText(groupName);

    /*    mToolbar = findViewById(R.id.activity_chat_toolbar);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        fuser = FirebaseAuth.getInstance().getCurrentUser();
    }

 /*   private void listener () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() { //Listens for data change and calls upon readmessage method if so
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readMessage(fuser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here
            }
        });
    }*/

/*    //Method takes in String msg and push it as a hashMap to database.
    private void sendMessage(String sender, String receiver, String message) {

        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("groups").push().setValue(hashMap);
    }*/

    /*  //Method that listens for data changes and adds the messages to our chatlist.
    private void readMessage(final String myid, final String userid) {
        chatList = new ArrayList<>(); //Arraylist to store our chats

        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() { //A listener to listen for any datachange.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Log.d("ANTON", snapshot.getKey());

                    for (int i = 0; i < groupUsers.size(); i++) { //MÅSTE KIKA PÅ DENNA
                        if (snapshot.getKey() == groupUsers.get(i)) {
                            User user = snapshot.getValue(User.class);
                            groupClassUsers.add(user);
                        }
                    }


                }
                adapter = new GroupInChatAdapter(GroupInChatActivity.this, chatList); //Creates our adapter with our ChatActivity and our chatList as constructor.
                messagesList.setAdapter(adapter); //Set our listview to with our adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here.
            }
        });
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            String path = imageUri.toString();
            String filename = path.substring(path.lastIndexOf("/") + 1);
            Log.d(TAG, "onActivityResult: " + filename);

            final StorageReference riverRef = mStorageRef.child("images/" + userid + "/chatimages/" + filename + ".jpg");
            riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            message = uri.toString();
                            Log.d(TAG, "onSuccess: " + message);
                            //sendMessage(fuser.getUid(), userid, message);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                }
            });
        }
    }


    private void getUsers() {

        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    for (int i = 0; i < groupUsers.size(); i++) { //MÅSTE KIKA PÅ DENNA
                        if (snapshot.getKey().equals(groupUsers.get(i))) {
                            User user = snapshot.getValue(User.class);
                            groupClassUsers.add(user);
                            Log.d("ANTON", "onDataChange: user added");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here.
            }
        });
        //  loadPicture();
    }

    private void initUserViews() {
        Glide.with(GroupInChatActivity.this).load(groupClassUsers.get(0)).into(userImage);
    }


    public void btnGroupSend(View view) {
        message = txtSend.getText().toString(); //Getting EditText value and adds it into sendMessage method.
        if (!message.equals("")) {
            sendMessage(fuser.getUid(), groupUsers, message);
        } else {
            Toast.makeText(GroupInChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        txtSend.setText("");
    }


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


    //Method that listens for data changes and adds the messages to our chatlist.
    private void readMessage(final String myid) {
        groupChatList = new ArrayList<>(); //Arraylist to store our chats


        reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("chats");
        reference.addValueEventListener(new ValueEventListener() { //A listener to listen for any datachange.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (int i = 0; i < groupUsers.size(); i++) {

                    groupChatList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GroupChat chat = snapshot.getValue(GroupChat.class);

                        for (int j = 0; j < chat.getReceiver().size(); j++) {


                            if (chat.getReceiver().get(j).equals(myid) && chat.getSender().equals(groupUsers.get(i)) ||
                                    chat.getReceiver().get(j).equals(groupUsers.get(i)) && chat.getSender().equals(myid)) {



                                    Log.d("ANTON", "HEJDÅ");
                                    groupChatList.add(chat);
                                    break;

                            }}
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
}










