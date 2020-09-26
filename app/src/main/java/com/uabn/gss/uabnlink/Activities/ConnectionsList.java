package com.uabn.gss.uabnlink.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.ConnectionsAdapter;
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

public class ConnectionsList extends AppCompatActivity {

    ProgressBar ProgessLoad;
    RecyclerView connectionlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<MemberDataModel> MembersDataModelArrayList = new ArrayList<>();
    Context activityContext;
    ConnectionsAdapter adapter;

    SharedPreferenceUtils preferances;
    String UserId, ConnectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections_list);

        preferances = SharedPreferenceUtils.getInstance(this);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

        ProgessLoad = findViewById(R.id.ProgessLoad);
        connectionlist = findViewById(R.id.connectionlist);

        ConnectionId = getIntent().getStringExtra("ConncetionId");

        GetMemberDetails(ConnectionId);


    }

    public void GetMemberDetails( String ConnectionId) {
        MembersDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL) + "getIdwiseConnections";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("view_id", ConnectionId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        final String myResponse = responseBody.string();

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = null;
                                        try {
                                            json = new JSONObject(myResponse);
                                            String success = json.getString("status");
                                            if (success.equalsIgnoreCase("success")) {
                                                String profile_pic_url = json.getString("profile_pic_url");
                                                JSONArray update = json.getJSONArray("my_connections");
                                                for (int i = 0; i < update.length(); i++) {
                                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                                    MemberDataModel model = new MemberDataModel();
                                                    model.setMemberName(UpdateDetails.getString("name").replace("null", ""));
                                                    model.setMemberId(UpdateDetails.getString("id").replace("null", ""));
                                                    model.setMemberImage(profile_pic_url + UpdateDetails.getString("id")+ "/" +UpdateDetails.getString("profile_picture").replace("null", "").replace(" ", "%20"));
                                                    MembersDataModelArrayList.add(model);
//                                                    adapter.notifyDataSetChanged();
                                                }

                                                adapter = new ConnectionsAdapter(ConnectionsList.this, MembersDataModelArrayList, activityContext);
                                                layoutmanager = new LinearLayoutManager(ConnectionsList.this);
                                                connectionlist.setLayoutManager(layoutmanager);
                                                connectionlist.setAdapter(adapter);

                                            } else {

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
