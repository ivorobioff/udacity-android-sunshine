package com.igorvorobiov.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastViewAdapter = null;

    public ForecastFragment() {
    }

    public void onStart(){
        super.onStart();

        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        ListView forecastListView = (ListView) root.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(getForecastViewAdapter());

        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = getForecastViewAdapter().getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, text);

                startActivity(intent);
            }
        });

        return root;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        if (id == R.id.action_map){

            String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", null);

            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri uri = Uri.parse("geo:0,0").buildUpon().appendQueryParameter("q", location).build();

            intent.setData(uri);

            if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info == null || !info.isConnected()){

            Toast toast = Toast.makeText(getActivity(), "No Internet connection.", Toast.LENGTH_LONG);
            toast.show();
            return ;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String location = preferences.getString("location", getString(R.string.pref_default_location_value));
        String units = preferences.getString("units", getString(R.string.pref_default_unit_value));

        new FetchForecastJsonTask().execute(location, units);
    }

    private ArrayAdapter<String> getForecastViewAdapter(){
        if (forecastViewAdapter == null){
            forecastViewAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview
            );
        }

        return forecastViewAdapter;
    }

    private class FetchForecastJsonTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchForecastJsonTask.class.getSimpleName();
        private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
        private final String API_KEY = "27eacfe34fdc092f79128efa585c8cf0";

        @Override
        protected String[] doInBackground(String... params) {
            String json = fetchForecastJson(params[0], params[1]);
            try {
                return parseForecastJson(json);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.toString());
                return new String[0];
            }
        }

        protected void onPostExecute(String[] items) {

            getForecastViewAdapter().clear();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getForecastViewAdapter().addAll(items);
            } else {
                for (String item : items){
                    getForecastViewAdapter().add(item);
                }
            }
        }

        private String fetchForecastJson(String location, String units)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String json = null;

            try {
                URL url = new URL(buildUrl(location, units));
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

        private String[] parseForecastJson(String json) throws JSONException{
            JSONObject root = new JSONObject(json);

            String[] result = new String[7];
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

                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                String day = shortenedDateFormat.format(calendar.getTimeInMillis());

                result[i] = day + " - " + description + " - " + max + "/" + min;
            }

            return result;
        }

        private String buildUrl(String location, String units)
        {
            return Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("q", location)
                    .appendQueryParameter("appid", API_KEY)
                    .appendQueryParameter("cnt", "7")
                    .appendQueryParameter("units", units)
                    .toString();
        }
    }
}
