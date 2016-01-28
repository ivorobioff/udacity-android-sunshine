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

    private ForecastAdapter forecastViewAdapter = null;

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
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map){

            String defaultLocation = getString(R.string.pref_default_location_value);

            String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", defaultLocation);

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

    private ForecastAdapter getForecastViewAdapter(){
        if (forecastViewAdapter == null){
            ArrayAdapter<String> d;
            forecastViewAdapter = new ForecastAdapter(getActivity(), R.layout.list_item_forecast);
        }

        return forecastViewAdapter;
    }

    private class FetchForecastJsonTask extends AsyncTask<String, Void, ForecastItem[]> {

        private final String LOG_TAG = FetchForecastJsonTask.class.getSimpleName();
        private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
        private final String API_KEY = "27eacfe34fdc092f79128efa585c8cf0";

        @Override
        protected ForecastItem[] doInBackground(String... params) {
            String json = fetchForecastJson(params[0], params[1]);
            try {
                return parseForecastJson(json);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.toString());
                return new ForecastItem[0];
            }
        }

        protected void onPostExecute(ForecastItem[] items) {
            getForecastViewAdapter().refresh(items);
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

        private ForecastItem[] parseForecastJson(String json) throws JSONException{
            JSONObject root = new JSONObject(json);

            ForecastItem[] result = new ForecastItem[7];
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

                result[i] = new ForecastItem(calendar, description, max, min);
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
