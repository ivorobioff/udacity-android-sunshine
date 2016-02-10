package com.igorvorobiov.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onStart(){
        super.onStart();

        updateTitle();
    }

    private void updateTitle()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultLocation = getString(R.string.pref_default_location_value);
        setTitle(preferences.getString("location", defaultLocation));
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
}
