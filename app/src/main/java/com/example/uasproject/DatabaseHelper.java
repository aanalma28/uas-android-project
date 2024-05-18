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
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, address TEXT DEFAULT null, phone NUMERIC DEFAULT null, agency TEXT DEFAULT null,  email TEXT, email_verified TEXT DEFAULT null, password TEXT, role TEXT DEFAULT 'Student')");


//
//        db.execSQL("CREATE TABLE IF NOT EXISTS posts (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "course TEXT, price INTEGER, location TEXT, instructor TEXT, FOREIGN KEY (user_id) REFERENCES Users(id))");
//
        db.execSQL("CREATE TABLE IF NOT EXISTS session (id INTEGER PRIMARY KEY, login TEXT)");
        String sql = "insert into session(id, login) VALUES (1, 'kosong')";
        db.execSQL(sql);


        db.execSQL("create table if not exists courses (course_id integer primary key autoincrement, " +
                "name text, instructor text, description text, status text, price numeric, image text, " +
                "foreign key(user_id) references users(id))");

        db.execSQL("create table if not exists orders (order_id integer primary key autoincrement, order_date date, " +
                "status text, foreign key(user_id) references users(id))");

        db.execSQL("create table if not exists order_detail (order_detali_id integer primary key autoincrement, price numeric, " +
                "payment_method text, foreign key(oder_id) references orders(order_id), foreign key(course_id) references courses(course_id))");

        db.execSQL("create table if not exists bab (bab_id primary key autoincrement, name text, foreign key(course_id) references courses(course_id))");

        db.execSQL("create table if not exists subbab (subbab_id primary key autoincrement, name text, foreign key(bab_id) references bab(bab_id))");

        db.execSQL("create table if not exists materi (materi_id primary key autoincrement, title text, content text, foreign key(subbab_id) references subbab(subbab_id))");

        db.execSQL("create table if not exists quiz (quiz_id primary key autoincrement, title text, content text, foreign key(subbab_id) references subbab(subbab_id))");

        db.execSQL("create table if not exists questions (question_id primary key autoincrement, question text, option_a text, option_b text, option_c text, option_d text, " +
                "answer text, foreign key(quiz_id) references quiz(quiz_id))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
//        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS session");
//        onCreate(db);
//        db.execSQL("drop table if exists text");
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
        long update = db.update("session", values, "id= "+id, null);
        if (update == -1){
            return false;
        }else{
            return true;
        }
    }

    //input user
    public boolean saveUser(String name, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        long insert = db.insert("users", null, values);
        if (insert == -1){
            return false;
        }else {
            return true;
        }

//        String sql = "insert into users (name, email, password) values ('"+name+"', '"+email+"', '"+password+"')";
//
//        db.execSQL(sql);
//        return 1;
    }

    // check login
    public Boolean checkLogin(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

}
