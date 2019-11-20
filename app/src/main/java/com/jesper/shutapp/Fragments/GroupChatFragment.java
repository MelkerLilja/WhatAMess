package com.jesper.shutapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.ChatActivity;
import com.jesper.shutapp.Activities.MainSettings;
import com.jesper.shutapp.FriendsListAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.GroupChatAdapter;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

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
    DatabaseReference reference;
    ArrayList<String> groupUsers;
    TextView textView;
    EditText groupName;
    String user;
    String stringGroupName;


    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        setHasOptionsMenu(true);
        init(view);
        getUsersFromFB();
        generateFriendList();


        return view;
    }

    private void init (View view) {
        groupUsers = new ArrayList<>();
        textView = view.findViewById(R.id.test_test);
        groupName = view.findViewById(R.id.edittext_groupchat);
        listView = view.findViewById(R.id.listview_friends_groupchat);
        friendListGroup = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mToolbar = view.findViewById(R.id.include_toolbar_groupchat);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        btnChecked = view.findViewById(R.id.imagebutton_checkbox);
        mToolbar.setTitle("");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.groupchat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.groupchat_check:
                stringGroupName = groupName.getText().toString();
                addGroupToDatabase(stringGroupName, groupUsers);
                removeOldGroup();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addGroupToDatabase(String string, List<String> users) {
        HashMap<String, Object> hashMap = new HashMap<>();
        reference = FirebaseDatabase.getInstance().getReference("groups");

        for (int i = 0; i < users.size(); i++) {
            hashMap.put(users.get(i) , users.get(i));
        }

        reference.child(string).setValue(hashMap);
    }

    private void getUsersFromFB() {

        stringGroupName = groupName.getText().toString();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("groups").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getKey();
                    groupUsers.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeOldGroup() {
        reference = FirebaseDatabase.getInstance().getReference("groups").child(fuser.getUid());
        reference.removeValue();
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
