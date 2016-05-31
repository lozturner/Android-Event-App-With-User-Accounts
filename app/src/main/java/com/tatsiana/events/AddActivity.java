//Used sources:
// https://www.numetriclabz.com/android-post-and-get-request-using-httpurlconnection/

package com.tatsiana.events;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    TextView lat;
    TextView lon;
    double pLat;
    double pLong;
    String id = "";
    String username = "";
    String password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        username = extras.getString("username");
        password = extras.getString("password");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new mylocationlistener();
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

    public void sendPostRequest(View View) {
        EditText n = (EditText) findViewById(R.id.nameT);
        if(n.getText().toString().equals("")){
            n.setError("Cannot be empty");
            return;
        }

        EditText d = (EditText) findViewById(R.id.date);
        if(d.getText().toString().equals("")){
            d.setError("Cannot be empty");
            return;
        }

        EditText c = (EditText) findViewById(R.id.city);
        if(c.getText().toString().equals("")){
            c.setError("Cannot be empty");
            return;
        }

        EditText str = (EditText) findViewById(R.id.street);
        if(str.getText().toString().equals("")){
            str.setError("Cannot be empty");
            return;
        }

        EditText st = (EditText) findViewById(R.id.state);
        if(st.getText().toString().equals("")){
            st.setError("Cannot be empty");
            return;
        }

        EditText z = (EditText) findViewById(R.id.zip);
        if(z.getText().toString().equals("")){
            z.setError("Cannot be empty");
            return;
        }

        new PostClass(this).execute();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class mylocationlistener implements LocationListener {

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


    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostClass(Context c) {
            this.context = c;
        }

        EditText nameField = (EditText) findViewById(R.id.nameT);
        String name = nameField.getText().toString();

        EditText cityField = (EditText) findViewById(R.id.city);
        String city = cityField.getText().toString();

        EditText dateField = (EditText) findViewById(R.id.date);
        String date = dateField.getText().toString();

        EditText streetField = (EditText) findViewById(R.id.street);
        String street = streetField.getText().toString();

        EditText stateField = (EditText) findViewById(R.id.state);
        String state = stateField.getText().toString();

        EditText zipField = (EditText) findViewById(R.id.zip);
        String zip = zipField.getText().toString();

        @Override
        protected Void doInBackground(String... params) {
            try {

                String userPassword = username + ":" + password;
                String encodedString = Base64.encodeToString(userPassword.getBytes(),
                        Base64.DEFAULT);
                System.out.println("Encoded String : " + encodedString);

                URL url = new URL("http://52.38.126.224:9000/api/events");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "name=" + name + "&date=" + date + "&street=" + street + "&city=" + city + "&state=" + state + "&zip=" + zip + "&owner_id=" + id;
                connection.setRequestProperty("Authorization", "Basic " + encodedString);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();
                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                AddActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(AddActivity.this, AddedActivity.class);
                        String value = responseOutput.toString();
                        Bundle extras = new Bundle();
                        extras.putString("username", username);
                        extras.putString("password", password);
                        extras.putString("id", id);
                        extras.putString("args", value);
                        intent.putExtras(extras);
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
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        EditText cityField = (EditText) findViewById(R.id.city);
        EditText stateField = (EditText) findViewById(R.id.state);
        EditText zipField = (EditText) findViewById(R.id.zip);

        List<Address> addresses = geocoder.getFromLocation(pLat, pLong, 1); //1 represent max location result to returned

        String city = addresses.get(0).getLocality();
        cityField.setText(city);
        String state = addresses.get(0).getAdminArea();
        stateField.setText(state);
        String postalCode = addresses.get(0).getPostalCode();
        zipField.setText(postalCode);
    }

    protected void backToMain (View view){
        Intent intent = new Intent(AddActivity.this,ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("id", id);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
