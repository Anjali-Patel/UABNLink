package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.uabn.gss.uabnlink.Activities.Fab;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.FindFreinds;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.FindContactAdapter;
import com.uabn.gss.uabnlink.model.MemberDataModel;

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


public class FindContactsFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText input_search;
    String UserId;

    SharedPreferenceUtils preferances;
    RecyclerView friendlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<MemberDataModel> MembersDataModelArrayList = new ArrayList<>();
    ArrayList<MemberDataModel> MembersDataModelArrayListTemp = new ArrayList<>();
    Context activityContext;
    FindContactAdapter adapter;
    Fab fab;
    View sheetView;
    View overlay;
    int statusBarColor;
    TextView my_connections, invite_contact, all_connections, find_contact;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private MaterialSheetFab materialSheetFab;
    String ProfiulePicBaseURL;
    FindFreinds freindList;

    FrameLayout progressBarHolder;
    public FindContactsFragment() {
    }

    public static FindContactsFragment newInstance(String param1, String param2) {
        FindContactsFragment fragment = new FindContactsFragment();
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

    private static final String TAG = "FindContactsFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_contacts, container, false);
        friendlist = view.findViewById(R.id.friendlist);
        fab = view.findViewById(R.id.fab);
        progressBarHolder=view.findViewById(R.id.progressBarHolder);
        sheetView = view.findViewById(R.id.fab_sheet);
        overlay = view.findViewById(R.id.overlay);
        my_connections = view.findViewById(R.id.my_connections);
        invite_contact = view.findViewById(R.id.invite_contact);
        all_connections = view.findViewById(R.id.all_connections);
        find_contact = view.findViewById(R.id.find_contact);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
//        send=view.findViewById(R.id.send);
        input_search = view.findViewById(R.id.input_search);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        setupFab();

         freindList = new FindFreinds() {

            @Override
            public void FreindListLoad() {
                GetMemberDetails();
                Log.i(TAG, "FreindListLoad: ");
            }
        };


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
                    adapter = new FindContactAdapter(getActivity(), MembersDataModelArrayListTemp, ProfiulePicBaseURL, freindList);
                    friendlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new FindContactAdapter(getActivity(), MembersDataModelArrayList, ProfiulePicBaseURL, freindList);
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
                    adapter = new FindContactAdapter(getActivity(), MembersDataModelArrayListTemp, ProfiulePicBaseURL, freindList);
                    friendlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new FindContactAdapter(getActivity(), MembersDataModelArrayList, ProfiulePicBaseURL, freindList);
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
        progressBarHolder.setVisibility(View.VISIBLE);


        MembersDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getAllMembers";

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

                                                ProfiulePicBaseURL = json.getString("profile_pic_url");

                                                JSONArray update = json.getJSONArray("members");
                                                for (int i = 0; i < update.length(); i++) {
                                                    JSONObject Memberdetail = update.getJSONObject(i);
//                                                    JSONObject Memberdetail = UpdateDetails.getJSONObject("User");
                                                    MemberDataModel model = new MemberDataModel();
                                                    model.setMemberGender(Memberdetail.getString("gender").replace("null", ""));
                                                    model.setMemberStatus(Memberdetail.getString("professional_status").replace("null", ""));
                                                    model.setMemberName(Memberdetail.getString("name").replace("null", ""));
                                                    model.setMemberLocation(Memberdetail.getString("Country").replace("null", ""));
                                                    model.setMemberId(Memberdetail.getString("user_id").replace("null", ""));
                                                    model.setMemberImage(Memberdetail.getString("profile_picture").replace("null", ""));
                                                    model.setRequestStatus(Memberdetail.getString("request_status").replace("null", ""));

                                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Memberdetail.getString("join_on"));
                                                    String newString = new SimpleDateFormat("MMM dd yyyy").format(date); // 25-03-2019
                                                    model.setMemberJoined(newString);


                                                    MembersDataModelArrayList.add(model);
//                                                    adapter.notifyDataSetChanged();
                                                }
                                                progressBarHolder.setVisibility(View.GONE);
                                                adapter = new FindContactAdapter(getActivity(), MembersDataModelArrayList, ProfiulePicBaseURL, freindList);
                                                layoutmanager = new LinearLayoutManager(getActivity());
                                                friendlist.setLayoutManager(layoutmanager);
                                                friendlist.setAdapter(adapter);

                                            } else {

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

    private void setupFab() {

        int sheetColor = getResources().getColor(R.color.primaryTextColor);
        int fabColor = getResources().getColor(R.color.colorPrimary);


        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);


        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {

                statusBarColor = getStatusBarColor();

                setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });


        find_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnFindContact();
                }
            }
        });

        invite_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnInviteContact();
                }
            }
        });

        my_connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnMyConnections();
                }
            }
        });


        all_connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnAllconnections();
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
        void OnFindContact();

        void OnInviteContact();

        void OnMyConnections();

        void OnAllconnections();
    }

}