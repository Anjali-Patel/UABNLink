package com.uabn.gss.uabnlink.fragment.MasterSearchResults;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.adapter.BusinessAdapter;
import com.uabn.gss.uabnlink.adapter.PBCR_Adapter;
import com.uabn.gss.uabnlink.model.BusinessdataModel;

import java.util.ArrayList;


public class SearchCertifiedBusinessFragment extends Fragment {
    RecyclerView my_recycler_view;
    RecyclerView.LayoutManager layoutmanager;
    BusinessAdapter adapter;
    Context activityContext;
    TextView nodataText;
    ArrayList<BusinessdataModel> CertifiedBussineswsModelArrayList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchCertifiedBusinessFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static SearchCertifiedBusinessFragment newInstance(String param1, String param2) {
        SearchCertifiedBusinessFragment fragment = new SearchCertifiedBusinessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search_certified_business, container, false);
           my_recycler_view=view.findViewById(R.id.my_recycler_view);
        nodataText=view.findViewById(R.id.nodataText);

        CertifiedBussineswsModelArrayList = new ArrayList<>();
        CertifiedBussineswsModelArrayList=CommonUtils.SEARCHCERTIFIEDBUSINESS;



        if (CertifiedBussineswsModelArrayList.size() > 0){
            adapter = new BusinessAdapter(getActivity(), CertifiedBussineswsModelArrayList, activityContext);
            layoutmanager = new LinearLayoutManager(getActivity());
            my_recycler_view.setLayoutManager(layoutmanager);
            my_recycler_view.setAdapter(adapter);
        }
        else{
            nodataText.setVisibility(View.VISIBLE);
        }



        return  view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
