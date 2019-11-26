package com.jesper.shutapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.FriendRequestAdapter;
import com.jesper.shutapp.FriendsListAdapter;
import com.jesper.shutapp.MessagesAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    ListView listView;
    ArrayList<User> friendList;
    ArrayList<User> requestList;
    FriendsListAdapter friendsListAdapter;
    FirebaseUser fuser;
    DatabaseReference reference;
    FriendRequestAdapter friendRequestAdapter;
    ListView listViewRequest;



    public ContactsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        friendList = new ArrayList<>();
        requestList = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        listView = view.findViewById(R.id.listview_friends_list);
        listViewRequest = view.findViewById(R.id.listview_friend_requests);

        generateFriendList();
        checkForFriendRequests();

        return view;

    }

    //Generate all user to the list
    private void generateFriendList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    friendList.add(user);
                }
                friendsListAdapter = new FriendsListAdapter(getActivity(), friendList);
                listView.setAdapter(friendsListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ANTON", "HAHA lol ");
            }
        });

    }

    private void checkForFriendRequests() {
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friendrequests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    requestList.add(user);
                }
                friendRequestAdapter = new FriendRequestAdapter(getActivity(), requestList);
                listView.setAdapter(friendRequestAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
