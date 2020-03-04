package com.ipekakova.smarteff_android;

/**
 * Created by User on 4.03.2020.
 */

public class AutomatedDevice extends Device {

    public AutomatedDevice(int id, int isOn, int automation, String suspendOrEnable) {
        super(id, isOn, automation, suspendOrEnable);
    }
    public String toString() {
        return "AutomatedDevice{" +
                "id=" + id +
                ", deviceIsOn=" + deviceIsOn +
                ", automation=" + automation +
                ", suspendOrEnable='" + suspendOrEnable + '\'' +
                '}';
    }
}
