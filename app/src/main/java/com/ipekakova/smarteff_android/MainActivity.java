package com.ipekakova.smarteff_android;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();
    ArrayList<Integer> userIds = new ArrayList<>();

    ArrayAdapter<String> adapter;
    Spinner usersSpinner;
    User loggedInUser;
    Map<Integer, String> usersDictionary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        users = sendGet();
        setContentView(R.layout.activity_main);

        //final AutoCompleteTextView usersSpinner = (AutoCompleteTextView) findViewById(R.id.text_view);
        usersSpinner = (Spinner) findViewById(R.id.spinner);
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        Log.i("LIST", users.toString());

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Allows the adapter to show the data inside the spinner
        usersSpinner.setAdapter(adapter);

        usersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                // TODO Auto-generated method stub
                String user_name = usersSpinner.getSelectedItem().toString();
                int user_id = (int) userIds.get(position);
                //usersDictionary.get(user_id);
                Log.i("Selected item : ",user_name);
                Log.i("Selected item : ", String.valueOf(user_id));
                loggedInUser = new User(user_id, user_name);
                JSONObject user_json =new JSONObject();
                try {
                    user_json.put("id",user_id);
                    user_json.put("name",user_name);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SELECTED", user_json.toString());
                LoginIntent(user_json.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
               return false;
               }

          @Override
          public boolean onQueryTextChange(String newText) {
              adapter.getFilter().filter((newText));
              return true;
               }
          }
        );
        return super.onCreateOptionsMenu(menu);
    }

    public ArrayList<String> sendGet(){
        usersDictionary = new HashMap<Integer, String>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL (getString(R.string.get_users_url));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");

                        }
                        bufferedReader.close();
                        //return stringBuilder.toString();
                        try {

                            while (true) {
                                JSONObject object = new JSONObject(stringBuilder.toString());
                                JSONArray stepsArray = new JSONArray(object.get("people").toString());

                                for (int i = 0 ; i < stepsArray.length() ; i++) {
                                    JSONObject obj = new JSONObject(stepsArray.get(i).toString());
                                    //Log.i("user",obj.toString());
                                    String name = obj.get("name").toString();
                                    Integer id = (Integer) obj.get("id");
                                    //usersDictionary.put(id, name);
                                    Log.i("Id: " , String.valueOf(id));
                                    Log.i("Name: " ,name);

                                    users.add(name);
                                    userIds.add(id);
                                }

                                break;
                            }
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    } finally {
                        conn.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        return users;
    }

    public void LoginIntent(String loggedInUser) {
        Intent loginIntent = new Intent(getApplicationContext(), ShowDevicesActivity.class);
        loginIntent.putExtra("LoggedInUser",  loggedInUser);
        startActivity(loginIntent);
    }


}
