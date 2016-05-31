//Used sources:
// https://www.numetriclabz.com/android-post-and-get-request-using-httpurlconnection/

package com.tatsiana.events;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    String info = "";
    String id = "";
    String username = "";
    String password = "";
    //list for holding ids for deleting and editing
    ArrayList<String> listId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        username = extras.getString("username");
        password = extras.getString("password");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
                        ArrayList<String> list = new ArrayList<String>();
                        ArrayList<String> dateArray = new ArrayList<String>();
                        ArrayList<String> addressArray = new ArrayList<String>();

                        try {
                            JSONArray jsonArray = new JSONArray(value);

                            for (int i = 0; i < jsonArray.length(); i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                String id = jsonObject.getString("_id");

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

                                list.add(name);
                                listId.add(id);
                                dateArray.add("Date: " + dateFormat.format(convertedDate));
                                addressArray.add("Address: " + street + ",  " +  city + ", " + state + " "+ zip);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //instantiate custom adapter
                        MyCustomAdapter adapter = new MyCustomAdapter(list, dateArray, addressArray, ProfileActivity.this);

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
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putString("password", password);
        extras.putString("id", id);
        intent.putExtras(extras);
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
                view = inflater.inflate(R.layout.event, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(nameList.get(position));

            TextView listItemText2 = (TextView)view.findViewById(R.id.date);
            listItemText2.setText(dateList.get(position));

            TextView listItemText3 = (TextView)view.findViewById(R.id.address);
            listItemText3.setText(addressList.get(position));

            //Handle buttons and add onClickListeners
            Button deleteBtn = (Button)view.findViewById(R.id.delete);
            Button editBtn = (Button)view.findViewById(R.id.edit);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String event = listId.get(position);
                    Log.i("DELETE", event);

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

                    nameList.remove(position);
                    dateList.remove(position);
                    addressList.remove(position);
                    notifyDataSetChanged();
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String event = listId.get(position);
                    Log.i("Event for Edition", event);

                    Intent intent = new Intent(ProfileActivity.this, EditActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("username", username);
                    extras.putString("password", password);
                    extras.putString("id", id);
                    extras.putString("event", event);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
