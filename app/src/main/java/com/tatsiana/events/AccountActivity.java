package com.tatsiana.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AccountActivity extends AppCompatActivity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

       EditText u = (EditText) findViewById(R.id.email);
        u.setText(user);
    }
    public void signIn (View View) {
        EditText e = (EditText) findViewById(R.id.email);
        if(e.getText().toString().equals("")){
            e.setError("Cannot be empty");
            return;
        }

        EditText p = (EditText) findViewById(R.id.password);
        if(p.getText().toString().equals("")){
            p.setError("Cannot be empty");
            return;
        }

        new SingInClass(this).execute();
    }

    private class SingInClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public SingInClass(Context c) {
            this.context = c;
        }

        String id = "";

        EditText emailField = (EditText) findViewById(R.id.email);
        String username = emailField.getText().toString();

        EditText passwordField = (EditText) findViewById(R.id.password);
        String password = passwordField.getText().toString();


        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                String userPassword = username + ":" + password;
                String encodedString = Base64.encodeToString(userPassword.getBytes(),
                        Base64.DEFAULT);
                System.out.println("Encoded String : " + encodedString);

                URL url = new URL("http://52.38.126.224:9000/api/users/" + username);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + encodedString);
                connection.setRequestMethod("GET");
                connection.setUseCaches(true);
                connection.setDefaultUseCaches(true);
                connection.setConnectTimeout(360);
                connection.setInstanceFollowRedirects(true);
                HttpsURLConnection.setFollowRedirects(true);
                connection.setReadTimeout(360);

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                AccountActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        String value = responseOutput.toString();
                        Log.i("Value of value is", value);

                        JSONArray jArray;
                        try {
                            jArray = new JSONArray(value);
                            JSONObject jsonObject;
                            jsonObject = jArray.getJSONObject(0);
                            id = jsonObject.getString("_id");
                            username = jsonObject.getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(AccountActivity.this, ProfileActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("username", username);
                        extras.putString("password", password);
                        extras.putString("id", id);
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

}
