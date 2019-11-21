package com.jesper.shutapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesper.shutapp.R;

import org.w3c.dom.Text;

public class ProfileSetting extends AppCompatActivity {

    private ImageView profileImg;
    private TextView usernameText;
    private TextView bioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        Toolbar mtoolbar =findViewById(R.id.profile_toolbar);
        mtoolbar.setTitle(getString(R.string.profile_txt));
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImg = findViewById(R.id.profile_image);
        usernameText = findViewById(R.id.user_name_profile_text);
        bioText = findViewById(R.id.bio_profile_text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.profile_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout_settings:
                Log.d("Amanda", "loggade ut");
                //logga ut kod
                break;
            case R.id.add_as_friend:
                Log.d("Amanda", "lägger till vän ");
                //lägg till vän kod
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
