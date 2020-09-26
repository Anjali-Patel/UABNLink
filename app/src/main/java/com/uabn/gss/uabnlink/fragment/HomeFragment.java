package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.CertifiedBusinessDetailActivity;
import com.uabn.gss.uabnlink.Activities.MainActivity;
import com.uabn.gss.uabnlink.DatabaseTable.UABNSQLitedatabase;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CheckNetwork;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.TemporaryHelper;
import com.uabn.gss.uabnlink.adapter.NewsAdapter;
import com.uabn.gss.uabnlink.model.MasterFilterModel;
import com.uabn.gss.uabnlink.model.NewsDataModel;
import com.uabn.gss.uabnlink.model.PostsDataModel;

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

import static com.uabn.gss.uabnlink.DatabaseTable.UABNSQLitedatabase.TABLE_NAME;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    UABNSQLitedatabase myDbHandler;
    private OnFragmentInteractionListener mListener;
  static  DialogueFilter filter ;

  static  RecyclerView newslist;
  static  RecyclerView.LayoutManager layoutmanager;
  static  ArrayList<NewsDataModel> NewsDataModelArrayList = new ArrayList<>();
 static    ArrayList<PostsDataModel> PostsDataModelArrayList = new ArrayList<>();
 static String PostBase, UserProfileBase, DocBase,Update_video;
    static  ArrayList<MasterFilterModel> MasterFilterlist;
    static  ArrayList<MasterFilterModel> MasterFilterlistTemp;
    static  ArrayList<String> SearchCategories;
    static  String user_id;
    ImageView filterlist;
    FrameLayout progressBarHolder;
    LinearLayout home_menu;
    TextView type_something;
    ImageView userImg;
    String databaseprofile_id,database_profile_name,database_profile_pic,database_description,databasepost_image,databaseuser_id,databasepost_id,database_date;
    static  Context activityContext;
  static  NewsAdapter adapter;
  static   SharedPreferenceUtils preferances;
    String UserId;
    String fetchdocpath,fetchprofilepath,fetchpostbase;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("UABN Home");
        newslist = view.findViewById(R.id.newslist);
        filterlist=view.findViewById(R.id.filterlist);
        progressBarHolder=view.findViewById(R.id.progressBarHolder);
        home_menu=view.findViewById(R.id.home_menu);
        userImg=view.findViewById(R.id.userImg);
        type_something=view.findViewById(R.id.type_something);
        preferances= SharedPreferenceUtils.getInstance(getActivity());
        user_id=preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        MasterFilterlist= new ArrayList<>();
        SearchCategories=new ArrayList<>();
        MasterFilterlistTemp= new ArrayList<>();
        myDbHandler = new UABNSQLitedatabase(getContext());
        TemporaryHelper.setUabnsqLitedatabase(myDbHandler);
        Glide.with(getActivity()).load(preferances.getStringValue(CommonUtils.MEMBER_IMAGE, "")).into(userImg);
        filter = new DialogueFilter();
        getFilterList();
        filterlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogueFilter filter = new DialogueFilter();
                filter.show(getActivity().getSupportFragmentManager(), DialogueFilter.class.getSimpleName());

            }
        });
        type_something.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.AddPost();
                }
            }
        });
        //GetNewsDetails();
        if(CheckNetwork.isInternetAvailable(getContext())){
            GetAllPosts();
        }else{
            Toast.makeText(getContext(),"No internet connection",Toast.LENGTH_LONG).show();
//            getPostDataOffline();

        }

