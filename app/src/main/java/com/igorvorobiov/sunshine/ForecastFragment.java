package com.igorvorobiov.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.igorvorobiov.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_WEATHER = "weather";

    private static final int FORECAST_LOADER_ID = 1;

    private ForecastAdapter forecastAdapter = null;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        ListView forecastListView = (ListView) root.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(getForecastAdapter());

        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) getForecastAdapter().getItem(position);

                WeatherViewModel model = new WeatherViewModel(cursor);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(EXTRA_WEATHER, model);

                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
    }

    public void onStart(){
        super.onStart();
        updateWeather();
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

        new FetchWeatherTask(getActivity()).execute(getPreferredLocation());
    }

    private String getPreferredLocation(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString("location", getString(R.string.pref_default_location_value));
    }

    private ForecastAdapter getForecastAdapter(){
        if (forecastAdapter == null){
            forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        }

        return  forecastAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                WeatherContract.WeatherEntry.buildContentUriByLocation(getPreferredLocation()),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getForecastAdapter().swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getForecastAdapter().swapCursor(null);
    }
}
