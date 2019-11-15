package com.jesper.shutapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.MainActivity;
import com.jesper.shutapp.Activities.MainSettings;
import com.jesper.shutapp.FriendsListAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private static boolean active = false;
    ListView usersListView;
    FriendsListAdapter friendsListAdapter;
    ImageView userPicture;

    Toolbar mToolbar;
    TextView userName;
    ArrayList<User> usersList;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        init(view);

        return view;
    }
    //Initiate all view and variables and set Current user.
    private void init (View view) {

        usersList = new ArrayList<>();
        usersListView = view.findViewById(R.id.users_list);
        userName = view.findViewById(R.id.user_name_homescreen);
        userPicture = view.findViewById(R.id.user_picture);

        setCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                generateUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    //Generate all user to the list
    private void generateUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    usersList.add(user);
                }
                friendsListAdapter = new FriendsListAdapter(getActivity(), usersList);
                usersListView.setAdapter(friendsListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Method for setting up the current user
    private void setCurrentUser() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (active) {
                    User user = dataSnapshot.getValue(User.class);
                    userName.setText(user.getName());
                    Glide.with(getActivity()).load(user.getProfile_picture()).into(userPicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}
