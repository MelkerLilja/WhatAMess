package com.jesper.shutapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    ImageButton btnSend;
    EditText txtSend;
    String msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.text_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msg = txtSend.getText().toString();
                sendMessage(msg);
                txtSend.setText("");
            }
        });




    }

    private void sendMessage (String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Chats").push().setValue(message);

    }
}
