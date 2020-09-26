package com.uabn.gss.uabnlink.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uabn.gss.uabnlink.Activities.Constants;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.EditPBCR;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.UpdatePBCR;
import com.uabn.gss.uabnlink.adapter.PBCR_Adapter;
import com.uabn.gss.uabnlink.adapter.RepliesAdapter;
import com.uabn.gss.uabnlink.model.CategoryModel;
import com.uabn.gss.uabnlink.model.PBCR_DataModel;
import com.uabn.gss.uabnlink.model.RegionModel;
import com.uabn.gss.uabnlink.model.ServiceModel;

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

public class PBCRfragment extends Fragment implements  UpdatePBCR, EditPBCR {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<String> SearchRegion;
    ArrayList<String> ServiceList;
    EditText search_by_keyword,search_comment;
    Spinner region;
    Spinner service;
    ArrayList<RegionModel> RegionModelArrayList;
    ArrayList<RegionModel> RegionModelArrayListTmp ;
    ArrayList<ServiceModel> ServiceModelArrayList;
    ArrayList<ServiceModel>ServiceModelArrayListTmp ;
    RecyclerView pbcrlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<PBCR_DataModel> PBCRDataModelArrayList = new ArrayList<>();
    Context activityContext;
    PBCR_Adapter adapter;
    Button AddButton,SearchButton;
    PbcrFragmentInterface pbcrInterface;
    String SelectedRegionId="",SelectedServiceId="",profile_pic_url;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    ProgressBar pb;
    SharedPreferenceUtils preferances;
    String UserId,str_search_comment = "",SelectedRegion = "",SelectedCategory = "";
    TextView SelectedRegions;
    RelativeLayout MainView;
    FrameLayout progressBarHolder;
    Activity pbcrActivity;


    public PBCRfragment() {
        // Required empty public constructor
    }

    public static PBCRfragment newInstance(String param1, String param2) {
        PBCRfragment fragment = new PBCRfragment();
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
        View view = inflater.inflate(R.layout.fragment_pbcrfragment, container, false);
        pbcrInterface = (PbcrFragmentInterface) getActivity();
        preferances = SharedPreferenceUtils.getInstance(getActivity());

        pbcrActivity = getActivity();

        getService();
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        SearchButton=view.findViewById(R.id.SearchButton);
        search_by_keyword=view.findViewById(R.id.search_by_keyword);
        search_comment=view.findViewById(R.id.search_comment);
        SelectedRegions=view.findViewById(R.id.SelectedRegions);
        MainView = view.findViewById(R.id.MainView);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        region = view.findViewById(R.id.region);
        service = view.findViewById(R.id.service);
        pbcrlist = view.findViewById(R.id.pbcrlist);
        AddButton = view.findViewById(R.id.AddButton);
        RegionModelArrayListTmp = new ArrayList<>();
        ServiceModelArrayList=new ArrayList<>();
        RegionModelArrayList = CommonUtils.REGIONARRAYLIST;
        ServiceModelArrayListTmp=new ArrayList<>();
        SearchRegion = new ArrayList<>();
        SearchRegion.add("Select Region");
        pb=view.findViewById(R.id.pb);
        ServiceList = new ArrayList<>();
        ServiceList.add("Select List");
        for (int i = 0; i < RegionModelArrayList.size(); i++) {
            final RegionModel Items = RegionModelArrayList.get(i);
            SearchRegion.add(Items.getRegionName());
        }



        ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, SearchRegion);
        region.setAdapter(Regionadapter);
        region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedRegion = parent.getItemAtPosition(position).toString();
                for(int i = 0; i<RegionModelArrayList.size();i++) {
                    if (RegionModelArrayList.get(i).getRegionName().contains(SelectedRegion)) {
                        RegionModelArrayListTmp.add(RegionModelArrayList.get(i));
                        SelectedRegionId = RegionModelArrayList.get(position-1).getRegionId();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> ServiceAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, ServiceList);
        service.setAdapter(ServiceAdapter);
        service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedCategory = parent.getItemAtPosition(position).toString();
                for(int i = 0; i<ServiceModelArrayList.size();i++) {
                    if (ServiceModelArrayList.get(i).getServiceName().contains(SelectedCategory)) {
                        ServiceModelArrayListTmp.add(ServiceModelArrayList.get(i));
                        SelectedServiceId = ServiceModelArrayList.get(position - 1).getServiceId();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        adapter = new PBCR_Adapter(getActivity(), PBCRDataModelArrayList,this, PBCRfragment.this);
        layoutmanager = new LinearLayoutManager(getActivity());
        pbcrlist.setLayoutManager(layoutmanager);
        pbcrlist.setAdapter(adapter);
        GetPBCRDetails();

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.addPbcr();
                }
            }
        });
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_search_comment=search_by_keyword.getText().toString().trim();
                    searchComment(str_search_comment,SelectedRegionId,SelectedServiceId,UserId);
                    pb.setVisibility(View.VISIBLE);

            }
        });

//        for (int i = 0; i < RegionModelArrayList.size(); i++) {
//            final RegionModel Items = RegionModelArrayList.get(i);
//            SearchRegion.add(Items.getRegionName());
//        }


