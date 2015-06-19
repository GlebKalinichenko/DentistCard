package com.example.gleb.dentistcard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by Gleb on 02.06.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dentistcards";
    public static final int VERSION = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table counties
        db.execSQL("CREATE TABLE " + META_TABLE_COUNTRIES.TABLE_COUNTRIES + " (" + META_TABLE_COUNTRIES._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_COUNTRIES.country + "TEXT NOT NULL);");

        //create table cities
        db.execSQL("CREATE TABLE " + META_TABLE_CITIES.TABLE_CITIES + " (" + META_TABLE_CITIES._ID + " INT PRIMATY KEY AUTOINCREMENT, " +
            META_TABLE_CITIES.city + " TEXT NOT NULL, " + "FOREIGN KEY (" + META_TABLE_CITIES.countryKod + ") REFERENCES " +
            META_TABLE_COUNTRIES.TABLE_COUNTRIES + "(" + META_TABLE_COUNTRIES._ID + "));" );

        //create table diagnoses
        db.execSQL("CREATE TABLE " + META_TABLE_DIAGNOSES.TABLE_DIAGNOSES + " ( " + META_TABLE_DIAGNOSES._ID + "INT PRIMARY KEY AUTOINCREAMENT, " +
            META_TABLE_DIAGNOSES.diagnose + " TEXT NOT NULL);");

        //create table kvalififcations
        db.execSQL("CREATE TABLE " + META_TABLE_KVALIFICATIONS.TABLE_KVALIFICAIONS + " (" + META_TABLE_KVALIFICATIONS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_KVALIFICATIONS.kvalifications + " TEXT NOT NULL);");

        //create table posts
        db.execSQL("CREATE TABLE " + META_TABLE_POSTS.TABLE_POSTS + " (" + META_TABLE_POSTS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_POSTS.post + " TEXT NOT NULL);");

        //create table kvalifications
        db.execSQL("CREATE TABLE " + META_TABLE_KVALIFICATIONS.TABLE_KVALIFICAIONS + " (" + META_TABLE_KVALIFICATIONS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_KVALIFICATIONS.kvalifications + " TEXT NOT NULL);");

        //create table changes
        db.execSQL("CREATE TABLE " + META_TABLE_CHANGE.TABLE_CHANGES + " (" + META_TABLE_CHANGE._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_CHANGE.numChange + " INT NOT NULL, " + META_TABLE_CHANGE.timeChange + " TEXT NOT NULL);");

        //create table departements
        db.execSQL("CREATE TABLE " + META_TABLE_DEPARTEMENT_DOCTORS.TABLE_DEPARTEMENT_DOCTORS + " (" + META_TABLE_DEPARTEMENT_DOCTORS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_DEPARTEMENT_DOCTORS.departement + " TEXT NOT NULL);");

        //create table doctors
        db.execSQL("CREATE TABLE " + META_TABLE_DOCTORS.TABLE_DOCTORS + " (" + META_TABLE_DOCTORS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
            META_TABLE_DOCTORS.FIO + "TEXT NOT NULL, " + META_TABLE_DOCTORS.postKod + " INT NOT NULL, " +
            META_TABLE_DOCTORS.expierience + "TEXT NOT NULL, " + META_TABLE_DOCTORS.kvalificationKod + " INT NOT NULL, " +
            META_TABLE_DOCTORS.pathPhoto + "TEXT NOT NULL, " + META_TABLE_DOCTORS.sexKod + " INT NOT NULL, " +
            META_TABLE_DOCTORS.timetableKod + " INT NOT NULL, FOREIGN KEY (" + META_TABLE_DOCTORS.postKod + ") REFERENCES " +
            META_TABLE_POSTS.TABLE_POSTS + "(" + META_TABLE_POSTS._ID + "), FOREIGN KEY ( " + META_TABLE_DOCTORS.kvalificationKod + ") REFERENCES " +
            META_TABLE_KVALIFICATIONS.TABLE_KVALIFICAIONS + "(" + META_TABLE_KVALIFICATIONS.kvalifications + "), FOREING KEY (" +
            META_TABLE_DOCTORS.timetableKod + ") REFERENCES " + META_TABLE_TIMETABLES_DOCTORS.TABLE_TIMETABLES + " (" + META_TABLE_TIMETABLES_DOCTORS._ID + "));");

        //create table timetables
        db.execSQL("CREATE TABLE " + META_TABLE_TIMETABLES_DOCTORS.TABLE_TIMETABLES + " (" + META_TABLE_TIMETABLES_DOCTORS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
        META_TABLE_TIMETABLES_DOCTORS.workDate + " DATE NOT NULL, " + META_TABLE_TIMETABLES_DOCTORS.changeKod + " INT NOT NULL, FOREIGN KEY (" +
                META_TABLE_TIMETABLES_DOCTORS.changeKod + ") REFERENCES " + META_TABLE_CHANGE.TABLE_CHANGES + "(" + META_TABLE_CHANGE._ID + "));");

        //create table particients
        db.execSQL("CREATE TABLE " + META_TABLE_PARTICIENTS.TABLE_PARTICIENTS + " (" + META_TABLE_PARTICIENTS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
                META_TABLE_PARTICIENTS.FIO + " TEXT NOT NULL, " + META_TABLE_PARTICIENTS.address + " TEXT NOT NULL, " +
                META_TABLE_PARTICIENTS.cityKod + " INT NOT NULL, " + META_TABLE_PARTICIENTS.phoneNumber + " TEXT NOT NULL, " +
                META_TABLE_PARTICIENTS.dateBorn + " DATE NOT NULL, " + " FOREIGN KEY (" + META_TABLE_PARTICIENTS.cityKod +
                ") REFERENCES " + META_TABLE_CITIES.TABLE_CITIES + "(" + META_TABLE_CITIES._ID + "));");

        //create table registrations
        db.execSQL("CREATE TABLE " + META_TABLE_REGISTRATIONS.TABLE_REGISTRATIONS + " (" + META_TABLE_REGISTRATIONS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
                META_TABLE_REGISTRATIONS.particientKod + " INT NOT NULL, " + META_TABLE_REGISTRATIONS.dateRegistration + " DATE NOT NULL, FOREIGN KEY (" +
                META_TABLE_REGISTRATIONS.particientKod + ") REFERENCES " + META_TABLE_PARTICIENTS.TABLE_PARTICIENTS + "(" + META_TABLE_PARTICIENTS._ID + "));");

        //create table tickets
        db.execSQL("CREATE TABLE " + META_TABLE_TICKETS.TABLE_TICKETS + " (" + META_TABLE_TICKETS._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
                META_TABLE_TICKETS.departmentKod + " INT NOT NULL, " + META_TABLE_TICKETS.registrationKod + " INT NOT NULL, " +
                META_TABLE_TICKETS.dateRegistration + " DATE NOT NULL, " + META_TABLE_TICKETS.doctorKod + " INT NOT NULL, " +
                "FOREIGN KEY (" + META_TABLE_TICKETS.departmentKod + ") " +
                " REFERENCES " + META_TABLE_TICKETS.TABLE_TICKETS + "(" + META_TABLE_TICKETS._ID + "), " +
                "FOREIGN KEY (" + META_TABLE_TICKETS.registrationKod + ") REFERENCES " + META_TABLE_REGISTRATIONS.TABLE_REGISTRATIONS + " (" +
                META_TABLE_REGISTRATIONS._ID + "), " + "FOREIGN KEY (" + META_TABLE_TICKETS.doctorKod + ") REFERENCES " + META_TABLE_DOCTORS.TABLE_DOCTORS +
                "(" + META_TABLE_DOCTORS._ID + "));");

        //create table recomendations
        db.execSQL("CREATE TABLE " + META_TABLE_RECOMENDATION.TABLE_RECOMENDATION + " (" + META_TABLE_RECOMENDATION._ID + " INT PRIMARY KEY AUTOINCREMENT, " +
                META_TABLE_RECOMENDATION.diagnoseKod + " INT NOT NULL, " + META_TABLE_RECOMENDATION.ticketKod + " INT NOT NULL, " +
                META_TABLE_RECOMENDATION.threament + " TEXT NOT NULL, " + META_TABLE_RECOMENDATION.complains + " TEXT NOT NULL, " + META_TABLE_RECOMENDATION.hystoryIll + "TEXT NOT NULL, " +
                META_TABLE_RECOMENDATION.objectiveValues + " TEXT NOT NULL, FOREIGN KEY (" + META_TABLE_RECOMENDATION.ticketKod + ") REFERENCES " +
                META_TABLE_TICKETS.TABLE_TICKETS + " (" + META_TABLE_TICKETS._ID + "), FOREIGN KEY (" + META_TABLE_RECOMENDATION.diagnoseKod + " REFERENCES " +
                META_TABLE_DIAGNOSES.TABLE_DIAGNOSES + "(" + META_TABLE_DIAGNOSES._ID + "));");

        /*CREATE TABLE COMPANY(
                ID INT PRIMARY KEY NOT NULL,
                NAME TEXT NOT NULL,
                AGE INT NOT NULL,
                ADDRESS CHAR(50),
                SALARY REAL
        );*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //------------------------------------------------------------------------------
    //references

    public static class META_TABLE_COUNTRIES implements  BaseColumns{
        public static final String TABLE_COUNTRIES = "countries";
        public static String country;
    }

    public static class META_TABLE_CITIES implements  BaseColumns{
        public static final String TABLE_CITIES = "cities";
        public static String city;
        public static int countryKod;
    }

    public static class META_TABLE_POSTS implements BaseColumns{
        public static final String TABLE_POSTS = "posts";
        public static String post;
    }

    public static class META_TABLE_KVALIFICATIONS implements BaseColumns{
        public static final String TABLE_KVALIFICAIONS = "kvalifications";
        public static String kvalifications;
    }

    public static class META_TABLE_DIAGNOSES implements BaseColumns{
        public static final String TABLE_DIAGNOSES = "diagnoses";
        public static String diagnose;
    }

    /**
     * Timetables of doctors to show when they work
     */
    public static class META_TABLE_TIMETABLES_DOCTORS implements BaseColumns{
        public static final String TABLE_TIMETABLES = "timetables";
        public static Date workDate;
        public static int changeKod;
    }

    /**
     * Work changes of doctors 1 or 2
     */
    public static class META_TABLE_CHANGE implements BaseColumns{
        public static final String TABLE_CHANGES = "changes";
        public static int numChange;
        public static int timeChange;
    }

    public static class META_TABLE_SEXS implements BaseColumns{
        public static final String TABLE_SEXS = "sexs";
        public static String sex;
    }

    /**
     * Department of doctors proteser
     */
    public static class META_TABLE_DEPARTEMENT_DOCTORS implements BaseColumns{
        public static final String TABLE_DEPARTEMENT_DOCTORS = "departement";
        public static String departement;
    }

    //---------------------------------------------------------------------------

    /**
     * Main tables
     */

    public static class META_TABLE_DOCTORS implements  BaseColumns{
        public static final String TABLE_DOCTORS = "doctors";
        public static String FIO;
        public static int postKod;
        public static int expierience;
        public static int kvalificationKod;
        public static String pathPhoto;
        public static int timetableKod;
        public static int sexKod;
    }

    public static class META_TABLE_PARTICIENTS implements  BaseColumns{
        public static final String TABLE_PARTICIENTS = "particients";
        public static String FIO;
        public static String address;
        public static int cityKod;
        public static String phoneNumber;
        public static String dateBorn;
    }

    public static class META_TABLE_REGISTRATIONS implements  BaseColumns{
        public static final String TABLE_REGISTRATIONS = "registrations";
        public static int particientKod;
        public static Date dateRegistration;
    }

    public static class META_TABLE_TICKETS implements  BaseColumns{
        public static final String TABLE_TICKETS = "tickets";
        public static int registrationKod;
        public static Date dateRegistration;
        public static int departmentKod;
        public static int doctorKod;
    }

    public static class META_TABLE_RECOMENDATION implements  BaseColumns{
        public static final String TABLE_RECOMENDATION = "recomendation";
        public static int ticketKod;
        public static int diagnoseKod;
        public static String threament;
        public static String complains;
        public static String hystoryIll;
        public static String objectiveValues;

    }






}
