package com.jesper.shutapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //public FragmentManager fragmentManager;
    private ProgressBar progressBar;
    private EditText mEmail;
    private EditText mPassword;

    private String TAG = "Jesper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView registerTxt = findViewById(R.id.register_link);
        progressBar = findViewById(R.id.progressBar);
        mEmail = findViewById(R.id.email_edittxt);
        mPassword = findViewById(R.id.password_edittxt);

        registerTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(MainActivity.this, RegisterUser.class);
                startActivity(intent);
                return false;
            }
        });
    }

    private void showProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgress()
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void submitOnClick(View view)
    {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(isValidEmail(email))
        {
            if(!isEmpty(email) && !isEmpty(password))
            {
                showProgress();
                Toast.makeText(this, "Login in", Toast.LENGTH_SHORT).show();

                //gå vidare med firebase kod
            }
        }
        else
        {
            Toast.makeText(this,"invalid email input",Toast.LENGTH_SHORT).show();
        }
    }
    private Boolean isEmpty(String text)
    {
        return text.equals("");
    }
    private Boolean isValidEmail(String email)
    {
        for(int i = 0; i < email.length(); i++)
        {
            if(email.charAt(i) == '@' && (email.contains(".com") || email.contains(".se")))
            {
                return true;
            }
        }
        return false;
    }
}
