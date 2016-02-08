package com.igorvorobiov.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherProvider extends ContentProvider {

    private static final int WEATHER = 100;
    private static final int WEATHER_BY_LOCATION = 102;

    private WeatherRepository weatherRepository;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static  {

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.WeatherEntry.URI_SEGMENT,
                WEATHER
        );

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.WeatherEntry.URI_SEGMENT + "/*",
                WEATHER_BY_LOCATION
        );
    }

    @Override
    public boolean onCreate() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(getContext());
        weatherRepository = new WeatherRepository(dbHelper);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (matcher.match(uri) == WEATHER_BY_LOCATION){
            Cursor cursor = weatherRepository.getAllByLocation(uri.getLastPathSegment());
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }

        throw new UnsupportedOperationException("Unable to get data for: " + uri);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        if (matcher.match(uri) == WEATHER_BY_LOCATION){
            return WeatherContract.WeatherEntry.CONTENT_DIR_TYPE;
        }

        throw new UnsupportedOperationException("Unable to get content type for: " + uri);
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {

        if (matcher.match(uri) == WEATHER){
            int count = weatherRepository.createAll(values);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        throw new UnsupportedOperationException("Unable to insert data for: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Insert is not supported.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Delete is not supported.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update is not supported.");
    }
}
