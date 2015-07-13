package com.example.gleb.tables;

/**
 * Created by gleb on 08.07.15.
 */
public class Ticket {
    public String doctorKod;
    public String registrationKod;
    public String dateReception;

    public Ticket(String doctorKod, String registrationKod, String dateReception) {
        this.doctorKod = doctorKod;
        this.registrationKod = registrationKod;
        this.dateReception = dateReception;
    }
}
