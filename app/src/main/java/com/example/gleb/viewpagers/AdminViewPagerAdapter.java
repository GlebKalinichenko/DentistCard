package com.example.gleb.viewpagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.gleb.fragments.ChangeFragment;
import com.example.gleb.fragments.CityFragment;
import com.example.gleb.fragments.CountryFragment;
import com.example.gleb.fragments.DepartmentDoctorFragment;
import com.example.gleb.fragments.DiagnoseFragment;
import com.example.gleb.fragments.DoctorFragment;
import com.example.gleb.fragments.KvalificationFragment;
import com.example.gleb.fragments.ParticientFragment;
import com.example.gleb.fragments.PostFragment;
import com.example.gleb.fragments.RecomendationFragment;
import com.example.gleb.fragments.RegistrationFragment;
import com.example.gleb.fragments.TicketDoctorFragment;
import com.example.gleb.fragments.TicketFragment;

/**
 * Created by gleb on 13.07.15.
 */
public class AdminViewPagerAdapter extends FragmentStatePagerAdapter {
    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when AdminViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the AdminViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public AdminViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        String profile = "admin";

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            ChangeFragment tab1 = new ChangeFragment(profile);
            return tab1;
        } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            if (position == 1) // if the position is 0 we are returning the First tab
            {
                CountryFragment tab2 = new CountryFragment(profile);
                return tab2;
            } else {
                if (position == 2) // if the position is 0 we are returning the First tab
                {
                    CityFragment tab3 = new CityFragment(profile);
                    return tab3;
                } else {
                    if (position == 3) // if the position is 0 we are returning the First tab
                    {
                        DoctorFragment tab4 = new DoctorFragment(profile);
                        return tab4;
                    } else {
                        if (position == 4) // if the position is 0 we are returning the First tab
                        {
                            ParticientFragment tab5 = new ParticientFragment(profile);
                            return tab5;
                        } else {
                            if (position == 5) // if the position is 0 we are returning the First tab
                            {
                                TicketDoctorFragment tab6 = new TicketDoctorFragment(profile);
                                return tab6;
                            } else {
                                if (position == 6) // if the position is 0 we are returning the First tab
                                {
                                    DepartmentDoctorFragment tab7 = new DepartmentDoctorFragment(profile);
                                    return tab7;
                                } else {
                                    if (position == 7) // if the position is 0 we are returning the First tab
                                    {
                                        DiagnoseFragment tab8 = new DiagnoseFragment(profile);
                                        return tab8;
                                    } else {
                                        if (position == 8) // if the position is 0 we are returning the First tab
                                        {
                                            KvalificationFragment tab9 = new KvalificationFragment(profile);
                                            return tab9;
                                        } else {
                                            if (position == 9) // if the position is 0 we are returning the First tab
                                            {
                                                PostFragment tab10 = new PostFragment(profile);
                                                return tab10;
                                            } else {
                                                if (position == 10) // if the position is 0 we are returning the First tab
                                                {
                                                    RecomendationFragment tab11 = new RecomendationFragment(profile);
                                                    return tab11;
                                                }

                                            }

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }
            RegistrationFragment tab12 = new RegistrationFragment(profile);
            return tab12;
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
