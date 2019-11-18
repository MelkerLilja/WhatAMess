package com.jesper.shutapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jesper.shutapp.Fragments.ContactsFragment;
import com.jesper.shutapp.Fragments.MessagesFragment;
import com.jesper.shutapp.Fragments.ProfileFragment;
import com.jesper.shutapp.Fragments.SearchFragment;
import com.jesper.shutapp.R;

import java.util.HashMap;

public class FragmentHolderActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseUser fuser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);

        MessagesFragment messagesFragment = new MessagesFragment();
        changeFragment(messagesFragment);

    }

    //Method that change our View with the Fragment we pass in
    public void changeFragment(Fragment frag){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.replace(R.id.fragment_activity_user_list_holder, frag);
        fragmentTransaction.commit();
    }


    //Button for calling ProfileFragment
    public void btnProfileFragment(View view) {
        ProfileFragment profileFragment = new ProfileFragment();
        changeFragment(profileFragment);
    }

    //Button for calling Message Fragment
    public void btnMessageFragment(View view) {
        MessagesFragment messagesFragment = new MessagesFragment();
        changeFragment(messagesFragment);
    }

    //Button for calling ContactsFragment
    public void btnContactsFragment(View view) {
        ContactsFragment contactsFragment = new ContactsFragment();
        changeFragment(contactsFragment);
    }

    //Button for calling SearchFragment
    public void btnSearchFragment(View view) {
        SearchFragment searchFragment = new SearchFragment();
        changeFragment(searchFragment);
    }

    private void status (String status) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
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
}
