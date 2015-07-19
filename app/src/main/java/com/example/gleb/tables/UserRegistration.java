package com.example.gleb.tables;

/**
 * Created by gleb on 15.07.15.
 */
public class UserRegistration {
    public String fullName;
    public String email;
    public String password;
    public String profileKod;

    public UserRegistration(String fullName, String email, String password, String profileKod) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.profileKod = profileKod;
    }
}
