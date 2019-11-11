package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainSettings extends AppCompatActivity {


    private FragmentManager mFragmentManager;
    private TermsOfService tos;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        tos = new TermsOfService();

        mFragmentManager = getSupportFragmentManager();

        FrameLayout mFragmentHolder = findViewById(R.id.fragment_main_settings_holder);
        mFragmentHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFragmentManager.beginTransaction().remove(tos).commit();

                return false;
            }
        });

        mToolbar = findViewById(R.id.user_settings_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Enter Settings activity
    public void user_data(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    // Change from dark to day theme
    public void theme(View view) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Day theme activated", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Night theme activated", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter terms of service fragment/activity
    public void terms_of_service(View view) {
        mFragmentManager.beginTransaction().add(R.id.fragment_main_settings_holder,tos,"TOS").commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.settings_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(MainSettings.this, MainSettings.class);
                startActivity(intent);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(MainSettings.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainSettings.this, UsersListActivity.class);
        startActivity(intent);
        finish();
    }
}