//        SelectedRegions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Choose some animals");
//                final String[] item = new String[3];
//
//                final String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
//                boolean[] checkedItems = {true, false, false, true, false};
//                builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                        item[0] = animals[which];
//                        item[1] = animals[which];
//                        item[2] = animals[which];
//
//                    }
//                });
//
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        SelectedRegions.setText(item[0]);
//                        dialog.dismiss();
//                    }
//                });
//                builder.setNegativeButton("Cancel", null);
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
        return view;

    }
    public void searchComment(String search_comment,String SelectedRegionId, String SelectedServiceId,String UserId){

        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);

        PBCRDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);
        String url = (CommonUtils.BASE_URL)+"pbcr_list";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("regions", SelectedRegionId)
                .add("services", SelectedServiceId)
                .add("keyword", search_comment)
                .build();
        Request request = new Request.Builder()
                .url(url)
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
                        pb.setVisibility(View.GONE);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(myResponse);
                        String success = json.getString("status");
                        if (success.equalsIgnoreCase("success")) {
                            profile_pic_url = json.getString("image_pathh");
                            JSONArray update = json.getJSONArray("pbcr_list");
                            for (int i = 0; i < update.length(); i++) {
                                JSONObject UpdateDetails = update.getJSONObject(i);
                                PBCR_DataModel model = new PBCR_DataModel();
                                model.setPBCRComments(UpdateDetails.getString("comments").replace("null", ""));
                                Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_on"));
                                String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                model.setPBCRDate(CommentPostDate);

                            model.setPBCRDate(UpdateDetails.getString("created_on").replace("null", ""));
//                                model.setPBCRImage(profile_pic_url + UpdateDetails.getString("pbcr_id")+ "/" +UpdateDetails.getString("image").replace("null", "").replace(" ", "%20"));
                                model.setPbcr_userId_(UpdateDetails.getString("user_id").replace("null", ""));
                                String ImageURL = json.getString("image_path") + UpdateDetails.getString("pbcr_id") + "/" + UpdateDetails.getString("image");
                                model.setPBCRImage(ImageURL);                                model.setPBCRService(UpdateDetails.getString("services").replace("null", ""));
                                model.setPBCRTitle(UpdateDetails.getString("item_title").replace("null", ""));
                                model.setPBCR_id(UpdateDetails.getString("pbcr_id").replace("null", ""));
                                PBCRDataModelArrayList.add(model);
                            }
                            MainView.setVisibility(View.VISIBLE);
                            progressBarHolder.setVisibility(View.GONE);
//                            Constants.hideKeyboard(pbcrActivity);
                            adapter.notifyDataSetChanged();

                        } else{
                            MainView.setVisibility(View.VISIBLE);
                            progressBarHolder.setVisibility(View.GONE);
                            Toast.makeText(getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                            adapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    }
                });
            }
        });


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


    public  void GetPBCRDetails() {


        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);

        PBCRDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL) + "pbcr_list";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("regions", "")
                .add("services", "")
                .add("keyword", "")
                .build();

        Request request = new Request.Builder()
                .url(url)
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
                                JSONArray update = json.getJSONArray("pbcr_list");
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject PBCRDetails = update.getJSONObject(i);
                                    PBCR_DataModel model = new PBCR_DataModel();
                                    model.setPBCRComments(PBCRDetails.getString("comments").replace("null", ""));
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(PBCRDetails.getString("created_on"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setPBCRDate(CommentPostDate);

                                    model.setPbcr_userId_(PBCRDetails.getString("user_id").replace("null", ""));

                                    String ImageURL = json.getString("image_path") + PBCRDetails.getString("pbcr_id") + "/" + PBCRDetails.getString("image");
                                    model.setPBCRImage(ImageURL);
                                    model.setPBCRService(PBCRDetails.getString("services").replace("null", ""));
                                    model.setPBCRTitle(PBCRDetails.getString("item_title").replace("null", ""));
                                    model.setPBCR_id(PBCRDetails.getString("pbcr_id").replace("null", ""));
                                    PBCRDataModelArrayList.add(model);
                                }

                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();

                            } else {
                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
                                adapter.notifyDataSetChanged();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void EditPBCR(String PBCRId) {
        if (mListener != null) {
            mListener.editpbcr(PBCRId);

        }
    }

    public interface PbcrFragmentInterface {
        public void addPbcr();
    }


    public interface OnFragmentInteractionListener {
        void addPbcr();
        void editpbcr(String PBCRId);

    }
    public void getService(){

        String url = (CommonUtils.BASE_URL)+"getServices";


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).get().build();

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

                                JSONArray json2 = json.getJSONArray("services");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("category");

                                    ServiceModel model = new ServiceModel();
                                    model.setServiceId(RegionObj.getString("id"));
                                    model.setServiceName(RegionObj.getString("name"));
                                    model.setServiceStatus(RegionObj.getString("status"));

                                    ServiceModelArrayList.add(model);
                                }
                                for (int i = 0; i < ServiceModelArrayList.size(); i++) {
                                    final ServiceModel Items = ServiceModelArrayList.get(i);
                                    ServiceList.add(Items.getServiceName());
                                }
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
