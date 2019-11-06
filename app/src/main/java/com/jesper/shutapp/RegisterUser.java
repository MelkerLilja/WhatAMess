package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterUser extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmedPassword;
    private ProgressBar mProgressbar;
    private CheckBox mCheckbox;
    private Button mRegisterBtn;

    private FragmentManager mFragmentManager;
    private TermsOfService tos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_layout);

        mEmail = findViewById(R.id.email_register_edittxt);
        mPassword = findViewById(R.id.password_register_edittxt);
        mConfirmedPassword = findViewById(R.id.confirm_password_register_edittxt);
        mProgressbar = findViewById(R.id.progressBar);
        mCheckbox = findViewById(R.id.tos_checkbox);
        mRegisterBtn = findViewById(R.id.register_btn);

        mFragmentManager = getSupportFragmentManager();
        FrameLayout mFragmentLayout = findViewById(R.id.fragment_holder);
        TextView mTos = findViewById(R.id.tos_txt);
        tos = new TermsOfService();

        mTos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFragmentManager.beginTransaction().add(R.id.fragment_holder, tos, "Terms of Service").commit();
                return false;
            }
        });
        mFragmentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFragmentManager.beginTransaction().remove(tos).commit();
                return false;
            }
        });

    }

    public void registerOnClick(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confPassword = mConfirmedPassword.getText().toString();


        if (!isEmpty(email) && !isEmpty(password) && !isEmpty(confPassword)) {
            if (password.length() >= 6 && confPassword.length() >= 6) {

                if (isValidEmail(email)) {
                    if (isMatchingPass(password, confPassword)) {
                        showProgress();
                        registerUser(email, password);

                    } else {
                        Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Not a valid Email", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "password too short, need to be atleast 6 characters", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "You have to fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }


    private Boolean isValidEmail(String email) {
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@' && (email.contains(".com") || email.contains(".se"))) {
                return true;
            }
        }
        return false;
    } //att fixa

    private Boolean isEmpty(String text) {
        return (text.equals(""));
    }

    private Boolean isMatchingPass(String password, String confirmedPassword) {
        return password.equals(confirmedPassword);
    }

    private void showProgress() {
        mProgressbar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (mProgressbar.getVisibility() == View.VISIBLE) {
            mProgressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void registerUser(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterUser.this, "User created", Toast.LENGTH_SHORT).show();
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //add user to database


                    FirebaseAuth.getInstance().signOut();
                    redirectLoginScreen();
                } else {
                    Toast.makeText(RegisterUser.this, "Unable to register", Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("Jesper", "Failed Registration: " + e.getMessage());
            }
        });
    }

    private void redirectLoginScreen() {
        Intent intent = new Intent(RegisterUser.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void confirmTos(View view) {
        if (mCheckbox.isChecked()) {
            mRegisterBtn.setVisibility(View.VISIBLE);
        } else {
            mRegisterBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.findFragmentById(R.id.fragment_holder).isVisible()) {
            mFragmentManager.beginTransaction().remove(tos).commit();
        } else {
            super.onBackPressed();
        }
    }
}