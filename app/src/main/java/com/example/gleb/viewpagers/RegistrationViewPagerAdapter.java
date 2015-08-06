package com.example.gleb.viewpagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.gleb.fragments.ChangeFragment;
import com.example.gleb.fragments.DoctorFragment;
import com.example.gleb.fragments.ParticientFragment;
import com.example.gleb.fragments.RegistrationFragment;
import com.example.gleb.fragments.TicketDoctorFragment;
import com.example.gleb.fragments.TimetableFragment;

/**
 * Created by gleb on 14.07.15.
 */
public class RegistrationViewPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when AdminViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the AdminViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public RegistrationViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
            String profile = "registrator";
            if(position == 0) // if the position is 0 we are returning the First tab
            {
                ChangeFragment tab1 = new ChangeFragment(profile);
                return tab1;
            }
            else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                if(position == 1) // if the position is 0 we are returning the First tab
                {
                    DoctorFragment tab2 = new DoctorFragment(profile);
                    return tab2;
                }
                else{
                    if(position == 2) // if the position is 0 we are returning the First tab
                    {
                        ParticientFragment tab3 = new ParticientFragment(profile);
                        return tab3;
                    }
                    else{
                        if(position == 3) // if the position is 0 we are returning the First tab
                        {
                            TicketDoctorFragment tab4 = new TicketDoctorFragment(profile);
                            return tab4;
                        }
                        else{
                            if(position == 4) // if the position is 0 we are returning the First tab
                            {
                                RegistrationFragment tab5 = new RegistrationFragment(profile);
                                return tab5;
                            }

                        }

                    }

                }

            }
            TimetableFragment tab6 = new TimetableFragment(profile);
            return tab6;
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
