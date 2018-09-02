package com.example.manisha.travelplan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeScreen extends AppCompatActivity {
    EditText trip_name;
    PlacesAutocompleteTextView placesAutocompleteTextView;
    TextView start_date,end_date;
    LinearLayout start_date_button,end_date_button;
    String id;
    Button create,cancel;
    TextView no_trip_text;
    ListView trip_list;
    private ArrayList<Trip_Object> dataModels;
    private CustomAdapter adapter;
    private DBHelper mydb;
    private SharedPreferences pref;
    private int mYear,mMonth,mDay;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(pref.getString(Constants.user_name,""));

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.create_newtrip_popup);

        placesAutocompleteTextView = (PlacesAutocompleteTextView)dialog.findViewById(R.id.places_autocomplete);
        start_date = (TextView) dialog.findViewById(R.id.start_date);
        end_date = (TextView)dialog. findViewById(R.id.end_date);
        trip_name = (EditText) dialog.findViewById(R.id.trip_name);
        create = (Button)dialog.findViewById(R.id.create);
        cancel = (Button)dialog.findViewById(R.id.cancel);

        no_trip_text = (TextView)findViewById(R.id.no_trip_text);
        no_trip_text.setVisibility(View.VISIBLE);
        trip_list = (ListView) findViewById(R.id.trip_list);
        dataModels= new ArrayList<>();
        mydb = new DBHelper(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        update_data();
    }

    public void update_data(){
        dataModels = mydb.getAllTrips();
        adapter= new CustomAdapter(dataModels, HomeScreen.this);
        trip_list.setAdapter(adapter);
        if(dataModels.isEmpty() || dataModels.size()==0){
            trip_list.setVisibility(View.GONE);
            no_trip_text.setVisibility(View.VISIBLE);
        }else {
            trip_list.setVisibility(View.VISIBLE);
            no_trip_text.setVisibility(View.GONE);
        }
    }

    public void showDialog(){

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);

        start_date_button = (LinearLayout) dialog.findViewById(R.id.start_date_button);
        end_date_button = (LinearLayout)dialog.findViewById(R.id.end_date_button);

        placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        placesAutocompleteTextView.setText(place.description);
                    }
                }
        );
        
        trip_name.setText("");
        placesAutocompleteTextView.setText("");
        
        start_date.setText(String.valueOf(mDay) + "/" + String.valueOf(mMonth) + "/" + String.valueOf(mYear));
        end_date.setText(String.valueOf(mDay) + "/" + String.valueOf(mMonth) + "/" + String.valueOf(mYear));

        start_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SELECT DATE START
                GetDate(start_date);
            }
        });

        end_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SELECT DATE START
                GetDate(end_date);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip_name.setText("");
                placesAutocompleteTextView.setText("");
                dialog.dismiss();

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;

                String name_text = trip_name.getText().toString();
                String placesAutocompleteTextView_text = placesAutocompleteTextView.getText().toString();

                if(TextUtils.isEmpty(name_text)){
                    valid=false;
                    trip_name.setError("Required Field");
                }else {
                    trip_name.setError(null);
                }

                if(TextUtils.isEmpty(placesAutocompleteTextView_text)){
                    valid=false;
                    placesAutocompleteTextView.setError("Required Field");
                }else {
                    placesAutocompleteTextView.setError(null);
                }

                if(valid){
                    if(mydb.CheckIsTAlreadyInDBorNot("trips","trip_name",name_text)){
                        Toast.makeText(HomeScreen.this, "Trip already exist with same name and place", Toast.LENGTH_SHORT).show();
                    }else {
                        if( mydb.insertTrip(name_text,
                                placesAutocompleteTextView_text,
                                start_date.getText().toString(),end_date.getText().toString())){
                            dialog.dismiss();
                            mydb.insertAvailability(pref.getString(Constants.user_name,""), name_text);
                            Toast.makeText(HomeScreen.this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                            update_data();
                        }else {
                            Toast.makeText(HomeScreen.this, "Some error occured, Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        dialog.show();

    }

    @Override
    public void onBackPressed() {

        if(dialog.isShowing()){
            dialog.dismiss();
        }else {
            super.onBackPressed();
        }
    }

    public void GetDate(final TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(HomeScreen.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;

                        textView.setText(String.valueOf(mDay) + "/" + String.valueOf(mMonth) + "/" + String.valueOf(mYear));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
