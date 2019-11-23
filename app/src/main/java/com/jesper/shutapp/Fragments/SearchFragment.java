package com.jesper.shutapp.Fragments;


import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.SearchAdapter;
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
    Context context;


    /* Min från förut */

    EditText searchUser;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> nameList;
    ArrayList<String> profilePicList;
    ArrayList<String> userBio;
    ArrayList<String> userUid;
    SearchAdapter searchAdapter;
    FrameLayout test;


    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_users, container, false);
        userSearchList = new ArrayList<>();

        test = view.findViewById(R.id.test_test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        searchUser = view.findViewById(R.id.search_users);
        recyclerView = view.findViewById(R.id.users_list);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        nameList = new ArrayList<>();
        profilePicList = new ArrayList<>();
        userBio = new ArrayList<>();
        userUid = new ArrayList<>();

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
                    userBio.clear();
                    userUid.clear();
                    recyclerView.removeAllViews();
                }
            }
        });


        return view;

    }


    private void setAdapter(final String searchedString) {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                nameList.clear();
                profilePicList.clear();
                userBio.clear();
                userUid.clear();
                recyclerView.removeAllViews();
                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String profilePic = snapshot.child("profile_picture").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    String uid = snapshot.child("uid").getValue(String.class);


                    if (name.toLowerCase().contains(searchedString.toLowerCase())){
                        nameList.add(name);
                        profilePicList.add(profilePic);
                        userBio.add(bio);
                        userUid.add(uid);

                        counter++;
                    }

                    if (counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(getContext(), nameList, profilePicList, userBio, userUid);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hideKeyboard (){
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
