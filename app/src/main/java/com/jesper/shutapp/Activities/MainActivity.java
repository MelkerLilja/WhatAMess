package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jesper.shutapp.R;

public class MainActivity extends AppCompatActivity {

    //public FragmentManager fragmentManager;
    private ProgressBar progressBar;
    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private String TAG = "Jesper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sp = getSharedPreferences("theme", Activity.MODE_PRIVATE);
        String theme = sp.getString("theme_key", "default");


        if (theme.equals("day")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        } else if (theme.equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }


        setContentView(R.layout.activity_main);


        TextView registerTxt = findViewById(R.id.register_link);
        progressBar = findViewById(R.id.progressBar);
        mEmail = findViewById(R.id.email_edittxt);
        mPassword = findViewById(R.id.password_edittxt);

        setupFirebaseAuth(); //sets up a firebaseListener for listening for login activity

        hideSoftKeyboard();

        registerTxt.setOnTouchListener(new View.OnTouchListener() //when the user presses the register
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(MainActivity.this, RegisterUser.class);
                startActivity(intent);
                return false;
            }
        });

    }

    private void showProgress() //shows the progressbar when called upon
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() // hides the progressbar when called, checks if the progressbar is visible
    {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void submitOnClick(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (isValidEmail(email)) //checks if a valid email else gives a toast for invalid
        {
            if (!isEmpty(email) && !isEmpty(password)) //checks if both fields are filled or not
            {
                showProgress();
                Toast.makeText(this, "Login in", Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password) //making connection to the firebase database and checks if there is a user with email password credetnials
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) { //if it is successfull hide the progressbar
                                hideProgress();
                            }
                        }).addOnFailureListener(new OnFailureListener() { //if it fails gives a toast saying it failed
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "invalid email input", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean isEmpty(String text) {
        return text.equals("");
    }

    private Boolean isValidEmail(String email) {
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@' && (email.contains(".com") || email.contains(".se"))) {
                return true;
            }
        }
        return false;
    }

    private void setupFirebaseAuth() { //sets up a firebase Authentication listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { //if the the user tries to log in
                FirebaseUser user = firebaseAuth.getCurrentUser(); //if successful a user is asigned else null

                if (user != null) { //if a user was found go to logged in activity
                    Log.d(TAG, "onAuthStateChanged: Signed in " + user.getUid());
                    Toast.makeText(MainActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, FragmentHolderActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() { //if mainActivity is loaded again add a listener
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() { //if the activity is not visible anymore, stop the listener
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
