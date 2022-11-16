package com.mzamodev.conferenceapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTabHost;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.annotations.NotNull;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements EventsInterface{

    private static final String YOUR_CLIENT_ID = "AU9HkARClIyAlkEwPpEhJjLKoybGzz6b7BGggwM62SMvbGIdlOA3tWq8b2Dv_1dpG0J83ZXGvcrBl3hf";
    //event views
    RecyclerView eventsView;
    List<Events> eventList;
    EventAdapter eventAdapter;

    //firebase stuff
    DatabaseReference database;

    LinearLayout progress;

    //event variables
    String eventName,eventDescription,time,date,location,price,eventCellphone;

    //user data
    String name,cellphone,studentNo,type;

    LinearLayout createEvent,floatingUI;

    //event creation variables
    EditText etEventName,etEventDescription,etLocation,etPrice,etTheme;
    String createTime = "",createDate = "";
    RadioButton rbTime,rbDate;

    //purchase window code
    LinearLayout purchaseWindow;
    TextView txtPrice,txtDescription,txtEventName,txtLocation,txtTime,txtDate;
    EditText guests;
    String pPrice,pDescription,pEventName,pLocation,pTime,pDate,pGuests;

    PaymentButtonContainer paymentButtonContainer;

    LinearLayout paypal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progress = (LinearLayout) findViewById(R.id.progress);
        createEvent = (LinearLayout) findViewById(R.id.createEventView);
        floatingUI  = (LinearLayout) findViewById(R.id.floatingUI);
        etEventDescription = (EditText) findViewById(R.id.eventDescription);
        etEventName = (EditText) findViewById(R.id.eventName);
        etLocation = (EditText) findViewById(R.id.eventAddress);
        etPrice = (EditText) findViewById(R.id.eventPrice);
        rbTime = (RadioButton) findViewById(R.id.rbTime);
        rbDate = (RadioButton) findViewById(R.id.rbDate);

        //purchase UI
        purchaseWindow = (LinearLayout) findViewById(R.id.purchaseView);
        paypal = (LinearLayout) findViewById(R.id.paypal);
        txtPrice = (TextView) findViewById(R.id.txtItemPrice);
        txtDescription = (TextView) findViewById(R.id.txtEventDescription);
        txtEventName = (TextView) findViewById(R.id.txtEventName);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDate = (TextView) findViewById(R.id.txtDate);
        guests = (EditText) findViewById(R.id.txtItemQuantity);

        //etTheme = (EditText) findViewById(R.id.floatingUI);

        //initializing bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //set Home tab as selected tab
        bottomNavigationView.setSelectedItemId(R.id.homeBtn);

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
        getEvent();

        initPaypal();
    }

    //firebase database read
    void getEvent(){

        //clear database tables
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.clearAllEvents();
        //dbHelper.clearUser();

        //firebase Stuff
        database = FirebaseDatabase.getInstance().getReference("Conference").child("events");
        //reading from firebase
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    progress.setVisibility(View.INVISIBLE);//hide loading

                    Events events = keyNode.getValue(Events.class);
                    eventName = events.getEventName();
                    eventDescription = events.getEventDescription();
                    time = events.getEventTime();
                    date = events.getEventDate();
                    eventCellphone = events.getCellphone();
                    location = events.getLocation();
                    price = events.getPrice();

                    dbHelper.insertEventData(eventName,eventDescription,date,time,eventCellphone,location,price);

                }
                showEvents();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                //Log.L(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), "Server connection Error", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.INVISIBLE);

            }
        });

    }

    //show events
    void showEvents(){


        //chatLayout.removeAllViews();
        DBHelper dbHelper = new DBHelper(Home.this);
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

                eventsView = (RecyclerView) findViewById(R.id.eventsView);
                eventsView.setHasFixedSize(true);
                eventsView.setLayoutManager(new GridLayoutManager(this,1));
                Events events = new Events(newEventName,newEventDescription,newEventTime,newEventDate,newEventLocation,newEventPrice,newEventCellphone);
                eventList = new ArrayList<>();
                eventList.add(events);
                eventAdapter = new EventAdapter(this,eventList,this);
                eventsView.setAdapter(eventAdapter);
            }
            if(!res.moveToNext()){
                dbHelper.clearAllEvents();
            }
        }

    }

    //Create event
    public void openCreate(View v){
        createEvent.setVisibility(View.VISIBLE);
    }

    //post event on firebase
    public void postEvent(View v){
        String eName = etEventName.getText().toString();
        String eDescription = etEventDescription.getText().toString();
        String eLocation = etLocation.getText().toString();
        String ePrice = etPrice.getText().toString();

        String id = "event"+cellphone +createDate+createTime;
        //push to firebase
        Events events = new Events(eName,eDescription,createTime,createDate,eLocation,ePrice,cellphone);
        database.child(id).setValue(events);

        Toast.makeText(this, "Event Uploaded successfully", Toast.LENGTH_LONG).show();
        createEvent.setVisibility(View.INVISIBLE);
    }

    //pick date
    public void selectDate(View v){
        dateTimePicker();
    }

    public void selectTime(View v){
        dateTimePicker();
    }

    void dateTimePicker(){
        // Initialize
        SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Conference Date Time",
                "OK",
                "Cancel"
        );