//        GetAllPosts();
        return view;
    }
    private void getPostDataOffline() {
        fetchpostbase = SharedPreferenceUtils.getInstance(getContext()).getStringValue(CommonUtils.POSTBASEURL, "");
        fetchdocpath = SharedPreferenceUtils.getInstance(getContext()).getStringValue(CommonUtils.POSTDOCBASE, "");
        fetchprofilepath = SharedPreferenceUtils.getInstance(getContext()).getStringValue(CommonUtils.PROFILEBASEURL, "");
        Cursor res=myDbHandler.getAllData(UserId);
        if(res.getCount()==0){
            Toast.makeText(getContext(), "No data found", Toast.LENGTH_LONG).show();
            return ;
        }
        while(res.moveToNext()){
            PostsDataModel model = new PostsDataModel();
            String a=res.getString(0);
            String b=res.getString(1);
            String c=res.getString(2);
            String d=res.getString(3);
            String e=res.getString(4);
            String f=res.getString(5);
            String g=res.getString(7);
            model.setPostUserId(res.getString(6));
            model.setPostdate(res.getString(3));
            model.setPostId(res.getString(7));
            model.setPostUserName(res.getString(2));
            model.setPostUserImage(res.getString(1));
            model.setPostMessage(res.getString(4));
            model.setPostImage(res.getString(5));
            PostsDataModelArrayList.add(model);
            // String a=res.getString(1);
        }
        progressBarHolder.setVisibility(View.GONE);
        home_menu.setVisibility(View.VISIBLE);
        adapter = new NewsAdapter(getActivity(), PostsDataModelArrayList, activityContext, fetchpostbase, fetchprofilepath, fetchdocpath);
        layoutmanager = new LinearLayoutManager(getActivity());
        newslist.setLayoutManager(layoutmanager);
        newslist.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(newslist, false);

    }

    public void getFilterList() {
        MasterFilterlist.clear();
        SearchCategories.clear();
        SearchCategories.add("Select Status");
        String url = (CommonUtils.BASE_URL)+"get_updates_category";
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
                                JSONArray json2 = json.getJSONArray("updates_categories");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject filter_categories = RegionList.getJSONObject("category");
                                    MasterFilterModel model = new MasterFilterModel();
                                    model.setFilterId(filter_categories.getString("id"));
                                    model.setFilterName(filter_categories.getString("name"));
                                    model.setFilterStatus(filter_categories.getString("status"));
                                    MasterFilterlist.add(model);
                                    SearchCategories.add(filter_categories.getString("name"));
                                } }
                        } catch (JSONException e) {
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
    public interface OnFragmentInteractionListener {
        void AddPost();
    }
    public void GetAllPosts() {
        progressBarHolder.setVisibility(View.VISIBLE);
        PostsDataModelArrayList.clear();
        String url = (CommonUtils.BASE_URL)+"get_updates";
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
                                JSONArray update = json.getJSONArray("updates");
                                PostBase = json.getString("user_update_image");
                                UserProfileBase = json.getString("profile_pic_url");
                                DocBase = json.getString("user_update_doc");
                                Update_video = json.getString("user_update_video");
                                SharedPreferenceUtils.getInstance(getContext()).setValue(CommonUtils.DOCUMENTBASEURL, DocBase);
                                SharedPreferenceUtils.getInstance(getContext()).setValue(CommonUtils.POSTBASEURL, PostBase);
                                SharedPreferenceUtils.getInstance(getContext()).setValue(CommonUtils.PROFILEBASEURL, UserProfileBase);
                                SharedPreferenceUtils.getInstance(getContext()).setValue(CommonUtils.VIDEOBASEURL, Update_video);
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                    JSONObject UserUpdate = UpdateDetails.getJSONObject("UserUpdate");
                                    databaseuser_id=UserUpdate.getString("user_id");
                                    JSONObject user = UpdateDetails.getJSONObject("User");
                                    PostsDataModel model = new PostsDataModel();
                                    model.setUpdate_video(json.getString("user_update_video"));
                                    if (!UserUpdate.getString("video_type").equalsIgnoreCase("")&&!UserUpdate.getString("video_type").equalsIgnoreCase("null")){
                                        model.setVideo_type(UserUpdate.getString("video_type"));
                                    }
//                                    else{
////                                        model.setVideo_type("");
////                                    }
                                    if (!UserUpdate.getString("document").equalsIgnoreCase("")){
                                        model.setPostImage(UserUpdate.getString("document"));
                                        databasepost_image=UserUpdate.getString("document");
                                    }
//                                    else{
//                                        model.setPostImage("");
//                                    }
//                              model.setYou_tubeLink(UserUpdate.getString("youtube_link"));
                                    if (UserUpdate.has(("islike"))){
                                        model.setPostIsLiked(UserUpdate.getString("islike"));
                                    }
                                    else{
                                        model.setPostIsLiked("0");
                                    }
                                    if (!UserUpdate.getString("embed_code").equalsIgnoreCase("") && !UserUpdate.getString("embed_code").equalsIgnoreCase("null")){
                                        model.setPostEmbeddedVideo(UserUpdate.getString("embed_code"));
                                    }
                                    else{
                                        model.setPostEmbeddedVideo("");
                                    }
//                                    String img=Update_video+UserUpdate.getString("id")+"/"+UserUpdate.getString("document");
//                                    model.setGallery_camera_video(img);
                                    if(!UserUpdate.getString("document_typ").equalsIgnoreCase("") && !UserUpdate.getString("document_typ").equalsIgnoreCase("null")){
                                        model.setPostMediaType(UserUpdate.getString("document_typ"));
                                    }
                                    model.setPostId(UserUpdate.getString("id"));
                                    model.setPostLikeCount(UserUpdate.getString("UserUpdatesLike_count"));
                                    model.setPostCommentCount(UserUpdate.getString("UserUpdatesComment_count"));
                                    model.setPostMessage(UserUpdate.getString("message"));
                                    database_description=UserUpdate.getString("message");
                                    databasepost_id=UserUpdate.getString("id");
                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UserUpdate.getString("created_date"));
                                    String newString = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(date); // 25-03-2019
                                    model.setPostdate(newString);
                                    database_date=newString;
                                    model.setPostUserId(user.getString("id"));
                                    databaseprofile_id=user.getString("id");
                                    model.setPostUserName(user.getString("name"));
                                    database_profile_name=user.getString("name");
                                    model.setPostUserImage(user.getString("profile_picture"));
                                    database_profile_pic=user.getString("profile_picture");
                                    insertDataOffline();

//                                    boolean isInserted = myDbHandler.insertData(databaseuser_id, database_profile_pic,database_profile_name,database_date,database_description,databasepost_image,databaseprofile_id,databasepost_id);
//                                    if (isInserted == true) {
//                                        Toast.makeText(getContext(), "Data inserted successfully", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(getContext(), "Data not  inserted successfully", Toast.LENGTH_LONG).show();
//                                    }
                                    PostsDataModelArrayList.add(model);
                                }
                                progressBarHolder.setVisibility(View.GONE);
                                home_menu.setVisibility(View.VISIBLE);
                                adapter = new NewsAdapter(getActivity(), PostsDataModelArrayList, activityContext, PostBase, UserProfileBase, DocBase);
                                layoutmanager = new LinearLayoutManager(getActivity());
                                newslist.setLayoutManager(layoutmanager);
                                newslist.setAdapter(adapter);
                                ViewCompat.setNestedScrollingEnabled(newslist, false);
