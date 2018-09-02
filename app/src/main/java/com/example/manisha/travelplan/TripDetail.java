package com.example.manisha.travelplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TripDetail extends AppCompatActivity {

    public SharedPreferences pref;
    ArrayList<Availibility_Object> availibility_objects = new ArrayList<>();
    TextView name, place, dates;
    ListView user_list;
    AvailabilityAdapter availabilityAdapter;
    public DBHelper mydb;
    String name_text,place_text,
    start_date_text,end_date_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        name = (TextView)findViewById(R.id.name);
        place = (TextView)findViewById(R.id.place);
        dates = (TextView)findViewById(R.id.dates);
        user_list = (ListView) findViewById(R.id.user_list);
        mydb = new DBHelper(this);


        Intent intent = getIntent();
        name_text = intent.getStringExtra("trip_name");
        place_text = intent.getStringExtra("trip_place");
        start_date_text = intent.getStringExtra("start_date");
        end_date_text = intent.getStringExtra("end_date");

        name.setText(name_text);
        place.setText(place_text);
        dates.setText(start_date_text +" to "+ end_date_text);

    }

    @Override
    protected void onResume() {
        super.onResume();
        update_data();
    }


    public void update_data(){
        availibility_objects = mydb.getUserAvailability(name_text);
        availabilityAdapter = new AvailabilityAdapter(availibility_objects, TripDetail.this );
        user_list.setAdapter(availabilityAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                pref.edit().remove(Constants.user_name).apply();
                pref.edit().remove(Constants.password).apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
