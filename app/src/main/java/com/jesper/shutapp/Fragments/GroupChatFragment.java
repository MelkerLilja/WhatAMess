package com.jesper.shutapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.MainSettings;
import com.jesper.shutapp.FriendsListAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.GroupChatAdapter;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {

    ListView listView;
    ArrayList<User> friendListGroup;
    FirebaseUser fuser;
    GroupChatAdapter groupChatAdapter;
    Toolbar mToolbar;
    CheckBox mCheckbox;
    ImageView btnChecked;

    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        setHasOptionsMenu(true);

        listView = view.findViewById(R.id.listview_friends_groupchat);
        friendListGroup = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mCheckbox = view.findViewById(R.id.checkbox_group);
        mToolbar = view.findViewById(R.id.include_toolbar_groupchat);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        btnChecked = view.findViewById(R.id.imagebutton_checkbox);

        generateFriendList();




/*        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheckbox.isChecked()){
                    //show add button
                }else {
                    //hide add button
                }
            }
        });*/

        return view;
    }


    //Generate all user to the list
    private void generateFriendList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    friendListGroup.add(user);
                }
                groupChatAdapter = new GroupChatAdapter(getActivity(), friendListGroup);
                listView.setAdapter(groupChatAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
