package com.igorvorobiov.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.igorvorobiov.sunshine.data.WeatherContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER_ID = 1;
    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment";

    public static final String VIEW_MODEL = "model";

    private Boolean hasDetailPanel;
    private String preferredLocation;
    private ForecastFragment forecastFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getForecastFragment().setOnItemClickListener(new ForecastFragment.OnItemClickListener() {
            @Override
            public void onItemClick(Cursor cursor) {

                if (hasDetailPanel()) {
                    refreshDetailFragment(cursor);
                } else {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(VIEW_MODEL, new WeatherViewModel(cursor));

                    startActivity(intent);
                }
            }
        });

        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        preferredLocation = getPreferredLocation();
    }

    public void onStart(){
        super.onStart();
        updateWeather();
        updateTitle();

        String location = getPreferredLocation();

        if (!preferredLocation.equals(location)){
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            preferredLocation = location;
        }
    }

    private void updateTitle()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultLocation = getString(R.string.pref_default_location_value);
        setTitle(preferences.getString("location", defaultLocation));
    }

    private void updateWeather() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {

            Toast toast = Toast.makeText(this, "No Internet connection.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        new FetchWeatherTask(this).execute(getPreferredLocation());
    }

    private String getPreferredLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("location", getString(R.string.pref_default_location_value));
    }

    private ForecastFragment getForecastFragment(){

        if (forecastFragment == null){
            forecastFragment = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.forecast_fragment);
        }

        return forecastFragment;
    }

    private boolean hasDetailPanel(){
        if (hasDetailPanel == null){
            View detailContainer = findViewById(R.id.fragment_detail_container);

            if (detailContainer != null){
                hasDetailPanel = true;
            } else {
                hasDetailPanel = false;
            }
        }

        return hasDetailPanel;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    SettingsActivity.GeneralPreferenceFragment.class.getName());

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void refreshDetailFragment(Cursor cursor){

        WeatherViewModel model = new WeatherViewModel(cursor, this);
        int position = cursor.getPosition();

        DetailFragment detailFragment = getDetailFragment();

        if (detailFragment == null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.add(
                    R.id.fragment_detail_container,
                    DetailFragment.newInstance(model, position)
            );

            fragmentTransaction.commit();
        } else {
            detailFragment.refresh(model, position);
        }
    }

    private DetailFragment getDetailFragment(){
        return (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_detail_container);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                WeatherContract.WeatherEntry.buildContentUriByLocation(getPreferredLocation()),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        DetailFragment detailFragment = getDetailFragment();

        if (detailFragment != null){

            int position = detailFragment.getPosition();
            boolean success = data.moveToPosition(position);

            if (success){
                refreshDetailFragment(data);
            }
        }

        Cursor c = getForecastFragment().getForecastAdapter().swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getForecastFragment().getForecastAdapter().swapCursor(null);
    }
}