//                                adapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getContext(),json.getString("message"),Toast.LENGTH_LONG).show();


//                                ProgessLoad.setVisibility(View.GONE);
//
//                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Register.this);
//                                builder.create();
//                                builder.setMessage("Opps! Some problem occured while registration, please try again after some time.");
//
//                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        startActivity(new Intent(Register.this,Register.class));
//                                    }
//                                });
//
//                                builder.show();
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
    private void insertDataOffline(){

try{
    boolean isInserted = myDbHandler.insertData(UserId, database_profile_pic,database_profile_name,database_date,database_description,databasepost_image,databaseprofile_id,databasepost_id);
    if (isInserted == true) {
        Toast.makeText(getContext(), "Data inserted successfully", Toast.LENGTH_LONG).show();
    } else {
        Toast.makeText(getContext(), "Data not  inserted successfully", Toast.LENGTH_LONG).show();
    }
}catch(Exception e){
    e.printStackTrace();
}

    }
    public static class DialogueFilter extends DialogFragment {

        String SelectedFilterId;


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.filter_layout, null);

            final TextView SearchText = (TextView) dialogView.findViewById(R.id.searchtext);
            ImageView close = (ImageView) dialogView.findViewById(R.id.search_close_btn);
            final ProgressBar progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            final Spinner priority = (Spinner) dialogView.findViewById(R.id.search_categories);
            Button search_filter = (Button) dialogView.findViewById(R.id.send);
            PostsDataModelArrayList = new ArrayList<>();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.text, SearchCategories);
            priority.setAdapter(adapter);
            priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String SelectedCategory = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < MasterFilterlist.size(); i++) {
                        if (MasterFilterlist.get(i).getFilterName().contains(SelectedCategory)) {
                            MasterFilterlistTemp.add(MasterFilterlist.get(i));
                            SelectedFilterId = MasterFilterlist.get(position -1).getFilterId();
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
            search_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(priority.getSelectedItem().toString().equalsIgnoreCase("Select Status")){
                     Toast.makeText(getContext(),"Please select status",Toast.LENGTH_LONG).show();
                    }else{
                        progess_load.setVisibility(View.VISIBLE);
//                    BusinessDataModelArrayList.clear();
                        String url = (CommonUtils.BASE_URL) + "get_categorywise_updates";
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("user_id", user_id)
                                .add("update_category_id", SelectedFilterId)
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
                                                JSONArray update = json.getJSONArray("updates");
                                                PostBase = json.getString("user_update_image");
                                                UserProfileBase = json.getString("profile_pic_url");
                                                DocBase = json.getString("user_update_doc");


                                                for (int i = 0; i < update.length(); i++) {
                                                    JSONObject UpdateDetails = update.getJSONObject(i);

                                                    JSONObject UserUpdate = UpdateDetails.getJSONObject("UserUpdate");
                                                    JSONObject user = UpdateDetails.getJSONObject("User");


                                                    PostsDataModel model = new PostsDataModel();
//                                                    model.setUpdate_video(json.optString("user_update_video"));
                                                    if (UserUpdate.getString("video_type").equalsIgnoreCase("")){
                                                        model.setVideo_type("");
                                                    }
                                                    else{
                                                        model.setVideo_type(UserUpdate.getString("video_type"));
                                                    }
                                                    if (UserUpdate.getString("document").equalsIgnoreCase("")) {
                                                        model.setPostImage("");
                                                    } else {
                                                        model.setPostImage(UserUpdate.getString("document"));
                                                    }

                                                    if (!UserUpdate.getString("embed_code").equalsIgnoreCase("") && !UserUpdate.getString("embed_code").equalsIgnoreCase("null")){
                                                        model.setPostEmbeddedVideo(UserUpdate.getString("embed_code"));
                                                    }
                                                    else{
                                                        model.setPostEmbeddedVideo("");
                                                    }
                                                    if (UserUpdate.has(("islike"))) {
                                                        model.setPostIsLiked(UserUpdate.getString("islike"));
                                                    } else {
                                                        model.setPostIsLiked("0");
                                                    }
                                                    model.setPostMediaType(UserUpdate.getString("document_typ"));
                                                    model.setPostId(UserUpdate.getString("id"));
                                                    model.setPostLikeCount(UserUpdate.getString("UserUpdatesLike_count"));
                                                    model.setPostCommentCount(UserUpdate.getString("UserUpdatesComment_count"));
                                                    model.setPostMessage(UserUpdate.getString("message"));
                                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UserUpdate.getString("created_date"));
                                                    String newString = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(date); // 25-03-2019
                                                    model.setPostdate(newString);
                                                    model.setPostUserId(user.getString("id"));
                                                    model.setPostUserName(user.getString("name"));
                                                    model.setPostUserImage(user.getString("profile_picture"));
                                                    PostsDataModelArrayList.add(model);
                                                }
                                                progess_load.setVisibility(View.GONE);
                                                filter.dismiss();
                                                NewsAdapter adapter = new NewsAdapter(getActivity(), PostsDataModelArrayList, activityContext, PostBase, UserProfileBase, DocBase);
                                                layoutmanager = new LinearLayoutManager(getActivity());
                                                newslist.setLayoutManager(layoutmanager);
                                                newslist.setAdapter(adapter);
                                                //adapter.notifyDataSetChanged();
                                            } else {
                                                progess_load.setVisibility(View.GONE);
                                                NewsAdapter adapter = new NewsAdapter(getActivity(), PostsDataModelArrayList, activityContext, PostBase, UserProfileBase, DocBase);
                                                layoutmanager = new LinearLayoutManager(getActivity());
                                                newslist.setLayoutManager(layoutmanager);
                                                newslist.setAdapter(adapter);
                                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                                filter.dismiss();
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
//                    str_search = SearchText.getText().toString().trim();




                }
            });
            return dialogView;
        }
    }
}






