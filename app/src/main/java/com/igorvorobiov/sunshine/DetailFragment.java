package com.igorvorobiov.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        WeatherViewModel model = getActivity().getIntent()
                .getParcelableExtra(ForecastFragment.EXTRA_WEATHER);

        model.setContext(getContext());

        ((TextView) root.findViewById(R.id.weather_description_textview)).setText(model.getDescription());
        ((TextView) root.findViewById(R.id.weather_day_textview)).setText(model.getDay());
        ((TextView) root.findViewById(R.id.weather_date_textview)).setText(model.getDate());
        ((TextView) root.findViewById(R.id.weather_high_textview)).setText(model.getMax());
        ((TextView) root.findViewById(R.id.weather_low_textview)).setText(model.getMin());
        ((TextView) root.findViewById(R.id.weather_wind_textview)).setText(model.getWind());
        ((TextView) root.findViewById(R.id.weather_humidity_textview)).setText(model.getHumidity());
        ((TextView) root.findViewById(R.id.weather_pressure_textview)).setText(model.getPressure());
        ((ImageView) root.findViewById(R.id.weather_icon)).setImageResource(model.getArtResource());

        return root;
    }
}
