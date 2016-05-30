package com.tatsiana.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class AddedActivity extends AppCompatActivity {

    String info = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added);
        Intent intent = getIntent();
        String passedArg = intent.getStringExtra("args");
        info = passedArg;
        Log.i("Passed", passedArg);

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
            dateField.setText(jsonObject.getString("date"));

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

}
