package com.vysiontech.sewagemonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="PersonalDetail.db";

    public static final String COL_1="id";
    public static final String COL_2="name";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE="CREATE TABLE name_Table(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlDrop="DROP TABLE IF EXISTS name_Table";
        db.execSQL(sqlDrop);
        onCreate(db);
    }

    public boolean addNAme(String name){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2,name);
        long result=db.insert("name_table",null,contentValues);
        db.close();
        if(result==-1){
            return false;
        }
        else
        {
            return true;
        }

    }
    public Cursor getData(){
        SQLiteDatabase db=getWritableDatabase();
        String path="Select * from name_Table";
        Cursor res=db.rawQuery(path,null);
        return res;
    }
}
