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
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.Activities.GroupInChatActivity;
import com.jesper.shutapp.R;
import com.jesper.shutapp.GroupInviteAdapter;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {

    private ListView listView;
    private ArrayList<User> friendListGroup;
    private FirebaseUser fuser;
    private GroupInviteAdapter groupInviteAdapter;
    private Toolbar mToolbar;
    private DatabaseReference reference, userRef;
    private ArrayList<String> groupUsers;
    private EditText groupName;
    private String user;
    private String stringGroupName;

    public GroupChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        setHasOptionsMenu(true);
        init(view);
        getUsersFromFB();
        generateFriendList();

        return view;
    }

    //Initiate all variables and views.
    private void init (View view) {
        groupUsers = new ArrayList<>();
        friendListGroup = new ArrayList<>();
        groupName = view.findViewById(R.id.edittext_groupchat);
        listView = view.findViewById(R.id.listview_friends_groupchat);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mToolbar = view.findViewById(R.id.include_toolbar_groupchat);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        groupUsers.add(fuser.getUid());
        mToolbar.setTitle("");
    }

    //Inflate our toolbar.
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.groupchat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Switch case for toolbar.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.groupchat_check:
                stringGroupName = groupName.getText().toString();
                addGroupToDatabase();
                removeOldGroup();
                addGroupNameToUsers();
                startGroupChatActivity();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Adds the group to database.
    private void addGroupToDatabase() {

        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for (int i = 0; i < groupUsers.size() ; i++) {


                        if (snapshot.getKey().equals(groupUsers.get(i))) {
                            User user = snapshot.getValue(User.class);
                            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups");
                            groupRef.child(stringGroupName).child("members").child(user.getUid()).setValue(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        }

    //Gets the group-users from FireBase.
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

    //Removes the old group from FireBase.
    private void removeOldGroup() {
        reference = FirebaseDatabase.getInstance().getReference("groups").child(fuser.getUid());
        reference.removeValue();
    }

    //Generate all user to the list.
    private void generateFriendList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    friendListGroup.add(user);
                }
                groupInviteAdapter = new GroupInviteAdapter(getActivity(), friendListGroup);
                listView.setAdapter(groupInviteAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Adds group-names into Users profiles.
    private void addGroupNameToUsers(){
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (int i = 0; i < groupUsers.size(); i++) {
                        if (snapshot.getKey().equals(groupUsers.get(i))){
                            reference.child(snapshot.getKey()).child("groups").child(stringGroupName).setValue(stringGroupName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Calls the GroupChatActivity
    private void startGroupChatActivity (){
        Intent intent = new Intent (getActivity(), GroupInChatActivity.class);
        intent.putExtra("groupname", stringGroupName);
        intent.putExtra("grouplist", groupUsers);
        startActivity(intent);
    }
}
