package com.commutestream.sdk_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.commutestream.sdk.AgencyInterest;

import java.security.AlgorithmParameterGenerator;
import java.util.ArrayList;

/**
 * Adapts and array of agency interests to generate TextViews appropriately
 */
public class InterestAdapter extends ArrayAdapter<AgencyInterest> {

    public InterestAdapter(Context context, ArrayList<AgencyInterest> interests) {
        super(context, 0, interests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AgencyInterest interest = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recent_item, parent, false);
        }
        // Lookup view for data population
        TextView tvInterest = (TextView) convertView.findViewById(R.id.interest);
        TextView tvAgency = (TextView) convertView.findViewById(R.id.agency);
        TextView tvRoute = (TextView) convertView.findViewById(R.id.route);
        TextView tvStop = (TextView) convertView.findViewById(R.id.stop);

        // Populate the data into the template view using the data object
        tvInterest.setText(interest.getInterest());
        tvAgency.setText(interest.getAgencyID());
        tvRoute.setText(interest.getRouteID());
        tvStop.setText(interest.getStopID());

        // Return the completed view to render on screen
        return convertView;
    }
}
