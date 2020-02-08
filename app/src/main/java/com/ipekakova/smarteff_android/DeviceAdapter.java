package com.ipekakova.smarteff_android;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by User on 2.02.2020.
 */

public class DeviceAdapter extends ArrayAdapter<Device> {
    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final ArrayList<Device> devices;

    public DeviceAdapter(Context context, ArrayList<Device> devices) {
        super(context,0, devices);
        this.context = context;
        this.devices = devices;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.device_list_entry, null);

            holder = new ViewHolder();
            holder.status = (ImageView) convertView.findViewById(R.id.status);
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.automation = (ImageView) convertView.findViewById(R.id.automation);
            holder.button = (Button) convertView.findViewById(R.id.enable_button);
            //holder.enableShutDown = (Switch) convertView.findViewById(R.id.enable_switch);
            convertView.setTag(holder);

        }
        else{
            //Get viewholder we already created
            holder = (ViewHolder)convertView.getTag();
        }

        Device device = devices.get(position);
        if(device != null){
            holder.deviceName.setText(device.getId());
            holder.status.setImageResource(device.getStatus());
            holder.automation.setImageResource(device.getAutomation());
            //holder.enableShutDown.setEnabled(device.getEnableShutdown());
            holder.button.setText(device.getSuspendOrEnable());

        }
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
}
