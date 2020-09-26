package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.EventEdit;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.UpdateEventList;
import com.uabn.gss.uabnlink.adapter.EventAdapter;
import com.uabn.gss.uabnlink.model.EventsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment implements UpdateEventList, EventEdit {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<EventsModel> EventsDataModelArrayList = new ArrayList<>();

    Button UpcomingEvents, PastEvents, MyEvents;

    SharedPreferenceUtils preferances;
    String UserId;

    EventAdapter adapter;
    Context activityContext;
    RecyclerView eventlist;
    RecyclerView.LayoutManager layoutmanager;
    FrameLayout progressBarHolder;

    String Occurance = "";




    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
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
        View view =  inflater.inflate(R.layout.fragment_event, container, false);

        preferances= SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");

        eventlist = view.findViewById(R.id.eventlist);
        UpcomingEvents = view.findViewById(R.id.UpcomingEvents);
        PastEvents = view.findViewById(R.id.PastEvents);
        MyEvents = view.findViewById(R.id.MyEvents);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);

        PastEvents.setVisibility(View.VISIBLE);
        UpcomingEvents.setVisibility(View.VISIBLE);
        MyEvents.setVisibility(View.GONE);
//        mListener.onChangeTitle("My Events");
//        String url = (CommonUtils.BASE_URL)+"myEvents";
        mListener.onChangeTitle("Upcoming Events");
        String url = (CommonUtils.BASE_URL)+"upcoming_events";
        GetAllEvents(url);


        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.add_event);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.onFabButtonAddEventInteraction("Add Event", "");
                }
            }
        });

        UpcomingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpcomingEvents.setVisibility(View.GONE);
                PastEvents.setVisibility(View.VISIBLE);
                MyEvents.setVisibility(View.VISIBLE);

                mListener.onChangeTitle("Upcoming Events");
                String url = (CommonUtils.BASE_URL)+"upcoming_events";
                GetAllEvents(url);
            }
        });

        PastEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PastEvents.setVisibility(View.GONE);
                UpcomingEvents.setVisibility(View.VISIBLE);
                MyEvents.setVisibility(View.VISIBLE);

                mListener.onChangeTitle("Past Events");
                String url = (CommonUtils.BASE_URL)+"past_events";
                GetAllEvents(url);
            }
        });

        MyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PastEvents.setVisibility(View.VISIBLE);
                UpcomingEvents.setVisibility(View.VISIBLE);
                MyEvents.setVisibility(View.GONE);

                mListener.onChangeTitle("My Events");
                String url = (CommonUtils.BASE_URL)+"myEvents";
                GetAllEvents(url);
            }
        });


//        AddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mListener != null){
//                    mListener.addPbcr();
//                }
//            }
//        });

        return view;
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
        mListener.onChangeTitle("My Events");
        String url = (CommonUtils.BASE_URL)+"myEvents";
        GetAllEvents(url);
    }

    @Override
    public void EditEvents(String title, String EventId) {
        if(mListener != null){
            mListener.onFabButtonAddEventInteraction(title, EventId);
            mListener.onChangeTitle(title);
        }
    }



    public interface OnFragmentInteractionListener {
        void onFabButtonAddEventInteraction(String title, String EventId);
        void onChangeTitle(String title);

    }


    public void GetAllEvents(final String EventUrl) {

        progressBarHolder.setVisibility(View.VISIBLE);
        EventsDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .build();

        Request request = new Request.Builder()
                .url(EventUrl)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                final String myResponse = responseBody.string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");

                            if (success.equalsIgnoreCase("success")) {

                                JSONArray Events = json.getJSONArray("event_data");

                                if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"myEvents")){
                                    Occurance = "MyEvents";
                                }
                                else if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"past_events")){
                                    Occurance = "PastEvents";
                                }
                                else if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"upcoming_events")){
                                    Occurance = "UpcomingEvents";
                                }

                                for (int i = 0; i < Events.length(); i++) {
                                    JSONObject EventDetails = Events.getJSONObject(i);


                                    EventsModel model = new EventsModel();

                                    model.setEvent_id(EventDetails.getString("id"));

                                    if (!EventDetails.getString("image").equalsIgnoreCase("null")){
                                        model.setEventImage(json.getString("cover_image_url") +EventDetails.getString("id") +"/"+ EventDetails.getString("image"));
                                        model.setEvent_image(EventDetails.getString("image"));
                                    }
                                    else{
                                        model.setEventImage("");
                                    }

                                    model.setTitle(EventDetails.getString("event_title").replace("null",""));
                                    model.setType(EventDetails.getString("event_type").replace("null",""));

//                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(EventDetails.getString("date_created"));
//                                    String newString = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(date); // 25-03-2019
                                    model.setDate(EventDetails.getString("start_date").replace("null",""));

//                                    model.setWhen(EventDetails.getString("when").replace("null",""));
                                    model.setWhere(EventDetails.getString("where").replace("null",""));
                                    model.setWebsite(EventDetails.getString("website_url").replace("null",""));
                                    model.setEventGuestList(EventDetails.getString("show_guests_list"));
                                    model.setEventOccurance(Occurance);
                                    EventsDataModelArrayList.add(model);
                                }
                                progressBarHolder.setVisibility(View.GONE);
                                adapter = new EventAdapter(getActivity(), EventsDataModelArrayList, EventFragment.this, EventFragment.this);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                eventlist.setLayoutManager(layoutmanager);
                                eventlist.setAdapter(adapter);
                                //adapter.notifyDataSetChanged();
                            }
                            else {
                                adapter = new EventAdapter(getActivity(), EventsDataModelArrayList, EventFragment.this, EventFragment.this);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                eventlist.setLayoutManager(layoutmanager);
                                eventlist.setAdapter(adapter);
                                Toast.makeText(getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                                progressBarHolder.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
