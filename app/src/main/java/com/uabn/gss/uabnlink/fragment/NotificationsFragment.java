package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.BusinessAdapter;
import com.uabn.gss.uabnlink.adapter.MembersAdapter;
import com.uabn.gss.uabnlink.adapter.NotificationAdapter;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.uabn.gss.uabnlink.model.NotificationModel;

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


public class NotificationsFragment extends Fragment {
    RecyclerView recycler_view;
    String UserId;
    SharedPreferenceUtils preferances;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<NotificationModel> NotificationList;
    NotificationAdapter adapter;
    FrameLayout progressBarHolder;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NotificationsFragment() {

    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        View view= inflater.inflate(R.layout.fragment_notifications, container, false);
        recycler_view=view.findViewById(R.id.recycler_view);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        NotificationList = new ArrayList<>();
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
//        adapter = new NotificationAdapter(getActivity(), NotificationList);
//        layoutmanager = new LinearLayoutManager(getActivity());
//        recycler_view.setLayoutManager(layoutmanager);
//        recycler_view.setAdapter(adapter);

        notificationList();
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void notificationList() {
        progressBarHolder.setVisibility(View.VISIBLE);
        NotificationList.clear();
        String url = (CommonUtils.BASE_URL) +"notification_list";
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
                                JSONArray members = json.getJSONArray("notofications");
                                for (int i = 0; i < members.length(); i++) {
                                    JSONObject notification_data = members.getJSONObject(i);
//                                    JSONObject Memberdetail = MemberDetails.getJSONObject("User");
                                    NotificationModel model = new NotificationModel();
                                    model.setDisplay_text(notification_data.getString("display_text").replace("null",""));
                                    model.setType(notification_data.getString("type").replace("null", ""));
                                    model.setId(notification_data.getString("id").replace("null", ""));
                                    model.setNotfn_id(notification_data.getString("notfn_id").replace("null", ""));
                                    model.setSection(notification_data.getString("section").replace("null", ""));
                                    NotificationList.add(model);
                                }
                                progressBarHolder.setVisibility(View.GONE);
                                adapter = new NotificationAdapter(getActivity(), NotificationList);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                recycler_view.setLayoutManager(layoutmanager);
                                recycler_view.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                progressBarHolder.setVisibility(View.GONE);
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
}
