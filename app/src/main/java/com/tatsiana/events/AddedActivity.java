package com.tatsiana.events;

import android.content.Intent;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddedActivity extends AppCompatActivity {

    String info = "";
    String id = "";
    String username = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        username = extras.getString("username");
        password = extras.getString("password");
        info = extras.getString("args");
        Log.i("Passed", info);

        TextView nameField = (TextView) findViewById(R.id.name);
        TextView cityField = (TextView) findViewById(R.id.city);
        TextView dateField = (TextView) findViewById(R.id.date);
        TextView streetField = (TextView) findViewById(R.id.street);
        TextView stateField = (TextView) findViewById(R.id.state);
        TextView zipField = (TextView) findViewById(R.id.zip);

        try {
            JSONObject jsonObject = new JSONObject(info);

            nameField.setText(jsonObject.getString("name"));
            Log.i("Name", jsonObject.getString("name"));

            String dateString = jsonObject.getString("date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            dateField.setText(dateFormat.format(convertedDate));

            String addressInfo = jsonObject.getString("address");
            Log.i("Address", addressInfo);
            JSONObject jsonObject2 = new JSONObject(addressInfo);


            cityField.setText(jsonObject2.getString("city"));
            streetField.setText(jsonObject2.getString("street"));
            stateField.setText(jsonObject2.getString("state"));
            zipField.setText(jsonObject2.getString("zip"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void backToMain (View view){
        Intent intent = new Intent(AddedActivity.this, AddActivity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("id", id);
        intent.putExtras(extras);
        startActivity(intent);
    }

}
