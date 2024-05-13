package com.example.uasproject;

import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "db_bimbel.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, email TEXT, password TEXT, role TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Posts (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course TEXT, price INTEGER, location TEXT, instructor TEXT, FOREIGN KEY (user_id) REFERENCES Users(id))");

        db.execSQL("CREATE TABLE session (id integer PRIMARY KEY, login text)");

        db.execSQL("INSERT INTO session(id, login) VALUES (1, 'kosong')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Posts");
        db.execSQL("DROP TABLE IF EXISTS session");
        onCreate(db);
    }


    // check session
    public Boolean checkSession(String value){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM session WHERE login = ?", new String[]{value});
        if (cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    // upgrade session
    public Boolean upgradeSession(String value, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login", value);
        long update = db.update("session", values, "id- "+id, null);
        if (update == -1){
            return false;
        }else{
            return true;
        }
    }

    //input user
    public Boolean saveUser(String nama, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", nama);
        values.put("email", email);
        values.put("password", password);
        long insert = db.insert("user", null, values);
        if (insert == -1){
            return false;
        }else{
            return true;
        }
    }

    // check login
    public Boolean checkLogin(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

}
