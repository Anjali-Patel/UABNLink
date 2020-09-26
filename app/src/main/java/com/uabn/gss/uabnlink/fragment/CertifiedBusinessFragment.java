package com.uabn.gss.uabnlink.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.BusinessAdapter;
import com.uabn.gss.uabnlink.model.BusinessdataModel;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.IndustryModel;
import com.uabn.gss.uabnlink.model.LeadershipBusinessModel;
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


public class CertifiedBusinessFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static RecyclerView businesslist;
    public static RecyclerView.LayoutManager layoutmanager;
    public static ArrayList<LeadershipBusinessModel> LeaderShipBusinessModel = new ArrayList<>();
    public static ArrayList<LeadershipBusinessModel> LeaderShipBusinessModelTemp = new ArrayList<>();
    public static ArrayList<String> SearchCategories;
    public static String SelectedBusiness = "";
    static String user_id;
    static ArrayList<BusinessdataModel> BusinessDataModelArrayList = new ArrayList<>();
    static Context activityContext;
    static DialogueFilterCertifiedBusiness search = new DialogueFilterCertifiedBusiness();
    String str_search = "";
    String profile_pic_url;
    FrameLayout progressBarHolder;
    SharedPreferenceUtils preferances;
    Button SearchButton;
    ArrayList<BusinessdataModel> BusinessDataModelArrayListTemp = new ArrayList<>();
    ArrayList<String> leadership_select;
    EditText search_by_keyword;
    BusinessAdapter adapter;
    ArrayList<CountryModel> CountryModelArrayList = new ArrayList<>();
    ArrayList<CountryModel> CountryModelArrayListTmp = new ArrayList<>();
    ArrayList<RegionModel> RegionModelArrayList = new ArrayList<>();
    ArrayList<RegionModel> RegionModelArrayListTmp = new ArrayList<>();
    ArrayList<ServiceModel> ServiceModelArrayList = new ArrayList<>();
    ArrayList<ServiceModel> ServiceModelArrayListTmp = new ArrayList<>();
    ArrayList<IndustryModel> IndustryModelArrayList = new ArrayList<>();
    ArrayList<IndustryModel> IndustryModelArrayListTmp = new ArrayList<>();
    Spinner country, region, services, industrykey;
    Button LeadshipButton;
    ArrayList<String> CountryList;
    ArrayList<String> RegionList;
    ArrayList<String> ServiceList;
    ArrayList<String> IndustryList;
    String SelectedCountryId = "", SelectedRegionId = "", SelectedServiceId = "", SelectedIndustryId = "";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public CertifiedBusinessFragment() {


    }


    public static CertifiedBusinessFragment newInstance(String param1, String param2) {
        CertifiedBusinessFragment fragment = new CertifiedBusinessFragment();
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
        View view = inflater.inflate(R.layout.fragment_certified_business, container, false);
        search_by_keyword = view.findViewById(R.id.search_by_keyword);
        businesslist = view.findViewById(R.id.businesslist);
        SearchButton = view.findViewById(R.id.SearchButton);
        country = view.findViewById(R.id.country);
        region = view.findViewById(R.id.region);
        services = view.findViewById(R.id.services);
        industrykey = view.findViewById(R.id.industrykey);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        LeadshipButton = view.findViewById(R.id.LeadshipButton);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

        CountryList = new ArrayList<>();
        CountryList.add("Select Country");

        RegionList = new ArrayList<>();
        RegionList.add("Select Region");

        ServiceList = new ArrayList<>();
        ServiceList.add("Select Service");

        IndustryList = new ArrayList<>();
        IndustryList.add("Select Industry");
//        leadership_select= new ArrayList<>();
//        leadership_select.add("");
        getRegionDetails();
        getCountryDetails();
        getServiceDetails();
        getIndustryDetails();
        getBusinessSelect();
        getCertifiedBusinesslist(user_id);


        ArrayAdapter<String> Countryadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, CountryList);
        country.setAdapter(Countryadapter);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < CountryModelArrayList.size(); i++) {
                    if (CountryModelArrayList.get(i).getCountryName().contains(SelectedCountry)) {
                        CountryModelArrayListTmp.add(CountryModelArrayList.get(i));
                        SelectedCountryId = CountryModelArrayList.get(position - 1).getCountryId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, RegionList);
        region.setAdapter(Regionadapter);
        region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedRegion = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < RegionModelArrayList.size(); i++) {
                    if (RegionModelArrayList.get(i).getRegionName().contains(SelectedRegion)) {

                        RegionModelArrayListTmp.add(RegionModelArrayList.get(i));
                        SelectedRegionId = RegionModelArrayList.get(position - 1).getRegionId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> Serviceadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, ServiceList);
        services.setAdapter(Serviceadapter);
        services.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedService = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < ServiceModelArrayList.size(); i++) {
                    if (ServiceModelArrayList.get(i).getServiceName().contains(SelectedService)) {

                        ServiceModelArrayListTmp.add(ServiceModelArrayList.get(i));
                        SelectedServiceId = ServiceModelArrayList.get(position - 1).getServiceId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> Industryadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, IndustryList);
        industrykey.setAdapter(Industryadapter);
        industrykey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedIndustry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < IndustryModelArrayList.size(); i++) {
                    if (IndustryModelArrayList.get(i).getIndustryName().contains(SelectedIndustry)) {

                        IndustryModelArrayListTmp.add(IndustryModelArrayList.get(i));
                        SelectedIndustryId = IndustryModelArrayList.get(position - 1).getIndustryId();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_search = search_by_keyword.getText().toString().trim();
                if (!(search_by_keyword.getText().toString().equalsIgnoreCase(""))) {
                    str_search = search_by_keyword.getText().toString();
                } else {
                    searchCertifiedBusinessList(user_id, str_search, SelectedCountryId, SelectedRegionId, SelectedServiceId, SelectedIndustryId);
                }
            }
        });
        LeadshipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.show(getActivity().getSupportFragmentManager(), DialogueFilterCertifiedBusiness.class.getSimpleName());
            }
        });

        return view;
    }

    public void getRegionDetails() {

        RegionModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getRegion";


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

                                JSONArray json2 = json.getJSONArray("regions");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("region");

                                    RegionModel model = new RegionModel();
                                    model.setRegionId(RegionObj.getString("id"));
                                    model.setRegionName(RegionObj.getString("name"));
                                    model.setRegionStatus(RegionObj.getString("status"));
                                    RegionModelArrayList.add(model);
                                }
                                for (int i = 0; i < RegionModelArrayList.size(); i++) {
                                    final RegionModel Items = RegionModelArrayList.get(i);
                                    RegionList.add(Items.getRegionName());
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


    public void getCountryDetails() {

        CountryModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getCountry";
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

                                JSONArray json2 = json.getJSONArray("countries");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("country");

                                    CountryModel model = new CountryModel();
                                    model.setCountryId(RegionObj.getString("id"));
                                    model.setCountryName(RegionObj.getString("name"));
                                    model.setCountryStatus(RegionObj.getString("status"));

                                    CountryModelArrayList.add(model);
                                }
                                for (int i = 0; i < CountryModelArrayList.size(); i++) {
                                    final CountryModel Items = CountryModelArrayList.get(i);
                                    CountryList.add(Items.getCountryName());
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

    public void getServiceDetails() {


        ServiceModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getServices";


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


    public void getIndustryDetails() {
        IndustryModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getIndustries";


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

                                    IndustryModel model = new IndustryModel();
                                    model.setIndustryId(RegionObj.getString("id"));
                                    model.setIndustryName(RegionObj.getString("name"));
                                    model.setIndustryStatus(RegionObj.getString("status"));
                                    IndustryModelArrayList.add(model);
                                }

                                for (int i = 0; i < IndustryModelArrayList.size(); i++) {
                                    final IndustryModel Items = IndustryModelArrayList.get(i);
                                    IndustryList.add(Items.getIndustryName());
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

    public void searchCertifiedBusinessList(String user_id, String str_search, String SelectedCountryId, String SelectedRegionId, String SelectedServiceId, String SelectedIndustryId) {
        progressBarHolder.setVisibility(View.VISIBLE);

        BusinessDataModelArrayList.clear();
        String url = (CommonUtils.BASE_URL) + "list_certified_business";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("country_id", SelectedCountryId)
                .add("region_id", SelectedRegionId)
                .add("service_id", SelectedServiceId)
                .add("key_industry_id", SelectedIndustryId)
                .add("keyword", str_search)
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
                                profile_pic_url = json.getString("image_url");
                                JSONArray update = json.getJSONArray("cb_data");
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                    BusinessdataModel model = new BusinessdataModel();
                                    model.setBusinesName(UpdateDetails.getString("item_title").replace("null", ""));
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_date"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setBusinesDate(CommentPostDate);
                                    model.setBusinesCountry(UpdateDetails.getString("country_name").replace("null", ""));
                                    model.setBusinesImage(profile_pic_url + UpdateDetails.getString("cb_id") + "/" + UpdateDetails.getString("image"));
                                    model.setBusinesServices(UpdateDetails.getString("service_name").replace("null", ""));
                                    model.setCb_id(UpdateDetails.getString("cb_id").replace("null", ""));
                                    model.setBusinesAddress(UpdateDetails.getString("address").replace("null", ""));
                                    model.setBusinesComment(UpdateDetails.getString("no_of_comments").replace("null", ""));
//                                    model.setPBCR_id(UpdateDetails.getString("pbcr_id").replace("null", ""));
                                    BusinessDataModelArrayList.add(model);
//                                    adapter.notifyDataSetChanged();
                                }
                                progressBarHolder.setVisibility(View.GONE);

                                adapter = new BusinessAdapter(getActivity(), BusinessDataModelArrayList, activityContext);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                businesslist.setLayoutManager(layoutmanager);
                                businesslist.setAdapter(adapter);

                            } else {
                                progressBarHolder.setVisibility(View.GONE);
                                adapter = new BusinessAdapter(getActivity(), BusinessDataModelArrayList, activityContext);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                businesslist.setLayoutManager(layoutmanager);
                                businesslist.setAdapter(adapter);

                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
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

    public void getCertifiedBusinesslist(String user_id) {

        progressBarHolder.setVisibility(View.VISIBLE);

        BusinessDataModelArrayList.clear();
        String url = (CommonUtils.BASE_URL) + "list_certified_business";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("country_id", "")
                .add("region_id", "")
                .add("service_id", "")
                .add("key_industry_id", "")
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
                                profile_pic_url = json.getString("image_url");
                                JSONArray update = json.getJSONArray("cb_data");
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                    BusinessdataModel model = new BusinessdataModel();
                                    model.setBusinesName(UpdateDetails.getString("item_title").replace("null", ""));
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_date"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setBusinesDate(CommentPostDate);
                                    model.setBusinesCountry(UpdateDetails.getString("country_name").replace("null", ""));
                                    model.setBusinesImage(profile_pic_url + UpdateDetails.getString("cb_id") + "/" + UpdateDetails.getString("image"));
                                    model.setBusinesServices(UpdateDetails.getString("service_name").replace("null", ""));
                                    model.setCb_id(UpdateDetails.getString("cb_id").replace("null", ""));
                                    model.setBusinesAddress(UpdateDetails.getString("address").replace("null", ""));
                                    model.setBusinesComment(UpdateDetails.getString("no_of_comments").replace("null", ""));
                                    BusinessDataModelArrayList.add(model);
                                }
                                progressBarHolder.setVisibility(View.GONE);

                                adapter = new BusinessAdapter(getActivity(), BusinessDataModelArrayList, activityContext);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                businesslist.setLayoutManager(layoutmanager);
                                businesslist.setAdapter(adapter);

                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
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

    public void getBusinessSelect() {
        RegionModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "home_certified_dropdown";


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

                                JSONArray json2 = json.getJSONArray("home_agency");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
//                                JSONObject RegionObj = RegionList.getJSONObject("region");

                                    LeadershipBusinessModel model = new LeadershipBusinessModel();
                                    model.setLeadershipId(RegionList.getString("id"));
                                    model.setLeadershipName(RegionList.getString("item_title"));
//                                model.setRegionStatus(RegionList.getString("status"));
                                    LeaderShipBusinessModel.add(model);
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("ValidFragment")
    public static class DialogueFilterCertifiedBusiness extends DialogFragment {
        String profile_pic_url;
        BusinessAdapter adapter;
        Spinner home_certified;
        Button send;
        String str_search;


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.business_leadership_search, null);
            send = dialogView.findViewById(R.id.send);
            final EditText SearchText = (EditText) dialogView.findViewById(R.id.searchtext);
            ImageView close = (ImageView) dialogView.findViewById(R.id.search_close_btn);
            final ProgressBar progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            Spinner certified = (Spinner) dialogView.findViewById(R.id.home_certified);

//            ArrayList<LeadershipBusinessModel> leadershipBusinessModels= new ArrayList<>();

//

            SearchCategories = new ArrayList<>();
            SearchCategories.add("Select Business");
//            getBusinessStatus();
            for (int i = 0; i < LeaderShipBusinessModel.size(); i++) {
                final LeadershipBusinessModel Items = LeaderShipBusinessModel.get(i);
                SearchCategories.add(Items.getLeadershipName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.text, SearchCategories);
            certified.setAdapter(adapter);
            certified.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String SelectedCategory = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < LeaderShipBusinessModel.size(); i++) {
                        if (LeaderShipBusinessModel.get(i).getLeadershipName().contains(SelectedCategory)) {

                            LeaderShipBusinessModelTemp.add(LeaderShipBusinessModel.get(i));
                            SelectedBusiness = LeaderShipBusinessModel.get(position - 1).getLeadershipId();
                        }
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    str_search = SearchText.getText().toString().trim();

                        progess_load.setVisibility(View.VISIBLE);
                        BusinessDataModelArrayList.clear();
                        String url = (CommonUtils.BASE_URL) + "search_cb_by_leadership";
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("user_id", user_id)
                                .add("keyword", str_search)
                                .add("home_agency", SelectedBusiness)
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
                                                profile_pic_url = json.getString("image_url");
                                                JSONArray update = json.getJSONArray("cb_data");
                                                for (int i = 0; i < update.length(); i++) {
                                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                                    BusinessdataModel model = new BusinessdataModel();
                                                    model.setBusinesName(UpdateDetails.getString("item_title").replace("null", ""));
                                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_date"));
                                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                                    model.setBusinesDate(CommentPostDate);
                                                    model.setBusinesCountry(UpdateDetails.getString("country_name").replace("null", ""));
                                                    model.setBusinesImage(profile_pic_url + UpdateDetails.getString("cb_id") + "/" + UpdateDetails.getString("image"));
                                                    model.setBusinesServices(UpdateDetails.getString("service_name").replace("null", ""));
                                                    model.setCb_id(UpdateDetails.getString("cb_id").replace("null", ""));
                                                    model.setBusinesAddress(UpdateDetails.getString("address").replace("null", ""));
                                                    model.setBusinesComment(UpdateDetails.getString("no_of_comments").replace("null", ""));
                                                    BusinessDataModelArrayList.add(model);
                                                }

                                                progess_load.setVisibility(View.GONE);

                                                BusinessAdapter adapter = new BusinessAdapter(getActivity(), BusinessDataModelArrayList, activityContext);
                                                layoutmanager = new LinearLayoutManager(getActivity());
                                                businesslist.setLayoutManager(layoutmanager);
                                                businesslist.setAdapter(adapter);
                                                search.dismiss();
                                            } else {
                                                progess_load.setVisibility(View.GONE);

                                                BusinessAdapter adapter = new BusinessAdapter(getActivity(), BusinessDataModelArrayList, activityContext);
                                                layoutmanager = new LinearLayoutManager(getActivity());
                                                businesslist.setLayoutManager(layoutmanager);
                                                businesslist.setAdapter(adapter);
                                                search.dismiss();
                                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
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
            });

            return dialogView;
        }


    }

}


