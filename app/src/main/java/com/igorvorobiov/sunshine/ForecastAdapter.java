package com.igorvorobiov.sunshine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class ForecastAdapter extends BaseAdapter {

    private Context context;
    private ForecastItem[] items = new ForecastItem[0];

    ForecastAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout item;

        if (convertView != null){
            item = (LinearLayout) convertView;
        } else {
            item = (LinearLayout) getInflater().inflate(R.layout.list_item_forecast, null);
        }

        ForecastItem forecastItem = (ForecastItem) getItem(position);

        ((TextView)item.findViewById(R.id.list_item_date_textview)).setText(forecastItem.getDay());
        ((TextView)item.findViewById(R.id.list_item_forecast_textview)).setText(forecastItem.getDescription());
        ((TextView)item.findViewById(R.id.list_item_low_textview)).setText(forecastItem.getMin().toString());
        ((TextView)item.findViewById(R.id.list_item_high_textview)).setText(forecastItem.getMax().toString());

        return item;
    }

    private LayoutInflater getInflater(){
        return LayoutInflater.from(context);
    }

    public void refresh(ForecastItem[] items){
        this.items = items;
        notifyDataSetChanged();
    }
}
