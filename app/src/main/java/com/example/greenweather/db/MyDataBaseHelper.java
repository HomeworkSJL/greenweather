package com.example.greenweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/9/3.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_PROVINCE = "create table Province ("
            +"id integer primary key autoincrement,"
            +"provincename text)";

    public static final String CREATE_CITY = "create table City ("
            +"id integer primary key autoincrement,"
            +"cityname text,"
            +"provinceid integer)";
    public static final String CREATE_COUNTY = "create table County ("
            +"id integer primary key autoincrement,"
            +"countyname text,"
            +"weatherid text,"
            +"cityid integer)";
    private Context mContext;
    public MyDataBaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version){
        super(context, name ,factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
       // db.execSQL(CREATE_PROVINCE);
       // db.execSQL(CREATE_CITY);
       // db.execSQL(CREATE_COUNTY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

}
