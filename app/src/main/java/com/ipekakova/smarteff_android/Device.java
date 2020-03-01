package com.ipekakova.smarteff_android;

/**
 * Created by User on 2.02.2020.
 */

public class Device {
    private int id;
    private int deviceIsOn;
    private int automation;
    private String suspendOrEnable;
    //private boolean enableShutDown;


    public Device(int id, int isOn, int automation, String suspendOrEnable) {
        this.id = id;
        this.deviceIsOn = isOn;
        this.automation = automation;
        //this.enableShutDown = enableShutDown;
        this.suspendOrEnable = suspendOrEnable;
    }
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
                "id='" + id + '\'' +
                ", deviceIsOn=" + deviceIsOn +
                ", automation=" + automation +
                ", suspendOrEnable='" + suspendOrEnable + '\'' +
                '}';
    }

    /*
    private class SuspensionRequest{
        private
    }
    */
}
