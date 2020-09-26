package com.uabn.gss.uabnlink.fragment.MasterSearchResults;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SearchPeopleFragment tab0 = new SearchPeopleFragment();

                return tab0;
            case 1:
                SearchEventFragment tab2 = new SearchEventFragment();
                return tab2;
            case 2:

                SearchPBCRFragment tab3 = new SearchPBCRFragment();
                return tab3;
            case 3:
                SearchCertifiedBusinessFragment tab4 = new SearchCertifiedBusinessFragment();
                return tab4;
            case 4:
                SearchGroupFragment tab5 = new SearchGroupFragment();
                return tab5;
            case 5:
                SearchLibraryFragment tab6 = new SearchLibraryFragment();
                return tab6;
//

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}