package com.uabn.gss.uabnlink.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MyProfileview extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ImageView userImg;
    TextView UserName, UserBio, country, state, city, org, proff_status,industry,business, services, status, phone,graduate_school,high_school,undergraduate_school;
    String UserId;
    ProgressBar progess_load;
    SharedPreferenceUtils preferances;
    LinearLayout EditProfile;
    String ImageURL;
    WebView bio_webview;


    public MyProfileview() {
        // Required empty public constructor
    }


    public static MyProfileview newInstance(String param1, String param2) {
        MyProfileview fragment = new MyProfileview();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_profileview, container, false);
        userImg = view.findViewById(R.id.userImg);
        UserName = view.findViewById(R.id.UserName);
        UserBio = view.findViewById(R.id.UserBio);
        bio_webview =  view.findViewById(R.id.bio_webview);
        country = view.findViewById(R.id.country);
        state = view.findViewById(R.id.state);
        city = view.findViewById(R.id.city);
        phone = view.findViewById(R.id.phone);
        org = view.findViewById(R.id.org);
        undergraduate_school=view.findViewById(R.id.undergraduate_school);
        high_school=view.findViewById(R.id.high_school);
        graduate_school=view.findViewById(R.id.graduate_school);
        proff_status = view.findViewById(R.id.proff_status);
        industry = view.findViewById(R.id.industry);
        business = view.findViewById(R.id.business);
        services = view.findViewById(R.id.services);
        status = view.findViewById(R.id.status);
        EditProfile = view.findViewById(R.id.EditProfile);

        progess_load = view.findViewById(R.id.progess_load);

        preferances= SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");

        getUserData(UserId);

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.editProfile();
                }
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(getContext(), R.style.DialogAnimation_2);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.setContentView(R.layout.zoom_profile_pic);
                ZoomImageView img = dialog.findViewById(R.id.img);
                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                ImageProgress.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(ImageURL).into(img);
                ImageProgress.setVisibility(View.GONE);
                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                dialog.show();
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


    public interface OnFragmentInteractionListener {
        void editProfile();

    }
    public void getUserData(String UserId) {
        progess_load.setVisibility(View.VISIBLE);
        String url = (CommonUtils.BASE_URL)+"viewUserInfo";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("view_id", UserId)
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
                                String ProfileBase = json.getString("profile_pic_url");
                                JSONObject UserData = json.getJSONObject("userinfo");
                                JSONObject UserUpdate = UserData.getJSONObject("User");
                                JSONObject grade_school = UserData.getJSONObject("GradeSchool");
                                JSONObject undergraduateSchool = UserData.getJSONObject("UnderGradeSchool");
                                JSONObject highschool = UserData.getJSONObject("HighSchool");
                                JSONObject Region = UserData.getJSONObject("Region");
                                    JSONObject UserType = UserData.getJSONObject("UserType");
                                    JSONObject Country = UserData.getJSONObject("Country");
                                    JSONObject Nation = UserData.getJSONObject("Nation");
                                    JSONObject State = UserData.getJSONObject("State");
                                    JSONObject Category = UserData.getJSONObject("Category");
                                    JSONObject Agency = UserData.getJSONObject("Agency");
                                    ImageURL = ProfileBase + UserUpdate.getString("id") + "/" + UserUpdate.getString("profile_picture");
                                    Glide.with(getActivity()).load(ImageURL).into(userImg);
                                    UserName.setText(UserUpdate.getString("name").replace("null", "-"));
                                    UserBio.setText(UserUpdate.getString("email").replace("null", "-"));
                                    country.setText(Country.getString("name").replace("null", "-"));
                                    state.setText(State.getString("name").replace("null", "-"));
                                    city.setText(UserUpdate.getString("city").replace("null", "-"));
                                    phone.setText(UserUpdate.getString("mobile_phone").replace("null","-"));
                                high_school.setText(highschool.getString("name").replace("null","-"));
                                undergraduate_school.setText(undergraduateSchool.getString("name").replace("null","-"));
                                graduate_school.setText(grade_school.getString("name").replace("null","-"));
                                org.setText(UserUpdate.getString("employer").replace("null", "-"));
                                    proff_status.setText(UserUpdate.getString("profession").replace("null", "-"));
                                    industry.setText(UserUpdate.getString("industry_focus").replace("null", "-"));
                                    business.setText(Agency.getString("item_title").replace("null", "-"));
                                    services.setText(UserUpdate.getString("core_services").replace("null", "-"));
                                    status.setText(UserType.getString("status").replace("null", "-"));
if(!UserUpdate.getString("bio").equalsIgnoreCase("")){
  String  html=UserUpdate.getString("bio");
//    bio_webview.l(html, "text/html", "utf-8")

    bio_webview.requestFocus();
    bio_webview.getSettings().setLightTouchEnabled(true);
    bio_webview.getSettings().setJavaScriptEnabled(true);
    bio_webview.getSettings().setGeolocationEnabled(true);
    bio_webview.setSoundEffectsEnabled(true);
    bio_webview.loadData(html, "text/html","UTF-8");
//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//        bio_webview.setText(Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY));
//    } else {
//        bio_webview.setText(Html.fromHtml(html));
//    }
//    bio_webview.setText(Html.fromHtml(html));
}
progess_load.setVisibility(View.GONE);
                            } else {
                                progess_load.setVisibility(View.GONE);
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

}
