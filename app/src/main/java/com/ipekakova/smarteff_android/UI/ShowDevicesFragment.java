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
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ipekakova.smarteff_android.DateUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 2.02.2020.
 */

public class ShowDevicesFragment extends Fragment {

    private DrawerLayout drawer;
    @Bind(R.id.device_list) RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;
    User currentUser = User.getInstance();
    HttpGetAsyncTask http;
    SharedPreferences sp;
    private String TAG="ShowDevicesFragment";
    public MainDevicesActivity mainActivity;
    private Context mainContext;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_all_devices, container, false);
        mainActivity = (MainDevicesActivity) getActivity();
        mainActivity.setTitle("All Devices");
        ButterKnife.bind(this, rootView);

        mAdapter = new DeviceAdapter(mainActivity, currentUser, new DeviceAdapter.MyAdapterListener() {
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
                    Toast.makeText(mainActivity, "Enabled shutdown", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Before change:" + String.valueOf(device.getClass()));
                    Log.d(TAG, "before-device" + device.toString());
                    Log.d(TAG, "Before: " + currentUser.getDevices().toString());
                    device.setAutomation(R.drawable.radio_green);
                    device.setSuspendOrEnable("Suspend");
                    currentUser.getDevices().remove(position);
                    AutomatedDevice newDevice = new AutomatedDevice(device.getId(), device.getStatus(), device.getAutomation(), device.getSuspendOrEnable(), device.getDeviceType());
                    currentUser.getDevices().add(position, newDevice);
                    Log.d(TAG, "After: " + currentUser.getDevices().toString());
                    //Http Post Request for sending enable automation to the server.
                    //http.sendPostForEnable(currentUser.getId(), device.getId());
                    try {
                        //sendPostForEnable(, device.getId());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("personid", currentUser.getId());
                        jsonObject.put("deviceid", device.getId());
                        new ShowDevicesFragment.HttpPost(jsonObject).execute(getString(R.string.enable_automation_url) );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //holder.automation.setImageResource(device.getAutomation());
                    //holder.button.setText(device.getSuspendOrEnable());
                    Log.i("after-device", device.toString());

                } else {
                    final Dialog dateDialog = new Dialog(mainActivity);
                    dateDialog.setContentView(R.layout.suspend_datepicker_dialog);
                    Button saatSec = (Button) dateDialog.findViewById(R.id.button_time_sec);
                    Button btnDateIptal = (Button) dateDialog.findViewById(R.id.button_date_iptal);
                    final DatePicker datePicker = (DatePicker) dateDialog.findViewById(R.id.datepicker);
                    saatSec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final int year = datePicker.getYear();
                            // Months are indexed starting at 0 in DateTime Class so need to add 1 to the month value!!
                            final int month = datePicker.getMonth()+1;
                            final int day = datePicker.getDayOfMonth();
                            final Dialog timeDialog = new Dialog(mainActivity);
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
                                    Toast.makeText(mainActivity.getApplicationContext(), "Suspended automated shutdown", Toast.LENGTH_SHORT).show();
                                    int hour = timePicker.getHour();
                                    int minute = timePicker.getMinute();
                                    //Toast.makeText(getApplicationContext(), "Suspendend until: " + day + "/" + month + "/" + year + "  " + hour + ": " + minute, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Before-devices:" + currentUser.getDevices().toString());
                                    currentUser.getDevices().remove(position);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(year, month-1, day, hour, minute,00);
                                    SuspendendDevice newDevice = new SuspendendDevice(device.getId(), device.getStatus(), device.getAutomation(), device.getSuspendOrEnable(),calendar, device.getDeviceType());
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
                                        new ShowDevicesFragment.HttpPost(jsonObject).execute(getString(R.string.suspend_automation_url));

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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter();
        filter.addAction("my.action");
        mainContext.registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainContext.unregisterReceiver(myBroadcastReceiver);
    }

    private BroadcastReceiver myBroadcastReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String updatedDevicesString = intent.getStringExtra("veri");
            Log.d("ipek","Updated devices :" + updatedDevicesString);
            ArrayList<Device> updatedDevices =  mainActivity.parseJsonForDevices(updatedDevicesString);
            currentUser.setDevices(updatedDevices);
            mAdapter.setDevices(currentUser.getDevices());
            mAdapter.notifyDataSetChanged();
            //Toast.makeText(ShowDevicesFragment.this, "Updated devices :" + updatedDevicesString, Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(rootView);
    }


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