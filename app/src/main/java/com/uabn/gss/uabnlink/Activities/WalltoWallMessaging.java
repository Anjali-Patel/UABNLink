package com.uabn.gss.uabnlink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.WallMessagesAdapter;
import com.uabn.gss.uabnlink.model.WallMessagesModel;

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

public class WalltoWallMessaging extends AppCompatActivity {

    SharedPreferenceUtils preferances;
    EditText Message;
    ImageView Send;
    String FriendId, UserId;
    FrameLayout progressBarHolder;

    WallMessagesAdapter adapter;
    RecyclerView wallmessageslist;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<WallMessagesModel> MessageDataModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallto_wall_messaging);

        preferances= SharedPreferenceUtils.getInstance(this);

        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        FriendId = getIntent().getStringExtra("FriendId");

        wallmessageslist = findViewById(R.id.wallmessageslist);
        Message = findViewById(R.id.Message);
        Send = findViewById(R.id.Send);
        progressBarHolder = findViewById(R.id.progressBarHolder);

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String StrMessage=Message.getText().toString();
                if(StrMessage.equalsIgnoreCase("")){
                    Toast.makeText(WalltoWallMessaging.this,"Please enter some text",Toast.LENGTH_LONG).show();
                }else{
                    progressBarHolder.setVisibility(View.VISIBLE);
                    PostMessage(UserId, FriendId, StrMessage);
                    Message.setText("");
                }
            }
        });



        GetAllMessages();

    }

    public void GetAllMessages() {

        progressBarHolder.setVisibility(View.VISIBLE);


        MessageDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"wall_to_wall";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("friend_id", FriendId)
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

                                JSONArray Messages = json.getJSONArray("msg_data");

                                for (int j = 0; j < Messages.length(); j++) {
                                    JSONObject MessageDetails = Messages.getJSONObject(j);

                                    WallMessagesModel model = new WallMessagesModel();
                                    model.setFromUserId(MessageDetails.getString("from_user_id"));
                                    model.setToUserId(MessageDetails.getString("to_user_id"));
                                    model.setMessage(MessageDetails.getString("message"));
                                    model.setMessageId(MessageDetails.getString("msg_id"));
                                    model.setUserName(MessageDetails.getString("from_user_name"));
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MessageDetails.getString("posted_date"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setMessageDate(CommentPostDate);

                                    String ProfilePic = json.getString("profile_pic_url") + MessageDetails.getString("from_user_id") + "/" + MessageDetails.getString("from_user_image");
                                    model.setProfilePicture(ProfilePic);

                                    MessageDataModelArrayList.add(model);

                                }
                                progressBarHolder.setVisibility(View.GONE);

                                wallmessageslist.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(WalltoWallMessaging.this);
                                wallmessageslist.setLayoutManager(mLayoutManager);
                                adapter = new WallMessagesAdapter(WalltoWallMessaging.this, MessageDataModelArrayList);
                                wallmessageslist.setAdapter(adapter);
                                mLayoutManager.setStackFromEnd(true);
                                wallmessageslist.scrollToPosition(MessageDataModelArrayList.size()-1);

                            }

                            else {
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(WalltoWallMessaging.this,json.getString("message"),Toast.LENGTH_LONG).show();
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


    public void PostMessage(String UserId, String FriendId, String Message) {


        String url = (CommonUtils.BASE_URL)+"send_wall_to_wall_msgs";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("friend_id",FriendId)
                .add("message", Message)
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
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(WalltoWallMessaging.this,json.getString("message"),Toast.LENGTH_LONG).show();
                                GetAllMessages();
                                Constants.hideKeyboard(WalltoWallMessaging.this);
                            } else {
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(WalltoWallMessaging.this,json.getString("message"),Toast.LENGTH_LONG).show();
                                Constants.hideKeyboard(WalltoWallMessaging.this);
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
