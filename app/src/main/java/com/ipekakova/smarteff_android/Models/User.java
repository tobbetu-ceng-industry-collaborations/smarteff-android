package com.ipekakova.smarteff_android.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private static User user;
    private int id;
    private String name;
    private ArrayList<Device> devices;


    private User(){
    }

    public static synchronized User getInstance() {
        if(user == null) {
            user = new User();
        }
        return user;
    }
    /*
    private User(int id,  String name) {
        this.id = id;
        this.name = name;
    }
    private User(int id,  String name, ArrayList<Device> devices) {
        this.id = id;
        this.name = name;
        this.devices = devices;
    }
    */
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
    public Device getDeviceFromId(int id){
        for (int i =0; i< devices.size(); i++){
            if ( devices.get(i).getId() == id){
                return devices.get(i);
            }
        }
        return null;
    }

    //Make singleton from serialize and deserialize operation.
    protected Object readResolve() {
        return getInstance();
    }
}
