package com.ipekakova.smarteff_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 2.02.2020.
 */

public class ShowDevicesActivity extends AppCompatActivity {
    //private ArrayList<Device> devices;
    private ListView listView;
    private DeviceAdapter listViewAdapter;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices);
        Intent intent = getIntent();
        currentUser = (User) intent.getExtras().getSerializable("logged_in_user");
        //currentUser = (User) intent.getSerializableExtra("logged_in_user");
        Log.i("Logged in user infos:", currentUser.toString());
        //User user = intent.getExtra("loggedInUser");
        initialize();

    }

    private void initialize() {
        listView = (ListView) findViewById(R.id.device_list);
        currentUser.setDevices(fillUserDevices());
        listViewAdapter = new DeviceAdapter(ShowDevicesActivity.this, currentUser.getDevices());
        listView.setAdapter(listViewAdapter);
    }

    private ArrayList<Device> fillUserDevices()  {
        final ArrayList<Device> devices = new ArrayList<Device>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL (getString(R.string.get_devices_url) );
                    //URL url = new URL (getString(R.string.get_devices_url) + "/" +  jsonUser.get("id").toString());
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
                                JSONArray devicesArray = new JSONArray(object.get("devices").toString());
                                Log.i("devicesArray", devicesArray.toString());

                                for (int i = 0 ; i < devicesArray.length() ; i++) {
                                    JSONObject obj = new JSONObject(devicesArray.get(i).toString());
                                    Log.i("obj",obj.toString());
                                    String name = obj.get("name").toString();
                                    Log.i("Device Name: " ,name);
                                    Boolean isOn = obj.getBoolean("isOn");
                                    JSONObject automationObj = new JSONObject(obj.get("automation").toString());
                                    Boolean suspended = automationObj.getBoolean("suspended");

                                    int isOnView, automationView;
                                    String buttonText;
                                    if (isOn){
                                        isOnView = R.drawable.radio_green;
                                    }else{
                                        isOnView = R.drawable.radio_red;
                                    }
                                    if (suspended){ // Otomatik kapatma ertelendiyse
                                        automationView = R.drawable.radio_red;
                                        buttonText = "Enable";
                                        String expiration = automationObj.get("expiration").toString();
                                        Log.i("expiration: " , expiration);
                                    }
                                    else{ //Automation active
                                        automationView = R.drawable.radio_green;
                                        buttonText = "Suspend";
                                    }
                                    Device device = new Device(name, isOnView, automationView, buttonText);
                                    devices.add(device);
                                    //usersDictionary.put(id, name);
                                    //Log.i("IsOn: " , String.valueOf(isOn));
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
        return devices;
    }

    private void fillArrayList(ArrayList<Device> devices) {
        for (int index = 0; index < 20; index++) {
            Device device = new Device("Device", R.drawable.radio_red, R.drawable.radio_green, "Button");
            devices.add(device);
        }
    }
}
