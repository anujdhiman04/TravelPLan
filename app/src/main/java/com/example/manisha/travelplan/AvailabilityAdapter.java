package com.example.manisha.travelplan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AvailabilityAdapter extends ArrayAdapter<Availibility_Object>{

    private ArrayList<Availibility_Object> dataSet;
    Context mContext;
    TripDetail tripDetail;
    // View lookup cache
    private static class ViewHolder {
        TextView user_name;
        CheckBox check_availability;
        View hover;
    /*BlurLayout blurLayout;*/
    }

    public AvailabilityAdapter(ArrayList<Availibility_Object> data, TripDetail tripDetail) {
        super(tripDetail, R.layout.trip_itemview, data);
        this.dataSet = data;
        this.mContext=tripDetail;
        this.tripDetail = tripDetail;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Availibility_Object Availibility_Object = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.availability_item, parent, false);
            TextView name,place,start_Date,end_Date;

            viewHolder.check_availability = (CheckBox) convertView.findViewById(R.id.check_availability);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.user_name.setText(Availibility_Object.getName());
        viewHolder.check_availability.setChecked(Availibility_Object.isAvailable());

        viewHolder.check_availability.setClickable(false);
        viewHolder.check_availability.setFocusable(false);
        viewHolder.check_availability.setActivated(false);

        final String user_name = tripDetail.pref.getString(Constants.user_name,"");

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(user_name.equals(Availibility_Object.getName())) {
                        if (tripDetail.mydb.CheckIsAvailabilityAlreadyInDBorNot("availability", "user_name", user_name,
                                "trip_name", tripDetail.name_text)) {
                            tripDetail.mydb.deleteAvailability(user_name,
                                    tripDetail.name_text);
                            viewHolder.check_availability.setChecked(false);
                        } else {
                            tripDetail.mydb.insertAvailability(user_name,
                                    tripDetail.name_text);
                            viewHolder.check_availability.setChecked(true);
                        }
                    }else {
                        Toast.makeText(mContext, "You can not mark any other person's availabilty.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        // Return the completed view to render on screen
        return convertView;
    }
}