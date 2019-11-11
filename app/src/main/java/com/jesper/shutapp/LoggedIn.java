package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.zip.Inflater;

public class LoggedIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logged_in_layout);
        Toolbar toolbar = findViewById(R.id.logged_in_toolbar);
        toolbar.setTitle(R.string.homepage_txt);
        setSupportActionBar(toolbar);
        //commit
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Intent intent;

        switch(item.getItemId())
        {
            case R.id.settings:
                intent = new Intent(LoggedIn.this,Settings.class);
                startActivity(intent);
                finish();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(LoggedIn.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void userbtn(View view) {
        Intent intent = new Intent (LoggedIn.this, UsersListActivity.class);
        startActivity(intent);
        finish();
    }
}
