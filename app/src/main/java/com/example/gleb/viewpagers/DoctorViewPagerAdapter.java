package com.example.gleb.viewpagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.gleb.fragments.DoctorFragmentDoctorProfile;
import com.example.gleb.fragments.ParticientDoctorProfile;
import com.example.gleb.fragments.RecomendationFragmentDoctorProfile;
import com.example.gleb.fragments.TicketDoctorProfile;

/**
 * Created by gleb on 14.07.15.
 */
public class DoctorViewPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = "TAG";
    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when AdminViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the AdminViewPagerAdapter is created
    private String fullName;
    public int freshTicket;
    public int allTicket;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public DoctorViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, String fullName, int freshTicket, int allTicket) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.fullName = fullName;
        this.freshTicket = freshTicket;
        this.allTicket = allTicket;
        Log.d(TAG, "DoctorViewPagerAdapter " + fullName);

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        String profile = "doctor";

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            DoctorFragmentDoctorProfile tab1 = new DoctorFragmentDoctorProfile(fullName, freshTicket, allTicket, profile);
            return tab1;
        } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            if (position == 1) // if the position is 0 we are returning the First tab
            {
                ParticientDoctorProfile tab2 = new ParticientDoctorProfile(fullName, freshTicket, allTicket, profile);
                return tab2;
            } else {
                if (position == 2) // if the position is 0 we are returning the First tab
                {
                    TicketDoctorProfile tab3 = new TicketDoctorProfile(fullName, freshTicket, allTicket, profile);
                    return tab3;
                }
            }
            RecomendationFragmentDoctorProfile tab4 = new RecomendationFragmentDoctorProfile(fullName, freshTicket, allTicket, profile);
            return tab4;
        }
    }



    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}
