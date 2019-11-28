package com.jesper.shutapp.Fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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
import com.jesper.shutapp.GroupInviteAdapter;
import com.jesper.shutapp.GroupListAdapter;
import com.jesper.shutapp.MessagesAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.GroupChat;
import com.jesper.shutapp.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private ListView usersListView, groupsListView;
    private MessagesAdapter messagesAdapter;
    private GroupListAdapter groupListAdapter;
    private ImageView userPicture;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private TextView userName;
    private ArrayList<User> usersList;
    private ArrayList<String> userGroups;
    private List<String> chatUsers;
    private Toolbar mToolbar;
    private ArrayList<GroupChat> groupChatArrayList;
    private ArrayList<String> groupUsers;
    private TextView textGroups, textMessages;
    private View dividerGroup, dividerMessage;

    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        init(view);
        setCurrentUser();
        getChatHistory();
        readChats();
        generateUserGroups();

        textGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usersListView.setVisibility(View.GONE);
                dividerMessage.setVisibility(View.INVISIBLE);
                dividerGroup.setVisibility(View.VISIBLE);
                groupsListView.setVisibility(View.VISIBLE);
                textGroups.setTypeface(null, Typeface.BOLD);
                textMessages.setTypeface(null, Typeface.NORMAL);
            }
        });

        textMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsListView.setVisibility(View.GONE);
                dividerGroup.setVisibility(View.INVISIBLE);
                usersListView.setVisibility(View.VISIBLE);
                dividerMessage.setVisibility(View.VISIBLE);
                textGroups.setTypeface(null, Typeface.NORMAL);
                textMessages.setTypeface(null, Typeface.BOLD);
            }
        });


        return view;
    }

    //Initiate all view and variables and set Current user.
    private void init (View view) {
        setHasOptionsMenu(true);
        usersList = new ArrayList<>();
        chatUsers = new ArrayList<>();
        userGroups = new ArrayList<>();
        groupChatArrayList = new ArrayList<>();
        mToolbar = view.findViewById(R.id.userlist_toolbar);
        textGroups = view.findViewById(R.id.btn_group_listview);
        usersListView = view.findViewById(R.id.users_list);
        userName = view.findViewById(R.id.user_name_homescreen);
        userPicture = view.findViewById(R.id.user_picture);
        groupsListView = view.findViewById(R.id.listview_groups);
        dividerGroup = view.findViewById(R.id.divider_line_groupspushed);
        dividerMessage = view.findViewById(R.id.divider_line_friendspushed);
        textMessages = view.findViewById(R.id.btn_messages_listview);
        dividerGroup.setVisibility(View.INVISIBLE);
        dividerMessage.setVisibility(View.VISIBLE);
        mToolbar.setTitle("");
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    //Saves strings UID of all person current user have been chatting with
    private void getChatHistory (){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())) {
                        if (!chatUsers.contains(chat.getReceiver())) {
                            chatUsers.add(chat.getReceiver());
                        }
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        if (!chatUsers.contains(chat.getSender())) {
                            chatUsers.add(chat.getSender());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    //Locates the user class which we've chat with and adds to adapter.
    private void readChats () {
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (String id : chatUsers) {
                        if (user.getUid().equals(id)) {
                            if (usersList.size() != 0) {
                                for (User user1 : usersList) {
                                    if (!user.getUid().equals(user1.getUid())){
                                        usersList.add(user);
                                        break;
                                    }
                                }
                            }   else {
                                usersList.add(user);
                            }
                        }
                    }
                }
                messagesAdapter = new MessagesAdapter(getActivity(), usersList);
                usersListView.setAdapter(messagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Method for setting up the current user
    private void setCurrentUser() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (isVisible()) {
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

    //Adds all the user groups to a string-array.
    private void generateUserGroups() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(fuser.getUid()).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userGroups.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userGroups.add(snapshot.getKey());
                    Log.d("ANTON", "userGroups.add " + snapshot.getKey());
                }
                generateGroups();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void generateGroups() {
        groupChatArrayList.clear();
        Log.d("ANTON", "generateGroups: generating groups");

        for (int i = 0; i < userGroups.size() ; i++) {
            GroupChat groupChat = new GroupChat();
            groupChat.setGroupName(userGroups.get(i));
            Log.d("ANTON", groupChat.getGroupName());
            groupChatArrayList.add(groupChat);
        }
        groupListAdapter = new GroupListAdapter(getActivity(), groupChatArrayList);
        groupsListView.setAdapter(groupListAdapter);
    }

    //Inflate our toolbar.
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.friendlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Switch case for toolbar.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_personToChat_button:
                GroupChatFragment groupChatFragment = new GroupChatFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_activity_user_list_holder, groupChatFragment);
                fragmentTransaction.commit();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
