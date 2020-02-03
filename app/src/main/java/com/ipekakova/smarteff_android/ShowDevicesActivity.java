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
    private ArrayList<Device> devices;
    private ListView listView;
    private DeviceAdapter listViewAdapter;
    String json_user = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices);
        Intent intent = getIntent();
        json_user = intent.getStringExtra("LoggedInUser");
        Log.i("Json User Extra:", json_user);
        //User user = intent.getExtra("loggedInUser");

        initialize();
        try {
            getUserDevices(devices, json_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fillArrayList(devices);
    }

    private void initialize() {
        devices = new ArrayList<Device>();
        listView = (ListView) findViewById(R.id.device_list);
        listViewAdapter = new DeviceAdapter(ShowDevicesActivity.this, devices);
        listView.setAdapter(listViewAdapter);
    }

    private void getUserDevices(ArrayList<Device> devices, String json_user) throws JSONException {

        final JSONObject jsonUser = new JSONObject(json_user);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL (getString(R.string.get_devices_url) + "/" +  jsonUser.get("id").toString());
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
    }

    private void fillArrayList(ArrayList<Device> devices) {
        for (int index = 0; index < 20; index++) {
            Device device = new Device("Device", R.drawable.radio_red, R.drawable.radio_green, true);
            devices.add(device);
        }

    }
}
