package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    
    ImageButton btnSend;
    EditText txtSend;
    String message;
    ListView messagesList;
    
    String sender = "Anton"; //These are examples will be changed to userid senderid "photo" etc.
    String receiver = "Kalle";

    MessageAdapter adapter;
    DatabaseReference reference;
    ArrayList<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.text_send);
        messagesList = findViewById(R.id.listview_message);
        
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = txtSend.getText().toString(); //Getting EditText value and adds it into sendMessage method.
                if (!message.equals("")) {
                    sendMessage(message);
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                txtSend.setText("");
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() { //Listens for data change and calls upon readmessage method if so
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readMessage();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here
            }
        });
    }


    private void sendMessage(String message) { //Method takes in String msg and push it as a hashMap to database.

        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>(); //Need to fix sender/receiver and add like time photo etc..
        hashMap.put("message", message);
        hashMap.put("receiver", receiver);
        hashMap.put("sender", sender);

        reference.child("chats").push().setValue(hashMap);
    }
    
    private void readMessage() {
        chatList = new ArrayList<>(); //Arraylist to store our chats

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() { //A listener to listen for any datachange.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear(); 
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class); //Getting the hashMap value into our chat object and adds it to our arraylist.
                    chatList.add(chat);
                }
                adapter = new MessageAdapter(ChatActivity.this, chatList); //Creates our adapter with our ChatActivity and our chatList as constructor.
                messagesList.setAdapter(adapter); //Set our listview to with our adapter
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Need to handle errors here.
            }
        });
    }
}
