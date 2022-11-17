package com.mzamodev.conferenceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Anouncement extends AppCompatActivity {

    LinearLayout chats,sendChat,floatingUI;
    DatabaseReference database;

    //user info
    String name,cellphone,studentNo,type;
    EditText etChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anouncement);

        chats = (LinearLayout) findViewById(R.id.chats);
        sendChat = (LinearLayout) findViewById(R.id.sendChat);
        etChat = (EditText) findViewById(R.id.etMessage);
        floatingUI  = (LinearLayout) findViewById(R.id.floatingUI);

        name = getIntent().getStringExtra("name");
        cellphone = getIntent().getStringExtra("cellphone");
        type = getIntent().getStringExtra("type");
        studentNo = getIntent().getStringExtra("studentNo");

        if(type.equalsIgnoreCase("user")){
            floatingUI.setVisibility(View.INVISIBLE);
        }

        //initializing bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //set Home tab as selected tab
        bottomNavigationView.setSelectedItemId(R.id.anouncements);

        //perform item selected listeners
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.rsvp:
                        Intent open = new Intent(getApplicationContext(),Rsvp.class);
                        //open.putExtra("subject","maths");
                        open.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open);
                        return true;
                    case R.id.aboutBtn:
                        Intent open1 = new Intent(getApplicationContext(),About.class);
                        //open1.putExtra("email",email);
                        open1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open1);
                        return true;
                    case R.id.homeBtn:
                        Intent open2 = new Intent(getApplicationContext(),Home.class);
                        //open1.putExtra("email",email);
                        open2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open2);
                        return true;
                }
                return false;
            }
        });

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.clearChatData();

        database = FirebaseDatabase.getInstance().getReference("Conference").child("notifications");
        //reading from firebase
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    Chat chat = keyNode.getValue(Chat.class);
                    String newCell = chat.getCellphone();
                    String newName = chat.getName();
                    String newText = chat.getText();

                    if(newName != null && newCell != null){
                        //if(!newName.equalsIgnoreCase(name) && !newCell.equalsIgnoreCase(cellphone))
                        dbHelper.insertChatData(newName,newCell,newText);
                    }
                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                //Log.L(TAG, "Failed to read value.", error.toException());

            }
        });

    }

    void loadChats(){
        chats.removeAllViews();
        DBHelper dbHelper = new DBHelper(this);
        Cursor res = dbHelper.getChatData();
        if(res.getCount() == 0){
            Toast.makeText(this, "No chats yet...", Toast.LENGTH_LONG).show();
        }else{
            while(res.moveToNext()){

                String newCell = res.getString(1);
                String newName = res.getString(0);
                String newText = res.getString(2);

                //if(!newName.equalsIgnoreCase(name) && !newCell.equalsIgnoreCase(cellphone))
                setChatUi(newName,newCell,newText);
            }
            if(!res.moveToNext()){
                dbHelper.clearChatData();
            }
        }
    }

    void setChatUi(String name,String cellphone,String text){
        //Outer layout
        LinearLayout outerBox = new LinearLayout(this);
        LinearLayout.LayoutParams outerBoxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        outerBoxParams.setMargins(10,10,10,10);
        outerBox.setOrientation(LinearLayout.VERTICAL);
        outerBox.setLayoutParams(outerBoxParams);
        outerBox.setBackgroundResource(R.drawable.rounded_coners);//bubble UI

        //name text view
        TextView textViewName = new TextView(this);
        LinearLayout.LayoutParams textViewNameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textViewNameParams.setMargins(10,10,10,0);
        textViewName.setLayoutParams(textViewNameParams);
        textViewName.setTextColor(Color.BLACK);
        textViewName.setTextSize(23f);
        textViewName.setTypeface(Typeface.DEFAULT_BOLD);//bold style
        textViewName.setText(name);

        outerBox.addView(textViewName);

        //chat textView
        TextView txtViewCell = new TextView(this);
        LinearLayout.LayoutParams textViewCellParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textViewCellParams.setMargins(10,0,10,10);
        txtViewCell.setLayoutParams(textViewCellParams);
        txtViewCell.setTextColor(Color.BLACK);
        txtViewCell.setTextSize(23f);
        //txtViewCell.setTypeface(Typeface.DEFAULT_BOLD);//bold style
        txtViewCell.setText(text);

        outerBox.addView(txtViewCell);

        chats.addView(outerBox);

        /*scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },1000);*/

        //when clicking the name
        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDialog("Name : "+name+"\nCellphone : "+cellphone,Anouncement.this);
            }
        });
    }

    void popupDialog(String message, Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Dismiss",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Starting whatsapp conversation.
                        //String url = "https://api.whatsapp.com/send?phone="+number;
                        //Intent i = new Intent(Intent.ACTION_VIEW);
                        //i.setData(Uri.parse(url));
                        //startActivity(i);
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //Create event
    public void openCreate(View v){
        sendChat.setVisibility(View.VISIBLE);
    }

    public void sedChat(View v){
        //send to firebase
        String text = etChat.getText().toString();

        if(!text.equalsIgnoreCase("") && text != null){
            // Write a message to the database
            String id = database.push().getKey();
            Chat obj = new Chat(name,cellphone,text);

            database.child(id).setValue(obj);
            etChat.setText("");
            sendChat.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Message Posted", Toast.LENGTH_LONG).show();
        }

        //sve to sqlite
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.insertChatData(name,cellphone,text);

        //reload UI
        loadChats();
        //setChatUi(name,cellphone,text);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        sendChat.setVisibility(View.INVISIBLE);
    }
}