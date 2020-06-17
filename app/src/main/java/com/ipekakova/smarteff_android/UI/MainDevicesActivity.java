package com.ipekakova.smarteff_android.UI;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ipekakova.smarteff_android.Adapters.DeviceAdapter;
import com.ipekakova.smarteff_android.DateUtils;
import com.ipekakova.smarteff_android.Models.AutomatedDevice;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import com.ipekakova.smarteff_android.Services.HttpGetAsyncTask;
import com.ipekakova.smarteff_android.Services.UpdateDevicesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 22.04.2020.
 */

public class MainDevicesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DrawerLayout drawer;
    @Bind(R.id.device_list)
    RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;
    User currentUser;
    HttpGetAsyncTask http;
    SharedPreferences sp;
    private String TAG="ShowDevicesFragment";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_all_devices);
        setContentView(R.layout.activity_nav_main);
        startUpdateService();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_username_tv);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ShowDevicesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_devices);
        }
        currentUser = User.getInstance();
        http = new HttpGetAsyncTask(this);
        navUsername.setText("Logged in as: "+ currentUser.getName().toUpperCase());
        Log.i("Logged in user infos:", currentUser.toString());
        try {
            initialize();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("oncreate-devices",currentUser.getDevices().toString());
        //TODO

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    public void startUpdateService() {
        Intent intent = new Intent( MainDevicesActivity.this, UpdateDevicesService.class);
        intent.putExtra("sleepTime", 50);
        startService(intent);
    }

    private void initialize() throws ExecutionException, InterruptedException {
        //listView = (ListView) findViewById(R.id.device_list);
        String devicesString = (String) http.execute(getString(R.string.get_devices_url) + "/" + currentUser.getId()).get();
        currentUser.setDevices(parseJsonForDevices(devicesString));
        Log.i("currentUser", String.valueOf(currentUser.getClass()));

    }
    //TODO
    @Override
    protected void onResume() {
        super.onResume();
        //Broadcast receiver’ı register ederken bir intent filter oluşturmamız ve bu intent filter’a bir action belirtmemiz gerekir.
        // Bu sayede broadcast receiverın OnReceive methodu hangi olayda tetiklenecekse bunu android işletim sistemine belirtmiş oluruz.
        /*
        IntentFilter filter=new IntentFilter();
        filter.addAction("my.action");
        registerReceiver(myBroadcastReceiver, filter);
        */
    }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverPost);
        //unregisterReceiver(myBroadcastReceiver);
    }

    public ArrayList<Device> parseJsonForDevices(String devicesString){
        ArrayList<Device> devices = new ArrayList<Device>();
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
                    String device_type = obj.getString("type");
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
                        DateUtils dateUtils = new DateUtils();
                        Log.d("locale: ", String.valueOf(Locale.getDefault()));
                        Calendar cal = dateUtils.getCalendarFromDateTime(automationObj.getString("expiration"));
                        Log.d("Date: ",cal.toString());
                        Device suspendendDevice = new SuspendendDevice(deviceId, isOnView, automationView, buttonText, cal, device_type);
                        devices.add(suspendendDevice);
                    }
                    else{ //Automation active
                        automationView = R.drawable.radio_green;
                        buttonText = "Suspend";
                        Device automatedDevice = new AutomatedDevice(deviceId, isOnView, automationView, buttonText, device_type);
                        devices.add(automatedDevice);
                    }
                }

                break;
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG,"Couldn't parse the expiration format!!");
        }
        return devices;
    }


    public Context getActivity() {
        return this.getApplication();
    }


    private BroadcastReceiver broadcastReceiverPost = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                String result = bundle.getString("json");
                Log.i("received broadcast: ", result);
                mAdapter.notifyDataSetChanged();
            }
        }
    };



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_devices:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ShowDevicesFragment()).commit();
                break;
            case R.id.nav_suspensions:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SuspendedShutdownsFragment()).commit();
                break;
            case R.id.nav_automations:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AutomatedShutdownsFragment()).commit();
                break;

        }
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
