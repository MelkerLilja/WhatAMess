package com.jesper.shutapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);
        Toolbar toolbar = findViewById(R.id.logged_in_toolbar);
        toolbar.setTitle(R.string.settings_txt);
        setSupportActionBar(toolbar);

    }

    // Enter Settings activity
    public void user_data(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    // Enter theme fragment/change theme directly
    public void theme(View view) {

    }

    // Enter notifications fragment/activity
    public void notifications(View view) {

    }

    // Log out and return to MainActivity
    public void log_out(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Delete account and return to MainActivity
    public void delete_account(View view) {

    }
}
