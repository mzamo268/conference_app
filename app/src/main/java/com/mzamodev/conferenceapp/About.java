package com.mzamodev.conferenceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        //initializing bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //set Home tab as selected tab
        bottomNavigationView.setSelectedItemId(R.id.aboutBtn);

        //perform item selected listeners
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.rsvp:
                        Intent open = new Intent(getApplicationContext(),Rsvp.class);
                        open.putExtra("subject","maths");
                        open.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open);
                        return true;
                    case R.id.homeBtn:
                        Intent open1 = new Intent(getApplicationContext(),Home.class);
                        //open1.putExtra("email",email);
                        open1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open1);
                        return true;
                    case R.id.anouncements:
                        Intent open2 = new Intent(getApplicationContext(),Anouncement.class);
                        //open1.putExtra("email",email);
                        open2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open2);
                        return true;
                }
                return false;
            }
        });

    }

    public void followMe(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/mzamodev")));
    }

}