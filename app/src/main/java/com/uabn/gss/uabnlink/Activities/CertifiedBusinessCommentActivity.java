package com.uabn.gss.uabnlink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.CertifiedBusinessAdapter;
import com.uabn.gss.uabnlink.model.CertifiedBusinessModel;

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

public class CertifiedBusinessCommentActivity extends AppCompatActivity  {
    EditText type_comment;
    ImageView Comment_Button,UserImage;
    String str_comment,cb_id,user_id,name,date,baseURL;
    public static String img;
    SharedPreferenceUtils preferances;
    ProgressBar pb;
    CertifiedBusinessAdapter adapter;
    RecyclerView pbcr_commentList;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<CertifiedBusinessModel> CommentDataModelArrayList;
    TextView Name,Date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certified_business_comment);
        type_comment=findViewById(R.id.type_comment);
        Comment_Button=findViewById(R.id.Comment_Button);
        pbcr_commentList=findViewById(R.id.pbcr_commentList);
        pb=findViewById(R.id.pb);
        UserImage=findViewById(R.id.UserImage);
        Name=findViewById(R.id.Name);
        Date=findViewById(R.id.Date);
        cb_id=getIntent().getStringExtra("cb_id");
        name=getIntent().getStringExtra("name");
        date=getIntent().getStringExtra("date");
        img=getIntent().getStringExtra("img");
        Name.setText(name);
        Date.setText(date);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(CertifiedBusinessCommentActivity.this).load(img).apply(options).into(UserImage);
//        UserImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              Glide.with(Certifithis).
//            }
//        });
        pbcr_commentList=findViewById(R.id.pbcr_commentList);
        cb_id=getIntent().getStringExtra("cb_id");
        CommentDataModelArrayList = new ArrayList<>();
        preferances = SharedPreferenceUtils.getInstance(CertifiedBusinessCommentActivity.this);
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        pb.setVisibility(View.VISIBLE);
        Comment_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_comment=type_comment.getText().toString().trim();
                if(str_comment.equalsIgnoreCase("")){
                    Toast.makeText(CertifiedBusinessCommentActivity.this,"Please enter some text",Toast.LENGTH_LONG).show();
                }else{
                    CertifiedComment(str_comment,user_id,cb_id);
                    pb.setVisibility(View.VISIBLE);
                    type_comment.setText("");
                }

            }
        });
    getCertifiedCommentList();
        pb.setVisibility(View.VISIBLE);

    }
    public void   CertifiedComment(String str_comment, final String user_id, final String cb_id){
        String url = (CommonUtils.BASE_URL)+"write_comment_on_cb";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("cb_id", cb_id)
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
                                Toast.makeText(  CertifiedBusinessCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                getCertifiedCommentList();
                                Constants.hideKeyboard(CertifiedBusinessCommentActivity.this);
                            }
                                else {
                                Toast.makeText(  CertifiedBusinessCommentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }
    public void getCertifiedCommentList() {
        CommentDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"view_cb_comments";

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("cb_id", cb_id)
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
                            baseURL=json.getString("profile_pic_url");
                            if (success.equalsIgnoreCase("success")) {
                                JSONArray userComments = json.getJSONArray("comment_data");
                                for (int i = 0; i < userComments.length(); i++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(i);
                                    CertifiedBusinessModel model = new CertifiedBusinessModel();
                                    model.setComment_id(CommentDetails.getString("comment_id"));
                                    model.setName(CommentDetails.getString("name"));
                                    model.setComment(CommentDetails.getString("comments"));
                                    model.setImage(baseURL+CommentDetails.getString("user_id")+"/"+CommentDetails.getString("profile_picture"));
                                    model.setUser_id(CommentDetails.getString("user_id"));
                                    model.setDate(CommentDetails.getString("display_date"));
                                    CommentDataModelArrayList.add(model);
                                }
                                adapter = new CertifiedBusinessAdapter(CertifiedBusinessCommentActivity.this, CommentDataModelArrayList, cb_id);
                                layoutmanager = new LinearLayoutManager(CertifiedBusinessCommentActivity.this);
                                pbcr_commentList.setLayoutManager(layoutmanager);
                                pbcr_commentList.setAdapter(adapter);
                                pbcr_commentList.scrollToPosition(CommentDataModelArrayList.size()-1);
                            } else {
                                Toast.makeText(CertifiedBusinessCommentActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
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