// Assign values
        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(2022, Calendar.MARCH, 4, 15, 20).getTime());
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
        final SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

// Define new day and month format
        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

// Set listener
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                // Date is get on positive button click
                createDate = myDateFormat.format(date);
                createTime = myTimeFormat.format(date);

                //set radioButtons
                rbTime.setText(createTime);
                rbDate.setText(createDate);
                // Do something
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
            }

        });

// Show
        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
    }

    public void checkout(View v){
        //payment processing
        int going = Integer.parseInt(guests.getText().toString());
        double priceDec = Double.parseDouble(pPrice);
        double tot = priceDec * going;
        String newPricefinal = ""+tot;

        paymentButtonContainer.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(newPricefinal)
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                database = FirebaseDatabase.getInstance().getReference("Conference").child("rsvp");
                                String eName = etEventName.getText().toString();
                                String eDescription = etEventDescription.getText().toString();
                                String eLocation = etLocation.getText().toString();
                                String ePrice = etPrice.getText().toString();

                                String id = "event"+cellphone +createDate+createTime;
                                //push to firebase
                                Events events = new Events(eName,eDescription,createTime,createDate,eLocation,ePrice,cellphone);
                                database.child(id).setValue(events);

                                Toast.makeText(Home.this, "RSVP SUCCESSFUL", Toast.LENGTH_LONG).show();
                                createEvent.setVisibility(View.INVISIBLE);
                                paypal.setVisibility(View.INVISIBLE);
                                purchaseWindow.setVisibility(View.INVISIBLE);
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                            }
                        });
                    }
                }
        );

        paypal.setVisibility(View.VISIBLE);

    }

    public void initPaypal(){
        PayPalCheckout.setConfig(new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
                Environment.SANDBOX,
                BuildConfig.APPLICATION_ID + "://paypalpay",
                CurrencyCode.USD,
                UserAction.PAY_NOW
        ));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        createEvent.setVisibility(View.INVISIBLE);
        purchaseWindow.setVisibility(View.INVISIBLE);
        paypal.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        pPrice = eventList.get(position).getPrice();
        pDescription = eventList.get(position).getEventDescription();
        pLocation = eventList.get(position).getLocation();
        pEventName = eventList.get(position).getEventName();
        pTime = eventList.get(position).getEventTime();
        pDate = eventList.get(position).getEventDate();

        //set up UI
        txtPrice.setText("PRICE: $"+pPrice);
        txtDescription.setText(pDescription);
        txtDate.setText("DATE"+pDate);
        txtTime.setText("TIME"+pTime);
        txtLocation.setText(pLocation);
        txtEventName.setText(pEventName);

        //show pay view
        purchaseWindow.setVisibility(View.VISIBLE);
    }
}