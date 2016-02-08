package com.igorvorobiov.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.igorvorobiov.sunshine.data.WeatherModel;

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

        TextView label = (TextView) root.findViewById(R.id.weather_textview);

        WeatherModel model = getActivity().getIntent()
                .getParcelableExtra(ForecastFragment.EXTRA_WEATHER);

        label.setText(model.getDescription());

        return root;
    }
}
