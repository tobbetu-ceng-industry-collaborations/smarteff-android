package com.ipekakova.smarteff_android.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ipekakova.smarteff_android.Adapters.ScheduledDeviceAdapter;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ScheduledShutdownsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = ScheduledShutdownsActivity.class.getSimpleName();

    @Bind(R.id.suspended_recyclier_view) RecyclerView mRecyclerView;
    private ScheduledDeviceAdapter mAdapter;
    private User currentUser;
    public ArrayList<SuspendendDevice> suspendendDevices = new ArrayList<>();
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ONEMLI!!!!
        setContentView(R.layout.activity_nav_suspended);

        Toolbar toolbar = findViewById(R.id.toolbar_suspended);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout_suspended);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_suspended);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
