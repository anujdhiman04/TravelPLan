package com.example.manisha.travelplan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Trip_Object>{

    private ArrayList<Trip_Object> dataSet;
    Context mContext;
    HomeScreen homeScreen;
    // View lookup cache
    private static class ViewHolder {
        TextView name,place,user_count;
    }

    public CustomAdapter(ArrayList<Trip_Object> data, HomeScreen homeScreen) {
        super(homeScreen, R.layout.trip_itemview, data);
        this.dataSet = data;
        this.mContext=homeScreen;
        this.homeScreen=homeScreen;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Trip_Object Trip_Object = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.trip_itemview, parent, false);
            TextView name,place,start_Date,end_Date;

            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.place = (TextView) convertView.findViewById(R.id.place);
            viewHolder.user_count = (TextView) convertView.findViewById(R.id.user_count);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.name.setText(Trip_Object.getName());
        viewHolder.place.setText(Trip_Object.getPlace());
        viewHolder.user_count.setText(Trip_Object.getAvailablecount());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeScreen,TripDetail.class);
                intent.putExtra("trip_name",Trip_Object.getName());
                intent.putExtra("trip_place",Trip_Object.getPlace());
                intent.putExtra("start_date",Trip_Object.getStart_date());
                intent.putExtra("end_date",Trip_Object.getEnd_date());
                homeScreen.startActivity(intent);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}