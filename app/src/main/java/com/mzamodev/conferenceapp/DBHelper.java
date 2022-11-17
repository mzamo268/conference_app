package com.mzamodev.conferenceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     */
    public DBHelper(Context context) {
        super(context,"conference.db",null,1);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table user(studentNo Text primary Key,password Text ,name Text,cellphone Text,type Text)");
        db.execSQL("create table event(eventName Text,eventDescription Text,date Text,time Text,price Text,cellphone Text,location Text,guests Text)");
        db.execSQL("create table chat(name Text,cellphone Text,message Text)");
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop Table if exists user");
        db.execSQL("drop Table if exists event");
        db.execSQL("drop Table if exists chat");
    }

    //event data control
    public Boolean insertEventData(String eventName,String eventDescription,String date,String time,String cellphone,String location,String price,String guests){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("eventName",eventName);
        contentValues.put("eventDescription",eventDescription);
        contentValues.put("date",date);
        contentValues.put("time",time);
        contentValues.put("cellphone",cellphone);
        contentValues.put("location",location);
        contentValues.put("price",price);
        contentValues.put("guests",guests);
        //contentValues.put("province",province);

        long results = db.insert("event",null,contentValues);

        if(results == -1){
            return false;
        }else {
            return true;
        }

    }
    public Cursor getAllEvents(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from event",null);
        //Cursor cursor = db.rawQuery("Select * from user where email = ?",new String[] {email});
        return cursor;

    }
    public void clearAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from event");
        db.close();
    }


    //user Data controls
    public Boolean insertDataLogin(String studentNo,String password,String name,String cellphone,String type){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("studentNo",studentNo);
        contentValues.put("password",password);
        contentValues.put("name",name);
        contentValues.put("cellphone",cellphone);
        contentValues.put("type",type);
        //contentValues.put("province",province);

        long results = db.insert("user",null,contentValues);

        if(results == -1){
            return false;
        }else {
            return true;
        }

    }
    public Boolean updateDataLogin(String studentNo,String password,String name,String cellphone,String type){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("name",name);
        contentValues.put("studentNo",studentNo);
        contentValues.put("password",password);
        contentValues.put("name",name);
        contentValues.put("cellphone",cellphone);
        contentValues.put("type",type);
        //contentValues.put("province",province);

        Cursor cursor = db.rawQuery("Select * from user where studentNo" +
                " = ?",new String[] {studentNo});

        if(cursor.getCount() > 0){

            long results = db.update("user",contentValues,"examNumber=?",new String[] {studentNo});

            if(results == -1){
                return false;
            }else {
                return true;
            }

        }else{
            insertData(studentNo,password,name,cellphone,type);
            return false;
        }

    }
    public Cursor getUserDataLogin(String studentNo){

        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery("Select * from user",null);
        Cursor cursor = db.rawQuery("Select * from user where studentNo = ?",new String[] {studentNo});
        return cursor;

    }
    public Cursor getUserInfoLogin(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from user",null);
        //Cursor cursor = db.rawQuery("Select * from user where email = ?",new String[] {email});
        return cursor;

    }
    public void clearUserLogin(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from user");
        db.close();
    }

    //user Data controls
    public Boolean insertData(String studentNo,String password,String name,String cellphone,String type){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("studentNo",studentNo);
        contentValues.put("password",password);
        contentValues.put("name",name);
        contentValues.put("cellphone",cellphone);
        contentValues.put("type",type);
        //contentValues.put("province",province);

        long results = db.insert("user",null,contentValues);

        if(results == -1){
            return false;
        }else {
            return true;
        }

    }
    public Boolean updateData(String studentNo,String password,String name,String cellphone,String type){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("name",name);
        contentValues.put("studentNo",studentNo);
        contentValues.put("password",password);
        contentValues.put("name",name);
        contentValues.put("cellphone",cellphone);
        contentValues.put("type",type);
        //contentValues.put("province",province);

        Cursor cursor = db.rawQuery("Select * from user where examNumber = ?",new String[] {studentNo});

        if(cursor.getCount() > 0){

            long results = db.update("user",contentValues,"examNumber=?",new String[] {studentNo});

            if(results == -1){
                return false;
            }else {
                return true;
            }

        }else{
            insertData(studentNo,password,name,cellphone,type);
            return false;
        }

    }
    public Cursor getUserData(String studentNo){

        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery("Select * from user",null);
        Cursor cursor = db.rawQuery("Select * from user where examNumber = ?",new String[] {studentNo});
        return cursor;

    }
    public Cursor getUserInfo(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from user",null);
        //Cursor cursor = db.rawQuery("Select * from user where email = ?",new String[] {email});
        return cursor;

    }
    public void clearUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from user");
        db.close();
    }

    //chat data controls
    public Boolean insertChatData(String name,String cellphone,String text){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("cellphone",cellphone);
        contentValues.put("message",text);


        long results = db.insert("chat",null,contentValues);

        if(results == -1){
            return false;
        }else {
            return true;
        }

    }
    public Cursor getChatData(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from chat",null);
        //Cursor cursor = db.rawQuery("Select * from user where name = ?",new String[] {modName});
        return cursor;

    }
    public void clearChatData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from chat");
        db.close();
    }


}
