package com.mzamodev.conferenceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

public class Splash extends AppCompatActivity {

    int SPLASH_TIME = 3000; //This is 3 seconds

    //billing variables
    //private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //firebase message stuff
        //FirebaseMessaging.getInstance().subscribeToTopic("ExamPrep");

        //Code to start timer and take action after the timer ends
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do any action here. Now we are moving to next page
                loadData();

                //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                finish();

            }
        }, SPLASH_TIME);

    }

    void loadData(){
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        Cursor res = dbHelper.getUserInfo();
        if(res.getCount() == 0){
            Toast.makeText(this, ".WELCOME.", Toast.LENGTH_LONG).show();
            Intent mySuperIntent = new Intent(Splash.this, MainActivity.class);
            startActivity(mySuperIntent);
            //Snackbar.make(v, "No Feedback available for that module try another one...", Snackbar.LENGTH_LONG)
            //     .setAction("Action", null).show();
        }else {
            if (res.moveToNext()) {
                //getting data from sqlite
                //email = res.getString(0);


                //Intent mySuperIntent = new Intent(Splash.this, MainActivity.class);
                //mySuperIntent.putExtra("name",name);
                //mySuperIntent.putExtra("cellphone",cellphone);
                //startActivity(mySuperIntent);

                String newStudentNo = res.getString(0);
                //String password = res.getString(1);
                String newName = res.getString(2);
                String newCell = res.getString(3);
                String newType = res.getString(4);

                openHome(newName,newCell,newStudentNo,newType);

            }else {
                startActivity(new Intent(Splash.this,MainActivity.class));
            }
        }
    }

    void openHome(String fName,String cellphone,String studentNo,String type){
        Intent open = new Intent(Splash.this,Home.class);
        open.putExtra("name",fName);
        open.putExtra("cellphone",cellphone);
        open.putExtra("studentNo",studentNo);
        open.putExtra("type",type);
        startActivity(open);
    }


}