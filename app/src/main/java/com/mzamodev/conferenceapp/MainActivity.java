package com.mzamodev.conferenceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    boolean signup = false;

    Object userDataObj = null;
    EditText etExamNumber,etPassword,etName,etExamNumberNew,etCell,etSchool,etprovince;
    String fName,studentNo,cellphone,newPassword;
    LinearLayout password_reset_layout,signUP_layout,verify_layout,new_password_layout,progress;
    //Account Type controls
    String type= "user";
    RadioButton rbUser,rbAdmin;


    //verification and reset
    EditText etVeriCode;

    //new password
    EditText etNewPass,etNewPass2;

    RelativeLayout rootLayout;

    //firebase stuff
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Keep Screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //binding = ActivityLocationBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        //input fields 'login Layout'
        etExamNumber = (EditText) findViewById(R.id.etExamNumber);
        etPassword = (EditText) findViewById(R.id.eTPassword);

        //input  fields for 'SignUp layout'
        etName = (EditText) findViewById(R.id.etFName);
        etExamNumberNew = (EditText) findViewById(R.id.etExamNumerNew);
        etCell = (EditText) findViewById(R.id.etCell);

        //Account Type
        rbAdmin = (RadioButton) findViewById(R.id.rbAdmin);
        rbUser = (RadioButton) findViewById(R.id.rbUser);

        //etSchool = (EditText) findViewById(R.id.etSchool);
        //tprovince = (EditText) findViewById(R.id.etProvince);

        //verification and reset inputs
        etVeriCode = (EditText) findViewById(R.id.etVeriCode);

        //new password inputs
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etNewPass2 = (EditText) findViewById(R.id.etNewPass2);

        //layouts
        password_reset_layout = (LinearLayout) findViewById(R.id.pswdReset_layout);
        new_password_layout = (LinearLayout) findViewById(R.id.password_layout);
        signUP_layout = (LinearLayout) findViewById(R.id.registration_layout);
        verify_layout = (LinearLayout) findViewById(R.id.verify_layout);
        progress = (LinearLayout) findViewById(R.id.progress);

        rootLayout = (RelativeLayout) findViewById(R.id.roorLayout);

        progress.setVisibility(View.VISIBLE);//show load ding

        //clear database tables
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        dbHelper.clearUser();

        rbUser.setChecked(true);

        Toast.makeText(getApplicationContext(), "Connecting to servers please wait", Toast.LENGTH_LONG).show();


        //firebase Stuff
        database = FirebaseDatabase.getInstance().getReference("Conference").child("users");
        //reading from firebase
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    progress.setVisibility(View.INVISIBLE);//hide loading
                    //Learners learners = keyNode.getValue(Learners.class);
                    Users users = keyNode.getValue(Users.class);
                    String newCell = users.getCellphone();
                    String newName = users.getFName();
                    String studentNo = users.getStudentNo();
                    String type = users.getType();
                    String password = users.getPassword();

                    if(newName != null && newCell != null){
                        //if(!newName.equalsIgnoreCase(name) && !newCell.equalsIgnoreCase(cellphone))
                        dbHelper.updateDataLogin(studentNo,password,newName,newCell,type);
                    }
                }

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

    //login button
    public void login(View v){
        //perform something when the login button is pressed
        String examNumber = etExamNumber.getText().toString();
        String pass = etPassword.getText().toString();
        progress.setVisibility(View.VISIBLE);

        if(!examNumber.equalsIgnoreCase("") && !pass.equalsIgnoreCase("")){

            boolean isFound = getLearner(examNumber,pass);
            if(isFound){
                //user is found
                openHome();
                //checkSubscription();
            }else{
                Toast.makeText(this, "User Not Found", Toast.LENGTH_LONG).show();
            }

        }else{

        }
    }
    //login from sqlite
    boolean getLearner(String examNumber,String pass){
        boolean found = false;
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        Cursor res = dbHelper.getUserDataLogin(examNumber);
        if(res.getCount() == 0){
            //later change to connection error
            Toast.makeText(this, "No users yet...", Toast.LENGTH_LONG).show();
        }else{
            while(res.moveToNext()){

                String newStudentNo = res.getString(0);
                String password = res.getString(1);
                String newName = res.getString(2);
                String newCell = res.getString(3);
                String newType = res.getString(4);
                //String province = res.getString(5);

                if(newStudentNo.equalsIgnoreCase(examNumber) && password.equalsIgnoreCase(pass)){
                    dbHelper.clearUserLogin();
                    dbHelper.updateDataLogin(newStudentNo,password,newName,newCell,newType);
                    //fName,studentNo,cellphone,newPassword;
                    fName = newName;
                    studentNo = newStudentNo;
                    cellphone = newCell;
                    type = newType;
                    password = newPassword;
                    found = true;
                }else {
                    found = false;
                }
            }
        }
        return found;
    }

    //SignUp button
    public void signUp(View v){
        //perform something when the signUp button is clicked
        signUP_layout.setVisibility(View.VISIBLE);
    }

    public void moveToVerify(View v){
        //clicking next on the signup screen.
        boolean move = verify_inputs();
        if(move){
            //verify_layout.setVisibility(View.VISIBLE);
            new_password_layout.setVisibility(View.VISIBLE);
            signUP_layout.setEnabled(false);
            signup = true;
            //send verification code
        }else{
            //error check all entered info.
        }
    }

    boolean verify_inputs(){
        boolean allGood = false;
        //!fName.equalsIgnoreCase("") && emailNew,cellphone,idNumber,carName,carYear,carRegistration;
        fName = etName.getText().toString();
        studentNo = etExamNumberNew.getText().toString();
        cellphone = etCell.getText().toString();
        //school  = etSchool.getText().toString();
        //province =  etprovince.getText().toString();

        if(!fName.equalsIgnoreCase("") && !studentNo.equalsIgnoreCase("") && !cellphone.equalsIgnoreCase("")){
            allGood = true;
        }else {
            allGood = false;
        }
        return allGood;
    }

    public void newPass(View v){
        String pass1 = etNewPass.getText().toString();
        String pass2 = etNewPass2.getText().toString();

        if(!pass1.equalsIgnoreCase("") && !pass2.equalsIgnoreCase("")){
            if(pass1.equalsIgnoreCase(pass2)){
                newPassword = pass1;
                if(signup){
                    //save everything to database
                    boolean doesExist = checkFortLearner(studentNo);
                    if(!doesExist){
                        //save learner data in firebase
                        String id = database.push().getKey();
                        //Learners learners = new Learners(fName,studentNo,cellphone,type,newPassword);

                        Users users = new Users(fName,studentNo,cellphone,type,newPassword);
                        database.child(id).setValue(users);

                        //data save move rto next screen
                        openHome();
                        //checkSubscription();
                    }else{
                        //already registered
                        Snackbar.make(rootLayout, "Student Already registered please login", Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                    }
                }else{
                    //save new password to database
                }

            }else {
                //password doesn't match
                Snackbar.make(rootLayout, "Password Doesn't match", Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show();
            }
        }
    }

    //signup from sqlite
    boolean checkFortLearner(String examNumber){
        boolean found = false;
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        Cursor res = dbHelper.getUserDataLogin(examNumber);
        if(res.getCount() == 0){
            //later change to connection error
            Toast.makeText(this, "No users yet...", Toast.LENGTH_LONG).show();
        }else{
            while(res.moveToNext()){

                String newStudentNo = res.getString(0);
                String password = res.getString(1);
                String newName = res.getString(2);
                String newCell = res.getString(3);
                String newType = res.getString(4);
                //String province = res.getString(5);

                if(newStudentNo.equalsIgnoreCase(examNumber)){
                    found = true;
                }else {
                    dbHelper.clearUserLogin();
                    dbHelper.updateData(examNumber,password,newName,newCell,type);
                    found = false;
                }
            }
        }
        return found;
    }

    void openHome(){
        Intent open = new Intent(MainActivity.this,Home.class);
        open.putExtra("name",fName);
        open.putExtra("cellphone",cellphone);
        open.putExtra("studentNo",studentNo);
        open.putExtra("type",type);
        startActivity(open);
    }

    //account type
    public void rbUserClick(View v){
        rbUser.setChecked(true);
        rbAdmin.setChecked(false);
        type = "user";
    }
    public void rbAdminClick(View v){
        rbUser.setChecked(false);
        rbAdmin.setChecked(true);
        type = "admin";
    }
    
    @Override
    public void onBackPressed() {
        signUP_layout.setVisibility(View.INVISIBLE);
        //super.onBackPressed();
    }

}