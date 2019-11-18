package com.jesper.shutapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.SearchAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.UserListAdapter;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    UserListAdapter adapter;
    ArrayList<User>userSearchList;
    ListView listView;


    /* Min från förut */

    EditText searchUser;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> nameList;
    ArrayList<String> profilePicList;
    ArrayList<User> usersList;
    SearchAdapter searchAdapter;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_users, container, false);
        userSearchList = new ArrayList<>();

        listView = view.findViewById(R.id.listview_search_users);


        searchUser = view.findViewById(R.id.search_users);
        recyclerView = view.findViewById(R.id.users_list);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        nameList = new ArrayList<>();
        profilePicList = new ArrayList<>();

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                } else {
                    nameList.clear();
                    profilePicList.clear();
                    recyclerView.removeAllViews();
                }
            }
        });


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


    private void setAdapter(final String searchedString) {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                nameList.clear();
                profilePicList.clear();
                recyclerView.removeAllViews();
                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String profilePic = snapshot.child("profile_picture").getValue(String.class);

                    if (name.toLowerCase().contains(searchedString.toLowerCase())){
                        nameList.add(name);
                        profilePicList.add(profilePic);
                        counter++;
                    }

                    if (counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(getContext(), nameList, profilePicList);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                adapter = new UserListAdapter(getActivity(), userSearchList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
