package com.jesper.shutapp.Fragments;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    Toolbar mToolbar;

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

        return view;
    }

    //Initiate all view and variables and set Current user.
    private void init (View view) {
        setHasOptionsMenu(true);
        usersList = new ArrayList<>();
        chatUsers = new ArrayList<>();
        mToolbar = view.findViewById(R.id.userlist_toolbar);
        usersListView = view.findViewById(R.id.users_list);
        userName = view.findViewById(R.id.user_name_homescreen);
        userPicture = view.findViewById(R.id.user_picture);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
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
