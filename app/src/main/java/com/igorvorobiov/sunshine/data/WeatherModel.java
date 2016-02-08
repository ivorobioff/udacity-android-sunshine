package com.igorvorobiov.sunshine.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherModel implements Parcelable {

    private Cursor cursor;

    private String description;
    private Long date;
    private Double min;
    private Double max;

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public WeatherModel(Cursor cursor) {
        description = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION));
        date = cursor.getLong(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        min = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN));
        max = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX));
    }

    protected WeatherModel(Parcel in) {
        description = in.readString();
        date = in.readLong();
        min = in.readDouble();
        max = in.readDouble();
    }

    public static final Creator<WeatherModel> CREATOR = new Creator<WeatherModel>() {
        @Override
        public WeatherModel createFromParcel(Parcel in) {
            return new WeatherModel(in);
        }

        @Override
        public WeatherModel[] newArray(int size) {
            return new WeatherModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeLong(date);
        dest.writeDouble(min);
        dest.writeDouble(max);
    }

    public Long getDate(){
        return date;
    }

    public String getDescription(){
        return description;
    }

    public Double getMin(){
        return min;
    }

    public Double getMax(){
        return max;
    }
}
