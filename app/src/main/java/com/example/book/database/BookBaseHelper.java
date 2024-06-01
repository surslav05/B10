package com.example.book.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BookBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bookBase.db";
    public BookBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookDbSchema.BookTable.NAME + "(" + " _id integer primary key autoincrement, "
                + BookDbSchema.BookTable.Cols.UUID + ", " + BookDbSchema.BookTable.Cols.TITLE + ", "
                + BookDbSchema.BookTable.Cols.DATE + ", " + BookDbSchema.BookTable.Cols.READED + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
