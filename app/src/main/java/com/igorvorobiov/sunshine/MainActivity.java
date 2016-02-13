package com.igorvorobiov.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import com.igorvorobiov.sunshine.data.WeatherContract;

public class MainActivity extends BaseActivity{

    private Boolean hasDetailPanel;
    private ForecastFragment forecastFragment;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getForecastFragment().setOnItemClickListener(new ForecastFragment.OnItemClickListener() {
            @Override
            public void onItemClick(Cursor cursor) {

                if (hasDetailPanel()) {
                    refreshDetailFragment(cursor);
                } else {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                    int day = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DAY));
                    
                    intent.putExtra(DetailActivity.INTENT_EXTRA_DAY, day);
                    startActivity(intent);
                }
            }
        });
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

        getForecastFragment().getForecastAdapter().swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getForecastFragment().getForecastAdapter().swapCursor(null);
    }
}
