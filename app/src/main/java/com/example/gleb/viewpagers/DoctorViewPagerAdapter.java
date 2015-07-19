package com.example.gleb.viewpagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.gleb.fragments.DoctorFragment;
import com.example.gleb.fragments.ParticientFragment;
import com.example.gleb.fragments.RecomendationFragment;
import com.example.gleb.fragments.TicketDoctorFragment;

/**
 * Created by gleb on 14.07.15.
 */
public class DoctorViewPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = "TAG";
    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when AdminViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the AdminViewPagerAdapter is created
    private String fullName;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public DoctorViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, String fullName) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.fullName = fullName;
        Log.d(TAG, "DoctorViewPagerAdapter " + fullName);

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        if (position == 0) // if the position is 0 we are returning the First tab
        {
            DoctorFragment tab1 = new DoctorFragment();
            return tab1;
        } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            if (position == 1) // if the position is 0 we are returning the First tab
            {
                ParticientFragment tab2 = new ParticientFragment();
                return tab2;
            } else {
                if (position == 2) // if the position is 0 we are returning the First tab
                {
                    TicketDoctorFragment tab3 = new TicketDoctorFragment(fullName);
                    return tab3;
                }
            }
            RecomendationFragment tab4 = new RecomendationFragment();
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
