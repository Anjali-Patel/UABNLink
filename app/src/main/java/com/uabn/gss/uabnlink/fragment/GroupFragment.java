package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.uabn.gss.uabnlink.Activities.Fab;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.GroupEdit;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.UpdateGroup;
import com.uabn.gss.uabnlink.adapter.GroupAdapter;
import com.uabn.gss.uabnlink.model.GroupDataModel;
import com.uabn.gss.uabnlink.model.GroupTypeModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GroupFragment extends Fragment implements View.OnClickListener, GroupEdit, UpdateGroup {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int month = 0, dd = 0, yer = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    RecyclerView grouplist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<GroupDataModel> GroupDataModelArrayList = new ArrayList<>();
    Context activityContext;
    GroupAdapter adapter;
    RelativeLayout MainView;
    FrameLayout progressBarHolder;
    SharedPreferenceUtils preferances;
    String UserId, GroupType;
    private MaterialSheetFab materialSheetFab;
    Fab fab;
    View sheetView;
    View overlay;
    int statusBarColor;
    TextView my_groups,liked_groups,latest_groups;
    String Status = "", GruopTypeId = "", Keyword = "";
    Button AddGroupButton, SearchButton;
    Spinner group_type, status;
    EditText keyword;
    ArrayList<String> ListGroupType;
    ArrayList<String> GroupStatus;
    ArrayList<GroupTypeModel> GroupTypeModelArrayList;
    ProgressBar progress_load;
    public GroupFragment() {
        // Required empty public constructor
    }
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
        View view =  inflater.inflate(R.layout.fragment_group, container, false);
        preferances= SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        grouplist = view.findViewById(R.id.grouplist);
        fab = view.findViewById(R.id.fab);
        sheetView = view.findViewById(R.id.fab_sheet);
        overlay = view.findViewById(R.id.overlay);
        my_groups = view.findViewById(R.id.my_groups);
        liked_groups = view.findViewById(R.id.liked_groups);
        latest_groups = view.findViewById(R.id.latest_groups);
        AddGroupButton = view.findViewById(R.id.AddGroupButton);
        SearchButton = view.findViewById(R.id.SearchButton);
        keyword = view.findViewById(R.id.keyword);
        MainView = view.findViewById(R.id.MainView);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        group_type = view.findViewById(R.id.group_type);
        status = view.findViewById(R.id.status);
        progress_load = view.findViewById(R.id.progress_load);
        GroupTypeModelArrayList = new ArrayList<GroupTypeModel>();
        ListGroupType = new ArrayList<>();
        ListGroupType.add("Select Type");
        GroupStatus = new ArrayList<>();
        GroupStatus.add("Select Status");
        GroupStatus.add("Active");
        GroupStatus.add("Inactive");
        getGroupTypes();
        group_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedType = parent.getItemAtPosition(position).toString();
                if (SelectedType.equalsIgnoreCase("Select Type")){
                    GruopTypeId = "";
                }
                else {
                    for(int i = 0; i < GroupTypeModelArrayList.size();i++) {
                        if (GroupTypeModelArrayList.get(i).getGroupName().contains(SelectedType)) {
                            GruopTypeId = GroupTypeModelArrayList.get(position - 1).getGroupId();
                        }
                    } } }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        setupFab();
        mListener.onChangeTitle("Groups");
        String url = (CommonUtils.BASE_URL)+"list_groups";
        GetAllGroups(url);


        //GetGroupDetails();

        AddGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.addGroup("Add Group", "");
                }
            }
        });

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Keyword = keyword.getText().toString();
                mListener.onChangeTitle("Result Groups");
                String url = (CommonUtils.BASE_URL)+"list_groups";
                GetAllGroups(url);
            }
        });

        ArrayAdapter<String> statusadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,GroupStatus);
        status.setAdapter(statusadapter);
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedStatus = parent.getItemAtPosition(position).toString();

                if (SelectedStatus.equalsIgnoreCase("Active")){
                    Status = "1";
                }
                else if (SelectedStatus.equalsIgnoreCase("Inactive")){
                    Status = "0";
                }
                else if (SelectedStatus.equalsIgnoreCase("Select Status")){
                    Status = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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
    public void EditGroup(String title, String GroupId) {
        if(mListener != null){
            mListener.addGroup(title, GroupId);
            mListener.onChangeTitle(title);
        }
    }

    @Override
    public void GroupUpdate() {
        mListener.onChangeTitle("My Groups");
        String url = (CommonUtils.BASE_URL)+"list_groups";
        GetAllGroups(url);    }


    public interface OnFragmentInteractionListener {
        void addGroup(String title, String GroupId);
        void onChangeTitle(String title);
    }

//    public void GetGroupDetails() {
//
//        for (int i = 0; i < 5; i++) {
//
//            GroupDataModel model = new GroupDataModel();
//
//            model.setGroupName("Kings United group");
//            model.setGroupType("Business");
//            model.setGroupDate("07-22-2019 09:41 am");
//            model.setGroupBy("Linna henderson");
//            model.setGroupViews("30");
//            model.setGroupComments("2");
//
////            model.setMemberImage("");
//
//
//            GroupDataModelArrayList.add(model);
//        }
//        adapter.notifyDataSetChanged();
//    }


    private void setupFab() {

        int sheetColor = getResources().getColor(R.color.primaryTextColor);
        int fabColor = getResources().getColor(R.color.colorPrimary);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });



        my_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onChangeTitle("My Groups");
                String url = (CommonUtils.BASE_URL)+"my_groups";
            getMyGroup(url);
                materialSheetFab.hideSheet();
            }
        });

        liked_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onChangeTitle("Liked Groups");
                String url = (CommonUtils.BASE_URL)+"liked_groups";
                GetAllGroups(url);
                materialSheetFab.hideSheet();
            }
        });
        latest_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onChangeTitle("Latest Groups");
                String url = (CommonUtils.BASE_URL)+"latest_groups";
                GetAllGroups(url);
                materialSheetFab.hideSheet();
            }
        });

        //findViewById(R.id.fab_sheet_item_note).setOnClickListener(this);
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getActivity().getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "Item pressed", Toast.LENGTH_SHORT).show();
        materialSheetFab.hideSheet();
    }
    public void getMyGroup(final String EventUrl) {
        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);
        GroupDataModelArrayList.clear();
        RequestBody formBody;
        Request request = null;
        OkHttpClient client = null;
        if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"my_groups")){
            GroupType = "MyGroup";
            client = new OkHttpClient();
            formBody= new FormBody.Builder().add("user_id", UserId).build();
             request = new Request.Builder()
                    .url(EventUrl)
                    .post(formBody)
                    .build();
        }
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
                                JSONArray Groups = json.getJSONArray("event_data");
                                String covetUrl = json.getString("bg_image_url");
                                for (int i = 0; i < Groups.length(); i++) {
                                    JSONObject GroupDetails = Groups.getJSONObject(i);
                                    GroupDataModel model = new GroupDataModel();
                                    model.setGroupId(GroupDetails.getString("id"));
                                    model.setGroupCreatorUserId(GroupDetails.getString("user_id"));
                                    model.setGroupName(GroupDetails.getString("group_name").replace("null", ""));
                                    model.setGroupType(GroupDetails.getString("group_type").replace("null", ""));
//                                    model.setGroupDate(GroupDetails.getString("display_date").replace("null", ""));
                                    model.setGroupBy(GroupDetails.getString("by").replace("null", ""));
                                    model.setGroupViews(GroupDetails.getString("no_of_views").replace("null", ""));
                                    model.setGroupComments(GroupDetails.getString("no_of_comments").replace("null", ""));
                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(GroupDetails.getString("date_created"));
                                                    String newString = new SimpleDateFormat("MMM dd, yyyy").format(date); // 25-03-2019
                                                    model.setGroupDate(newString);
//
//                                    String dateString = GroupDetails.getString("display_date");
//                                    DateFormat df = new SimpleDateFormat("MM dd, yyyy");
//                                    Date readDate = df.parse(dateString);
//                                    Calendar cal = Calendar.getInstance();
//                                    cal.setTime(readDate);
//                                    month = cal.get(Calendar.MONTH);
//                                    dd = cal.get(Calendar.DATE);
//                                    yer = cal.get(Calendar.YEAR);
//                                    String display_date=String.valueOf(month)+"" + dd +","+ yer;
//
//                                    model.setGroupDate(display_date);



                                    if (GroupDetails.has("isjoined")){
                                        model.setIsJoined(GroupDetails.getString("isjoined").replace("null", ""));
                                    }
                                    else{
                                        model.setIsJoined("0");
                                    }
                                    if (GroupDetails.has("islike")){
                                        model.setIsGroupLiked(GroupDetails.getString("islike").replace("null", ""));
                                    }
                                    else{
                                        model.setIsGroupLiked("0");
                                    }
                                    model.setUserGroupType(GroupType);

                                    String IconUrl = covetUrl + GroupDetails.getString("id") +"/"+ GroupDetails.getString("bg_image");
                                    model.setGroupIcon(IconUrl);

                                    GroupDataModelArrayList.add(model);


                                }

                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);

                                adapter = new GroupAdapter(getActivity(), GroupDataModelArrayList, activityContext, GroupFragment.this, GroupFragment.this);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                grouplist.setLayoutManager(layoutmanager);
                                grouplist.setAdapter(adapter);

                            }

                            else {
                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);

                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
                                adapter = new GroupAdapter(getActivity(), GroupDataModelArrayList, activityContext, GroupFragment.this, GroupFragment.this);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                grouplist.setLayoutManager(layoutmanager);
                                grouplist.setAdapter(adapter);
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

        progress_load.setVisibility(View.GONE);

    }
    public void GetAllGroups(final String EventUrl) {

        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);
        GroupDataModelArrayList.clear();

        RequestBody formBody;
        Request request = null;
        OkHttpClient client = null;

        if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"list_groups")){

            GroupType = "MyGroup";

            client = new OkHttpClient();
            formBody= new FormBody.Builder()
                    .add("user_id", UserId)
                    .add("status", Status)
                    .add("group_type_id", GruopTypeId)
                    .add("keyword", Keyword)
                    .build();
            request = new Request.Builder()
                    .url(EventUrl)
                    .post(formBody)
                    .build();
        }
        else if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"latest_groups")){
            GroupType = "LatestGroups";
            client = new OkHttpClient();
            formBody = new FormBody.Builder()
                    .add("user_id", UserId)
                    .build();
            request = new Request.Builder()
                    .url(EventUrl)
                    .post(formBody)
                    .build();
        }
        else if (EventUrl.equalsIgnoreCase((CommonUtils.BASE_URL)+"liked_groups")){
            GroupType = "LikedGroups";
            client = new OkHttpClient();
            formBody = new FormBody.Builder()
                    .add("user_id", UserId)
                    .add("status", Status)
                    .add("group_type_id", GruopTypeId)
                    .add("keyword", Keyword)
                    .build();
            request = new Request.Builder()
                    .url(EventUrl)
                    .post(formBody)
                    .build();
        }

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
                                JSONArray Groups = json.getJSONArray("event_data");
                                String covetUrl = json.getString("bg_image_url");
                                for (int i = 0; i < Groups.length(); i++) {
                                    JSONObject GroupDetails = Groups.getJSONObject(i);
                                    GroupDataModel model = new GroupDataModel();
                                    model.setGroupId(GroupDetails.getString("id"));
                                    model.setGroupCreatorUserId(GroupDetails.getString("user_id"));
                                    model.setGroupName(GroupDetails.getString("group_name").replace("null", ""));
                                    model.setGroupType(GroupDetails.getString("group_type").replace("null", ""));
//                                    model.setGroupDate(GroupDetails.getString("display_date").replace("null", ""));
                                    model.setGroupBy(GroupDetails.getString("by").replace("null", ""));
                                    model.setGroupViews(GroupDetails.getString("no_of_views").replace("null", ""));
                                    model.setGroupComments(GroupDetails.getString("no_of_comments").replace("null", ""));
                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(GroupDetails.getString("date_created"));
                                    String newString = new SimpleDateFormat("MMM dd, yyyy").format(date); // 25-03-2019
                                    model.setGroupDate(newString);
//
//                                    String dateString = GroupDetails.getString("display_date");
//                                    DateFormat df = new SimpleDateFormat("MM dd, yyyy");
//                                    Date readDate = df.parse(dateString);
//                                    Calendar cal = Calendar.getInstance();
//                                    cal.setTime(readDate);
//                                    month = cal.get(Calendar.MONTH);
//                                    dd = cal.get(Calendar.DATE);
//                                    yer = cal.get(Calendar.YEAR);
//                                    String display_date=String.valueOf(month)+"" + dd +","+ yer;
//
//                                    model.setGroupDate(display_date);

                                    if (GroupDetails.has("isjoined")){
                                        model.setIsJoined(GroupDetails.getString("isjoined").replace("null", ""));
                                    }
                                    else{
                                        model.setIsJoined("0");
                                    }

                                    if (GroupDetails.has("islike")){
                                        model.setIsGroupLiked(GroupDetails.getString("islike").replace("null", ""));
                                    }
                                    else{
                                        model.setIsGroupLiked("0");
                                    }
                                    model.setUserGroupType(GroupType);

                                    String IconUrl = covetUrl + GroupDetails.getString("id") +"/"+ GroupDetails.getString("bg_image");
                                    model.setGroupIcon(IconUrl);

                                    GroupDataModelArrayList.add(model);
                                }MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                                adapter = new GroupAdapter(getActivity(), GroupDataModelArrayList, activityContext, GroupFragment.this, GroupFragment.this);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                grouplist.setLayoutManager(layoutmanager);
                                grouplist.setAdapter(adapter);

                            } else {
                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
                                adapter = new GroupAdapter(getActivity(), GroupDataModelArrayList, activityContext, GroupFragment.this, GroupFragment.this);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                grouplist.setLayoutManager(layoutmanager);
                                grouplist.setAdapter(adapter);
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

        progress_load.setVisibility(View.GONE);

    }


    public void getGroupTypes(){

        GroupTypeModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getGroupTypeDropdown";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).get().build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
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

                                JSONArray json2 = json.getJSONArray("group_types");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("group_type");

                                    GroupTypeModel model = new GroupTypeModel();
                                    model.setGroupId(RegionObj.getString("id"));
                                    model.setGroupName(RegionObj.getString("type_title"));
                                    model.setGroupStatus(RegionObj.getString("status"));

                                    ListGroupType.add(RegionObj.getString("type_title"));

                                    GroupTypeModelArrayList.add(model);
                                }
                                ArrayAdapter<String> typewrapper = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListGroupType);
                                group_type.setAdapter(typewrapper);
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
