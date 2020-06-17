package com.ipekakova.smarteff_android.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.ipekakova.smarteff_android.Adapters.ScheduledDeviceAdapter;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SuspendedShutdownsFragment extends Fragment {

    public static final String TAG = SuspendedShutdownsFragment.class.getSimpleName();

    @Bind(R.id.suspended_recyclier_view) RecyclerView mRecyclerView;
    private ScheduledDeviceAdapter mAdapter;
    private User currentUser = User.getInstance();;
    public ArrayList<SuspendendDevice> suspendendDevices = new ArrayList<>();
    public MainDevicesActivity mainActivity;
    private Context mainContext;
    View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.scheduled_shutdowns_activity, container, false);
        mainActivity = (MainDevicesActivity) getActivity();
        mainActivity.setTitle("Suspended Shutdowns");

        ButterKnife.bind(this, rootView);
        suspendendDevices = getSuspendendDevices();
        Log.d(TAG, "suspendend_devices: "+ suspendendDevices);
        mAdapter = new ScheduledDeviceAdapter(mainActivity, suspendendDevices);
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