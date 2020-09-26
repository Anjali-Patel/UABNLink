package com.uabn.gss.uabnlink.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommentListRefresh;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.PBCRCommentAdapter;
import com.uabn.gss.uabnlink.model.PBCRCommentModel;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PBCRCommentActivity extends AppCompatActivity implements CommentListRefresh, BSImagePicker.OnMultiImageSelectedListener{
static EditText type_comment;
    static EditText EmbeddedVideo;
    static EditText Link;
    String  update_group_comment;
ImageView Comment_Button,UserImage, AddMedia;
String str_comment,user_id,name,date, img, type;
    SharedPreferenceUtils preferances;
    ProgressBar pb;
    PBCRCommentAdapter.IOnItemClickListener iOnItemClickListener;
    PBCRCommentAdapter adapter;
    RecyclerView pbcr_commentList;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<PBCRCommentModel> CommentDataModelArrayList;
    TextView Name,Date;
    RelativeLayout LikeCommentInfoRelative;
    static TextView SelectedDoc;
    private static RecyclerView mRecyclerView;
    static String AttatchmentPath =  "";
    static AddPhotoBottomDialogFragment addPhotoBottomDialogFragment;
    static ArrayList<String> ImagesList = new ArrayList<String>();
    private RecyclerView.Adapter mAdapter;


    String url,update_group_comment_id;

    ArrayList<String> AttatchmentList = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbcrcomment);
        type_comment=findViewById(R.id.type_comment);
        Comment_Button=findViewById(R.id.Comment_Button);
        pb=findViewById(R.id.pb);
        UserImage=findViewById(R.id.UserImage);
        Name=findViewById(R.id.Name);
        Date=findViewById(R.id.Date);
        AddMedia=findViewById(R.id.AddMedia);
        LikeCommentInfoRelative=findViewById(R.id.LikeCommentInfoRelative);
         RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
         mRecyclerView = findViewById(R.id.Images_listview);
        SelectedDoc = findViewById(R.id.SelectedDoc);
        addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
        Link = findViewById(R.id.Link);
        EmbeddedVideo = findViewById(R.id.EmbeddedVideo);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(PBCRCommentActivity.this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        name=getIntent().getStringExtra("name");
        date=getIntent().getStringExtra("date");
        img=getIntent().getStringExtra("img");
        Name.setText(name);
        Date.setText(date);
          iOnItemClickListener = new PBCRCommentAdapter.IOnItemClickListener() {
            @Override
            public void onItemClick(String text,String text1,String text2) {
                type_comment.setText(text);
                update_group_comment_id=text1;
                update_group_comment=text2;

            }
        };
          Glide.with(PBCRCommentActivity.this).asBitmap().load(img).apply(options).into(UserImage);
        pbcr_commentList=findViewById(R.id.pbcr_commentList);
        CommentDataModelArrayList = new ArrayList<>();
        preferances = SharedPreferenceUtils.getInstance(PBCRCommentActivity.this);
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        type = getIntent().getStringExtra("type");
        {
            if (type.equalsIgnoreCase("group")){
                String group_id=getIntent().getStringExtra("group_id");
                url = (CommonUtils.BASE_URL)+"view_comments";
                getGroupCommentList(group_id,user_id);
                AddMedia.setVisibility(View.VISIBLE);
            }
            else if (type.equalsIgnoreCase("pbcr")){
                String pbcr_id=getIntent().getStringExtra("pbcr_id");
                url = (CommonUtils.BASE_URL)+"getPBCRComments";
                getPBCRCommentList(pbcr_id,user_id);
                AddMedia.setVisibility(View.GONE);
            }
            else if (type.equalsIgnoreCase("event")){
                String event_id=getIntent().getStringExtra("pbcr_id");
                url = (CommonUtils.BASE_URL)+"view_event_comments";
                getEventCommentList(event_id,user_id);
                AddMedia.setVisibility(View.GONE);
            }
        }
        pb.setVisibility(View.VISIBLE);
        Comment_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("group")){
                    String EmabeddedVideo = "", UrlLink = "";
                    String group_id=getIntent().getStringExtra("group_id");
                     EmabeddedVideo = EmbeddedVideo.getText().toString();
                     UrlLink = Link.getText().toString().trim();
                     if (UrlLink.equalsIgnoreCase("http://")){
                         UrlLink = "";
                     }
                    str_comment=type_comment.getText().toString().trim();
                    if(str_comment.equalsIgnoreCase("")){
                        Toast.makeText(PBCRCommentActivity.this,"Please enter some text",Toast.LENGTH_LONG).show();
                    }else{
                        if(update_group_comment=="1"){
                          updateGroupComment( user_id,group_id, update_group_comment_id,str_comment);
                            pb.setVisibility(View.VISIBLE);
                            type_comment.setText("");
                        }else{
                            new PostGroupcommentAsync().execute(user_id,group_id,str_comment,UrlLink , EmabeddedVideo);
                            pb.setVisibility(View.VISIBLE);
                            type_comment.setText("");
                        }
                        //PostGroupComment(str_comment,user_id,group_id);

                    }

                }
                else if (type.equalsIgnoreCase("pbcr")){
                    String pbcr_id=getIntent().getStringExtra("pbcr_id");
                    str_comment=type_comment.getText().toString().trim();
                    if(str_comment.equalsIgnoreCase("")){
                        Toast.makeText(PBCRCommentActivity.this,"Please enter some text",Toast.LENGTH_LONG).show();
                    }else{
                        PBCRComment(str_comment,user_id,pbcr_id);
                        pb.setVisibility(View.VISIBLE);
                        type_comment.setText("");
                    }
                }
                else if (type.equalsIgnoreCase("event")){
                    String event_id=getIntent().getStringExtra("pbcr_id");
                    str_comment=type_comment.getText().toString().trim();
                    if(str_comment.equalsIgnoreCase("")){
                        Toast.makeText(PBCRCommentActivity.this,"Please enter some text",Toast.LENGTH_LONG).show();
                    }else{
                        PostEventComment(str_comment,user_id,event_id);
                        pb.setVisibility(View.VISIBLE);
                        type_comment.setText("");
                    }
                }
            }
        });

        AddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagesList.clear();
                addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
                addPhotoBottomDialogFragment.setContext(PBCRCommentActivity.this);
                //addPhotoBottomDialogFragment.setChatFriendId(FriendId);
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(), "add_photo_dialog_fragment");
            }
        });

        pb.setVisibility(View.VISIBLE);

    }
    public void PBCRComment(String str_comment, final String user_id, final String pbcr_id){
        String url = (CommonUtils.BASE_URL)+"write_pbcr_comment";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("pbcr_id", pbcr_id)
                .add("comment", str_comment)
                .add("path", "")
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

                 runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(myResponse);
                                String success = json.getString("status");
                                if (success.equalsIgnoreCase("success")) {
                                    Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                    getPBCRCommentList(pbcr_id,user_id);
                                    Constants.hideKeyboard(PBCRCommentActivity.this);
                                } else {
                                    Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        );

    }
    public void getPBCRCommentList(String pbcr_id,String user_id) {
        CommentDataModelArrayList.clear();
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("pbcr_id", pbcr_id)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                 JSONArray userComments = json.getJSONArray("comments_data");
                                for (int i = 0; i < userComments.length(); i++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(i);
                                    PBCRCommentModel model = new PBCRCommentModel();
                                    model.setComment_id(CommentDetails.getString("comment_id").replace("null", ""));
                                    model.setName(CommentDetails.getString("name").replace("null", ""));
                                    model.setComment(CommentDetails.getString("comments").replace("null", ""));
                                    model.setUser_id(CommentDetails.getString("user_id").replace("null", ""));
                                    model.setPath(CommentDetails.getString("path").replace("null", ""));
                                    model.setPbcr_id(CommentDetails.getString("pbcr_id").replace("null", ""));
                                    model.setType("pbcr");
                                    String BasePathImage = json.getString("profile_pic_url") + CommentDetails.getString("user_id") +"/"+ CommentDetails.getString("profile_picture");
                                    model.setImage(BasePathImage);
                                    model.setDate(CommentDetails.getString("display_date").replace("null", ""));
                                    CommentDataModelArrayList.add(model);
                                }
                                adapter = new PBCRCommentAdapter(PBCRCommentActivity.this, CommentDataModelArrayList, PBCRCommentActivity.this,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(PBCRCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                            } else {
                                adapter = new PBCRCommentAdapter(PBCRCommentActivity.this, CommentDataModelArrayList, PBCRCommentActivity.this,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(PBCRCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                                Toast.makeText(PBCRCommentActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void getGroupCommentList(String group_id,String user_id) {
        CommentDataModelArrayList.clear();
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("group_id", group_id)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            String ImageBase = json.getString("GROUP_COMMENT_PHOTO");
                            String DocBase = json.getString("GROUP_COMMENT_DOCUMENT");
                            String BasePathImage;
                            if (success.equalsIgnoreCase("success")) {
                                JSONArray userComments = json.getJSONArray("comments_data");
                                for (int i = 0; i < userComments.length(); i++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(i);
                                    JSONArray GroupCommentDocument = CommentDetails.getJSONArray("GroupCommentDocument");
                                    PBCRCommentModel model = new PBCRCommentModel();
                                    model.setComment_id(CommentDetails.getString("comment_id").replace("null", ""));
                                    model.setName(CommentDetails.getString("name").replace("null", ""));
                                    model.setComment(CommentDetails.getString("comment").replace("null", ""));
                                    model.setUser_id(CommentDetails.getString("user_id").replace("null", ""));
                                    model.setPath("");
                                    model.setReply_count(CommentDetails.getString("reply_count").replace("null", ""));
                                    model.setGroup_id(CommentDetails.getString("group_id").replace("null", ""));
                                    model.setDate(CommentDetails.getString("display_date").replace("null", ""));
                                    model.setType("group");
                                    if (!CommentDetails.getString("link").equalsIgnoreCase("")){
                                        model.setGroupcommentLink(CommentDetails.getString("link"));
                                    }
                                    else{
                                        model.setGroupcommentLink("");
                                    }
                                    if (!CommentDetails.getString("embed_code").equalsIgnoreCase("")){
                                        model.setGroupcommentEmddedVideo(CommentDetails.getString("embed_code"));
                                    }
                                    else{
                                        model.setGroupcommentEmddedVideo("");
                                    }
                                    ArrayList<String> groupcommentImageList = new ArrayList<>();
                                    for (int j = 0; j < GroupCommentDocument.length(); j++) {
                                        JSONObject MediaDetails = GroupCommentDocument.getJSONObject(j);

                                        String Type = MediaDetails.getString("type");
                                        if (Type.equalsIgnoreCase("1")){

                                            String imagePath = ImageBase + CommentDetails.getString("comment_id") + "/" + MediaDetails.getString("path");
                                            groupcommentImageList.add(imagePath);
                                        }
                                        else {
                                            model.setGroupcommentDocument(DocBase + CommentDetails.getString("comment_id") + "/" + MediaDetails.getString("path"));
                                        }
                                    }
                                    model.setGroupcommentImageList(groupcommentImageList);
                                    BasePathImage = json.getString("profile_pic_url") + CommentDetails.getString("user_id") +"/"+ CommentDetails.getString("profile_picture");
                                    model.setImage(BasePathImage);
                                    CommentDataModelArrayList.add(model);
                                }
                                adapter = new PBCRCommentAdapter(PBCRCommentActivity.this, CommentDataModelArrayList, PBCRCommentActivity.this,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(PBCRCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                            } else {
                                adapter = new PBCRCommentAdapter(PBCRCommentActivity.this, CommentDataModelArrayList, PBCRCommentActivity.this,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(PBCRCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                                Toast.makeText(PBCRCommentActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void getEventCommentList(String event_id,String user_id) {
        CommentDataModelArrayList.clear();


        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("event_id", event_id)
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            String BasePathImage;
                            if (success.equalsIgnoreCase("success")) {
                                JSONArray userComments = json.getJSONArray("comments_data");
                                for (int i = 0; i < userComments.length(); i++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(i);
                                    PBCRCommentModel model = new PBCRCommentModel();
                                    model.setComment_id(CommentDetails.getString("comment_id"));
                                    model.setName(CommentDetails.getString("name").replace("null", ""));

                                    model.setComment(CommentDetails.getString("comments").replace("null", ""));
                                    model.setUser_id(CommentDetails.getString("user_id").replace("null", ""));
                                    model.setEvent_Id(CommentDetails.getString("event_id").replace("null", ""));
                                    model.setPath("");
                                    model.setDate(CommentDetails.getString("display_date").replace("null", ""));
                                    model.setType("event");

                                    BasePathImage = json.getString("profile_pic_url") + CommentDetails.getString("user_id") +"/"+ CommentDetails.getString("profile_picture");
                                    model.setImage(BasePathImage);

                                    CommentDataModelArrayList.add(model);
                                }
                                adapter = new PBCRCommentAdapter(PBCRCommentActivity.this, CommentDataModelArrayList, PBCRCommentActivity.this,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(PBCRCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                            } else {
                                adapter = new PBCRCommentAdapter(PBCRCommentActivity.this, CommentDataModelArrayList, PBCRCommentActivity.this,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(PBCRCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                                Toast.makeText(PBCRCommentActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void PostGroupComment(String str_comment, final String user_id, final String groupId){
        String url = (CommonUtils.BASE_URL)+"write_comment_on_group";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("group_id", groupId)
                .add("comment", str_comment)
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                getGroupCommentList(groupId,user_id);
                                Constants.hideKeyboard(PBCRCommentActivity.this);
                            }
                                else {
                                Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }
    class PostGroupcommentAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
        String GroupId;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("group_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("comment", params[2]));
                nameValuePairs.add(new BasicNameValuePair("link", params[3]));
                nameValuePairs.add(new BasicNameValuePair("embed_code", params[4]));

                int Imagecount = 0;
                for (int i=0;i<ImagesList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("img"+(Imagecount+1), ImagesList.get(i)));
                    Imagecount++;
                }


                if (!AttatchmentPath.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("doc1", AttatchmentPath));
                }

                Log.d("datap", nameValuePairs.toString());

                GroupId = params[1];

                String Url = (CommonUtils.BASE_URL) + "write_comment_on_group";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ViewProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //{"success":"1","message":"Data saved successfully!"}
            try {
                String success = json.getString("status");
                if (success.equalsIgnoreCase("success")) {
                    ImagesList.clear();
                    type_comment.setText("");
                    EmbeddedVideo.setText("");
                    Link.setText("");
                    AttatchmentPath = "";
//                    addPhotoBottomDialogFragment.dismiss();
                    EmbeddedVideo.setVisibility(View.GONE);
                    Link.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    SelectedDoc.setVisibility(View.GONE);
                    Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                    getGroupCommentList(GroupId,user_id);
                    Constants.hideKeyboard(PBCRCommentActivity.this);
                    //ViewProgress.setVisibility(View.GONE);


                } else {

                    Toast.makeText(PBCRCommentActivity.this, "Opps! Some problem occured while posting your update, please ensure your location services are on at the highest accuracy.",
                            Toast.LENGTH_LONG).show();
                    //ViewProgress.setVisibility(View.GONE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    public void updateGroupComment(final String user_id, final String group_id, String group_comment_id, String str_comment ){
        String url = (CommonUtils.BASE_URL)+"update_group_comment";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("group_id", group_id)
                .add("group_comment_id", group_comment_id)
                .add("updated_group_comment", str_comment)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                getGroupCommentList(group_id,user_id);
                                update_group_comment="0";

                                Constants.hideKeyboard(PBCRCommentActivity.this); }
                            else {
                                Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    public void PostEventComment(String str_comment, final String user_id, final String event_id){
        String url = (CommonUtils.BASE_URL)+"write_event_comment";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("event_id", event_id)
                .add("comment", str_comment)
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                getEventCommentList(event_id,user_id);
                                Constants.hideKeyboard(PBCRCommentActivity.this); }
                            else {
                                Toast.makeText(  PBCRCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }
    @Override
    public void RefreshList(String Type, String Id) {
        type = Type;
        {
            if (type.equalsIgnoreCase("group")){
                String group_id=getIntent().getStringExtra("group_id");
                url = (CommonUtils.BASE_URL)+"view_comments";
                getGroupCommentList(group_id,user_id);
            }
            else if (type.equalsIgnoreCase("pbcr")){
                String pbcr_id=getIntent().getStringExtra("pbcr_id");
                url = (CommonUtils.BASE_URL)+"getPBCRComments";
                getPBCRCommentList(pbcr_id,user_id);
            }
            else if (type.equalsIgnoreCase("event")){
                String event_id=getIntent().getStringExtra("pbcr_id");
                url = (CommonUtils.BASE_URL)+"view_event_comments";
                getEventCommentList(event_id,user_id);
            }
        }
    }
    public static class AddPhotoBottomDialogFragment extends BottomSheetDialogFragment {
        public static AddPhotoBottomDialogFragment newInstance() {
            return new AddPhotoBottomDialogFragment();
        }
        Context context;
        TextView tv_btn_add_photo_camera, tv_btn_add_video, add_url, tv_btn_add_document;
        String ChatFriendId;
        public void setContext(Context context) { this.context = context; }
        public Context getContext() { return context; }
        public void setChatFriendId(String user) { this.ChatFriendId = user; }
        public String getChatFriendId() { return ChatFriendId; }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.media_selection_sheet_group, container,
                    false);
            tv_btn_add_photo_camera = view.findViewById(R.id.tv_btn_add_photo_camera);
            tv_btn_add_video = view.findViewById(R.id.tv_btn_add_video);
            add_url = view.findViewById(R.id.add_url);
            tv_btn_add_document = view.findViewById(R.id.tv_btn_add_document);
            tv_btn_add_photo_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                            .setMaximumDisplayingImages(Integer.MAX_VALUE)
                            .isMultiSelect()
                            .setMinimumMultiSelectCount(1)
                            .setMaximumMultiSelectCount(5)
                            .build();
                    pickerDialog.show(getChildFragmentManager(), "Picker");
                }
            });
            tv_btn_add_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);
                }
            });

            add_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    addPhotoBottomDialogFragment.dismiss();
                    EmbeddedVideo.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    SelectedDoc.setVisibility(View.GONE);
                    Link.setVisibility(View.VISIBLE);

                }
            });

            tv_btn_add_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    addPhotoBottomDialogFragment.dismiss();
                    Link.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    SelectedDoc.setVisibility(View.GONE);
                    EmbeddedVideo.setVisibility(View.VISIBLE);
                }
            });

            // get the views and attach the listener

            return view;

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == 100) {

                String displayName = null;
                StringBuilder fileName = new StringBuilder();

                if (data != null) {

                    if (data.getClipData() != null) {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {

                            Uri uri = data.getClipData().getItemAt(index).getUri();
                            String uriString = uri.toString();
                            File myFile = new File(uriString);
                            String path = myFile.getAbsolutePath();


                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];

                            if ("primary".equalsIgnoreCase(type)) {
                                String a =  Environment.getExternalStorageDirectory() + "/" + split[1];
                                //DocumetPath = a;
                                Log.i("Path", "onActivityResult: "+a);
                            }

                            try {
                                String mypath = PathUtil.getPath(getActivity(), uri);
                                AttatchmentPath = mypath;

                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }




                            if (uriString.startsWith("content://")) {
                                Cursor cursor = null;
                                try {
                                    cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

//                                        addPhotoBottomDialogFragment.dismiss();
                                        EmbeddedVideo.setVisibility(View.GONE);
                                        Link.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.GONE);
                                        SelectedDoc.setText(displayName);
                                        SelectedDoc.setVisibility(View.VISIBLE);

                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile.getName();

//                                addPhotoBottomDialogFragment.dismiss();
                                EmbeddedVideo.setVisibility(View.GONE);
                                Link.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.GONE);
                                SelectedDoc.setText(displayName);
                                SelectedDoc.setVisibility(View.VISIBLE);

                            }

                            Log.d("Path", myFile.getAbsolutePath());
                            fileName.append(displayName).append("\n");
                            Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
                        }

                    } else {

                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        String path = myFile.getAbsolutePath();


                        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
                        String selection = null;
                        String[] selectionArgs = null;
                        Cursor cursor = null;
                        String Pdfpath = "";

                        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                            if (isExternalStorageDocument(uri)) {
                                final String docId = DocumentsContract.getDocumentId(uri);
                                final String[] split = docId.split(":");
                                String p =  Environment.getExternalStorageDirectory() + "/" + split[1];
                            } else if (isDownloadsDocument(uri)) {
                                try {
                                    cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                                    if (cursor != null && cursor.moveToFirst())
                                    {
                                        String Name = cursor.getString(0);
                                        Pdfpath = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                        AttatchmentPath = Pdfpath;

                                        if (!TextUtils.isEmpty(path)) {
                                            String p =  Pdfpath;
                                        }
                                    }
                                }
                                catch (Exception e){
                                    Log.e("Doc", "onActivityResult: ", e);
                                }

                            } else if (isMediaDocument(uri)) {
                                final String docId = DocumentsContract.getDocumentId(uri);
                                final String[] split = docId.split(":");
                                final String type = split[0];
                                if ("image".equals(type)) {
                                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                } else if ("video".equals(type)) {
                                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                } else if ("audio".equals(type)) {
                                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                }
                                selection = "_id=?";
                                selectionArgs = new String[]{ split[1] };
                            }
                        }

                        if ("file".equalsIgnoreCase(uri.getScheme())) {
                        }



                        if (uriString.startsWith("content://")) {
                            Cursor cursordoc = null;
                            try {
                                cursordoc = getActivity().getContentResolver().query(uri, null, null, null, null);
                                if (cursordoc != null && cursordoc.moveToFirst()) {
                                    displayName = cursordoc.getString(cursordoc.getColumnIndex(OpenableColumns.DISPLAY_NAME));

//                                    addPhotoBottomDialogFragment.dismiss();
                                    EmbeddedVideo.setVisibility(View.GONE);
                                    Link.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.GONE);
                                    SelectedDoc.setText(displayName);
                                    SelectedDoc.setVisibility(View.VISIBLE);

                                }
                            } finally {
                                cursordoc.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                            AttatchmentPath =  uri.getPath();

//                            addPhotoBottomDialogFragment.dismiss();
                            EmbeddedVideo.setVisibility(View.GONE);
                            Link.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.GONE);
                            SelectedDoc.setText(displayName);
                            SelectedDoc.setVisibility(View.VISIBLE);
                        }

                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));

                        Log.d("fileUri: ", String.valueOf(uri));
                    }
                }
            }

            else if(requestCode == 1){

                if(resultCode == RESULT_OK){

                    Uri audioFileUri = data.getData();
                    try {
                        String MP3Path = PathUtil.getPath(getActivity(), audioFileUri);
                        AttatchmentPath = MP3Path;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
            }

            else if(requestCode == 2){

                if(resultCode == RESULT_OK){

                    Uri videoFileUri = data.getData();
                    try {
                        String video = PathUtil.getPath(getActivity(), videoFileUri);
                        AttatchmentPath = video;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
            }




            super.onActivityResult(requestCode, resultCode, data);


        }

        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

        for (int i = 0; i < uriList.size(); i++) {

            Uri selectedImage = uriList.get(i);
            File imageFile = new File(getRealPathFromURI(selectedImage));
            ImagesList.add(String.valueOf(imageFile));

        }

        if (ImagesList.size() == 0) {
//            addPhotoBottomDialogFragment.dismiss();
            mRecyclerView.setVisibility(View.GONE);
        } else {
//            addPhotoBottomDialogFragment.dismiss();
            EmbeddedVideo.setVisibility(View.GONE);
            Link.setVisibility(View.GONE);
            SelectedDoc.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new MultipleImagesAdapter(PBCRCommentActivity.this, ImagesList);
            mRecyclerView.setAdapter(mAdapter);

        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleImagesList;

        public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList) {
            context1 = context;
            MultipleImagesList = ImagesList;
        }

        @NonNull
        @Override
        public MultipleImagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_listitem, parent, false);
            return new MultipleImagesAdapter.MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MultipleImagesAdapter.MyViewHolder holder, final int position) {

            if (MultipleImagesList.get(position).contains("file:")) {
                Glide.with(context1).asBitmap().load(MultipleImagesList.get(position)).into(holder.adapterImage);
            } else {
                Glide.with(context1).asBitmap().load("file:" + MultipleImagesList.get(position)).into(holder.adapterImage);
            }

        }

        @Override
        public int getItemCount() {
            return MultipleImagesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adapterImage;

            public MyViewHolder(View v) {
                super(v);
                adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
            }
        }
    }



}

