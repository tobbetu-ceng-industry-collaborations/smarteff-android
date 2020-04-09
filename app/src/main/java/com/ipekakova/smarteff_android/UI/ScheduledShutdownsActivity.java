package com.ipekakova.smarteff_android.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.ipekakova.smarteff_android.Adapters.ScheduledDeviceAdapter;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduledShutdownsActivity extends AppCompatActivity {

    public static final String TAG = ScheduledShutdownsActivity.class.getSimpleName();

    @Bind(R.id.suspended_recyclier_view) RecyclerView mRecyclerView;
    private ScheduledDeviceAdapter mAdapter;
    private User currentUser;
    public ArrayList<SuspendendDevice> suspendendDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduled_shutdowns_activity);
        currentUser = User.getInstance();

        ButterKnife.bind(this);
        suspendendDevices = getSuspendendDevices();
        Log.d(TAG, "devices: "+ suspendendDevices);
        mAdapter = new ScheduledDeviceAdapter(getApplicationContext(), suspendendDevices);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(ScheduledShutdownsActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

    }

    protected ArrayList<SuspendendDevice> getSuspendendDevices(){
        ArrayList<SuspendendDevice> suspendedDevices = new ArrayList<>();

        for(int i = 0; i< currentUser.getDevices().size(); i++){
            Device d = currentUser.getDevices().get(i);
            if ( d.getClass().equals(SuspendendDevice.class)){
                suspendedDevices.add((SuspendendDevice) d);
            }
        }
        return suspendedDevices;
    }

}
