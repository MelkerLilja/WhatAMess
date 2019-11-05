package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    ImageButton btnSend;
    EditText txtSend;
    String msg;
    ListView listvMessages;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.text_send);
        listvMessages = findViewById(R.id.listview_messages);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msg = txtSend.getText().toString(); //Getting EditText value and adds it into sendMessage method.
                sendMessage(msg);
                txtSend.setText("");
            }
        });




    }




    private void sendMessage (String message) { //Method takes in String msg and push it to database

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Chats").push().setValue(message);

    }










}
