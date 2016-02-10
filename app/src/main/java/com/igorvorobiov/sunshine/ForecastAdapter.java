package com.igorvorobiov.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        View view = LayoutInflater.from(context).inflate(resource, null);

        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        WeatherViewModel model = new WeatherViewModel(cursor);
        model.setContext(context);

        holder.description.setText(model.getDescription());
        holder.day.setText(model.getDay());
        holder.max.setText(model.getMax());
        holder.min.setText(model.getMin());

        if (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY){
            holder.icon.setImageResource(model.getArtResource());
        } else {
            holder.icon.setImageResource(model.getIconResource());
        }
    }

    private static class ViewHolder {
        TextView description;
        TextView day;
        TextView min;
        TextView max;
        ImageView icon;

        ViewHolder(View view){
            day = (TextView) view.findViewById(R.id.weather_day_textview);
            description = (TextView) view.findViewById(R.id.weather_description_textview);
            max = (TextView)view.findViewById(R.id.weather_high_textview);
            min = (TextView) view.findViewById(R.id.weather_low_textview);
            icon = (ImageView) view.findViewById(R.id.weather_icon);
        }
    }
}
