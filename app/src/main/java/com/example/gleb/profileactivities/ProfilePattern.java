package com.example.gleb.profileactivities;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;

import com.example.gleb.fragments.SlidingTabLayout;
import com.mikepenz.materialdrawer.Drawer;

/**
 * Created by gleb on 14.07.15.
 */
abstract class ProfilePattern extends ActionBarActivity {
    protected Toolbar toolbar;
    protected ViewPager pager;
    protected SlidingTabLayout tabs;
    protected ActionMode actionMode;
    protected Drawer.Result drawerResult = null;
    protected FragmentStatePagerAdapter adapter;

}
