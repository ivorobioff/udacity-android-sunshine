package com.igorvorobiov.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherRepository {

    private WeatherDbHelper dbHelper;

    public WeatherRepository(WeatherDbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public int createAll(ContentValues[] values){
        int count = 0;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            for (ContentValues value : values){
                long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);

                if (id != -1){
                    count ++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return count;
    }

    public Cursor getAllByLocation(String location){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(WeatherContract.WeatherEntry.TABLE_NAME, null,
                WeatherContract.WeatherEntry.COLUMN_LOCATION + " = ?", new String[]{ location },
                null, null, "date ASC");
    }
}
