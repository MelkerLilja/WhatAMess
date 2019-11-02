package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

public class Settings extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        user = FirebaseAuth.getInstance().getCurrentUser();

        TextView textView = findViewById(R.id.userid_txt);
        textView.setText(getString(R.string.userid_txt) + user.getEmail());

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setTitle(R.string.settings_menu);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_settings) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updatePassword(View view) {
        EditText newPasswordEdit = findViewById(R.id.new_password_edittxt);
        EditText newConfPasswordEdit = findViewById(R.id.new_password_confirmed_edittxt);

        String newPassword = newPasswordEdit.getText().toString();
        String newConfPassword = newConfPasswordEdit.getText().toString();

        if (newPassword.length() >= 6 && newConfPassword.length() >= 6) {
            if (newPassword.equals(newConfPassword)) {
                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Settings.this, "Password accepted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Settings.this, "Password not accepted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this, "Couldn't update password", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(Settings.this, "The new Password doesn't match the confirmed new Password", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Settings.this, "Password too short", Toast.LENGTH_LONG).show();
        }
    }

    public void updateEmail(View view) {
        EditText newEmailEdit = findViewById(R.id.email_settings_edittext);
        String newEmail = newEmailEdit.getText().toString();

        if (isValidEmail(newEmail)) {
            user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(Settings.this, Settings.class);
                    Toast.makeText(Settings.this, "Updating window", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Settings.this, "Couldn't update Email", Toast.LENGTH_SHORT).show();
                    Log.d("Jesper", "onFailure: " + e.toString());
                }
            });
        } else {
            Toast.makeText(Settings.this, "not a valid Email", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean isValidEmail(String email) {
        if ((email.contains(".com") || email.contains(".se"))) {
            for (int i = 0; i < email.length(); i++) {
                if (email.charAt(i) == '@') {
                    return true;
                }
            }
        }
        return false;
    }
}