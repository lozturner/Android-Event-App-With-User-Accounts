//Used sources:
// https://www.numetriclabz.com/android-post-and-get-request-using-httpurlconnection/

package com.tatsiana.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class SignupActivity extends AppCompatActivity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signUp(View View) {
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

        new SingUpClass(this).execute();
    }

    private class SingUpClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public SingUpClass(Context c) {
            this.context = c;
        }

        EditText emailField = (EditText) findViewById(R.id.email);
        String email = emailField.getText().toString();

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

                URL url = new URL("http://52.38.126.224:9000/api/users");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "username=" + email + "&password=" + password;
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

                SignupActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(SignupActivity.this, AccountActivity.class);
                        intent.putExtra("user", email);
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

    protected void signInRedirect (View view){
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    protected void backToMain (View view){
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
