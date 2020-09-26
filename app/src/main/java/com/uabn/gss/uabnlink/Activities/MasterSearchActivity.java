package com.uabn.gss.uabnlink.Activities;

import android.net.Uri;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.adapter.PagerAdapter;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchCertifiedBusinessFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchEventFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchGroupFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchLibraryFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchPBCRFragment;
import com.uabn.gss.uabnlink.fragment.MasterSearchResults.SearchPeopleFragment;

public class MasterSearchActivity extends AppCompatActivity implements SearchPeopleFragment.OnFragmentInteractionListener,
        SearchCertifiedBusinessFragment.OnFragmentInteractionListener, SearchEventFragment.OnFragmentInteractionListener,
        SearchGroupFragment.OnFragmentInteractionListener, SearchLibraryFragment.OnFragmentInteractionListener,
        SearchPBCRFragment.OnFragmentInteractionListener{


    private PagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_master_search);
        }catch(Exception e){
            e.printStackTrace();
        }


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Results");

        adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchPBCRFragment(), "PBCR");
        adapter.addFragment(new SearchGroupFragment(), "Groups");
        adapter.addFragment(new SearchPeopleFragment(), "Members");
        adapter.addFragment(new SearchEventFragment(), "Events");
        adapter.addFragment(new SearchCertifiedBusinessFragment(), "Certified Business");
        adapter.addFragment(new SearchLibraryFragment(), "Libraries");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
