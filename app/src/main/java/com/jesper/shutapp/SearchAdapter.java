package com.jesper.shutapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jesper.shutapp.Activities.UserPageActivity;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> nameList;
    ArrayList<String> profilePicList;
    ArrayList<String> userBio;
    ArrayList<String> userUid;
    ArrayList<String> userAge;
    ArrayList<String> userGender;
    final String TAG = "LALA";

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView profilePic;
        TextView nameOfUser;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_pic);
            nameOfUser = itemView.findViewById(R.id.name_of_user);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), UserPageActivity.class);
            String name = nameList.get(getAdapterPosition());
            String bio = userBio.get(getAdapterPosition());
            String photo = profilePicList.get(getAdapterPosition());
            String uid = userUid.get(getAdapterPosition());
            String age = userAge.get(getAdapterPosition());
            String gender = userGender.get(getAdapterPosition());
            intent.putExtra("bio", bio);
            intent.putExtra("photo", photo);
            intent.putExtra("name", name);
            intent.putExtra("uid", uid);
            intent.putExtra("age", age);
            intent.putExtra("gender",gender);
            itemView.getContext().startActivity(intent);
            Log.d("MELKER", "onClick: click");
        }
    }


    public SearchAdapter(Context context, ArrayList<String> nameList,
                         ArrayList<String> profilePicList, ArrayList<String> userBio,
                         ArrayList<String> userUid, ArrayList<String> userAge, ArrayList<String> userGender) {
        this.context = context;
        this.nameList = nameList;
        this.profilePicList = profilePicList;
        this.userBio = userBio;
        this.userUid = userUid;
        this.userAge = userAge;
        this.userGender = userGender;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked");
            }
        });

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
