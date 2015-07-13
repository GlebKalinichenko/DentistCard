package com.example.gleb.tables;

/**
 * Created by gleb on 08.07.15.
 */
public class Registration {
    public int idRegistration;
    public String dateRegistration;
    public String particientKod;

    public Registration(int idRegistration, String dateRegistration, String particientKod) {
        this.idRegistration = idRegistration;
        this.dateRegistration = dateRegistration;
        this.particientKod = particientKod;
    }
}
