package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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

    // Change from dark to day theme
    public void theme(View view) {

        SharedPreferences sp = getSharedPreferences("theme", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("theme_key", "day");
        editor.putString("theme_key", "night");
        editor.apply();


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

        RegisterUser.mFragmentManager = getSupportFragmentManager();
        FrameLayout mFragmentLayout = findViewById(R.id.fragment_holder_main_settings);
        TextView mTos1 = findViewById(R.id.tos_txt);
        RegisterUser.tos = new TermsOfService();

        mTos1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RegisterUser.mFragmentManager.beginTransaction().add(R.id.fragment_holder_main_settings, RegisterUser.tos, "Terms of Service").commit();
                return false;
            }
        });
        mFragmentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RegisterUser.mFragmentManager.beginTransaction().remove(RegisterUser.tos).commit();
                return false;
            }
        });

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("HEJ", "User account deleted.");
                            //FirebaseDatabase.getInstance().getReference().child(getString
                            // (R.string.db_users)).child(FirebaseAuth.getInstance().
                            // getCurrentUser().getUid()).removeValue();
                        }

                    }
                });
        log_out(view);
        finish();


    }

}
