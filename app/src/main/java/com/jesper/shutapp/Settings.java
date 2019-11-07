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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jesper.shutapp.model.User;

public class Settings extends AppCompatActivity {

    FirebaseUser user;
    private static final int PICK_IMAGE = 100;
    private ImageView userPic;
    private Uri imageUri;
    private StorageReference mStorageRef;

    private EditText usernameTxt;
    private EditText emailTxt;
    private final String TAG = "Settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        init();
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        userPic = findViewById(R.id.user_pic_view);
        usernameTxt = findViewById(R.id.user_name_settings_edittxt);
        emailTxt = findViewById(R.id.email_settings_edittext);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setTitle(R.string.settings_menu);
        setSupportActionBar(toolbar);

        getUserAccountData();
    }

    private void getUserAccountData() {
        Log.d(TAG, "getUserAccountData: retrieving data from database");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_users)).
                orderByKey().
                equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getUserAccountData: Data retrieved");
                    User user = singleSnapshot.getValue(User.class);
                    emailTxt.setText(user.getEmail());
                    usernameTxt.setText(user.getName());

                    if(!user.getEmail().equals("nothing"))
                    {
                        Glide.with(Settings.this).load(user.getProfile_picture()).into(userPic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getUserAccountData: couldn't retrieve data");
            }
        });
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

    public void updateUser(View view) {
        EditText newEmailEdit = findViewById(R.id.email_settings_edittext);
        EditText newPasswordEdit = findViewById(R.id.new_password_edittxt);
        final EditText newUsername = findViewById(R.id.user_name_settings_edittxt);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        if (!newEmailEdit.getText().toString().equals("") &&
                !FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(newEmailEdit.getText().toString())) {
            if (isValidEmail(newEmailEdit.getText().toString())) {
                user.updateEmail(newEmailEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update email in database here
                        reference.child(getString(R.string.db_users)).
                                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                child(getString(R.string.field_email)).
                                setValue(emailTxt.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                emailTxt.setText(emailTxt.getText().toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: couldn't change email" + e.toString());
                            }
                        });
                        Toast.makeText(Settings.this, "Email updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this, "Email couldn't be updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: Email couldn't be updated" + e.toString());
                    }
                });
            } else {
                Toast.makeText(Settings.this, "Not a valid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Settings.this, "Enter a new email", Toast.LENGTH_SHORT).show();
        }
        if (!newPasswordEdit.getText().toString().equals("")) {
            user.updatePassword(newPasswordEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(Settings.this, "Password updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Settings.this, "Password couldn't be updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!newUsername.getText().toString().equals("")) {
            //update username here
            reference.child(getString(R.string.db_users)).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child(getString(R.string.field_name)).
                    setValue(newUsername.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    newUsername.setText(newUsername.getText().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: couldn't change username" + e.toString());
                }
            });
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

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();

            String path = imageUri.toString();
            String filename = path.substring(path.lastIndexOf("/")+1);
            String uid = user.getUid();

            final StorageReference riverRef = mStorageRef.child("images/"+uid+"/"+filename+".jpg");
            riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Jesper", "onSuccess: Picture added" + uri);

                            Glide.with(Settings.this).load(uri).into(userPic);

                            reference.child(getString(R.string.db_users)).
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                    child(getString(R.string.field_picture)).
                                    setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: image url added to user database table");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: couldn't imageurl" + e.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Picture couldn't be added " + e.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: BUUUU " + e.toString());
                }
            });

        }
    }

    public void changePic(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Settings.this, UsersListActivity.class);
        startActivity(intent);
        finish();
    }
}