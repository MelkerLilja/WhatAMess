package com.jesper.shutapp.Fragments;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.R;
import com.jesper.shutapp.GroupChatAdapter;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {

    ListView listView;
    ArrayList<User> friendListGroup;
    FirebaseUser fuser;
    GroupChatAdapter groupChatAdapter;
    Toolbar mToolbar;
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
                addGroupToDatabase(stringGroupName, groupUsers);
                removeOldGroup();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Adds the group to database.
    private void addGroupToDatabase(String string, List<String> users) {
        HashMap<String, Object> hashMap = new HashMap<>();
        reference = FirebaseDatabase.getInstance().getReference("groups");

        for (int i = 0; i < users.size(); i++) {
            hashMap.put(users.get(i) , users.get(i));
        }
        reference.child(string).setValue(hashMap);
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
                groupChatAdapter = new GroupChatAdapter(getActivity(), friendListGroup);
                listView.setAdapter(groupChatAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
