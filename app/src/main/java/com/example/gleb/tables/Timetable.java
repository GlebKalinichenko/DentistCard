package com.example.gleb.tables;

/**
 * Created by gleb on 09.07.15.
 */
public class Timetable {
    public String dateWork;
    public String doctorKod;
    public String changeKod;

    public Timetable(String dateWork, String doctorKod, String changeKod) {
        this.dateWork = dateWork;
        this.doctorKod = doctorKod;
        this.changeKod = changeKod;
    }
}
