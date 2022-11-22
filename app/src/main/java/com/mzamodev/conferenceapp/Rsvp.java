package com.mzamodev.conferenceapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Rsvp extends AppCompatActivity implements EventsInterface{


    //firebase stuff
    DatabaseReference database;

    //event variables
    String eventName,eventDescription,time,date,location,price,eventCellphone;

    //user data
    String name,cellphone,studentNo,type;

    //event views
    RecyclerView eventsView;
    List<Events> eventList;
    EventAdapter eventAdapter;

    //purchase window code
    LinearLayout purchaseWindow,qrWindow;
    //TextView txtPrice,txtDescription,txtEventName,txtLocation,txtTime,txtDate;
    EditText guests;
    String pPrice,pDescription,pEventName,pLocation,pTime,pDate,pGuests;
    ImageView qrImage;

    //event overview
    TextView txtEventName,txtEventDate,txtPrice,txtGuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsvp);

        qrWindow = (LinearLayout) findViewById(R.id.qrWindow);
        qrImage = (ImageView) findViewById(R.id.qrCode);

        //event overview
        txtEventName = (TextView) findViewById(R.id.txtEventName);
        txtEventDate = (TextView) findViewById(R.id.txtEventDate);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtGuests = (TextView) findViewById(R.id.txtEventGuests);

        //get parsed user info
        name = getIntent().getStringExtra("name");
        cellphone = getIntent().getStringExtra("cellphone");
        type = getIntent().getStringExtra("type");
        studentNo = getIntent().getStringExtra("studentNo");

        //initializing bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //set Home tab as selected tab
        bottomNavigationView.setSelectedItemId(R.id.rsvp);

        //perform item selected listeners
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.anouncements:
                        Intent open = new Intent(getApplicationContext(),Anouncement.class);
                        //open.putExtra("subject","maths");
                        open.putExtra("name",name);
                        open.putExtra("cellphone",cellphone);
                        open.putExtra("studentNo",studentNo);
                        open.putExtra("type",type);
                        open.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open);
                        return true;
                    case R.id.aboutBtn:
                        Intent open1 = new Intent(getApplicationContext(),About.class);
                        //open1.putExtra("email",email);
                        open1.putExtra("name",name);
                        open1.putExtra("cellphone",cellphone);
                        open1.putExtra("studentNo",studentNo);
                        open1.putExtra("type",type);
                        open1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open1);
                        return true;
                    case R.id.homeBtn:
                        Intent open2 = new Intent(getApplicationContext(),Home.class);
                        //open1.putExtra("email",email);
                        open2.putExtra("name",name);
                        open2.putExtra("cellphone",cellphone);
                        open2.putExtra("studentNo",studentNo);
                        open2.putExtra("type",type);
                        open2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(open2);
                        return true;
                }
                return false;
            }
        });

        getEvent();

    }

    //firebase database read
    void getEvent(){

        //clear database tables
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.clearAllEvents();
        //dbHelper.clearUser();

        if(type.equalsIgnoreCase("user")){
            //firebase Stuff
            database = FirebaseDatabase.getInstance().getReference("Conference").child("rsvp");
        }else if(type.equalsIgnoreCase("admin")){
            //firebase Stuff
            database = FirebaseDatabase.getInstance().getReference("Conference").child("events");
        }

        //reading from firebase
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    //progress.setVisibility(View.INVISIBLE);//hide loading

                    Events events = keyNode.getValue(Events.class);
                    eventName = events.getEventName();
                    eventDescription = events.getEventDescription();
                    time = events.getEventTime();
                    date = events.getEventDate();
                    eventCellphone = events.getCellphone();
                    location = events.getLocation();
                    price = events.getPrice();
                    pGuests =  events.getGuest();

                    dbHelper.insertEventData(eventName,eventDescription,date,time,eventCellphone,location,price,pGuests);

                }
                showEvents();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                //Log.L(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), "Server connection Error", Toast.LENGTH_LONG).show();
                //progress.setVisibility(View.INVISIBLE);

            }
        });

    }

    //show events
    void showEvents(){

        //chatLayout.removeAllViews();
        DBHelper dbHelper = new DBHelper(Rsvp.this);
        Cursor res = dbHelper.getAllEvents();
        if(res.getCount() == 0){
            Toast.makeText(this, "No Events yet...", Toast.LENGTH_LONG).show();
        }else{
            while(res.moveToNext()){

                String newEventName = res.getString(0);
                String newEventDescription = res.getString(1);
                String newEventTime = res.getString(3);
                String newEventDate = res.getString(2);
                String newEventPrice = res.getString(4);
                String newEventCellphone = res.getString(5);
                String newEventLocation = res.getString(6);
                String newEventGuest = res.getString(7);

                if(type.equalsIgnoreCase("admin")){

                    if(newEventCellphone != null){
                        if ( newEventCellphone.equalsIgnoreCase(cellphone)) {
                            eventsView = (RecyclerView) findViewById(R.id.eventsView);
                            eventsView.setHasFixedSize(true);
                            eventsView.setLayoutManager(new GridLayoutManager(this, 1));
                            Events events = new Events(newEventName, newEventDescription, newEventTime, newEventDate, newEventLocation, newEventPrice, newEventCellphone, newEventGuest);
                            eventList = new ArrayList<>();
                            eventList.add(events);
                            eventAdapter = new EventAdapter(this, eventList, this);
                            eventsView.setAdapter(eventAdapter);
                        }
                    }else {
                        Toast.makeText(this, "You don't have any events posted. post an event in home Tab", Toast.LENGTH_LONG).show();
                    }

                }else if(type.equalsIgnoreCase("user")){

                    if(newEventCellphone.equalsIgnoreCase(cellphone)){
                        eventsView = (RecyclerView) findViewById(R.id.eventsView);
                        eventsView.setHasFixedSize(true);
                        eventsView.setLayoutManager(new GridLayoutManager(this,1));
                        Events events = new Events(newEventName,newEventDescription,newEventTime,newEventDate,newEventLocation,newEventPrice,newEventCellphone,newEventGuest);
                        eventList = new ArrayList<>();
                        eventList.add(events);
                        eventAdapter = new EventAdapter(this,eventList,this);
                        eventsView.setAdapter(eventAdapter);
                    }else {
                        Toast.makeText(this, "You don't have any events booked please go to home page and RSVP", Toast.LENGTH_LONG).show();
                    }

                }

            }
            if(!res.moveToNext()){
                dbHelper.clearAllEvents();
            }
        }

    }

    //firebase database read
    void getRSVPData(){

        //clear database tables
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.clearAllEvents();
        //dbHelper.clearUser();

        //firebase Stuff
        database = FirebaseDatabase.getInstance().getReference("Conference").child("rsvp");
        //reading from firebase
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    //progress.setVisibility(View.INVISIBLE);//hide loading

                    Events events = keyNode.getValue(Events.class);
                    eventName = events.getEventName();
                    eventDescription = events.getEventDescription();
                    time = events.getEventTime();
                    date = events.getEventDate();
                    eventCellphone = events.getCellphone();
                    location = events.getLocation();
                    price = events.getPrice();
                    pGuests = events.getGuest();

                    dbHelper.insertEventData(eventName,eventDescription,date,time,eventCellphone,location,price,pGuests);

                }
                getRsvpUser();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                //Log.L(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), "Server connection Error", Toast.LENGTH_LONG).show();
                //progress.setVisibility(View.INVISIBLE);

            }
        });

    }

    //rsvp data control
    private void getRsvpUser() {

        double totalFinal = 0.0;
        int finalGuests = 0;
        //chatLayout.removeAllViews();
        DBHelper dbHelper = new DBHelper(Rsvp.this);
        Cursor res = dbHelper.getAllEvents();
        if(res.getCount() == 0){
            Toast.makeText(this, "No Events yet...", Toast.LENGTH_LONG).show();
        }else{
            while(res.moveToNext()){

                String newEventName = res.getString(0);
                String newEventDescription = res.getString(1);
                String newEventTime = res.getString(3);
                String newEventDate = res.getString(2);
                String newEventPrice = res.getString(4);
                String newEventCellphone = res.getString(5);
                String newEventLocation = res.getString(6);
                String newEventGuest = res.getString(7);

                int g = Integer.parseInt(newEventGuest);
                double p = Double.parseDouble(newEventPrice);

                double t = p * g;
                totalFinal =  totalFinal + t;
                finalGuests = finalGuests + g;

                //Toast.makeText(this, "You don't have any events posted. post an event in home Tab", Toast.LENGTH_LONG).show();

            }
            if(!res.moveToNext()){
                dbHelper.clearAllEvents();
                popupDialog("Total Guest : "+finalGuests+"\nRevenue : "+totalFinal,Rsvp.this);
            }
        }

    }


    @Override
    public void onItemClick(int position) {
        pPrice = eventList.get(position).getPrice();
        pDescription = eventList.get(position).getEventDescription();
        pLocation = eventList.get(position).getLocation();
        pEventName = eventList.get(position).getEventName();
        pTime = eventList.get(position).getEventTime();
        pDate = eventList.get(position).getEventDate();

        if(type.equalsIgnoreCase("user")){

            String newDate = pDate +" "+pTime;

            txtEventName.setText(pEventName);
            txtPrice.setText(pEventName);
            txtGuests.setText("Guests: "+pGuests);
            txtEventDate.setText(newDate);

            //show QR code
            String qrValue = "Conference App event admission";
            // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
            QRGEncoder qrgEncoder = new QRGEncoder(qrValue, null, QRGContents.Type.TEXT, 10);
            qrgEncoder.setColorBlack(Color.RED);
            qrgEncoder.setColorWhite(Color.BLUE);
            try {
                // Getting QR-Code as Bitmap
                Bitmap bitmap = qrgEncoder.getBitmap();
                // Setting Bitmap to ImageView
                qrImage.setImageBitmap(bitmap);
                qrWindow.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }

        }else if(type.equalsIgnoreCase("admin")){

            getRSVPData();

        }

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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        qrWindow.setVisibility(View.INVISIBLE);
    }
}