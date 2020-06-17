package com.ipekakova.smarteff_android.UI;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipekakova.smarteff_android.Adapters.AutomatedDeviceAdapter;
import com.ipekakova.smarteff_android.Adapters.ScheduledDeviceAdapter;
import com.ipekakova.smarteff_android.DateUtils;
import com.ipekakova.smarteff_android.Models.AutomatedDevice;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import com.ipekakova.smarteff_android.Services.HttpGetAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 22.04.2020.
 */

public class AutomatedShutdownsFragment extends Fragment {

    public static final String TAG = AutomatedShutdownsFragment.class.getSimpleName();

    @Bind(R.id.suspended_recyclier_view) RecyclerView mRecyclerView;
    private AutomatedDeviceAdapter mAdapter;
    private User currentUser = User.getInstance();;
    public ArrayList<AutomatedDevice> automatedDevices = new ArrayList<>();
    public MainDevicesActivity mainActivity;
    private Context mainContext;
    private HttpGetAsyncTask httpGet;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.scheduled_shutdowns_activity, container, false);
        mainActivity = (MainDevicesActivity) getActivity();
        mainActivity.setTitle("Automated Shutdowns");
        ButterKnife.bind(this, rootView);
        httpGet = new HttpGetAsyncTask(mainActivity);
        automatedDevices = getAutomatedDevices();
        Log.d(TAG, "automated_devices: "+ automatedDevices);
        mAdapter = new AutomatedDeviceAdapter(mainActivity, automatedDevices);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
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
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(rootView);
    }
    //TODO
    protected ArrayList<AutomatedDevice> getAutomatedDevices(){
        String devicesString = "";
        try {
            devicesString = (String) httpGet.execute(getString(R.string.get_scheduled_shutdowns) + "/" + currentUser.getId()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        HashMap<Integer, String> devices = getTimeStamps(devicesString);
        ArrayList<AutomatedDevice> automatedDevices = new ArrayList<>();
        Set<Integer> keySet = devices.keySet();
        ArrayList<Integer> userIds = new ArrayList<Integer>(keySet);

        // Find user devices that is about to be shutdown soon.
        for(int i = 0; i< userIds.size(); i++){
            Device d = currentUser.getDeviceFromId(userIds.get(i));
            if (d != null){
                // Set the shutdown timestamp field for each automated device that is about to shutdown soon.
                d.setExpiration(devices.get(userIds.get(i)));
                automatedDevices.add((AutomatedDevice) d);
            }
        }
        Log.d(TAG, "automated-devices" + automatedDevices);
        return automatedDevices;
    }

    // Get scheduled shutdowns for current user devices from server.
    private HashMap<Integer, String> getTimeStamps(String devicesString){
        HashMap<Integer, String> timeStamps = new HashMap<>();

        DateUtils dateUtils = new DateUtils();
        ArrayList<AutomatedDevice> devices = new ArrayList<>();
        try {
            while (true) {
                JSONObject object = new JSONObject(devicesString);
                JSONArray devicesArray = new JSONArray(object.get("scheduledShutdowns").toString());
                Log.i("scheduledShutdowns", devicesArray.toString());
                for (int i = 0 ; i < devicesArray.length() ; i++) {
                    JSONObject obj = new JSONObject(devicesArray.get(i).toString());
                    String timestamp = obj.getString("timestamp");
                    try {
                        Calendar cal = dateUtils.getCalendarFromDateTime(timestamp);
                        timestamp = dateUtils.getStringFromDate(cal.getTime(), "EEE, dd MMM HH:mm");

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Couldn't parse the timestamp");
                    }
                    JSONObject deviceObj = new JSONObject(obj.get("device").toString());
                    int deviceId = deviceObj.getInt("id");
                    timeStamps.put(deviceId, timestamp);
                }
                break;
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        return timeStamps;
    }


}
