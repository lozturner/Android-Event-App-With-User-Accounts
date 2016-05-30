package com.tatsiana.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {

    String info = "";
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String passedArg = intent.getStringExtra("args");
        info = passedArg;
        Log.i("Passed", passedArg);

        String[] myDataArray = {};

        try {
            JSONArray jsonArray = new JSONArray(info);

            myDataArray = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");

                myDataArray[i] = name;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(this, R.layout.item, myDataArray);
        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setAdapter(stringAdapter);

    }

    protected void backToMain (View view){
        Intent intent = new Intent(ResultActivity.this, SearchActivity.class);
        startActivity(intent);
    }
}
