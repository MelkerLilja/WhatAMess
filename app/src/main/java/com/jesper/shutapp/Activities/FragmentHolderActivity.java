package com.jesper.shutapp.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Fragments.ContactsFragment;
import com.jesper.shutapp.Fragments.MessagesFragment;
import com.jesper.shutapp.Fragments.ProfileFragment;
import com.jesper.shutapp.Fragments.SearchFragment;
import com.jesper.shutapp.R;
import java.util.HashMap;

public class FragmentHolderActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseUser fuser;
    ImageView notificationRequests;
    FragmentManager fragmentManager = getSupportFragmentManager();
    MessagesFragment messagesFragment;
    ImageButton contactBtn;
    ImageButton chatBtn;
    ImageButton profileBtn;
    ImageButton searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        init();
        checkForRequests();
    }

    //Initiate views and variables
    private void init () {
        notificationRequests = findViewById(R.id.notification_requests);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        messagesFragment = new MessagesFragment();
        fragmentManager.beginTransaction().add(R.id.fragment_activity_user_list_holder, messagesFragment, "message").commit();
        //changeFragment(messagesFragment);
        searchBtn = findViewById(R.id.search_icon);
        contactBtn = findViewById(R.id.contacts_icon);
        chatBtn = findViewById(R.id.message_icon);
        profileBtn = findViewById(R.id.profile_icon);
    }

    //Method that change our View with the Fragment we pass in
    public void changeFragment(Fragment frag,String message) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_activity_user_list_holder, frag,message).addToBackStack("");
        fragmentTransaction.commit();
    }

    //Button for calling ProfileFragment
    public void btnProfileFragment(View view) {
        ProfileFragment profileFragment = new ProfileFragment();
        changeFragment(profileFragment,"profile");

        profileBtn.setImageResource(R.drawable.profile_icon_filled);
        chatBtn.setImageResource(R.drawable.chat_icon);
        contactBtn.setImageResource(R.drawable.contacts_icon);
        searchBtn.setImageResource(R.drawable.search_icon);
    }

    //Button for calling Message Fragment
    public void btnMessageFragment(View view) {
        MessagesFragment messagesFragment = new MessagesFragment();
        changeFragment(messagesFragment,"message");

        chatBtn.setImageResource(R.drawable.chat_icon_filled);
        profileBtn.setImageResource(R.drawable.profile_icon);
        contactBtn.setImageResource(R.drawable.contacts_icon);
        searchBtn.setImageResource(R.drawable.search_icon);
    }

    //Button for calling ContactsFragment
    public void btnContactsFragment(View view) {
        ContactsFragment contactsFragment = new ContactsFragment();
        changeFragment(contactsFragment,"contact");

        contactBtn.setImageResource(R.drawable.contacts_icon_filled);
        profileBtn.setImageResource(R.drawable.profile_icon);
        chatBtn.setImageResource(R.drawable.chat_icon);
        searchBtn.setImageResource(R.drawable.search_icon);
    }

    //Button for calling SearchFragment
    public void btnSearchFragment(View view) {
        SearchFragment searchFragment = new SearchFragment();
        changeFragment(searchFragment,"search");

        searchBtn.setImageResource(R.drawable.search_icon_filled);
        profileBtn.setImageResource(R.drawable.profile_icon);
        chatBtn.setImageResource(R.drawable.chat_icon);
        contactBtn.setImageResource(R.drawable.contacts_icon);
    }

    private void status(String status) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    //Checking for friend-requests and set notification visibility if so
    private void checkForRequests () {
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friendrequests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    notificationRequests.setVisibility(View.VISIBLE);
                }   else {
                    notificationRequests.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag("message").isVisible()) {
            fragmentManager.popBackStack();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void setButtons()
    {
        searchBtn.setImageResource(R.drawable.search_icon_filled);
        profileBtn.setImageResource(R.drawable.profile_icon);
        chatBtn.setImageResource(R.drawable.chat_icon);
        contactBtn.setImageResource(R.drawable.contacts_icon);
    }
}
