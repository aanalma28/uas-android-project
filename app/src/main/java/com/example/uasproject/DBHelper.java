package com.example.uasproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Educa";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE session (id integer PRIMARY KEY, login text)");
        db.execSQL("CREATE TABLE user (id integer PRIMARY KEY AUTOINCREMENT, name text, email text, password text, email_verified boolean DEFAULT false, address text DEFAULT '', phone text DEFAULT '', role text DEFAULT 'default', agency text DEFAULT '')");
        db.execSQL("INSERT INTO session(id, login) VALUES (1, 'kosong')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS session");
        db.execSQL("DROP TABLE IF EXISTS user");
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
