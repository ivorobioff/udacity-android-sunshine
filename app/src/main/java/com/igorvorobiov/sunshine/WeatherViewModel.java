package com.igorvorobiov.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.igorvorobiov.sunshine.data.WeatherContract;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherViewModel implements Parcelable {

    private Context context;

    private String description;
    private Long date;
    private Double min;
    private Double max;
    private Double pressure;
    private Double humidity;
    private Double wind;
    private Double degrees;
    private Integer condition;


    public WeatherViewModel(Cursor cursor, Context context) {
        this(cursor);
        setContext(context);
    }

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public WeatherViewModel(Cursor cursor) {
        description = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION));
        date = cursor.getLong(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        min = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN));
        max = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX));
        pressure = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        humidity = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        wind = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND));
        degrees = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
        condition = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CONDITION));
    }

    protected WeatherViewModel(Parcel in) {
        description = in.readString();
        date = in.readLong();
        min = in.readDouble();
        max = in.readDouble();
        pressure = in.readDouble();
        humidity = in.readDouble();
        wind = in.readDouble();
        degrees = in.readDouble();
        condition = in.readInt();
    }

    public static final Creator<WeatherViewModel> CREATOR = new Creator<WeatherViewModel>() {
        @Override
        public WeatherViewModel createFromParcel(Parcel in) {
            return new WeatherViewModel(in);
        }

        @Override
        public WeatherViewModel[] newArray(int size) {
            return new WeatherViewModel[size];
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
        dest.writeDouble(pressure);
        dest.writeDouble(humidity);
        dest.writeDouble(wind);
        dest.writeDouble(degrees);
        dest.writeInt(condition);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public int getArtResource(){
        if (condition >= 200 && condition <= 232) {
            return R.drawable.art_storm;
        }

        if (condition >= 300 && condition <= 321) {
            return R.drawable.art_light_rain;
        }

        if (condition >= 500 && condition <= 504) {
            return R.drawable.art_rain;
        }

        if (condition == 511) {
            return R.drawable.art_snow;
        }

        if (condition >= 520 && condition <= 531) {
            return R.drawable.art_rain;
        }

        if (condition >= 600 && condition <= 622) {
            return R.drawable.art_snow;
        }

        if (condition >= 701 && condition <= 761) {
            return R.drawable.art_fog;
        }

        if (condition == 761 || condition == 781) {
            return R.drawable.art_storm;
        }

        if (condition == 800) {
            return R.drawable.art_clear;
        }

        if (condition == 801) {
            return R.drawable.art_light_clouds;
        }

        if (condition >= 802 && condition <= 804) {
            return R.drawable.art_clouds;
        }

        return -1;
    }

    public int getIconResource(){
        if (condition >= 200 && condition <= 232) {
            return R.drawable.ic_storm;
        }

        if (condition >= 300 && condition <= 321) {
            return R.drawable.ic_light_rain;
        }

        if (condition >= 500 && condition <= 504) {
            return R.drawable.ic_rain;
        }

        if (condition == 511) {
            return R.drawable.ic_snow;
        }

        if (condition >= 520 && condition <= 531) {
            return R.drawable.ic_rain;
        }

        if (condition >= 600 && condition <= 622) {
            return R.drawable.ic_snow;
        }

        if (condition >= 701 && condition <= 761) {
            return R.drawable.ic_fog;
        }

        if (condition == 761 || condition == 781) {
            return R.drawable.ic_storm;
        }

        if (condition == 800) {
            return R.drawable.ic_clear;
        }

        if (condition == 801) {
            return R.drawable.ic_light_clouds;
        }

        if (condition >= 802 && condition <= 804) {
            return R.drawable.ic_cloudy;
        }

        return -1;
    }

    public String getDate(){
        return new SimpleDateFormat("MMM d").format(date);
    }

    public String getDescription(){
        return WordUtils.capitalize(description);
    }

    public String getMin(){
        return context.getString(R.string.formatted_temperature, convertUnits(min));
    }

    public String getMax(){
        return context.getString(R.string.formatted_temperature, convertUnits(max));
    }

    public String getHumidity() {
        return context.getString(R.string.formatted_humidity, humidity);
    }

    public String getPressure() {
        return context.getString(R.string.formatted_pressure, pressure);
    }

    public String getWind() {

        Double speed;
        int resource;

        if (isMetric()){
            speed = wind;
            resource = R.string.formatted_wind_kmh;
        } else{
            speed = .621371192237334f * wind;
            resource = R.string.formatted_wind_mph;
        }

        String[] directions = new String[] {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return context.getString(resource, speed, directions[(int) Math.round(degrees / 45) % 8]);
    }

    public String getDay(){

        if (DateUtils.isToday(date)){
            return context.getString(R.string.formatted_day_today);
        }

        GregorianCalendar tomorrow = new GregorianCalendar();
        tomorrow.add(GregorianCalendar.DAY_OF_MONTH, 1);

        GregorianCalendar current = new GregorianCalendar();
        current.setTimeInMillis(date);

        if (tomorrow.get(GregorianCalendar.YEAR) == current.get(GregorianCalendar.YEAR)
                && tomorrow.get(GregorianCalendar.DAY_OF_YEAR) == current.get(GregorianCalendar.DAY_OF_YEAR)){
            return context.getString(R.string.formatted_day_tomorrow);
        }

        return new SimpleDateFormat("EEEE").format(date);
    }

    private Double convertUnits(Double number){

        if (isMetric()){
            return number;
        }

        return (number * 1.8) + 32;
    }

    private boolean isMetric(){

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("units", context.getString(R.string.pref_default_unit_value))
                .equals(context.getString(R.string.pref_units_metric));
    }
}
