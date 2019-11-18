package com.jesper.shutapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jesper.shutapp.R;
import com.jesper.shutapp.model.Chat;
import com.jesper.shutapp.model.User;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> nameList;
    ArrayList<String> profilePicList;

    public class SearchViewHolder extends RecyclerView.ViewHolder{

        ImageView profilePic;
        TextView nameOfUser;
        RelativeLayout parentLayout;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_pic);
            nameOfUser = itemView.findViewById(R.id.name_of_user);
        }
    }


    public SearchAdapter(Context context, ArrayList<String> nameList, ArrayList<String> profilePicList) {
        this.context = context;
        this.nameList = nameList;
        this.profilePicList = profilePicList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, final int position) {
        holder.nameOfUser.setText(nameList.get(position));
        Glide.with(context).load(profilePicList.get(position)).into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }
}
