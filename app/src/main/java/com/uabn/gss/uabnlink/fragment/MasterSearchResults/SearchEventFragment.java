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
import com.uabn.gss.uabnlink.Utility.EventEdit;
import com.uabn.gss.uabnlink.Utility.UpdateEventList;
import com.uabn.gss.uabnlink.adapter.BusinessAdapter;
import com.uabn.gss.uabnlink.adapter.EventAdapter;
import com.uabn.gss.uabnlink.model.EventsModel;

import java.util.ArrayList;


public class SearchEventFragment extends Fragment implements UpdateEventList, EventEdit {
    RecyclerView my_recycler_view;
    RecyclerView.LayoutManager layoutmanager;
    EventAdapter adapter;
    Context activityContext;
    TextView nodataText;
    ArrayList<EventsModel> EventsDataModelArrayList;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchEventFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchEventFragment newInstance(String param1, String param2) {
        SearchEventFragment fragment = new SearchEventFragment();
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

        View view= inflater.inflate(R.layout.fragment_search_event, container, false);
        EventsDataModelArrayList = new ArrayList<>();
        EventsDataModelArrayList=CommonUtils.SEARCHEVENT;
        my_recycler_view=view.findViewById(R.id.my_recycler_view);
        nodataText=view.findViewById(R.id.nodataText);

        if (EventsDataModelArrayList.size() > 0){
            adapter = new EventAdapter(getActivity(),EventsDataModelArrayList, SearchEventFragment.this,SearchEventFragment.this);
            layoutmanager = new LinearLayoutManager(getActivity());
            my_recycler_view.setLayoutManager(layoutmanager);
            my_recycler_view.setAdapter(adapter);

        }
        else{
            nodataText.setVisibility(View.VISIBLE);
        }


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
    public void UpdateEvents() {

    }

    @Override
    public void EditEvents(String title, String EventId) {

    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
