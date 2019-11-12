package com.jesper.shutapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.jesper.shutapp.Activities.MainSettings;
import com.jesper.shutapp.Activities.Settings;
import com.jesper.shutapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Toolbar mToolbar;


    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setHasOptionsMenu(true);
        mToolbar = view.findViewById(R.id.include_toolbar_xml);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);



        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getActivity(), MainSettings.class);
        startActivity(intent);

        switch (item.getItemId()) {
            case R.id.settings:
                getActivity().startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
