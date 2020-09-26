package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

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


public class MyConnections extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String UserId;
    EditText input_search;
    SharedPreferenceUtils preferances;
    RecyclerView friendlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<MemberDataModel> MembersDataModelArrayList = new ArrayList<>();
    ArrayList<MemberDataModel> MembersDataModelArrayListTemp = new ArrayList<>();
    Context activityContext;
    MembersAdapter adapter;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    FrameLayout progressBarHolder;
    public MyConnections() {

    }
    public static MyConnections newInstance(String param1, String param2) {
        MyConnections fragment = new MyConnections();
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
        View view = inflater.inflate(R.layout.fragment_my_connections, container, false);
        friendlist = view.findViewById(R.id.friendlist);
        progressBarHolder=view.findViewById(R.id.progressBarHolder);
        adapter = new MembersAdapter(getActivity(), MembersDataModelArrayList, activityContext, "MyConnections");
        layoutmanager = new LinearLayoutManager(getActivity());
        friendlist.setLayoutManager(layoutmanager);
        friendlist.setAdapter(adapter);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        input_search = view.findViewById(R.id.input_search);
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
                    adapter = new MembersAdapter(getActivity(), MembersDataModelArrayList, activityContext,"");
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

    public void GetMemberDetails() {
        progressBarHolder.setVisibility(View.VISIBLE);
        MembersDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL) + "getMyConnections";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
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
                            String profile_pic_url = json.getString("profile_pic_url");


                            JSONArray members = json.getJSONArray("my_connections");

                            for (int i = 0; i < members.length(); i++) {
                                JSONObject MemberDetails = members.getJSONObject(i);
                                JSONObject Memberdetail = MemberDetails.getJSONObject("User");

                                MemberDataModel model = new MemberDataModel();
                                model.setMemberName(Memberdetail.getString("name").replace("null", ""));
                                model.setMemberAddress(Memberdetail.getString("city").replace("null", ""));
                                model.setMemberId(Memberdetail.getString("id").replace("null", ""));
                                model.setMemberImage(profile_pic_url + Memberdetail.getString("id") + "/" + Memberdetail.getString("profile_picture").replace("null", "").replace(" ", "%20"));

                                // model.setMemberImage(Memberdetail.getString("profile_picture").replace("null", ""));
                                MembersDataModelArrayList.add(model);
                                adapter.notifyDataSetChanged();
                                progressBarHolder.setVisibility(View.GONE);

                            }
                        } else {
                            Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
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
        void onFragmentInteraction(Uri uri);
    }

}
