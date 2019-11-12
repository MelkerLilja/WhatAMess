package com.jesper.shutapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.UsersListActivity;
import com.jesper.shutapp.FriendsListAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.UserListAdapter;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchUsers extends Fragment {
    UserListAdapter userListAdapter;
    ArrayList<User>userSearchList;
    ListView listView;


    public SearchUsers() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_users, container, false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                generateUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});

        return view;
    }

    private void generateUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userSearchList.add(user);
                }
                userListAdapter = new UserListAdapter(getActivity(), userSearchList);
                listView.setAdapter(userListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
