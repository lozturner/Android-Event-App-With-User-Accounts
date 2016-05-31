package com.tatsiana.events;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    String info = "";
    ListView mListView;
    String id = "";
    String username = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        username = extras.getString("username");
        password = extras.getString("password");
        info = extras.getString("args");

        ArrayList<String> myDataArray = new ArrayList<String>();
        ArrayList<String> dateArray = new ArrayList<String>();
        ArrayList<String> addressArray = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(info);

            for (int i = 0; i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
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

                myDataArray.add("Event name: " + name);
                dateArray.add("Date: " + dateFormat.format(convertedDate));
                addressArray.add("Address: " + street + ",  " +  city + ", " + state + " "+ zip);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyCustomAdapter stringAdapter = new MyCustomAdapter(myDataArray, dateArray, addressArray, this);
        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setAdapter(stringAdapter);

    }

    protected void backToMain (View view){
        Intent intent = new Intent(ResultActivity.this, SearchActivity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("id", id);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> nameList = new ArrayList<String>();
        private ArrayList<String> dateList = new ArrayList<String>();
        private ArrayList<String> addressList = new ArrayList<String>();
        private Context context;


        public MyCustomAdapter(ArrayList<String> nameList, ArrayList<String> dateList, ArrayList<String> addressList, Context context) {
            this.nameList = nameList;
            this.dateList = dateList;
            this.addressList = addressList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return nameList.size();
        }

        @Override
        public Object getItem(int pos) {
            return nameList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.tv);
            listItemText.setText(nameList.get(position));

            TextView listItemText2 = (TextView)view.findViewById(R.id.date);
            listItemText2.setText(dateList.get(position));

            TextView listItemText3 = (TextView)view.findViewById(R.id.address);
            listItemText3.setText(addressList.get(position));

            return view;
        }
    }
}
