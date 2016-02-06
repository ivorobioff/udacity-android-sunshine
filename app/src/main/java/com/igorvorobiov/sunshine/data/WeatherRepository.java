package com.igorvorobiov.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherRepository {

    private WeatherDbHelper dbHelper;

    public WeatherRepository(WeatherDbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public long create(ContentValues values) throws SQLException{
        return 0L;
    }

    public int deleteAll(){
        return 0;
    }

    public Cursor getAllByLocationSetting(String[] projection, String sortOrder){
        return null;
    }

    public Cursor getAllByLocationSettingAndDate(String[] projection, String sortOrder){
        return null;
    }
}
