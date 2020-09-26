package com.uabn.gss.uabnlink.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchCertifiedBusinessFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchEventFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchGroupFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchLibraryFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchPBCRFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchPeopleFragment;

import java.util.ArrayList;
import java.util.List;


public class PagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();    }
}