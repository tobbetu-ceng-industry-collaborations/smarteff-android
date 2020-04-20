package com.ipekakova.smarteff_android.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextClock;
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
import com.ipekakova.smarteff_android.Models.AutomatedDevice;
import com.ipekakova.smarteff_android.Models.Device;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by User on 2.02.2020.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    public interface MyAdapterListener {
        void suspendOrEnableButtonClick(View v, int position);
        //void iconImageViewOnClick(View v, int position);
    }

    private final Context context;
    private User currentUser;
    private ArrayList<Device> devices;
    public MyAdapterListener onClickListener;
    private String TAG="DeviceAdapter";


    public DeviceAdapter(Context context, User currentUser, MyAdapterListener listener) {
        this.context = context;
        this.devices = currentUser.getDevices();
        this.currentUser = currentUser;
        this.onClickListener = listener;
    }
    public ArrayList<Device> getDevices(){
        return devices;
    }

    public void updateDeviceList(ArrayList<Device> newlist) {
        this.devices = newlist;
        this.notifyDataSetChanged();
    }

    public void setDevices(ArrayList<Device> newlist){
        this.devices = newlist;
    }
    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_entry, parent, false);
        DeviceAdapter.DeviceViewHolder viewHolder = new DeviceAdapter.DeviceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceAdapter.DeviceViewHolder holder, int position) {
        holder.bindDevice(devices.get(position));
    }

    @Override
    public long getItemId(int position) {
        return devices.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.device_name) TextView deviceName;
        @Bind(R.id.device_type) ImageView deviceType;
        @Bind(R.id.status) ImageView status;
        @Bind(R.id.automation) ImageView automation;
        //Switch enableShutDown;
        @Bind(R.id.enable_button) Button button;

        private Context mContext;

        public DeviceViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.suspendOrEnableButtonClick(v, getAdapterPosition());
                }
            });
        }

        public void bindDevice(Device device) {

            deviceName.setText("D:" + device.getId());
            status.setImageResource(device.getStatus());
            automation.setImageResource(device.getAutomation());
            button.setText(device.getSuspendOrEnable());
            String type = device.getDeviceType();
            if (type.equals("Lamp")){
                deviceType.setImageResource(R.drawable.ic_light);
            }
            else if(type.equals("Air Conditioner")){
                deviceType.setImageResource(R.drawable.ic_airconditioner);
            }else if(type.equals("Desk")){
                deviceType.setImageResource(R.drawable.ic_workstation);
            }else{
                deviceType.setImageResource(R.drawable.ic_laptop);

            }
        }
    }

}



