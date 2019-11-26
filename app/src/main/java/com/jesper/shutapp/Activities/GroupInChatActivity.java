package com.jesper.shutapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.jesper.shutapp.R;

import java.util.ArrayList;

public class GroupInChatActivity extends AppCompatActivity {

    ImageButton btnSend;
    EditText txtSend;
    String message;
    ListView messagesList;
    TextView userNameChat, txtUserGroup;
    ImageView userImage;

    DatabaseReference reference;
   // ArrayList<User> groupClassUsers;
    FirebaseUser fuser;
    String groupName;
   // ArrayList<String> groupUsers;
    String userid;
    Intent intent;
   // ArrayList<GroupChat> groupChatList;
   // GroupInChatAdapter adapter;
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

    private void init() {
        btnSend = findViewById(R.id.group_btn_send);
        txtSend = findViewById(R.id.group_text_send);
        userNameChat = findViewById(R.id.text_userName_chat);
        userImage = findViewById(R.id.group_image_user);
        txtUserGroup = findViewById(R.id.group_text_name);
        listView = findViewById(R.id.group_listview_message);

       // groupClassUsers = new ArrayList<>();
     //   groupUsers = new ArrayList<>();
        intent = getIntent();
        groupName = intent.getStringExtra("groupname");
      //  groupUsers = intent.getStringArrayListExtra("groupChat");


        txtUserGroup.setText(groupName);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
