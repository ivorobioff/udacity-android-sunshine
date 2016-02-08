package com.igorvorobiov.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.igorvorobiov.sunshine.data.WeatherContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private final String API_KEY = "27eacfe34fdc092f79128efa585c8cf0";

    private Context context;

    public FetchWeatherTask(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        String location = params[0];

        String json = fetchJSON(location);

        try {
            persistJSON(json, location);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.toString());
        }
        return null;
    }

    private String fetchJSON(String location)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json = null;

        try {
            URL url = new URL(buildUrl(location));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            json = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return json;
    }

    private void persistJSON(String json, String location) throws JSONException {
        JSONObject root = new JSONObject(json);

        ContentValues[] values = new ContentValues[7];

        JSONArray list = root.getJSONArray("list");

        for (int i = 0; i < list.length(); i++){

            JSONObject item = list.getJSONObject(i);

            String description = item.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("description");

            double max = item.getJSONObject("temp").getDouble("max");
            double min = item.getJSONObject("temp").getDouble("min");

            GregorianCalendar calendar = new GregorianCalendar();

            calendar.add(GregorianCalendar.DATE, i);

            ContentValues value = new ContentValues();
            value.put(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION, description);
            value.put(WeatherContract.WeatherEntry.COLUMN_DATE, calendar.getTimeInMillis());
            value.put(WeatherContract.WeatherEntry.COLUMN_MAX, max);
            value.put(WeatherContract.WeatherEntry.COLUMN_MIN, min);
            value.put(WeatherContract.WeatherEntry.COLUMN_LOCATION, location);
            value.put(WeatherContract.WeatherEntry.COLUMN_DAY, calendar.get(GregorianCalendar.DAY_OF_WEEK));

            values[i] = value;
        }

        context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, values);
    }

    private String buildUrl(String location)
    {
        return Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("q", location)
                .appendQueryParameter("appid", API_KEY)
                .appendQueryParameter("cnt", "7")
                .appendQueryParameter("units", context.getString(R.string.pref_default_unit_value))
                .toString();
    }
}