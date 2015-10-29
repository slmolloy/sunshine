package com.example.android.sunshine.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] weekForecast = {
            "Today -- Sunny -- 88/63",
            "Tomorrow -- Rainy -- 23/12",
            "Wednesday -- Sunny -- 88/12",
            "Thursday -- Snowing -- 23/2",
            "Friday -- Sunny -- 98/88",
            "Saturday -- Cloudy -- 65/54",
            "Sunday -- Rainy -- 54/48",
            "Monday -- Snowing -- 23/9",
            "Tuesday -- Sunny -- 103/98",
            "Wednesday -- Sunny -- 78/76"
        };

        mForecastAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }
}
