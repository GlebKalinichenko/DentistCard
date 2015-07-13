package com.example.gleb.tables;

/**
 * Created by gleb on 10.07.15.
 */
public class Doctor {
    public String FIO;
    public String postKod;
    public String kvalificationKod;
    public String departmentKod;
    public int expirience;

    public Doctor(String FIO, String postKod, String kvalificationKod, String departmentKod, int expirience) {
        this.FIO = FIO;
        this.postKod = postKod;
        this.kvalificationKod = kvalificationKod;
        this.departmentKod = departmentKod;
        this.expirience = expirience;
    }
}
