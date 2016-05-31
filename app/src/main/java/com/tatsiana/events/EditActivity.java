package com.tatsiana.events;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    String id = "";
    String username = "";
    String password = "";
    String event = "";
    String value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        username = extras.getString("username");
        password = extras.getString("password");
        event = extras.getString("event");

        try {
            URL url = new URL ("http://52.38.126.224:9000/api/events/" + event);

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

            value = responseOutput.toString();
            Log.i("Value of value is", value);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(value);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String name = jsonObject.getString("name");
            String dateString = jsonObject.getString("date");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            String addressInfo = jsonObject.getString("address");
            JSONObject jsonObjectAddress = new JSONObject(addressInfo);

            String street = jsonObjectAddress.getString("street");
            String city = jsonObjectAddress.getString("city");
            String state = jsonObjectAddress.getString("state");
            String zip = jsonObjectAddress.getString("zip");

            EditText nameField = (EditText) findViewById(R.id.nameT);
            EditText dateField = (EditText) findViewById(R.id.date);
            EditText streetField = (EditText) findViewById(R.id.street);
            EditText stateField = (EditText) findViewById(R.id.state);
            EditText cityField = (EditText) findViewById(R.id.city);
            EditText zipField = (EditText) findViewById(R.id.zip);

            nameField.setText(name);
            dateField.setText(dateFormat.format(convertedDate));
            streetField.setText(street);
            stateField.setText(state);
            cityField.setText(city);
            zipField.setText(zip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPutRequest(View View) {
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

        new PutClass(this).execute();
    }

    private class PutClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PutClass(Context c) {
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

                URL url = new URL("http://52.38.126.224:9000/api/events/" + event);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "name=" + name + "&date=" + date + "&street=" + street + "&city=" + city + "&state=" + state + "&zip=" + zip + "&owner_id=" + id;
                connection.setRequestProperty("Authorization", "Basic " + encodedString);
                connection.setRequestMethod("PUT");
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
                String value = responseOutput.toString();

                EditActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView message = (TextView) findViewById(R.id.messageSaved);
                        message.setText("Changes were saved");
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

    protected void backToMain (View view){
        Intent intent = new Intent(EditActivity.this,ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("id", id);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
