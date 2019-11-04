package com.jesper.shutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class Settings extends AppCompatActivity {

    FirebaseUser user;
    private static final int PICK_IMAGE = 100;
    private ImageView userPic;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        user = FirebaseAuth.getInstance().getCurrentUser();

        userPic = findViewById(R.id.user_pic_view);

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

    public void updateUser(View view)
    {
        EditText newEmailEdit = findViewById(R.id.email_settings_edittext);
        EditText newPasswordEdit = findViewById(R.id.new_password_edittxt);
        EditText newUsername = findViewById(R.id.user_name_settings_edittxt);

        if(!newEmailEdit.getText().toString().equals(""))
        {
            if(isValidEmail(newEmailEdit.getText().toString()))
            {
                user.updateEmail(newEmailEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Settings.this, "Email updated",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this, "Email couldn't be updated",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                Toast.makeText(Settings.this, "Not a valid email",Toast.LENGTH_SHORT).show();
            }
        }
        if(!newPasswordEdit.getText().toString().equals(""))
        {
            user.updatePassword(newPasswordEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Settings.this, "Password updated",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Settings.this, "Password couldn't be updated",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(!newUsername.getText().toString().equals(""))
        {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Jesper", "onActivityResult: wtf" + requestCode);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            userPic.setImageURI(imageUri);

            //skicka till databasen och spara den där också
            //skicka den till storage för att ladda den därifrån
        }
    }

    public void changePic(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
}