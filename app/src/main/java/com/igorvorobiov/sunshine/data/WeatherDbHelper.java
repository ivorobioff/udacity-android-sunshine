package com.igorvorobiov.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.igorvorobiov.sunshine.data.WeatherContract.WeatherEntry;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    private Context ctx;

    public WeatherDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
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
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +
                " UNIQUE (" + WeatherEntry.COLUMN_DAY + ", " +
                WeatherEntry.COLUMN_LOCATION + ") ON CONFLICT REPLACE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * This is used for debugging purposes
     */
    public void export(){

        File source = new File(getReadableDatabase().getPath());
        File destination = new File(Environment.getExternalStorageDirectory(), "sunshine.db");

        try {
            FileUtils.copyFileToDirectory(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
