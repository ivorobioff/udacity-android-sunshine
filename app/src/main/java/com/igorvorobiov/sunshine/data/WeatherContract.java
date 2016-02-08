package com.igorvorobiov.sunshine.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.igorvorobiov.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class WeatherEntry implements BaseColumns {

        public static final String URI_SEGMENT = "weather";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(URI_SEGMENT)
                .build();


        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + URI_SEGMENT;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + URI_SEGMENT;

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MIN = "min";
        public static final String COLUMN_MAX = "max";


        public static Uri buildContentUriByLocation(String location){
            return CONTENT_URI.buildUpon().appendEncodedPath(location).build();
        }
    }
}
