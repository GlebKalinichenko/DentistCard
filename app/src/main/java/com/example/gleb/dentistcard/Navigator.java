package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by Gleb on 07.06.2015.
 */
public class Navigator extends ActionBarActivity {
    private Button countryButton;
    private Button cityButton;
    private Button departmentButton;
    private Button changeButton;
    private Button kvalificationButton;
    private Button timetableButton;
    private Button doctorButton;
    private Button particientsButton;
    private Button diagnoseButton;
    private Button registrationButton;
    private Button ticketButton;
    private Button recommendationButton;
    private Button postButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate);

        countryButton = (Button) findViewById(R.id.countryButton);
        cityButton = (Button) findViewById(R.id.cityButton);
        departmentButton = (Button) findViewById(R.id.departmentButton);
        changeButton = (Button) findViewById(R.id.changeButton);
        kvalificationButton = (Button) findViewById(R.id.kvalificationButton);
        timetableButton = (Button) findViewById(R.id.timetableButton);
        doctorButton = (Button) findViewById(R.id.doctorButton);
        particientsButton = (Button) findViewById(R.id.particientsButton);
        diagnoseButton = (Button) findViewById(R.id.diagnoseButton);
        registrationButton = (Button) findViewById(R.id.registrationButton);
        ticketButton = (Button) findViewById(R.id.ticketButton);
        recommendationButton = (Button) findViewById(R.id.recommndationButton);
        postButton = (Button) findViewById(R.id.postButton);

        countryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Countries.class);
                startActivity(intent);
            }
        });

        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Cities.class);
                startActivity(intent);
            }
        });

        departmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DepartmentsDoctors.class);
                startActivity(intent);
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Changes.class);
                startActivity(intent);
            }
        });

        kvalificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Kvalifications.class);
                startActivity(intent);
            }
        });

        timetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Timetables.class);
                startActivity(intent);
            }
        });

        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Doctors.class);
                startActivity(intent);
            }
        });

        particientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Particients.class);
                startActivity(intent);
            }
        });

        diagnoseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Diagnoses.class);
                startActivity(intent);

            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Registrations.class);
                startActivity(intent);
            }
        });

        ticketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Tickets.class);
                startActivity(intent);
            }
        });

        recommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Recommendations.class);
                startActivity(intent);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Posts.class);
                startActivity(intent);
            }
        });
    }
}
