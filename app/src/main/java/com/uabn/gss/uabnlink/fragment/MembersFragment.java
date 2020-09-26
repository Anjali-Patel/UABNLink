package com.uabn.gss.uabnlink.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.uabn.gss.uabnlink.Activities.Fab;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.MembersAdapter;
import com.uabn.gss.uabnlink.model.MemberDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MembersFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferenceUtils preferances;
    String UserId;
    EditText input_search;
    String profile_pic_url;
    RecyclerView friendlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<MemberDataModel> MembersDataModelArrayListTemp = new ArrayList<>();
    ArrayList<MemberDataModel> MembersDataModelArrayList = new ArrayList<>();
    Context activityContext;
    MembersAdapter adapter;
    Fab fab;
    View sheetView;
    View overlay;
    int statusBarColor;
    TextView fab_sheet_item_recording, fab_sheet_item_reminder;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private MaterialSheetFab materialSheetFab;

    FrameLayout progressBarHolder;
    LinearLayout MainView;

    public MembersFragment() {

    }


    public static MembersFragment newInstance(String param1, String param2) {
        MembersFragment fragment = new MembersFragment();
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
        View view = inflater.inflate(R.layout.fragment_members, container, false);

        friendlist = view.findViewById(R.id.friendlist);

        fab = view.findViewById(R.id.fab);
        sheetView = view.findViewById(R.id.fab_sheet);
        overlay = view.findViewById(R.id.overlay);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        input_search = view.findViewById(R.id.input_search);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        fab_sheet_item_recording = view.findViewById(R.id.fab_sheet_item_recording);
        fab_sheet_item_reminder = view.findViewById(R.id.fab_sheet_item_reminder);
        MainView = view.findViewById(R.id.MainView);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);

        setupFab();


        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    MembersDataModelArrayListTemp.clear();
                    for (int i = 0; i < MembersDataModelArrayList.size(); i++) {
                        if (MembersDataModelArrayList.get(i).getMemberName().toLowerCase().startsWith(strText.toLowerCase())) {
                            MembersDataModelArrayListTemp.add(MembersDataModelArrayList.get(i));
                        }
                    }
                    adapter = new MembersAdapter(getActivity(), MembersDataModelArrayListTemp, activityContext,"");
                    friendlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new MembersAdapter(getActivity(), MembersDataModelArrayList, activityContext, "");
                    friendlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    MembersDataModelArrayListTemp.clear();
                    for (int i = 0; i < MembersDataModelArrayList.size(); i++) {
                        if (MembersDataModelArrayList.get(i).getMemberName().toLowerCase().startsWith(strText.toLowerCase())) {
                            MembersDataModelArrayListTemp.add(MembersDataModelArrayList.get(i));
                        }
                    }
                    adapter = new MembersAdapter(getActivity(), MembersDataModelArrayListTemp, activityContext,"");
                    friendlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new MembersAdapter(getActivity(), MembersDataModelArrayList, activityContext,"");
                    friendlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        });


        GetMemberDetails();

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
    public void GetMemberDetails() {
        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);

        MembersDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL) + "memberlist";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        final String myResponse = responseBody.string();

                        getActivity().runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = null;
                                        try {
                                            json = new JSONObject(myResponse);
                                            String success = json.getString("status");
                                            if (success.equalsIgnoreCase("success")) {
                                                profile_pic_url = json.getString("profile_pic_url");
                                                JSONArray update = json.getJSONArray("members");
                                                for (int i = 0; i < update.length(); i++) {
                                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                                    MemberDataModel model = new MemberDataModel();
                                                    model.setMemberName(UpdateDetails.getString("name").replace("null", ""));
                                                    model.setMemberAddress(UpdateDetails.getString("Country").replace("null", ""));
                                                    model.setMemberId(UpdateDetails.getString("user_id").replace("null", ""));
                                                    model.setMemberImage(profile_pic_url + UpdateDetails.getString("user_id")+ "/" +UpdateDetails.getString("profile_picture").replace("null", "").replace(" ", "%20"));
                                                    MembersDataModelArrayList.add(model);
//                                                    adapter.notifyDataSetChanged();
                                                }

                                                MainView.setVisibility(View.VISIBLE);
                                                progressBarHolder.setVisibility(View.GONE);

                                                adapter = new MembersAdapter(getActivity(), MembersDataModelArrayList, activityContext,"");
                                                layoutmanager = new LinearLayoutManager(getActivity());
                                                friendlist.setLayoutManager(layoutmanager);
                                                friendlist.setAdapter(adapter);

                                            } else {
                                                MainView.setVisibility(View.VISIBLE);
                                                progressBarHolder.setVisibility(View.GONE);
                                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        }
                });
        }

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

        // Set material sheet item click listeners
//        fab_sheet_item_recording.setOnClickListener(this);

        fab_sheet_item_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFabButtonFindInteraction();
                }
            }
        });

        fab_sheet_item_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFabButtonInviteInteraction();
                }
            }
        });

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFabButtonFindInteraction();

        void onFabButtonInviteInteraction();

    }

}
