package com.example.gleb.tables;

/**
 * Created by gleb on 12.07.15.
 */
public class Recomendation {
    public String ticketKod;
    public String diagnoseKod;
    public String therapy;
    public String complaints;
    public String historyIllness;
    public String objectiveValues;

    public Recomendation(String ticketKod, String diagnoseKod, String therapy, String complaints, String historyIllness, String objectiveValues) {
        this.ticketKod = ticketKod;
        this.diagnoseKod = diagnoseKod;
        this.therapy = therapy;
        this.complaints = complaints;
        this.historyIllness = historyIllness;
        this.objectiveValues = objectiveValues;
    }
}
