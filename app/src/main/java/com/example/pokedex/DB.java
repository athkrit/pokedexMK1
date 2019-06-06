package com.example.pokedex;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "pokemonDelTable";
    private static final String collumname = "pokemonName";

    public long getProfilesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

    public DB(Context context) {
        super(context, "MainDB V.1", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + collumname + " TEXT(20) ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long InsertData(String PKMname) {
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            ContentValues Val = new ContentValues();
            Val.put("pokemonName", PKMname);

            long rows = db.insert(TABLE_NAME, null, Val);

            db.close();
            return rows;

        } catch (Exception e) {
            return -1;
        }
    }

    public ArrayList<String> SelectData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> pokeDel = new ArrayList<String>();
        String query = "SELECT pokemonName FROM pokemonDelTable";
        Cursor cursor = db.rawQuery(query, null,null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) {
                while (cursor.moveToNext()) {
                    pokeDel.add(cursor.getString(cursor.getColumnIndex("pokemonName")));
                }
            }
        }
        cursor.close();
        db.close();
        return pokeDel;
    }
}

