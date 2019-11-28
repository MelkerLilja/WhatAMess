package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.jesper.shutapp.InChatAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.Chat;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnSend;
    private EditText txtSend;
    private String message;
    private ListView messagesList;
    private TextView userNameChat;
    private ImageView userImage;
    private Toolbar mToolbar;
    private InChatAdapter adapter;
    private DatabaseReference reference;
    private ArrayList<Chat> chatList;
    private FirebaseUser fuser;
    private String userid;
    private String username;
    private String userpic;
    private String userbio;
    private Intent intent;
    private static String TAG = "JesperChat";
    private static int PICK_IMAGE = 100;
    private RequestManager imageLoader;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        //On click listener for send button.
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = txtSend.getText().toString(); //Getting EditText value and adds it into sendMessage method.
                if (!message.equals("")) {
                    sendMessage(fuser.getUid(), userid, message);
                } else {
                    Toast.makeText(ChatActivity.this, getText(R.string.empty_message_toast), Toast.LENGTH_SHORT).show();
                }
                txtSend.setText("");
            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(userid);
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
    }

    //Initiate all variables and views.
    private void init () {
        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.text_send);
        messagesList = findViewById(R.id.listview_message);
        userNameChat = findViewById(R.id.text_userName_chat);
        userImage = findViewById(R.id.image_user_chat);
        frameLayout = findViewById(R.id.frame_chat);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        username = intent.getStringExtra("username");
        userpic = intent.getStringExtra("userpic");
        userbio = intent.getStringExtra("bio");

        imageLoader = Glide.with(this);
        imageLoader.load(userpic).into(userImage);
        userNameChat.setText(username);

        mToolbar = findViewById(R.id.activity_chat_toolbar);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    //Method takes in String msg and push it as a hashMap to database.
    private void sendMessage(String sender, String receiver, String message) {

        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("chats").push().setValue(hashMap);
    }

    //Method that listens for data changes and adds the messages to our chatlist.
    private void readMessage(final String myid, final String userid) {
        chatList = new ArrayList<>(); //Arraylist to store our chats

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() { //A listener to listen for any datachange.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class); //Getting the hashMap value into our chat object and adds it to our arraylist.
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        chatList.add(chat);
                    }
                }
                adapter = new InChatAdapter(ChatActivity.this, chatList); //Creates our adapter with our ChatActivity and our chatList as constructor.
                messagesList.setAdapter(adapter); //Set our listview to with our adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here.
            }
        });
    }

    //Add picture method from gallery.
    public void addPic(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

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
                            sendMessage(fuser.getUid(), userid, message);
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

    //Button to get to user-page Activity.
    public void btnUserProfile(View view) {
        Intent intent = new Intent(ChatActivity.this, UserPageActivity.class);
        intent.putExtra("name", username);
        intent.putExtra("bio", userbio);
        intent.putExtra("photo", userpic);
        intent.putExtra("uid", userid);
        startActivity(intent);
    }

    public void zoomPic(View view) {
        ImageView zoomPic = findViewById(R.id.zoomed_image);
        ImageView temp = (ImageView) view;
        zoomPic.setVisibility(View.VISIBLE);
        zoomPic.setImageDrawable(temp.getDrawable());
    }

    public void zoomedOff(View view) {
        ImageView zoomPic = findViewById(R.id.zoomed_image);
        zoomPic.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        ImageView zoomPic = findViewById(R.id.zoomed_image);
        if (zoomPic.getVisibility() == View.VISIBLE) {
            zoomPic.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void hideKeyboard (){
        final InputMethodManager imm = (InputMethodManager) ChatActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
