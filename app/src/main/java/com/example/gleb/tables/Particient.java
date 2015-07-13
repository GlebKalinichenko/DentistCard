package com.example.gleb.tables;

/**
 * Created by gleb on 12.07.15.
 */
public class Particient {
    public String FIO;
    public String address;
    public String cityKod;
    public String phoneNumber;
    public String dateBorn;
    public String FIOParent;

    public Particient(String FIO, String address, String cityKod, String phoneNumber, String dateBorn, String FIOParent) {
        this.FIO = FIO;
        this.address = address;
        this.cityKod = cityKod;
        this.phoneNumber = phoneNumber;
        this.dateBorn = dateBorn;
        this.FIOParent = FIOParent;
    }
}
