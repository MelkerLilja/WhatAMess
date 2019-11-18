package com.jesper.shutapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jesper.shutapp.R;

public class UserPageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView userName;
    TextView userBio;
    Intent intent;
    String name;
    String bio;
    String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        imageView = findViewById(R.id.image_user_page);
        userBio = findViewById(R.id.text_userpage_bio);
        userName = findViewById(R.id.text_username_userpage);
        intent = getIntent();
        name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");
        photo = intent.getStringExtra("photo");

        Glide.with(this).load(photo).into(imageView);

        userName.setText(name);
        userBio.setText(bio);


    }
}
