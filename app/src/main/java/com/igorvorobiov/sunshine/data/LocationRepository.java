package com.igorvorobiov.sunshine.data;

import android.content.ContentValues;
import android.database.SQLException;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class LocationRepository {

    private WeatherDbHelper dbHelper;

    public LocationRepository(WeatherDbHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public long create(ContentValues values) throws SQLException{
        return 0L;
    }

    public int deleteAll(){
        return 0;
    }
}
