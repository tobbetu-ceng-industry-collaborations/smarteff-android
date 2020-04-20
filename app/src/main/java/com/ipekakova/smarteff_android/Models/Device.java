package com.ipekakova.smarteff_android.Models;

/**
 * Created by User on 3.03.2020.
 */

public abstract class Device {
    public String deviceName;
    public int id ;
    public int deviceIsOn;
    public int automation;
    public String suspendOrEnable;
    private String device_type;
    //private boolean enableShutDown;


    public Device(int id, int isOn, int automation, String suspendOrEnable, String device_type) {
        this.id = id;
        this.deviceIsOn = isOn;
        this.automation = automation;
        //this.enableShutDown = enableShutDown;
        this.suspendOrEnable = suspendOrEnable;
        this.device_type = device_type;
    }

    public String getDeviceName(){ return deviceName; }
    public int getId(){
        return id;
    }
    public int getStatus(){
        return deviceIsOn;
    }
    public int getAutomation(){
        return automation;
    }
    public  String getSuspendOrEnable(){ return suspendOrEnable; }
    public String getDeviceType(){
        return device_type;
    }


    public void setDeviceName(String deviceName){
        deviceName = this.deviceName;
    }
    public void setDeviceIsOn(int isOn){
        deviceIsOn = isOn;
    }
    public void setAutomation(int shutdown){
        automation = shutdown;
    }
    public void setSuspendOrEnable(String suspendOrEnable){
        this.suspendOrEnable = suspendOrEnable;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceName='" + deviceName + '\'' +
                ", id=" + id +
                ", deviceIsOn=" + deviceIsOn +
                ", automation=" + automation +
                ", suspendOrEnable='" + suspendOrEnable + '\'' +
                ", device_type='" + device_type + '\'' +
                '}';
    }
}
