package com.igorvorobiov.sunshine.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherModel extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public WeatherModel(Cursor cursor) {
        super(cursor);
    }

    public Long getDate(){
        return getLong(getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
    }

    public String getDescription(){
        return getString(getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION));
    }

    public Double getMin(){
        return getDouble(getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN));
    }

    public Double getMax(){
        return getDouble(getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX));
    }
}
