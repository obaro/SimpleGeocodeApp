package com.sample.foo.simplegeocodeapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivityWithAsyncTask extends AppCompatActivity {

    EditText latitudeEdit, longitudeEdit, addressEdit;
    ProgressBar progressBar;
    TextView infoText;
    CheckBox checkBox;

    public static final int USE_ADDRESS_NAME = 1;
    public static final int USE_ADDRESS_LOCATION = 2;

    int fetchType = USE_ADDRESS_LOCATION;

    private static final String TAG = "MAIN_ACTIVITY_ASYNC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitudeEdit = (EditText) findViewById(R.id.longitudeEdit);
        latitudeEdit = (EditText) findViewById(R.id.latitudeEdit);
        addressEdit = (EditText) findViewById(R.id.addressEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        infoText = (TextView) findViewById(R.id.infoText);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioAddress:
                if (checked) {
                    fetchType = USE_ADDRESS_NAME;
                    longitudeEdit.setEnabled(false);
                    latitudeEdit.setEnabled(false);
                    addressEdit.setEnabled(true);
                    addressEdit.requestFocus();
                }
                break;
            case R.id.radioLocation:
                if (checked) {
                    fetchType = USE_ADDRESS_LOCATION;
                    latitudeEdit.setEnabled(true);
                    latitudeEdit.requestFocus();
                    longitudeEdit.setEnabled(true);
                    addressEdit.setEnabled(false);
                }
                break;
        }
    }

    public void onButtonClicked(View view) {
        new GeocodeAsyncTask().execute();
    }

    class GeocodeAsyncTask extends AsyncTask<Void, Void, Address> {

        String errorMessage = "";

        @Override
        protected void onPreExecute() {
            infoText.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Address doInBackground(Void ... none) {
            Geocoder geocoder = new Geocoder(MainActivityWithAsyncTask.this, Locale.getDefault());
            List<Address> addresses = null;

            if(fetchType == USE_ADDRESS_NAME) {
                String name = addressEdit.getText().toString();
                try {
                    addresses = geocoder.getFromLocationName(name, 1);
                } catch (IOException e) {
                    errorMessage = "Service not available";
                    Log.e(TAG, errorMessage, e);
                }
            }
            else if(fetchType == USE_ADDRESS_LOCATION) {
                double latitude = Double.parseDouble(latitudeEdit.getText().toString());
                double longitude = Double.parseDouble(longitudeEdit.getText().toString());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException ioException) {
                    errorMessage = "Service Not Available";
                    Log.e(TAG, errorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    errorMessage = "Invalid Latitude or Longitude Used";
                    Log.e(TAG, errorMessage + ". " +
                            "Latitude = " + latitude + ", Longitude = " +
                            longitude, illegalArgumentException);
                }
            }
            else {
                errorMessage = "Unknown Type";
                Log.e(TAG, errorMessage);
            }

            if(addresses != null && addresses.size() > 0)
                return addresses.get(0);

            return null;
        }

        protected void onPostExecute(Address address) {
            if(address == null) {
                progressBar.setVisibility(View.INVISIBLE);
                infoText.setVisibility(View.VISIBLE);
                infoText.setText(errorMessage);
            }
            else {
                String addressName = "";
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressName += " --- " + address.getAddressLine(i);
                }
                progressBar.setVisibility(View.INVISIBLE);
                infoText.setVisibility(View.VISIBLE);
                infoText.setText("Latitude: " + address.getLatitude() + "\n" +
                        "Longitude: " + address.getLongitude() + "\n" +
                        "Address: " + addressName);
            }
        }
    }
}
