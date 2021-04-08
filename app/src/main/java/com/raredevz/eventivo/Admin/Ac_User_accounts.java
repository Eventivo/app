package com.raredevz.eventivo.Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.raredevz.eventivo.Helper.ViewPagerAdapterAdminAccounts;
import com.raredevz.eventivo.R;

public class Ac_User_accounts extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapterAdminAccounts viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac__user_accounts);

        viewPager = (ViewPager) findViewById(R.id.viewPagerAcct);
        viewPagerAdapter = new ViewPagerAdapterAdminAccounts(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutAcct);
        tabLayout.setupWithViewPager(viewPager);

    }
}