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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */

public abstract class BaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DEFAULT_LOADER = 1;

    private String preferredLocation;

    protected  abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(DEFAULT_LOADER, null, this);
        preferredLocation = getPreferredLocation();
    }

    public void onStart(){
        super.onStart();
        updateWeather();
        updateTitle();

        String location = getPreferredLocation();

        if (!preferredLocation.equals(location)){
            getSupportLoaderManager().restartLoader(DEFAULT_LOADER, null, this);
            preferredLocation = location;
        }
    }

    protected String getPreferredLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("location", getString(R.string.pref_default_location_value));
    }

    protected void refreshDetailFragment(Cursor cursor){
        refreshDetailFragment(cursor, cursor.getPosition());
    }

    protected void refreshDetailFragment(Cursor cursor, int position){

        WeatherViewModel model = new WeatherViewModel(cursor, this);

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

    protected DetailFragment getDetailFragment(){
        return (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_detail_container);
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

    private void updateTitle()
    {
        setTitle(onUpdateTitle());
    }

    protected String onUpdateTitle(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultLocation = getString(R.string.pref_default_location_value);
        return preferences.getString("location", defaultLocation);
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
}
