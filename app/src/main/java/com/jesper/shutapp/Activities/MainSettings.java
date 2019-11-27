package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainSettings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseUser user;
    DatabaseReference reference;
    ArrayAdapter<CharSequence> adapter;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final int PICK_IMAGE = 100;
    private ImageView userPic;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private EditText usernameTxt, emailTxt, bioTxt, fromTxt;
    private ImageButton calenderBtn;
    private Spinner genderSpinner;
    private static String genderChoice;
    private TextView ageTxt;
    private final String TAG = "Settings";
    private String ageString;

    private String currentImagePath = null;
    private static final int IMAGE_CODE = 101;

    private FragmentManager mFragmentManager;
    private TermsOfService tos;

    private Toolbar mToolbar;

    private static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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


        // Here is the gender spinner

        genderSpinner = findViewById(R.id.gender_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_choices, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);


        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), datePickerListener, mYear, mMonth, mDay);
                dateDialog.getDatePicker().setMaxDate(new Date().getTime());
                dateDialog.show();
            }
        });

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String age = Integer.toString(calculateAge(c.getTimeInMillis()));
            reference.child(getString(R.string.db_users)).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child(getString(R.string.field_age)).
                    setValue(age);
            String ageOfTheUser = ageString + " " + age;
            ageTxt.setText(ageOfTheUser);
        }
    };

    int calculateAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.update_profile) {
            updateUser();
        }

        // fixed so the homebutton brings the user back to UserListAcitivity
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MainSettings.this, FragmentHolderActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Change from dark to day theme
    public void theme(View view) {

        sp = getSharedPreferences("theme", Activity.MODE_PRIVATE);
        editor = sp.edit();

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, getText(R.string.day_theme_toast), Toast.LENGTH_SHORT).show();
            editor.putString("theme_key", "day");

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, getText(R.string.night_theme_toast), Toast.LENGTH_SHORT).show();
            editor.putString("theme_key", "night");
        }
        editor.apply();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainSettings.this, FragmentHolderActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        fromTxt = findViewById(R.id.from_edittxt);
        ageString = getString(R.string.age_txt);
        ageTxt = findViewById(R.id.age_txt);
        calenderBtn = findViewById(R.id.calender_btn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userPic = findViewById(R.id.user_mainpic_view);
        usernameTxt = findViewById(R.id.user_name_mainsettings_edittxt);
        emailTxt = findViewById(R.id.email_edittext);
        bioTxt = findViewById(R.id.user_bio_edittxt);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();
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
                    bioTxt.setText(user.getBio());
                    ageTxt.setText(user.getAge());
                    fromTxt.setText(user.getFrom());

                    if (!user.getEmail().equals("nothing")) {

                        //Test if the user have uploaded a profile pic or not
                        //First example will use a default pic, other will chose the uploaded pic

                        if (active) {
                            if (user.getProfile_picture() == null) {
                                Glide.with(MainSettings.this).load(R.drawable.placeholder).into(userPic);
                            } else {
                                Glide.with(MainSettings.this).load(user.getProfile_picture()).into(userPic);
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: " + user.getGender());
                    if (user.getGender() != null) {
                        if (user.getGender().equals("Man")) {
                            genderSpinner.setSelection(1);
                        } else if (user.getGender().equals("Woman")) {
                            genderSpinner.setSelection(2);
                        }
                    }
                    if(user.getAge() != null) {
                        String userAge = user.getAge();
                        String ageOfTheUser = ageString + " " + userAge;
                        ageTxt.setText(ageOfTheUser);
                    }
                    if (user.getFrom() != null) {
                        fromTxt.setText(user.getFrom());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getUserAccountData: couldn't retrieve data");
            }
        });

    }

    public void updateUser() {
        EditText newEmailEdit = findViewById(R.id.email_edittext);
        EditText newPasswordEdit = findViewById(R.id.new_password_edittxt);
        final EditText newBioEdit = findViewById(R.id.user_bio_edittxt);
        final EditText newFromEdit = findViewById(R.id.from_edittxt);
        final EditText newUsername = findViewById(R.id.user_name_mainsettings_edittxt);

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
                        Toast.makeText(MainSettings.this, getString(R.string.email_updated), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainSettings.this, getString(R.string.email_not_updated), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: Email couldn't be updated" + e.toString());
                    }
                });
            } else {
                Toast.makeText(MainSettings.this, getString(R.string.invalid_email_txt), Toast.LENGTH_SHORT).show();
            }
        }
        if (!newPasswordEdit.getText().toString().equals("")) {
            user.updatePassword(newPasswordEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(MainSettings.this, getString(R.string.password_updated_toast), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainSettings.this, getString(R.string.password_not_match_toast), Toast.LENGTH_SHORT).show();
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
        if (!newBioEdit.getText().toString().equals("")) {
            //update userbio here
            reference.child(getString(R.string.db_users)).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child(getString(R.string.field_bio)).
                    setValue(newBioEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    newBioEdit.setText(newBioEdit.getText().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: couldn't change userbio" + e.toString());
                }
            });
        }
        if (!newFromEdit.getText().toString().equals("")) {
            //update userbio here
            reference.child(getString(R.string.db_users)).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child(getString(R.string.field_from)).
                    setValue(newFromEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    newFromEdit.setText(newFromEdit.getText().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: couldn't change user nationality" + e.toString());
                }
            });
        }

        Toast.makeText(MainSettings.this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
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
        if (resultCode == RESULT_OK && requestCode == IMAGE_CODE) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            Log.d(TAG, "onActivityResult: " + imageUri);
            userPic.setImageBitmap(bitmap);

            String uid = user.getUid();
            final StorageReference riverRef = mStorageRef.child("images/" + uid + "/" + "camera.jpg");
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
    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Delete account and return to MainActivity
    public void deleteAccount(View view) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Remove user from database

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getUid());

        userRef.removeValue();

        /*-----Remove user from Authentication-----*/

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("HEJ", "User account deleted.");
                            FirebaseDatabase.getInstance().getReference().child(getString
                                    (R.string.db_users)).child(FirebaseAuth.getInstance().
                                    getCurrentUser().getUid()).removeValue();
                        }

                    }
                });


        logOut(view);
        finish();


    }

    public void termsOfService(View view) {
        mFragmentManager.beginTransaction().add(R.id.fragment_holder_main_settings, tos, "Terms of Service").commit();
    }

    public void takePic(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.jesper.shutapp", imageFile);
                this.imageUri = imageUri;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_CODE);
            }
        }
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();

        return imageFile;
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                genderChoice = String.valueOf(parent.getItemAtPosition(position));
                reference.child(getString(R.string.db_users)).
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        child(getString(R.string.field_gender)).
                        setValue(genderChoice);
                break;
            case 2:
                // Whatever you want to happen when the third item gets selected
                genderChoice = String.valueOf(parent.getItemAtPosition(position));
                reference.child(getString(R.string.db_users)).
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        child(getString(R.string.field_gender)).
                        setValue(genderChoice);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void reportAproblem(View view) {
        //Send to new activity problemActivity
    }
}

