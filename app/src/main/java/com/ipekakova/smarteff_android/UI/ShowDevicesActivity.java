package com.ipekakova.smarteff_android.UI;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ipekakova.smarteff_android.R;
import com.ipekakova.smarteff_android.UI.*;
import com.ipekakova.smarteff_android.Adapters.DeviceAdapter;
import com.ipekakova.smarteff_android.Models.AutomatedDevice;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.Services.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 2.02.2020.
 */

public class ShowDevicesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    @Bind(R.id.device_list) RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;
    User currentUser;
    HttpGetAsyncTask http;
    SharedPreferences sp;
    private String TAG="ShowDevicesActivity";


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
        sp = getSharedPreferences("login",MODE_PRIVATE);
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
        Intent intent = new Intent( ShowDevicesActivity.this, UpdateDevicesService.class);
        intent.putExtra("sleepTime", 50);
        startService(intent);
    }

    private void initialize() throws ExecutionException, InterruptedException {
        //listView = (ListView) findViewById(R.id.device_list);
        String devicesString = (String) http.execute(getString(R.string.get_devices_url) + "/" + currentUser.getId()).get();
        currentUser.setDevices(parseJsonForDevices(devicesString));
        Log.i("currentUser", String.valueOf(currentUser.getClass()));
        ButterKnife.bind(this);
        mAdapter = new DeviceAdapter(getApplicationContext(), currentUser, new DeviceAdapter.MyAdapterListener() {
            @Override
            public void suspendOrEnableButtonClick(View v, final int position) {

                final Device device = currentUser.getDevices().get(position);
                Log.d(TAG, "Selected position: " + position);
                Log.d(TAG, "Device: " + device);
                // If selected device button is "Enable" then don't show any dialog, directly activate the automated shutdown
                //if (device.getAutomation() == R.drawable.radio_red){
                if (device.getClass().equals(SuspendendDevice.class)) {
                    Log.d(TAG, "getClassequals: " + device.getClass().equals(SuspendendDevice.class));
                    Log.d(TAG, "equals: " + device.equals(SuspendendDevice.class));
                    Toast.makeText(getApplicationContext(), "Enabled shutdown", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Before change:" + String.valueOf(device.getClass()));
                    Log.d(TAG, "before-device" + device.toString());
                    Log.d(TAG, "Before: " + currentUser.getDevices().toString());
                    device.setAutomation(R.drawable.radio_green);
                    device.setSuspendOrEnable("Suspend");
                    currentUser.getDevices().remove(position);
                    AutomatedDevice newDevice = new AutomatedDevice(device.getId(), device.getStatus(), device.getAutomation(), device.getSuspendOrEnable());
                    currentUser.getDevices().add(position, newDevice);
                    Log.d(TAG, "After: " + currentUser.getDevices().toString());
                    //Http Post Request for sending enable automation to the server.
                    //http.sendPostForEnable(currentUser.getId(), device.getId());
                    try {
                        //sendPostForEnable(, device.getId());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("personid", currentUser.getId());
                        jsonObject.put("deviceid", device.getId());
                        new HttpPost(jsonObject).execute(getString(R.string.enable_automation_url) );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //holder.automation.setImageResource(device.getAutomation());
                    //holder.button.setText(device.getSuspendOrEnable());
                    Log.i("after-device", device.toString());

                } else {
                    final Dialog dateDialog = new Dialog(ShowDevicesActivity.this);
                    dateDialog.setContentView(R.layout.suspend_datepicker_dialog);
                    Button saatSec = (Button) dateDialog.findViewById(R.id.button_time_sec);
                    Button btnDateIptal = (Button) dateDialog.findViewById(R.id.button_date_iptal);
                    final DatePicker datePicker = (DatePicker) dateDialog.findViewById(R.id.datepicker);
                    saatSec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final int year = datePicker.getYear();
                            final int month = datePicker.getMonth();
                            final int day = datePicker.getDayOfMonth();
                            final Dialog timeDialog = new Dialog(ShowDevicesActivity.this);
                            timeDialog.setContentView(R.layout.suspend_timepicker_dialog);

                            Button kaydet = (Button) timeDialog.findViewById(R.id.button_kaydet);
                            Button iptal = (Button) timeDialog.findViewById(R.id.button_iptal);
                            final TimePicker timePicker = (TimePicker) timeDialog.findViewById(R.id.time_picker);
                            kaydet.setOnClickListener(new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(View view) {
                                    Log.i("red", "Clicked on SUSPEND button");
                                    // Automation is on ise kapatti.
                                    device.setSuspendOrEnable("Enable");
                                    device.setAutomation(R.drawable.radio_red);
                                    Toast.makeText(getApplicationContext(), "Suspended automated shutdown", Toast.LENGTH_SHORT).show();
                                    int hour = timePicker.getHour();
                                    int minute = timePicker.getMinute();
                                    Toast.makeText(getApplicationContext(), "Suspendend until: " + day + "/" + month + "/" + year + "  " + hour + ": " + minute, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Before-devices:" + currentUser.getDevices().toString());
                                    currentUser.getDevices().remove(position);
                                    SuspendendDevice newDevice = new SuspendendDevice(device.getId(), device.getStatus(), device.getAutomation(), device.getSuspendOrEnable(), day, month, year);
                                    //Http Post Request for sending suspension time infos to the server.
                                    currentUser.getDevices().add(position, newDevice);
                                    Log.d(TAG, "After-devices:" + currentUser.getDevices().toString());
                                    Log.i("After change:", String.valueOf(device.getClass()));
                                    // Post request for suspension
                                    try {
                                        //sendPostForSuspend(currentUser.getId(),device.getId(),year,month,day, hour, minute);
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("personid", currentUser.getId());
                                        jsonObject.put("deviceid", device.getId());
                                        // “until”:”2020-01-29-10-30-00”
                                        jsonObject.put("until", year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + "00");
                                        new HttpPost(jsonObject).execute(getString(R.string.suspend_automation_url));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //holder.automation.setImageResource(device.getAutomation());
                                    //holder.button.setText(device.getSuspendOrEnable());
                                    //notifyDataSetChanged();
                                    timeDialog.dismiss();
                                    dateDialog.dismiss();
                                }
                            });
                            iptal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    timeDialog.dismiss();
                                }
                            });

                            timeDialog.show();
                        }
                    });

                    btnDateIptal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dateDialog.dismiss();
                        }
                    });
                    dateDialog.show();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShowDevicesActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);
    }


        //TODO
        @Override
        protected void onResume() {
            super.onResume();
            //Broadcast receiver’ı register ederken bir intent filter oluşturmamız ve bu intent filter’a bir action belirtmemiz gerekir.
            // Bu sayede broadcast receiverın OnReceive methodu hangi olayda tetiklenecekse bunu android işletim sistemine belirtmiş oluruz.
            IntentFilter filter=new IntentFilter();
            filter.addAction("my.action");
            registerReceiver(myBroadcastReceiver, filter);
        }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverPost);
        unregisterReceiver(myBroadcastReceiver);
    }

    private ArrayList<Device> parseJsonForDevices(String devicesString){
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
                    }

                    break;
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        return devices;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_devices) {
            mAdapter.setDevices(currentUser.getDevices());
            mAdapter.notifyDataSetChanged();
        }
        else {
            /*
            ArrayList<Device> suspendedDevices = new ArrayList<>();
              for(int i = 0; i< currentUser.getDevices().size(); i++){
                  Device d = currentUser.getDevices().get(i);
                  if ( d.getClass().equals(SuspendendDevice.class)){
                      suspendedDevices.add(d);
                  }
              }
                Log.i("suspendedDevices", suspendedDevices.toString());
                mAdapter.setDevices(suspendedDevices);
                mAdapter.notifyDataSetChanged();

            */
            Intent suspendedActivity = new Intent(this, ScheduledShutdownsActivity.class);
            startActivity(suspendedActivity);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private BroadcastReceiver myBroadcastReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String updatedDevicesString = intent.getStringExtra("veri");
            Log.d("ipek","Updated devices :" + updatedDevicesString);
            ArrayList<Device> updatedDevices =  parseJsonForDevices(updatedDevicesString);
            currentUser.setDevices(updatedDevices);
            mAdapter.setDevices(currentUser.getDevices());
            mAdapter.notifyDataSetChanged();
            //Toast.makeText(ShowDevicesActivity.this, "Updated devices :" + updatedDevicesString, Toast.LENGTH_SHORT).show();

        }
    };



    private class HttpPost extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public HttpPost(JSONObject postData) {
            if (postData != null) {
                this.postData = postData;
            }
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                // This is getting the url from the string we passed in
                URL url = new URL(params[0]);
                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }
                int statusCode = urlConnection.getResponseCode();
                Log.i("json", postData.toString());
                Log.i("STATUS", String.valueOf(statusCode));
                Log.i("MSG", urlConnection.getResponseMessage());

                if (statusCode ==  200) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    inputStream.close();
                    urlConnection.disconnect();
                    return response;
                } else {
                    // Status code is not 200
                    // Do something to handle the error
                    urlConnection.disconnect();
                    Log.d("statusCode:", String.valueOf(statusCode));
                    return null;
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return null;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bufferedReader.close();
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "Result: "+ result);
            if(result != null){
                Log.d(TAG,"onPostExecute:"+ currentUser.getDevices().toString());
                mAdapter.notifyDataSetChanged();
            }

            //updateDeviceList(devices);
        }
    }


}
