package com.ipekakova.smarteff_android;

/**
 * Created by User on 2.02.2020.
 */

public class Device {
    private String id;
    private int deviceIsOn;
    private int automatedShutdownIsOn;
    private String suspendOrEnable;
    //private boolean enableShutDown;


    public Device(String id, int isOn, int automatedShutdownIsOn, String suspendOrEnable) {
        this.id = id;
        this.deviceIsOn = isOn;
        this.automatedShutdownIsOn = automatedShutdownIsOn;
        //this.enableShutDown = enableShutDown;
        this.suspendOrEnable = suspendOrEnable;
    }
    public String getId(){
        return id;
    }
    public int getStatus(){
        return deviceIsOn;
    }
    public int getAutomation(){
        return automatedShutdownIsOn;
    }
    public  String getSuspendOrEnable(){ return suspendOrEnable; }

}
