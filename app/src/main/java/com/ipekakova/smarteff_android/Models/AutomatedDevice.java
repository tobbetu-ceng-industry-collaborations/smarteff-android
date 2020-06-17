package com.ipekakova.smarteff_android.Models;

/**
 * Created by User on 4.03.2020.
 */

public class AutomatedDevice extends Device {

    public String shutdown_time;

    public AutomatedDevice(int id, int isOn, int automation, String suspendOrEnable, String deviceType) {
        super(id, isOn, automation, suspendOrEnable, deviceType);
    }

    public String getExpiration() {
        return shutdown_time;
    }

    public void setExpiration(String expiration) {
        this.shutdown_time = expiration;
    }

    public String toString() {
        return "AutomatedDevice{" +
                "id=" + id +
                ", deviceIsOn=" + deviceIsOn +
                ", automation=" + automation +
                ", suspendOrEnable='" + suspendOrEnable + '\'' +
                ", device_type='" + super.getDeviceType() + '\'' +
                '}';
    }
}
