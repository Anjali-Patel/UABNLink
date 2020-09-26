package com.uabn.gss.uabnlink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.adapter.GraduateAdapter;
import com.uabn.gss.uabnlink.adapter.GroupRepliesAdapter;
import com.uabn.gss.uabnlink.adapter.HighSchoolAdapter;
import com.uabn.gss.uabnlink.model.GraduateModel;
import com.uabn.gss.uabnlink.model.HighSchoolModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GraduateSchoolActivity extends AppCompatActivity {
    Toolbar toolbar;
    ExpandableRelativeLayout expandableLayout1;

    RelativeLayout other_relative;
    FrameLayout progressBarHolder;
    EditText input_search,other_school;
    RecyclerView high_school_list;
    ArrayList<GraduateModel> highSchoolModelArraylist;
    ArrayList<GraduateModel> highSchoolModelArraylistTemp;
    GraduateAdapter adapter;
    Button add;
    RecyclerView.LayoutManager layoutmanager;
    GraduateAdapter.IOnItemClickListener iOnItemClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduate_school);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        input_search=findViewById(R.id.input_search);
        high_school_list=findViewById(R.id.high_school_list);
        progressBarHolder=findViewById(R.id.progressBarHolder);
         other_relative=findViewById(R.id.other_relative);
        other_school=findViewById(R.id.other_school);
        add=findViewById(R.id.add);
        expandableLayout1=findViewById(R.id.expandableLayout1);
        highSchoolModelArraylist=new ArrayList<>();
        highSchoolModelArraylistTemp= new ArrayList<>();
        iOnItemClickListener = new GraduateAdapter.IOnItemClickListener() {
            @Override
            public void onItemClick(String text) {
//                Type_Comment.setText(text);
                CommonUtils.graduate_school_id=text;
//                String a= CommonUtils.graduate_school_id;
//                Toast.makeText(GraduateSchoolActivity.this,a,Toast.LENGTH_LONG).show();
                GraduateSchoolActivity.this.finish();
            }
        };
        other_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLayout1.toggle();
                other_school.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str_otherschool=other_school.getText().toString().trim();
                        if(str_otherschool.equalsIgnoreCase("")){
                            Toast.makeText(GraduateSchoolActivity.this,"Please enter High School Name",Toast.LENGTH_LONG).show();
                        }else{
                            CommonUtils.othergraduate_school_id=str_otherschool;
                        }
                    }
                });
            }
        });
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    highSchoolModelArraylistTemp.clear();
                    for (int i = 0; i < highSchoolModelArraylist.size(); i++) {
                        if (highSchoolModelArraylist.get(i).getGraduateschool_name().toLowerCase().startsWith(strText.toLowerCase())) {
                            highSchoolModelArraylistTemp.add(highSchoolModelArraylist.get(i));
                        }
                    }
                    adapter = new GraduateAdapter(GraduateSchoolActivity.this, highSchoolModelArraylistTemp,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new GraduateAdapter(GraduateSchoolActivity.this, highSchoolModelArraylist,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
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
                    highSchoolModelArraylistTemp.clear();
                    for (int i = 0; i < highSchoolModelArraylist.size(); i++) {
                        if (highSchoolModelArraylist.get(i).getGraduateschool_name().toLowerCase().startsWith(strText.toLowerCase())) {
                            highSchoolModelArraylistTemp.add(highSchoolModelArraylist.get(i));
                        } }
                    adapter = new GraduateAdapter(GraduateSchoolActivity.this, highSchoolModelArraylistTemp,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new GraduateAdapter(GraduateSchoolActivity.this, highSchoolModelArraylist,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        getHighschoolList();

    }
    private void getHighschoolList() {
        progressBarHolder.setVisibility(View.VISIBLE);
        highSchoolModelArraylist.clear();
        String url = (CommonUtils.BASE_URL)+"graduate_school_dropdown";
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                JSONArray json2 = json.getJSONArray("school_data");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("schools");
                                    GraduateModel model = new GraduateModel();
                                    model.setGraduateschool_id(RegionObj.getString("id"));
                                    model.setGraduateschool_name(RegionObj.getString("name"));
                                    model.setGraduateschool_status(RegionObj.getString("status"));
                                    highSchoolModelArraylist.add(model);
                                }
                                progressBarHolder.setVisibility(View.GONE);
                                adapter = new GraduateAdapter(GraduateSchoolActivity.this, highSchoolModelArraylist,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(GraduateSchoolActivity.this);
                                high_school_list.setLayoutManager(layoutmanager);
                                high_school_list.setAdapter(adapter);
                                //    for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
//                                    final PoliticalAffiliationModel Items = PoliticalAffiliationModelArrayList.get(i);
//                                    politicalAffiliationList.add(Items.getPolitical_name());
//                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });

    }
}
