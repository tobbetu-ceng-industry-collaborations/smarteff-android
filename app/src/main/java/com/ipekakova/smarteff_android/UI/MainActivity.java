package com.ipekakova.smarteff_android.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import com.ipekakova.smarteff_android.Services.HttpGetAsyncTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    HashMap<Integer,String> userHashMap;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> tv_adapter;
    AutoCompleteTextView autoCompleteTextView;
    EditText tv_password;
    Button login_button;
    User loggedInUser;
    HttpGetAsyncTask http;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (sp.getBoolean("logged", false)) {
            goToDevicesActivity();
        }
        else{
            http = new HttpGetAsyncTask(this);
            userHashMap = getUsersMap();
            tv_password = (EditText) findViewById(R.id.tv_password);
            login_button = (Button) findViewById(R.id.loginButton);
            Log.i("userHashMap", userHashMap.toString());
            //Creating an ArrayList of keys(user ids) by passing the keySet
            Set<Integer> keySet = userHashMap.keySet();
            final ArrayList<Integer> userIds = new ArrayList<Integer>(keySet);

            //Creating an ArrayList of values(user names) by passing the valueSet
            final Collection<String> values = userHashMap.values();
            ArrayList<String> users = new ArrayList<String>(values);

            autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.text_view);
            Log.i("LIST", users.toString());
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, users);
            tv_adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, users);
            autoCompleteTextView.setAdapter(tv_adapter);

            autoCompleteTextView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String user_name = parent.getItemAtPosition(position).toString();
                    int user_id = (int) userIds.get(position);
                    Log.i("Selected item : ", user_name);
                    Log.i("Selected item : ", String.valueOf(user_id));

                    sp.edit().putString("user_name",user_name).apply();
                    sp.edit().putInt("user_id",user_id).apply();

                    loggedInUser = User.getInstance();
                    loggedInUser.setId(user_id);
                    loggedInUser.setName(user_name);
                    JSONObject user_json = new JSONObject();
                    try {
                        user_json.put("id", user_id);
                        user_json.put("name", user_name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("SELECTED", user_json.toString());

                }
            }));

            autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    autoCompleteTextView.showDropDown();
                    return false;
                }
            });

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Allows the adapter to show the data inside the spinner

        } // end of else
    }   // END OF ONCREATE() METHOD


    public HashMap<Integer, String> getUsersMap(){
        HashMap<Integer,String> users = new HashMap<Integer, String>();
        try {
            String usersString  = (String) http.execute(getString(R.string.get_users_url)).get();
            Log.i("String", usersString);
            try {
                while (true) {
                    JSONObject object = new JSONObject(usersString);
                    JSONArray usersArray = new JSONArray(object.get("people").toString());
                    for (int i = 0 ; i < usersArray.length() ; i++) {
                        JSONObject obj = new JSONObject(usersArray.get(i).toString());
                        //Log.i("user",obj.toString());
                        String name = obj.get("name").toString();
                        Integer id = (Integer) obj.get("id");
                        //usersDictionary.put(id, name);
                        Log.i("Id: " , String.valueOf(id));
                        Log.i("Name: " ,name);
                        users.put(id, name);
                    }
                    break;
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return users;
    }

    //Listener for Login Button
    public void loginListener(View view) {
        //Toast.makeText(getApplicationContext(),"Login Deneme",Toast.LENGTH_LONG).show();
        Intent loginIntent = new Intent(getApplicationContext(), MainDevicesActivity.class);
        startActivity(loginIntent);
    }
    public void goToDevicesActivity(){
        Intent i = new Intent(this,MainDevicesActivity.class);
        startActivity(i);
    }
}
