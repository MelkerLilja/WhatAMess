package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

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
import com.google.firebase.database.FirebaseDatabase;
import com.jesper.shutapp.Fragments.TermsOfService;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.User;


public class RegisterUser extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmedPassword;
    private ProgressBar mProgressbar;
    private CheckBox mCheckbox;
    private Button mRegisterBtn;
    private Toolbar toolbarRegister;

    private FragmentManager mFragmentManager;
    private TermsOfService tos;
    private final String TAG = "Database";


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
        toolbarRegister=findViewById(R.id.register_toolbar);

        setSupportActionBar(toolbarRegister);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFragmentManager = getSupportFragmentManager();
        FrameLayout mFragmentLayout = findViewById(R.id.fragment_holder);
        TextView mTos = findViewById(R.id.tos_txt);
        tos = new TermsOfService();

        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTos.setOnTouchListener(new View.OnTouchListener()
        {
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
                        Toast.makeText(this, getText(R.string.password_not_match_toast), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getText(R.string.invalid_email_txt), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.too_short_password_toast), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.all_field_toast), Toast.LENGTH_SHORT).show();
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

    private void registerUser(final String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterUser.this, "User created", Toast.LENGTH_SHORT).show();
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //adds user to database
                    User user = new User();
                    user.setName(email.substring(0,email.indexOf("@")));
                    user.setEmail(email);
                    user.setProfile_picture("https://www.cfdating.com/user_images/default.png");
                    user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    user.setBio("");
                    user.setAge("");
                    user.setFrom("");
                    user.setGender("");

                    FirebaseDatabase.
                            getInstance().
                            getReference().
                            child(getString(R.string.db_users)).
                            child(FirebaseAuth.getInstance().getUid()).
                            setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.d(TAG, "onComplete: added user to database");
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: added user to database");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: couldn't add user" + e.toString());
                        }
                    });
                    //end of adding user to database


                    FirebaseAuth.getInstance().signOut();
                    redirectLoginScreen();
                } else {
                    Toast.makeText(RegisterUser.this, getText(R.string.unable_register_toast), Toast.LENGTH_SHORT).show();
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