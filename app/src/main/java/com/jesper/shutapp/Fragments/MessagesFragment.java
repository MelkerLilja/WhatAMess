package com.jesper.shutapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.jesper.shutapp.MessagesAdapter;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    ListView usersListView;
    MessagesAdapter messagesAdapter;
    ImageView userPicture;
    FirebaseUser fuser;
    DatabaseReference reference;

    TextView userName;
    ArrayList<User> usersList;
    private List<String> chatUsers;

    public MessagesFragment() {
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
        chatUsers = new ArrayList<>();
        usersListView = view.findViewById(R.id.users_list);
        userName = view.findViewById(R.id.user_name_homescreen);
        userPicture = view.findViewById(R.id.user_picture);
        setCurrentUser();







        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())){
                        if (!chatUsers.contains(chat.getReceiver())) {
                            chatUsers.add(chat.getReceiver());
                        }
                    }
                    if (chat.getReceiver().equals(fuser.getUid())){
                        if (!chatUsers.contains(chat.getSender())) {
                            chatUsers.add(chat.getSender());
                        }
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

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

    //Generate all user to the list
    private void generateUsers() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(usersList.size() >= 1)
                {
                    usersList.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    usersList.add(user);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
