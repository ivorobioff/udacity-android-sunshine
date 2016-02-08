package com.igorvorobiov.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.igorvorobiov.sunshine.data.WeatherContract.WeatherEntry;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_DAY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MIN + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_MAX + " REAL NOT NULL, " +
                " UNIQUE (" + WeatherEntry.COLUMN_DAY + ", " +
                WeatherEntry.COLUMN_LOCATION + ") ON CONFLICT REPLACE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
