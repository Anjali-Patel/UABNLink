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
import com.uabn.gss.uabnlink.Utility.EditPBCR;
import com.uabn.gss.uabnlink.Utility.UpdatePBCR;
import com.uabn.gss.uabnlink.adapter.EventAdapter;
import com.uabn.gss.uabnlink.adapter.GroupAdapter;
import com.uabn.gss.uabnlink.adapter.PBCR_Adapter;
import com.uabn.gss.uabnlink.fragment.PBCRfragment;
import com.uabn.gss.uabnlink.model.EventsModel;
import com.uabn.gss.uabnlink.model.PBCR_DataModel;

import java.util.ArrayList;


public class SearchPBCRFragment extends Fragment implements UpdatePBCR, EditPBCR {
    RecyclerView my_recycler_view;
    RecyclerView.LayoutManager layoutmanager;
    PBCR_Adapter adapter;
    Context activityContext;
    TextView nodataText;
    ArrayList<PBCR_DataModel> PBCRDataModelArrayList = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchPBCRFragment() {

    }



    public static SearchPBCRFragment newInstance(String param1, String param2) {
        SearchPBCRFragment fragment = new SearchPBCRFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View view=inflater.inflate(R.layout.fragment_search_pbcr, container, false);
        PBCRDataModelArrayList = new ArrayList<>();
        my_recycler_view=view.findViewById(R.id.my_recycler_view);
        nodataText=view.findViewById(R.id.nodataText);

        PBCRDataModelArrayList= CommonUtils.SEARCHPBCR;

        if (PBCRDataModelArrayList.size() > 0){
            adapter = new PBCR_Adapter(getActivity(), PBCRDataModelArrayList,this, this);
            layoutmanager = new LinearLayoutManager(getActivity());
            my_recycler_view.setLayoutManager(layoutmanager);
            my_recycler_view.setAdapter(adapter);
        }
        else{
            nodataText.setVisibility(View.VISIBLE);
        }




        return view;
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

    @Override
    public void EditPBCR(String PBCRId) {

    }

    @Override
    public void GetPBCRDetails() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
