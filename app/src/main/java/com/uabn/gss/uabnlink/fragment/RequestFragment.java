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
import com.uabn.gss.uabnlink.Utility.UpdateRequests;
import com.uabn.gss.uabnlink.adapter.FriendReqAdapter;
import com.uabn.gss.uabnlink.model.FriendRequestDataModel;

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


public class RequestFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SharedPreferenceUtils preferances;
    String UserId;

    FrameLayout progressBarHolder;

    FriendReqAdapter adapter;
    RecyclerView requestlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<FriendRequestDataModel> RequestDataModelArrayList = new ArrayList<>();

    public RequestFragment() {
        // Required empty public constructor
    }


    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        preferances = SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        requestlist = view.findViewById(R.id.requestlist);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);

        GetAllRequests();


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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void GetAllRequests() {

        progressBarHolder.setVisibility(View.VISIBLE);


        RequestDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"get_friend_request_list";

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

                                JSONArray Requests = json.getJSONArray("request_list");

                                for (int j = 0; j < Requests.length(); j++) {
                                    JSONObject RequestDetail = Requests.getJSONObject(j);
                                    JSONObject Request = RequestDetail.getJSONObject("FriendInvitation");
                                    JSONObject Sender = RequestDetail.getJSONObject("User");

                                    FriendRequestDataModel model = new FriendRequestDataModel();

                                    model.setReqId(Request.getString("id"));
                                    model.setReqSenderId(Request.getString("sender_id"));
                                    model.setReqReceiverId(Request.getString("receiver_id"));
                                    model.setReqStatus(Request.getString("status"));
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Request.getString("request_date"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setReqDate(CommentPostDate);

                                    model.setReqSenderName(Sender.getString("name"));
                                    String ProfilePic = json.getString("profile_pic_url") + Sender.getString("id") + "/" + Sender.getString("profile_picture");
                                    model.setReqSenderImage(ProfilePic);

                                    RequestDataModelArrayList.add(model);

                                }
                                progressBarHolder.setVisibility(View.GONE);

                                adapter = new FriendReqAdapter(getActivity(), RequestDataModelArrayList, new UpdateRequests() {
                                    @Override
                                    public void UpdateRequests() {
                                            GetAllRequests();
                                    }
                                });
                                layoutmanager = new LinearLayoutManager(getActivity());
                                requestlist.setLayoutManager(layoutmanager);
                                requestlist.setAdapter(adapter);
                                requestlist.scrollToPosition(RequestDataModelArrayList.size()-1);

                            }

                            else {
                                layoutmanager = new LinearLayoutManager(getActivity());
                                requestlist.setLayoutManager(layoutmanager);
                                requestlist.setAdapter(adapter);
                                requestlist.scrollToPosition(RequestDataModelArrayList.size()-1);

                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),json.getString("message"),Toast.LENGTH_LONG).show();
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

}
