package com.example.yellow.birthdayreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yellow on 2017-12-10.
 */

public class MyDB extends SQLiteOpenHelper{
    private static final String DB_NAME="Contacts_db";
    private static final String TABLE_NAME="Contacts";
    private static final int DB_VERSION=1;

    //private SQLiteDatabase db;

    public MyDB(Context context,String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        //getDB(context);
        //createTable();

        //insertExample();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //db.execSQL("drop table "+TABLE_NAME);
        String CREATE_TABLE="create table if not exists "+TABLE_NAME
                +" (_id integer primarily key,"
                +" name text,"
                +" birthday text,"
                +" gift text)";
        db.execSQL(CREATE_TABLE);

        //createTable();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        //do nothing
        //可以执行alter等表更新操作，不是数据更新，是属性
    }
    /*public void getDB(Context context){
        db=SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().toString()+"/"+DB_NAME,null);
    }
    public void createTable(){//SQLiteDatabase db
        String CREATE_TABLE="create table if not exists "+TABLE_NAME
                +" (_id integer primarily key,"
                +" name text"
                +" birthday text"
                +" gift text)";
        db.execSQL(CREATE_TABLE);
    }*/
    public void insert(String name,String birth,String gift){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("birthday",birth);
        values.put("gift",gift);
        db.insert(TABLE_NAME,null,values);
        db.close();
        //db.execSQL("insert into "+TABLE_NAME+" values(?,?,?)",new String[] {name,birth,gift});
    }
    public void update(String name,String birth,String gift){
        SQLiteDatabase db=getWritableDatabase();
        String whereClause="name = ?";
        String[] whereArgs={name};
        ContentValues values=new ContentValues();
        values.put("birthday",birth);
        values.put("gift",gift);
        db.update(TABLE_NAME,values,whereClause,whereArgs);
        db.close();
        /*db.execSQL("update table "+TABLE_NAME
                +" set name="+newName
                +" ,birth="+birth
                +" ,gift="+gift
                +" where name="+oldName);*/
    }
    public void delete(String name){
        SQLiteDatabase db=getWritableDatabase();
        String whereClause="name = ?";
        String[] whereArgs={name};
        db.delete(TABLE_NAME,whereClause,whereArgs);
        //db.execSQL("delete from "+TABLE_NAME+" where name=?",whereArgs);
    }
    public Cursor getAllInfo(){
        SQLiteDatabase db=getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);
    }
    public void insertExample(){
        delete("大眼萌");
        insert("大眼萌","2011.11.11","香蕉");
        //insert("","2011.11.11","香蕉");
    }
    public Cursor getAllofOne(String name){
        SQLiteDatabase db=getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME+" where name=?",new String[]{name});
    }

}
