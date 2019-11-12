package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jesper.shutapp.R;
import com.jesper.shutapp.Fragments.TermsOfService;
import com.jesper.shutapp.model.User;

public class MainSettings extends AppCompatActivity {

    FirebaseUser user;
    private static final int PICK_IMAGE = 100;
    private ImageView userPic;
    private Uri imageUri;
    private StorageReference mStorageRef;

    private EditText usernameTxt;
    private EditText emailTxt;
    private final String TAG = "Settings";


    private FragmentManager mFragmentManager;
    private TermsOfService tos;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        init();

        tos = new TermsOfService();
        mFragmentManager = getSupportFragmentManager();

        FrameLayout mFragmentHolder = findViewById(R.id.fragment_holder_main_settings);
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
            Intent intent = new Intent(MainSettings.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // fixed so the homebutton brings the user back to UserListAcitivity
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MainSettings.this, UsersListActivity.class);
            startActivity(intent);
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainSettings.this, UsersListActivity.class);
        startActivity(intent);
        finish();
    }


    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userPic = findViewById(R.id.user_mainpic_view);
        usernameTxt = findViewById(R.id.user_name_mainsettings_edittxt);
        emailTxt = findViewById(R.id.email_mainsettings_edittext);
        mStorageRef = FirebaseStorage.getInstance().getReference();
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

                    if (!user.getEmail().equals("nothing")) {

                        //Test if the user have uploaded a profile pic or not
                        //First example will use a default pic, other will chose the uploaded pic

                        if (user.getProfile_picture() == null) {
                            Glide.with(MainSettings.this).load(R.drawable.placeholder).into(userPic);
                        } else {
                            Glide.with(MainSettings.this).load(user.getProfile_picture()).into(userPic);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getUserAccountData: couldn't retrieve data");
            }
        });

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
                        Toast.makeText(MainSettings.this, "Email updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainSettings.this, "Email couldn't be updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: Email couldn't be updated" + e.toString());
                    }
                });
            } else {
                Toast.makeText(MainSettings.this, "Not a valid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainSettings.this, "Enter a new email", Toast.LENGTH_SHORT).show();
        }
        if (!newPasswordEdit.getText().toString().equals("")) {
            user.updatePassword(newPasswordEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(MainSettings.this, "Password updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainSettings.this, "Password couldn't be updated", Toast.LENGTH_SHORT).show();
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
            String filename = path.substring(path.lastIndexOf("/") + 1);
            String uid = user.getUid();

            final StorageReference riverRef = mStorageRef.child("images/" + uid + "/" + filename + ".jpg");
            riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Jesper", "onSuccess: Picture added" + uri);

                            Glide.with(MainSettings.this).load(uri).into(userPic);

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
                                    Log.d(TAG, "onFailure: couldn't save imageurl" + e.toString());
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
                    Log.d(TAG, "onFailure: Couldn't  " + e.toString());
                }
            });

        }
    }

    public void changePic(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
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

    public void terms_of_service(View view) {
        mFragmentManager.beginTransaction().add(R.id.fragment_holder_main_settings, tos, "Terms of Service").commit();
    }
}

