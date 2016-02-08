package com.igorvorobiov.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igorvorobiov.sunshine.data.WeatherModel;

import java.text.SimpleDateFormat;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LinearLayout view = (LinearLayout) LayoutInflater
                .from(context)
                .inflate(R.layout.list_item_forecast, null);

        populateView(view, cursor);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        populateView((LinearLayout) view, cursor);
    }

    private void populateView(LinearLayout view, Cursor cursor){

        WeatherModel model = new WeatherModel(cursor);

        ((TextView)view.findViewById(R.id.list_item_forecast_textview)).setText(model.getDescription());

        String date = new SimpleDateFormat("MM/dd/yyyy").format(model.getDate());
        ((TextView) view.findViewById(R.id.list_item_date_textview)).setText(date);

        String defaultUnits = mContext.getString(R.string.pref_default_unit_value);

        String units = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString("units", defaultUnits);

        Double max = convertUnits(model.getMax(), units, defaultUnits);
        Double min = convertUnits(model.getMin(), units, defaultUnits);

        ((TextView) view.findViewById(R.id.list_item_low_textview)).setText(String.valueOf(max.intValue()));
        ((TextView)view.findViewById(R.id.list_item_high_textview)).setText(String.valueOf(min.intValue()));
    }

    private Double convertUnits(Double number, String from, String to){

        if (from.equals(to)){
            return number;
        }

        if (from.equals(mContext.getString(R.string.pref_units_imperial))
                && to.equals(mContext.getString(R.string.pref_units_metric))){
            return (number - 32) / 1.8;
        }

        if (from.equals(mContext.getString(R.string.pref_units_metric))
                && to.equals(mContext.getString(R.string.pref_units_imperial))){
            return (number * 1.8) + 32;
        }

        throw new UnsupportedOperationException("Unable to convert '" + from + "' to '" + to + "'.");
    }
}
