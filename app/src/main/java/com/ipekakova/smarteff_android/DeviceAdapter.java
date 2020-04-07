package com.ipekakova.smarteff_android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import android.view.ViewGroup.LayoutParams;

import static android.content.ContentValues.TAG;


/**
 * Created by User on 2.02.2020.
 */

public class DeviceAdapter extends ArrayAdapter<Device> {
    private final LayoutInflater inflater;
    private final Context context;
    //private idi deneme icin değistirdim.
    private DeviceViewHolder holder;
    private User currentUser;
    private ArrayList<Device> devices;
    private String TAG="DeviceAdapter";


    public DeviceAdapter(Context context, ArrayList<Device> devices, User currentUser) {
        super(context,0, devices);
        this.context = context;
        this.devices = devices;
        this.currentUser = currentUser;
        inflater = LayoutInflater.from(context);
    }
    public ArrayList<Device> getDevices(){
        return devices;
    }

    public void updateDeviceList(ArrayList<Device> newlist) {
        this.devices = newlist;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.device_list_entry, null);
            holder = new DeviceViewHolder();
            holder.status = (ImageView) convertView.findViewById(R.id.status);
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.automation = (ImageView) convertView.findViewById(R.id.automation);
            holder.button = (Button) convertView.findViewById(R.id.enable_button);
            holder.button.setOnClickListener(new View.OnClickListener() {
                Device device = devices.get(position);
                @Override
                public void onClick(View view) {

                    //Device device = devices.get(position);
                    // If selected device button is "Enable" then don't show any dialog, directly activate the automated shutdown
                    //if (device.getAutomation() == R.drawable.radio_red){
                    if(device.getClass().equals(SuspendendDevice.class)){
                        Log.d(TAG, "getClassequals: "+device.getClass().equals(SuspendendDevice.class));
                        Log.d(TAG, "equals: "+device.equals(SuspendendDevice.class));

                        Toast.makeText(context, "Enabled shutdown", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Before change:"+String.valueOf(device.getClass()));
                        Log.d(TAG, "before-device"+ device.toString());
                        Log.d(TAG,"Before: "+devices.toString());
                        device.setAutomation(R.drawable.radio_green);
                        device.setSuspendOrEnable("Suspend");
                        devices.remove(position);
                        AutomatedDevice newDevice = new AutomatedDevice(device.getId(), device.getStatus(), device.getAutomation(),device.getSuspendOrEnable());
                        devices.add(position, newDevice);
                        Log.d(TAG,"After: "+devices.toString());
                        //Http Post Request for sending enable automation to the server.
                        //http.sendPostForEnable(currentUser.getId(), device.getId());
                        try {
                            //sendPostForEnable(, device.getId());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("personid",currentUser.getId());
                            jsonObject.put("deviceid",device.getId());
                            new HttpPost(jsonObject).execute(context.getString(R.string.enable_automation_url));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        holder.automation.setImageResource(device.getAutomation());
                        holder.button.setText(device.getSuspendOrEnable());
                        Log.i("after-device", device.toString());

                    }
                    else {
                        final Dialog dateDialog = new Dialog(context);
                        dateDialog.setContentView(R.layout.suspend_datepicker_dialog);
                        Button  saatSec = (Button)dateDialog.findViewById(R.id.button_time_sec);
                        Button btnDateIptal = (Button) dateDialog.findViewById(R.id.button_date_iptal);
                        final DatePicker datePicker = (DatePicker) dateDialog.findViewById(R.id.datepicker);
                        saatSec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final int year = datePicker.getYear();
                                final int month = datePicker.getMonth();
                                final int day = datePicker.getDayOfMonth();
                                final Dialog timeDialog = new Dialog(context);
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
                                        Toast.makeText(context, "Suspended automated shutdown", Toast.LENGTH_SHORT).show();
                                        int hour = timePicker.getHour();
                                        int minute = timePicker.getMinute();
                                        Toast.makeText(context, "Suspendend until: "+ day+"/"+ month+"/" + year +"  "+ hour +": "+ minute, Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Before-devices:"+ devices.toString());
                                        devices.remove(position);
                                        SuspendendDevice newDevice = new SuspendendDevice(device.getId(),device.getStatus(),device.getAutomation(), device.getSuspendOrEnable(), day, month,year);
                                        //Http Post Request for sending suspension time infos to the server.
                                        devices.add(position, newDevice);
                                        Log.d(TAG, "After-devices:"+ devices.toString());
                                        Log.i("After change:", String.valueOf(device.getClass()));
                                        // Post request for suspension
                                        try {
                                            //sendPostForSuspend(currentUser.getId(),device.getId(),year,month,day, hour, minute);
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("personid",currentUser.getId());
                                            jsonObject.put("deviceid",device.getId());
                                            // “until”:”2020-01-29-10-30-00”
                                            jsonObject.put("until", year+ "-"+ month + "-"+ day+ "-"+ hour + "-"+minute+ "-"+ "00");
                                            new HttpPost(jsonObject).execute(context.getString(R.string.suspend_automation_url));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        holder.automation.setImageResource(device.getAutomation());
                                        holder.button.setText(device.getSuspendOrEnable());
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

            convertView.setTag(holder);
        }
        else{
            //Get viewholder we already created
            holder = (DeviceViewHolder)convertView.getTag();
        }

        final Device device = devices.get(position);
        if(device != null){
            holder.deviceName.setText("D:" + device.getId());
            holder.status.setImageResource(device.getStatus());
            holder.automation.setImageResource(device.getAutomation());
            //holder.enableShutDown.setEnabled(device.getEnableShutdown());
            holder.button.setText(device.getSuspendOrEnable());

        }
        Log.i("adapter-device", devices.get(position).toString());
        return convertView;
    }

    @Override
    public int getCount() {
        return devices.size();
    }
    // Hangi satırdaki item için işlem yapıyorsak o satırdaki item’ı döndürür.
    @Override
    public Device getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return devices.get(position).hashCode();
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    private static class DeviceViewHolder{
        TextView deviceName;
        ImageView status;
        ImageView automation;
        //Switch enableShutDown;
        Button button;
    }
    /*
    private void sendPostForEnable( int user_id,  int device_id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personid",user_id);
        jsonObject.put("deviceid",device_id);

        new HttpPostAsyncTask(new WeakReference<Context>(context),"json", jsonObject).execute(context.getString(R.string.enable_automation_url));
    }

    private void sendPostForSuspend(int user_id, int device_id, int year, int month, int day, int hour, int minute) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personid",user_id);
        jsonObject.put("deviceid",device_id);
        // “until”:”2020-01-29-10-30-00”
        jsonObject.put("until", year+ "-"+ month + "-"+ day+ "-"+ hour + "-"+minute+ "-"+ "00");
        new HttpPostAsyncTask(new WeakReference<Context>(context),"json", jsonObject).execute(context.getString(R.string.suspend_automation_url));
    }

    */
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
            Log.d(TAG,"onPostExecute:"+devices.toString());
            notifyDataSetChanged();
            //updateDeviceList(devices);
        }
    }

}



