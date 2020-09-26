package com.uabn.gss.uabnlink.DatabaseTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class UABNSQLitedatabase extends SQLiteOpenHelper {
    public static final String Database_Name = "UABNDatabase.db";
    public static final String TABLE_NAME = "HOMEPOSTLIST";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "PROFILEPIC";
    public static final String COL_3 = "TITLE";
    public static final String COL_4 = "DATE";
    public static final String COL_5 = "DESCRIPTION";
    public static final String COL_6 = "POSTIMAGE";
//    public static final String COL_7 = "POSTVIDEO";
////    public static final String COL_8 = "POSTIFRAME";
    public static final String COL_7="PROFILE_ID";
    public static final String COL_8= "POSTIMAGEID";
    public UABNSQLitedatabase( Context context) {
//        super(context, name, factory, version);
        super(context, Database_Name, null, 7);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID TEXT PRIMARY KEY,PROFILEPIC TEXT,TITLE TEXT,DATE TEXT,DESCRIPTION TEXT,POSTIMAGE TEXT ,POSTVIDEO TEXT,POSTIFRAME TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<newVersion){
            String s= "DROP TABLE IF EXISTS " + TABLE_NAME ;
            String a= "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME + "(ID TEXT PRIMARY KEY,PROFILEPIC TEXT,TITLE TEXT,DATE TEXT,DESCRIPTION TEXT,POSTIMAGE TEXT ,POSTVIDEO TEXT,POSTIFRAME TEXT)" ;

        }
    }

    public boolean insertData(String user_id, String profile_pic,String title,String date,String description,String postimage,String profile_id ,String postimage_id) {

        String s= "DROP TABLE IF EXISTS " + TABLE_NAME ;
        String a= "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME + "(ID TEXT PRIMARY KEY,PROFILEPIC TEXT,TITLE TEXT,DATE TEXT,DESCRIPTION TEXT,POSTIMAGE TEXT ,POSTVIDEO TEXT,POSTIFRAME TEXT)" ;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(s);
        db.execSQL(a);
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, user_id);
        contentValues.put(COL_2, profile_pic);
        contentValues.put(COL_3,title);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5,description);
        contentValues.put(COL_6,postimage);
//        contentValues.put(COL_7, postvideo);
//        contentValues.put(COL_8,PostIframe);
        contentValues.put(COL_7,profile_id);
        contentValues.put(COL_8,postimage_id);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllData(String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE " + COL_1 + " = " + userid, null);
        return res;
    }
    public Integer delete(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID=?", new String[]{user_id});
    }




}
