package com.ipekakova.smarteff_android;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 2.02.2020.
 */

public class ShowDevicesActivity extends AppCompatActivity {
    //private ArrayList<Device> devices;
    private ListView listView;
    private DeviceAdapter listViewAdapter;
    User currentUser;
    HttpRequest http;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices);
        Intent intent = getIntent();
        http = new HttpRequest(this);
        currentUser = (User) intent.getExtras().getSerializable("logged_in_user");

        //currentUser = (User) intent.getSerializableExtra("logged_in_user");
        Log.i("Logged in user infos:", currentUser.toString());
        //User user = intent.getExtra("loggedInUser");
        initialize();
        Log.i("oncreate-devices",currentUser.getDevices().toString());

    }

    private void initialize() {
        listView = (ListView) findViewById(R.id.device_list);
        currentUser.setDevices(getUserDevices());
        Log.i("initialize-devices",currentUser.getDevices().toString());
        listViewAdapter = new DeviceAdapter(ShowDevicesActivity.this, currentUser.getDevices(),currentUser);
        listView.setAdapter(listViewAdapter);
    }

        //TODO
        @Override
        protected void onResume() {
            super.onResume();
            currentUser.setDevices(listViewAdapter.devices);
            //listViewAdapter.notifyDataSetChanged();
            Log.i("onresume-devices", currentUser.getDevices().toString());
        }


private ArrayList<Device> getUserDevices(){
    ArrayList<Device> devices = new ArrayList<Device>();
    try {
        String devicesString  = (String) http.execute(getString(R.string.get_devices_url)).get();
        try {
            while (true) {
                JSONObject object = new JSONObject(devicesString);
                JSONArray devicesArray = new JSONArray(object.get("devices").toString());
                Log.i("devicesArray", devicesArray.toString());

                for (int i = 0 ; i < devicesArray.length() ; i++) {
                    JSONObject obj = new JSONObject(devicesArray.get(i).toString());
                    Log.i("obj",obj.toString());
                    //String name = obj.get("name").toString();
                    int name = obj.getInt("name");
                    String devicename = "Device:" + name;
                    Log.i("Device Name: " ,devicename);
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
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
    return devices;
}
    private void fillArrayList(ArrayList<Device> devices) {
        for (int index = 0; index < 20; index++) {
            Device device = new Device(1, R.drawable.radio_red, R.drawable.radio_green, "Button");
            devices.add(device);
        }
    }
}
