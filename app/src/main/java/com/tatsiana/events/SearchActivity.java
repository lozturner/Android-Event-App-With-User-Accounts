package com.tatsiana.events;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    TextView lat;
    TextView lon;
    double pLat;
    double pLong;

    private ProgressDialog progress;

    class myLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                pLong = location.getLongitude();
                pLat = location.getLatitude();

                lat.setText(Double.toString(pLat));
                lon.setText(Double.toString(pLong));
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new myLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    }

    public void sendGetRequest(View View) {
        new GetClass(this).execute();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public GetClass(Context c) {
            this.context = c;
        }

        EditText cityField = (EditText) findViewById(R.id.city);
        String city = cityField.getText().toString();

        EditText zipField = (EditText) findViewById(R.id.zip);
        String zip = zipField.getText().toString();

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                String str = "";
                if(zip.equals("")){
                    str = "http://52.38.126.224:9000/api/city/" + city;
                }
                else{
                    str = "http://52.38.126.224:9000/api/zip/" + zip;
                }

                URL url = new URL (str);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                SearchActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
                        String value = responseOutput.toString();
                        Log.i("Value of value is", value);
                        intent.putExtra("args", value);
                        startActivity(intent);
                    }
                });

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    public void fillInfo(View view) throws IOException {
        Geocoder geocoder= new Geocoder(this, Locale.getDefault());

        EditText cityField = (EditText) findViewById(R.id.city);
        EditText zipField = (EditText) findViewById(R.id.zip);

        List<Address> addresses = geocoder.getFromLocation(pLat, pLong, 1);

        if(addresses.size() != 0){
            String city = addresses.get(0).getLocality();
            cityField.setText(city);
            String postalCode = addresses.get(0).getPostalCode();
            zipField.setText(postalCode);
        }

        else{
            Log.i("address", "nothing");
        }

    }

    protected void backToMain (View view){
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
    }

}