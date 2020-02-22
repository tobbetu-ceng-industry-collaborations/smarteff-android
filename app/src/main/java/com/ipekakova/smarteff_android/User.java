package com.ipekakova.smarteff_android;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private int id;
    private String name;
    private ArrayList<Device> devices;

    public User(int id,  String name) {
        this.id = id;
        this.name = name;
    }
    public User(int id,  String name, ArrayList<Device> devices) {
        this.id = id;
        this.name = name;
        this.devices = devices;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
    public ArrayList<Device> getDevices(){
        return devices;
    }
    public String  getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
