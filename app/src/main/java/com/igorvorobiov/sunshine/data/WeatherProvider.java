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
    private static final int WEATHER_BY_ID = 101;
    private static final int WEATHER_BY_LOCATION = 102;
    private static final int WEATHER_BY_LOCATION_AND_DATE = 103;

    private static final int LOCATION = 200;
    private static final int LOCATION_BT_ID = 201;

    private WeatherRepository weatherRepository;
    private LocationRepository locationRepository;


    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static  {

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.WeatherEntry.URI_SEGMENT,
                WEATHER
        );

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.WeatherEntry.URI_SEGMENT + "/#",
                WEATHER_BY_ID
        );
        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.WeatherEntry.URI_SEGMENT + "/*",
                WEATHER_BY_LOCATION
        );

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.WeatherEntry.URI_SEGMENT + "/*/*",
                WEATHER_BY_LOCATION_AND_DATE
        );

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.LocationEntry.URI_SEGMENT,
                LOCATION
        );
        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.LocationEntry.URI_SEGMENT + "/#",
                LOCATION_BT_ID
        );
    }

    @Override
    public boolean onCreate() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(getContext());
        weatherRepository = new WeatherRepository(dbHelper);
        locationRepository = new LocationRepository(dbHelper);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (matcher.match(uri)){
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_DIR_TYPE;
            case WEATHER_BY_ID:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_BY_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_DIR_TYPE;
            case WEATHER_BY_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_DIR_TYPE;
            case LOCATION_BT_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri result;

        switch (matcher.match(uri)){
            case WEATHER:
                long weatherId = weatherRepository.create(values);
                result = WeatherContract.WeatherEntry.buildContentUriWithId((int) weatherId);
                break;

            case LOCATION:
                long locationId = locationRepository.create(values);
                result = WeatherContract.LocationEntry.buildContentUriWithId((int) locationId);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int result;

        switch (matcher.match(uri)){
            case WEATHER:
                result = weatherRepository.deleteAll();
                break;

            case LOCATION:
                result = locationRepository.deleteAll();
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized URI: " + uri);
        }

        if (result > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
