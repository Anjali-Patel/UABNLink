package com.uabn.gss.uabnlink.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.MembersAdapter;
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

public class EventGuestList extends AppCompatActivity {

    RecyclerView guestlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<MemberDataModel> MembersDataModelArrayList = new ArrayList<>();
    ArrayList<MemberDataModel> MembersDataModelArrayListTemp = new ArrayList<>();

    Context activityContext;
    MembersAdapter adapter;

    SharedPreferenceUtils preferances;
    String UserId, EventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_guest_list);


        preferances= SharedPreferenceUtils.getInstance(this);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        EventId = getIntent().getStringExtra("EventId");

        guestlist = findViewById(R.id.guestlist);


        adapter = new MembersAdapter(this, MembersDataModelArrayList, activityContext, "");
        layoutmanager = new LinearLayoutManager(this);
        guestlist.setLayoutManager(layoutmanager);
        guestlist.setAdapter(adapter);
        preferances = SharedPreferenceUtils.getInstance(this);


        GetAttendanceDetails(EventId);
    }


    public void GetAttendanceDetails(String EventId) {

        MembersDataModelArrayList.clear();
        //ProgessLoad.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL) + "view_guest_list";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("event_id", EventId)

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
                                String profile_pic_url = json.getString("profile_pic_url");


                                JSONArray members = json.getJSONArray("guest_list");

                                for (int i = 0; i < members.length(); i++) {
                                    JSONObject Memberdetail = members.getJSONObject(i);

                                    MemberDataModel model = new MemberDataModel();
                                    model.setMemberName(Memberdetail.getString("name").replace("null", ""));
                                    model.setMemberAddress(Memberdetail.getString("event_going_status").replace("null", ""));
                                    model.setMemberId(Memberdetail.getString("user_id").replace("null", ""));
                                    model.setMemberImage(profile_pic_url + Memberdetail.getString("user_id") + "/" + Memberdetail.getString("profile_picture").replace("null", "").replace(" ", "%20"));

                                    // model.setMemberImage(Memberdetail.getString("profile_picture").replace("null", ""));
                                    MembersDataModelArrayList.add(model);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(EventGuestList.this, json.getString("message"), Toast.LENGTH_LONG).show();
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
