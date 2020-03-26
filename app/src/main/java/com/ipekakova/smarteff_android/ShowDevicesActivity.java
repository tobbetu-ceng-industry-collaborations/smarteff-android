package com.ipekakova.smarteff_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 2.02.2020.
 */

public class ShowDevicesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //private ArrayList<Device> devices;
    private ListView listView;
    private DeviceAdapter listViewAdapter;
    User currentUser;
    HttpRequest http;
    private BroadcastReceiver mMessageReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_all_devices);
        setContentView(R.layout.activity_nav_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_username_tv);

        Intent intent = getIntent();
        http = new HttpRequest(this);
        currentUser = (User) intent.getExtras().getSerializable("logged_in_user");
        navUsername.setText("Logged in as: "+ currentUser.getName().toUpperCase());

        Log.i("Logged in user infos:", currentUser.toString());
        initialize();
        Log.i("oncreate-devices",currentUser.getDevices().toString());
        //TODO

    }

    private void initialize() {
        listView = (ListView) findViewById(R.id.device_list);
        currentUser.setDevices(getUserDevices());
        //Log.i("initialize-devices",currentUser.getDevices().toString());
        listViewAdapter = new DeviceAdapter(ShowDevicesActivity.this, currentUser.getDevices(),currentUser);
        listView.setAdapter(listViewAdapter);
        Log.i("currentUser", String.valueOf(currentUser.getClass()));
        //usernameTv.setText("Logged in as: "+currentUser.getName());

    }

        //TODO
        @Override
        protected void onResume() {
            super.onResume();
            currentUser.setDevices(listViewAdapter.devices);
            LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mMessageReceiver,
                    new IntentFilter("myFunction"));
            //listViewAdapter.notifyDataSetChanged();
            //Log.i("onresume-devices", currentUser.getDevices().toString());
        }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mMessageReceiver);
    }
private ArrayList<Device> getUserDevices(){
    ArrayList<Device> devices = new ArrayList<Device>();
    try {
        //String devicesString  = (String) http.execute( getString(R.string.get_devices_url) ).get();
        String devicesString  = (String) http.execute( getString(R.string.get_devices_url)+ "/"+ currentUser.getId() ).get();
        Log.i("devices_url", getString(R.string.get_devices_url)+ "/"+ currentUser.getId());
        try {
            while (true) {
                JSONObject object = new JSONObject(devicesString);
                JSONArray devicesArray = new JSONArray(object.get("devices").toString());
                Log.i("devicesArray", devicesArray.toString());

                for (int i = 0 ; i < devicesArray.length() ; i++) {
                    JSONObject obj = new JSONObject(devicesArray.get(i).toString());
                    Log.i("obj",obj.toString());

                    Integer deviceId = (Integer) obj.get("id");
                    Log.i("Device Name: " ,deviceId.toString());

                    String deviceName = obj.get("name").toString();
                    Log.i("Device Name: " ,deviceName);
                    //Boolean isOn = obj.getBoolean("isOn");
                    Integer isOn = (Integer) obj.get("isOn");
                    JSONObject automationObj = new JSONObject(obj.get("automation").toString());
                    String suspended = automationObj.get("suspend").toString();
                    Log.i("suspend", suspended);

                    int isOnView, automationView;
                    String buttonText;
                    if (isOn == 1){
                        isOnView = R.drawable.radio_green;
                    }else{
                        isOnView = R.drawable.radio_red;
                    }
                    if (suspended.equals("True")){ // Otomatik kapatma ertelendiyse
                        automationView = R.drawable.radio_red;
                        buttonText = "Enable";
                        String expiration = automationObj.get("expiration").toString();
                        Device suspendendDevice = new SuspendendDevice(deviceId, isOnView, automationView, buttonText, expiration);
                        devices.add(suspendendDevice);
                        Log.i("expiration: " , expiration);
                    }
                    else{ //Automation active
                        automationView = R.drawable.radio_green;
                        buttonText = "Suspend";
                        Device automatedDevice = new AutomatedDevice(deviceId, isOnView, automationView, buttonText);
                        devices.add(automatedDevice);
                    }
                    //Device device = new Device(name, isOnView, automationView, buttonText);
                    //devices.add(device);
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
    Log.i("get-devices", devices.toString());
    return devices;
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_devices) {
            listViewAdapter.setDevices(currentUser.getDevices());
            listViewAdapter.notifyDataSetChanged();
        }
        else {

            ArrayList<Device> suspendedDevices = new ArrayList<>();

          /*
                String jsonObj =  (String) http.execute(R.string.get_scheduled_shutdowns+"/"+ currentUser.getId()).get();
                JSONObject object = new JSONObject(jsonObj);
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
                    int isOnView, automationView;
                    String buttonText;
                    if (isOn){
                        isOnView = R.drawable.radio_green;
                    }else{
                        isOnView = R.drawable.radio_red;
                    }
                    JSONObject automationObj = new JSONObject(obj.get("automation").toString());
                    Boolean suspended = automationObj.getBoolean("suspended");
                    String expiration = automationObj.get("expiration").toString();
                    Device d = new SuspendendDevice(name, isOnView, R.drawable.radio_red,"Enable", expiration);
                    suspendedDevices.add(d);
                }
        */
          for(int i = 0; i< currentUser.getDevices().size(); i++){
              Device d = currentUser.getDevices().get(i);
              if ( d.getClass().equals(SuspendendDevice.class)){
                  suspendedDevices.add(d);
              }else{

              }
          }
            Log.i("suspendedDevices", suspendedDevices.toString());
            listViewAdapter.setDevices(suspendedDevices);
            listViewAdapter.notifyDataSetChanged();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Context getActivity() {
        return this.getApplication();
    }
}
