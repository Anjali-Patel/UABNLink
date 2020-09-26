package com.uabn.gss.uabnlink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.RepliesAdapter;
import com.uabn.gss.uabnlink.model.CommentDataModel;
import com.squareup.picasso.Picasso;

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

public class CommentReplies extends AppCompatActivity implements RepliesAdapter.UpdatePost {

    String CommentId, UserID, Comment,CommentImage, Date, Name, CommentorId, P_UserProfileBase,str_comment, UserId, UserProfileBase, CurrentPostId;
    SharedPreferenceUtils preferances;
    EditText Type_Comment;
    String Update_id;
    ImageView CommentorImage,Comment_Button;
    TextView CommentDate,CommentText, CommentorName;

    RepliesAdapter adapter;
    RecyclerView commentreplylist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<CommentDataModel> CommentDataModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_replies);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Comment Replies");
//        setSupportActionBar(toolbar);
        Comment_Button=findViewById(R.id.Comment);
        CommentorImage = findViewById(R.id.CommentorImage);
        CommentDate = findViewById(R.id.CommentDate);
        CommentText = findViewById(R.id.CommentText);
        CommentorName = findViewById(R.id.CommentorName);
        commentreplylist = findViewById(R.id.commentreplylist);
        Type_Comment=findViewById(R.id.Type_Comment);
        Update_id=getIntent().getStringExtra("CurrentPostId");
        CommentId = getIntent().getStringExtra("CommentId");
        CommentorId = getIntent().getStringExtra("CommentorId");
        Name=  getIntent().getStringExtra("CommentName");
        UserID = getIntent().getStringExtra("UserID");
        Comment = getIntent().getStringExtra("CommentText");
        CommentImage = getIntent().getStringExtra("CommentImage");
        Date = getIntent().getStringExtra("CommentDate");
        P_UserProfileBase = getIntent().getStringExtra("P_UserProfileBase");
        CurrentPostId = getIntent().getStringExtra("CurrentPostId");
        preferances= SharedPreferenceUtils.getInstance(this);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        CommentDate.setText(Date);
        CommentText.setText(Comment);
        CommentorName.setText(Name);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        String ImgURL = P_UserProfileBase+"/" +CommentorId+ "/" +CommentImage.replace(" ","%20");
        Glide.with(this).load(ImgURL).apply(options).into(CommentorImage);

        GetReplyDetails();
        Comment_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_comment=Type_Comment.getText().toString();
                if(str_comment.equalsIgnoreCase("")){
                    Toast.makeText(CommentReplies.this,"Please enter some text",Toast.LENGTH_LONG).show();
                }else{
                    PostCommentReply(UserId,CommentId,Update_id,str_comment);
                    Type_Comment.setText("");
                }

            }
        });

    }



    public void GetReplyDetails() {

        CommentDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"view_replies";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("comment_id", CommentId)
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
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {

                                UserProfileBase = json.getString("profile_pic_url");


                                JSONArray update = json.getJSONArray("replies_data");

                                JSONArray userComments = json.getJSONArray("replies_data");

                                for (int j = 0; j < userComments.length(); j++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(j);

                                    CommentDataModel model = new CommentDataModel();
                                    model.setCommentId(CommentDetails.getString("id"));
                                    model.setCommentorUserId(CommentDetails.getString("user_id"));
                                    model.setCommentatorName(CommentDetails.getString("name"));
                                    model.setCommentetorImage(CommentDetails.getString("profile_picture"));
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CommentDetails.getString("added_on"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setCommentDate(CommentPostDate);

                                    model.setCommentText(CommentDetails.getString("reply"));


                                    if (CommentDetails.has("like_count")) {
                                        model.setCommentLikeCount(CommentDetails.getString("like_count"));
                                    }
                                    else{
                                        model.setCommentLikeCount("0");
                                    }

                                    model.setCommentISLiked(CommentDetails.getString("islike"));


                                    CommentDataModelArrayList.add(model);

                                }


                                adapter = new RepliesAdapter(CommentReplies.this, CommentDataModelArrayList, UserProfileBase, CurrentPostId);
                                layoutmanager = new LinearLayoutManager(CommentReplies.this);
                                commentreplylist.setLayoutManager(layoutmanager);
                                commentreplylist.setAdapter(adapter);
                                commentreplylist.scrollToPosition(CommentDataModelArrayList.size()-1);



                                //ProgessLoad.setVisibility(View.GONE);



                            }

                            else {

                                adapter = new RepliesAdapter(CommentReplies.this, CommentDataModelArrayList, "", CurrentPostId);
                                layoutmanager = new LinearLayoutManager(CommentReplies.this);
                                commentreplylist.setLayoutManager(layoutmanager);
                                commentreplylist.setAdapter(adapter);
                                commentreplylist.scrollToPosition(CommentDataModelArrayList.size()-1);
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


    public void PostCommentReply(final String UserId, final String CommentId, String Update_id, String str_comment) {

        CommentDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"reply_to_comment";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("comment_id",CommentId).add("update_id", Update_id)
                .add("reply_comment", str_comment)
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
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(CommentReplies.this,json.getString("message"),Toast.LENGTH_LONG).show();
                                GetReplyDetails();
                            } else {
                                Toast.makeText(CommentReplies.this,json.getString("message"),Toast.LENGTH_LONG).show();
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
    public void UpdateComments() {
        GetReplyDetails();
    }
}