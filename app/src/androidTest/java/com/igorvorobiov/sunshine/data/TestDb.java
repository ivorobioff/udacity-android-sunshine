package com.igorvorobiov.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        getContext().deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }


    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        SQLiteDatabase db = new WeatherDbHelper(getContext()).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testLocationTable() {

        SQLiteDatabase db = new WeatherDbHelper(getContext()).getWritableDatabase();

        ContentValues locationValues = TestUtilities.createChisinauLocationValues();

        long id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, locationValues);

        Cursor cursor = db.rawQuery("SELECT * FROM " + WeatherContract.LocationEntry.TABLE_NAME, null);

        TestUtilities.validateCursor(
                "Error. Turns out the location table is not populated with data.",
                cursor,
                locationValues);

        db.close();
    }

    public void testWeatherTable() {

        long locationId = TestUtilities.insertChisinauLocationValues(getContext());
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationId);

        SQLiteDatabase db = new WeatherDbHelper(getContext()).getWritableDatabase();

        long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);

        Cursor cursor = db.rawQuery("SELECT * FROM " + WeatherContract.WeatherEntry.TABLE_NAME, null);

        TestUtilities.validateCursor(
                "Error. Turns out the weather table is not populated with data.",
                cursor,
                weatherValues);

        db.close();
    }
}
