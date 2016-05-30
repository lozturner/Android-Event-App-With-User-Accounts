package com.tatsiana.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    String info = "";
    String id = "";
    String username = "";
    String password = "";
    String[] myDataArray = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        String passedArg = intent.getStringExtra("args");
        String pass = intent.getStringExtra("pass");
        info = passedArg;
        password = pass;
        Log.i("Passed", passedArg);

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(info);
            JSONObject jsonObject = null;
            jsonObject = jArray.getJSONObject(0);
            id = jsonObject.getString("_id");
            Log.i("ID passed", id);
            username = jsonObject.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getEvents(View View) {
        new EventsClass(this).execute();
    }

    private class EventsClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public EventsClass(Context c) {
            this.context = c;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String userPassword = username + ":" + password;
                String encodedString = Base64.encodeToString(userPassword.getBytes(),
                        Base64.DEFAULT);
                System.out.println("Encoded String : " + encodedString);

                URL url = new URL("http://52.38.126.224:9000/api/events/users/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + encodedString);
                connection.setRequestMethod("GET");
                //connection.setDoOutput(false);
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

                final String value = responseOutput.toString();
                Log.i("Value of value is", value);

                ProfileActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //generate list
                        ArrayList<String> list = new ArrayList<>();

                        try {
                            JSONArray jsonArray = new JSONArray(value);

                            myDataArray = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                String id = jsonObject.getString("_id");

                                list.add(name);
                                list.add(id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //instantiate custom adapter
                        MyCustomAdapter adapter = new MyCustomAdapter(list, ProfileActivity.this);

                        //handle listview and assign adapter
                        ListView lView = (ListView)findViewById(R.id.eventListView);
                        lView.setAdapter(adapter);
                    }
                });


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    protected void searchRedirect (View view){
        Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    protected void signOut (View view){
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    protected void addRedirect (View view){
        Intent intent = new Intent(ProfileActivity.this, AddActivity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("id", id);
        intent.putExtras(extras);

        Log.i("ID extra", id);
        Log.i("name extra", username);
        Log.i("pass extra", password);
        startActivity(intent);
    }

    private class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;



        public MyCustomAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            /*int halfCountOfList = list.size()/2;
            // Add +1 in halfCountOfList if itemList size is odd.
            int finalCount = halfCountOfList + list.size()%2;
            return finalCount;*/
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.event, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            Button deleteBtn = (Button)view.findViewById(R.id.delete);
            Button editBtn = (Button)view.findViewById(R.id.edit);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.i("item", list.get(position));

                    //list.remove(position);

                    String event = list.get(position);
                    Log.i("Event for Deletion", event);

                    try {
                        String userPassword = username + ":" + password;
                        String encodedString = Base64.encodeToString(userPassword.getBytes(),
                                Base64.DEFAULT);
                        System.out.println("Encoded String : " + encodedString);

                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("http://52.38.126.224:9000/api/events/" + event)
                                .delete()
                                .addHeader("Authorization", "Basic " + encodedString.substring(0, 12))
                                .build();

                        Response response = client.newCall(request).execute();
                        System.out.println("RESPONSE : " + response);

                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = getIntent();
                    finish();
                    TextView deletedMessage = (TextView)findViewById(R.id.deleted);
                    deletedMessage.setText("Selected event is removed");
                    startActivity(intent);
                    notifyDataSetChanged();
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.i("Will be edited", "Something");
                }
            });

            return view;
        }
    }
}
