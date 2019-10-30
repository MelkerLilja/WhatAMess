package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterUser extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmedPassword;
    private ProgressBar mProgressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_layout);

        mEmail = findViewById(R.id.email_register_edittxt);
        mPassword = findViewById(R.id.password_register_edittxt);
        mConfirmedPassword = findViewById(R.id.confirm_password_register_edittxt);
        mProgressbar = findViewById(R.id.progressBar);

    }

    public void registerOnClick(View view)
    {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confPassword = mConfirmedPassword.getText().toString();

        if(!isEmpty(email) && !isEmpty(password) && !isEmpty(confPassword))
        {
           if(isValidEmail(email))
           {
               if(isMatchingPass(password,confPassword))
               {
                   showProgress();
                   registerUser(email,password);

               }
               else
               {
                    Toast.makeText(this,"Password doesn't match",Toast.LENGTH_SHORT).show();
               }
           }
           else
           {
               Toast.makeText(this,"Not a valid Email",Toast.LENGTH_SHORT).show();
           }
        }
        else
        {
            Toast.makeText(this,"You have to fill all the fields",Toast.LENGTH_SHORT).show();
        }
    }


    private Boolean isValidEmail(String email)
    {
        for(int i = 0; i < email.length(); i++)
        {
            if(email.charAt(i) == '@' && (email.contains(".com") || email.contains(".se")) )
            {
                return true;
            }
        }
        return false;
    } //att fixa
    private Boolean isEmpty(String text)
    {
        return (text.equals(""));
    }
    private Boolean isMatchingPass(String password, String confirmedPassword)
    {
        return password.equals(confirmedPassword);
    }

    private void showProgress()
    {
        mProgressbar.setVisibility(View.VISIBLE);
    }
    private void hideProgress()
    {
        if (mProgressbar.getVisibility() == View.VISIBLE)
        {
            mProgressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void registerUser(String email, String password)
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(RegisterUser.this,"User created",Toast.LENGTH_SHORT).show();
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    redirectLoginScreen();
                    FirebaseAuth.getInstance().signOut();
                }
                else
                {
                    Toast.makeText(RegisterUser.this, "Unable to register", Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }
        });
    }
    private void redirectLoginScreen() {
        Intent intent = new Intent(RegisterUser.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}