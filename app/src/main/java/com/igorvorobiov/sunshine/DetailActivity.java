package com.igorvorobiov.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.igorvorobiov.sunshine.data.WeatherContract;

public class DetailActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String INTENT_EXTRA_DAY = "day";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT));
        intent.setType("text/plain");

        actionProvider.setShareIntent(intent);

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                WeatherContract.WeatherEntry.buildContentUriByLocationAndDay(
                        getPreferredLocation(),
                        getSelectedDay()
                ),
                null, null, null, null
        );
    }

    private int getSelectedDay(){
        return getIntent().getIntExtra(INTENT_EXTRA_DAY, -1);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            refreshDetailFragment(data, DetailFragment.NO_POSITION);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
