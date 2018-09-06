package com.android.chrishsu.gsbookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.chrishsu.gsbookstore.data.BookContract.BookEntry;

// Create a BookDbHelper class from SQLiteOpenHelper
public class BookDbHelper extends SQLiteOpenHelper{

    // Create a variable for database file name
    private static final String DATABASE_NAME = "gsbookstore.db";

    // Create a variable for database version
    private static final int DATABASE_VERSION = 1;

    // Init the constructor
    public BookDbHelper(Context context) {
        // Insert the database credential
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Overrid onCreate method
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a string variable for SQL statement
        // that create a new table in SQLite
        String SQL_CREATE_BOOKSTORE_TALBE = "CREATE TABLE "
                + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + "  REAL NOT NULL, "
                + BookEntry.COLUMN_QTY + " INTEGER NOT NULL DEFAULT 1, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";

        // Use execSQL to execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKSTORE_TALBE);
    }

    // Overrid the onUpgrade method
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /* Do the upgrade next (but skip for now)
        if (oldVersion < 2) {

        }
        */
    }
}
