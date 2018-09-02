package com.example.manisha.travelplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

   public static final String DATABASE_NAME = "MyDBName.db";

   private HashMap hp;
   Context context;

   public DBHelper(Context context) {
      super(context, DATABASE_NAME , null, 1);
      this.context = context;
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      db.execSQL(
         "create table IF not EXISTS trips " +
         "(trip_name text primary key,place text not null,start_date text not null, end_date text not null)"
      );

      db.execSQL(
              "create table IF not EXISTS users " +
                      "(user_name text primary key,user_password text not null)"
      );


      db.execSQL(
              "create table IF not EXISTS availability " +
                      "(id integer primary key AUTOINCREMENT, user_name text not null,trip_name text not null)"
      );

   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS trips");
      db.execSQL("DROP TABLE IF EXISTS availability");
      db.execSQL("DROP TABLE IF EXISTS users");
      onCreate(db);
   }

   public boolean insertTrip (String trip_name, String place, String start_date, String end_date) {
      
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("trip_name", trip_name);
      contentValues.put("start_date", start_date);
      contentValues.put("end_date", end_date);
      contentValues.put("place", place);

      db.insert("trips", null, contentValues);
      return true;
   }

   public boolean insertAvailability (String user_name, String trip_name) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("user_name", user_name);
      contentValues.put("trip_name", trip_name);

      db.insert("availability", null, contentValues);
      return true;
   }

   public int insertUser(String user_name, String password) {
      int authorised=0;
      if(!CheckIsTAlreadyInDBorNot("users", "user_name", user_name)){
         SQLiteDatabase db = this.getWritableDatabase();
         ContentValues contentValues = new ContentValues();
         contentValues.put("user_name", user_name);
         contentValues.put("user_password", password);
         db.insert("users", null, contentValues);
         Toast.makeText(context, "New User", Toast.LENGTH_SHORT).show();
         authorised = 1;
      }else if(CheckIsUserAuthorised("users", "user_name", user_name,
              "user_password", password)){
         authorised = 1;
         Toast.makeText(context, "Old User", Toast.LENGTH_SHORT).show();
      }else {
         authorised = 2;
         Toast.makeText(context, "Fake User", Toast.LENGTH_SHORT).show();
      }

      return authorised;
   }

   public  boolean CheckIsTAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue) {
      SQLiteDatabase sqldb = this.getReadableDatabase();
      String Query = "Select * from '" + TableName + "' where " + dbfield + " = '" + fieldValue+"'";
      Cursor cursor = sqldb.rawQuery(Query, null);
      if(cursor.getCount() <= 0){
         cursor.close();
         return false;
      }
      cursor.close();
      return true;
   }

   public  boolean CheckIsAvailabilityAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue,
                                                       String dbfield2, String fieldValue2) {
      SQLiteDatabase sqldb = this.getReadableDatabase();
      String Query = "Select * from '" + TableName + "' where " + dbfield + " = '" + fieldValue
              + "' AND " + dbfield2 + " = '" + fieldValue2+"'";
      Cursor cursor = sqldb.rawQuery(Query, null);
      if(cursor.getCount() <= 0){
         cursor.close();
         return false;
      }
      cursor.close();
      return true;
   }


   public  boolean CheckIsUserAuthorised(String TableName,
                                               String dbfield, String fieldValue,
                                         String dbfield2, String fieldValue2) {
      boolean matched = false;
      SQLiteDatabase sqldb = this.getReadableDatabase();
      String Query = "Select * from '" + TableName + "' where " + dbfield + " = '" + fieldValue + "'";

      Cursor cursor = sqldb.rawQuery(Query, null);
      cursor.moveToFirst();

      while(cursor.isAfterLast() == false){

         String password = cursor.getString(cursor.getColumnIndex("user_password"));
         if(password.equals(fieldValue2)){
            matched = true;
         }
         cursor.moveToNext();
      }
      cursor.close();
      return matched;
   }
   
   public  ArrayList<Availibility_Object> getUserAvailability(String trip_name){
      ArrayList<Availibility_Object> array_list = new ArrayList<>();
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor users =  db.rawQuery( "select * from 'users'", null );
      
      users.moveToFirst();
      while(users.isAfterLast() == false){
         boolean available =  CheckIsAvailabilityAlreadyInDBorNot("availability", "user_name",
                 users.getString(users.getColumnIndex("user_name")),"trip_name", trip_name);
         
            Availibility_Object trip_object= new Availibility_Object(
                         users.getString(users.getColumnIndex("user_name")),
                         available
            );
            
            array_list.add(trip_object);
         users.moveToNext();
      }
      users.close();
      return array_list;
   }

   
   public int getAvaiableCount(String trip_name) {
      String countQuery = "SELECT  * FROM 'availability' where " + "trip_name = '"+trip_name+"'" ;
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor cursor = db.rawQuery(countQuery, null);
      int count = cursor.getCount();
      cursor.close();
      return count;
   }

   public void deleteTrip(String trip_name){
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DELETE FROM 'trips' WHERE trip_name = '"+trip_name+"'");
      db.close();
   }


   public void deleteAvailability(String user_name, String trip_name){
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DELETE FROM 'availability' WHERE user_name = '" + user_name + "' AND trip_name = '"+trip_name+"'");
      db.close();
   }


   public ArrayList<Trip_Object> getAllTrips() {
      ArrayList<Trip_Object> array_list = new ArrayList<Trip_Object>();
      
      //hp = new HashMap();
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from 'trips'", null );
      res.moveToFirst();
      
      while(res.isAfterLast() == false){
         if(!CheckIsTAlreadyInDBorNot("availability","trip_name", res.getString(res.getColumnIndex("trip_name")))){
            deleteTrip(res.getString(res.getColumnIndex("trip_name")));
         }else {
            Trip_Object trip_object = new Trip_Object(
                    res.getString(res.getColumnIndex("trip_name")),
                    res.getString(res.getColumnIndex("place")),
                    res.getString(res.getColumnIndex("start_date")),
                    res.getString(res.getColumnIndex("end_date")),
                            String.valueOf(getAvaiableCount(res.getString(res.getColumnIndex("trip_name"))))
            );
            array_list.add(trip_object);
         }
         res.moveToNext();
      }
      res.close();
      return array_list;
   }

}