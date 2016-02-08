package com.igorvorobiov.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igorvorobiov.sunshine.data.WeatherModel;

import java.text.SimpleDateFormat;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int resource;

        if (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY){
            resource = R.layout.list_item_forecast_today;
        } else {
            resource = R.layout.list_item_forecast;
        }

        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(resource, null);

        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        WeatherModel model = new WeatherModel(cursor);

        holder.description.setText(model.getDescription());

        String date = new SimpleDateFormat("MM/dd/yyyy").format(model.getDate());
        holder.date.setText(date);

        String defaultUnits = mContext.getString(R.string.pref_default_unit_value);

        String units = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString("units", defaultUnits);

        Double max = convertUnits(model.getMax(), units, defaultUnits);
        Double min = convertUnits(model.getMin(), units, defaultUnits);

        holder.max.setText(context.getString(R.string.formatted_temperature, max));
        holder.min.setText(context.getString(R.string.formatted_temperature, min));
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

    private static class ViewHolder {
        TextView description;
        TextView date;
        TextView min;
        TextView max;
        ImageView icon;

        ViewHolder(View view){
            date = (TextView) view.findViewById(R.id.list_item_date_textview);
            description = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            max = (TextView)view.findViewById(R.id.list_item_high_textview);
            min = (TextView) view.findViewById(R.id.list_item_low_textview);
            icon = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }
}
