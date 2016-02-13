package com.igorvorobiov.sunshine;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    public static final String VIEW_MODEL = "model";
    public static final String POSITION = "position";

    public static final int NO_POSITION = -1;

    private String preferredUnits;

    private WeatherViewModel model;
    private int position;

    public DetailFragment() {

    }

    public static DetailFragment newInstance(WeatherViewModel model, int position){
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.initialize(model, position);
        return detailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null){
            model = savedInstanceState.getParcelable(VIEW_MODEL);
            position = savedInstanceState.getInt(POSITION);
        }

        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        refreshFragment(root);
        preferredUnits = getPreferredUnits();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        String units = getPreferredUnits();

        if (!preferredUnits.equals(units)){
            refreshFragment();
            preferredUnits = units;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(VIEW_MODEL, model);
        outState.putInt(POSITION, position);
    }

    public int getPosition(){
        return position;
    }

    private void refreshFragment(){
        refreshFragment(getView());
    }

    private void refreshFragment(View root){

        ((TextView) root.findViewById(R.id.weather_description_textview)).setText(model.getDescription());
        ((TextView) root.findViewById(R.id.weather_day_textview)).setText(model.getDay());
        ((TextView) root.findViewById(R.id.weather_date_textview)).setText(model.getDate());
        ((TextView) root.findViewById(R.id.weather_high_textview)).setText(model.getMax());
        ((TextView) root.findViewById(R.id.weather_low_textview)).setText(model.getMin());
        ((TextView) root.findViewById(R.id.weather_wind_textview)).setText(model.getWind());
        ((TextView) root.findViewById(R.id.weather_humidity_textview)).setText(model.getHumidity());
        ((TextView) root.findViewById(R.id.weather_pressure_textview)).setText(model.getPressure());
        ((ImageView) root.findViewById(R.id.weather_icon)).setImageResource(model.getArtResource());
    }

    public void refresh(WeatherViewModel model, int position){
        initialize(model, position);
        refreshFragment();
    }

    private String getPreferredUnits() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString("units", getString(R.string.pref_default_unit_value));
    }

    public void initialize(WeatherViewModel model, int position){
        this.model = model;
        this.position = position;
    }
}
