package com.uabn.gss.uabnlink.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.ChatListAdapter;
import com.uabn.gss.uabnlink.model.ChatListModel;

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


public class ChatList extends AppCompatActivity {

    String UserId;
    EditText input_search;
    SharedPreferenceUtils preferances;
    RecyclerView chatlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<ChatListModel> ChatListDataModelArrayList = new ArrayList<>();
    ArrayList<ChatListModel> ChatListDataModelArrayListTemp = new ArrayList<>();
    Context activityContext;
    ChatListAdapter adapter;
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        chatlist = findViewById(R.id.chatlist);
        progressBarHolder = findViewById(R.id.progressBarHolder);

        adapter = new ChatListAdapter(this, ChatListDataModelArrayList, activityContext);
        layoutmanager = new LinearLayoutManager(this);
        chatlist.setLayoutManager(layoutmanager);
        chatlist.setAdapter(adapter);
        preferances = SharedPreferenceUtils.getInstance(this);

        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        input_search = findViewById(R.id.input_search);
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    ChatListDataModelArrayListTemp.clear();
                    for (int i = 0; i < ChatListDataModelArrayList.size(); i++) {
                        if (ChatListDataModelArrayList.get(i).getToUserName().toLowerCase().startsWith(strText.toLowerCase())) {
                            ChatListDataModelArrayListTemp.add(ChatListDataModelArrayList.get(i));
                        }
                    }
                    adapter = new ChatListAdapter(ChatList.this, ChatListDataModelArrayListTemp, activityContext);
                    chatlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new ChatListAdapter(ChatList.this, ChatListDataModelArrayList, activityContext);
                    chatlist.setAdapter(adapter);
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
                    ChatListDataModelArrayListTemp.clear();
                    for (int i = 0; i < ChatListDataModelArrayList.size(); i++) {
                        if (ChatListDataModelArrayList.get(i).getToUserName().toLowerCase().startsWith(strText.toLowerCase())) {
                            ChatListDataModelArrayListTemp.add(ChatListDataModelArrayList.get(i));
                        }
                    }
                    adapter = new ChatListAdapter(ChatList.this, ChatListDataModelArrayListTemp, activityContext);
                    chatlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new ChatListAdapter(ChatList.this, ChatListDataModelArrayList, activityContext);
                    chatlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        });

        GetChatListDetails();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatList.this,ChatContacts.class);
                startActivity(i);
            }
        });
    }


    public void GetChatListDetails() {

        progressBarHolder.setVisibility(View.VISIBLE);

        ChatListDataModelArrayList.clear();
        ChatListDataModelArrayListTemp.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);


//        String url = "http://demo1.geniesoftsystem.com/newweb/icomuabn/index.php/apis/recent_chats";
        String url = (CommonUtils.BASE_URL)+"recent_chats";
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarHolder.setVisibility(View.GONE);
                    }
                });
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
                                String profile_pic_url=json.getString("profile_pic_url");


                                JSONArray chats = json.getJSONArray("chat_data");

                                for (int i = 0; i < chats.length(); i++) {
                                    JSONObject ChatDetails = chats.getJSONObject(i);

                                    ChatListModel model = new ChatListModel();
                                    model.setChatId(ChatDetails.getString("id").replace("null", ""));
                                    model.setToUserId(ChatDetails.getString("to_user_id").replace("null", ""));
                                    model.setToUserName(ChatDetails.getString("toUserName").replace("null", ""));
                                    model.setFromUserImage(profile_pic_url + ChatDetails.getString("to_user_id") + "/" + ChatDetails.getString("toUserProfilePic"));
                                    model.setMessage(ChatDetails.getString("message").replace("null", ""));
                                    model.setUnreadMessages(ChatDetails.getString("unread_count").replace("null", ""));

                                    if (!ChatDetails.getString("posted_date").equalsIgnoreCase("null")) {
                                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ChatDetails.getString("posted_date"));
                                        String newString = new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019
                                        Date td_date = new Date();
                                        String todayStr = new SimpleDateFormat("dd-MM-yyyy").format(td_date); //
                                        if (newString.compareTo(todayStr) == 0) {
                                            newString = new SimpleDateFormat("hh:mm a").format(date); // 9:00
                                            model.setDate(newString);
                                        } else {
                                            newString = new SimpleDateFormat("dd-MMM-yyyy").format(date); // 25-03-2019
                                            model.setDate(newString);

                                        }
                                    }
                                    progressBarHolder.setVisibility(View.GONE);
                                    ChatListDataModelArrayList.add(model);
                                    ChatListDataModelArrayListTemp.add(model);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(ChatList.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            progressBarHolder.setVisibility(View.GONE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            progressBarHolder.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
