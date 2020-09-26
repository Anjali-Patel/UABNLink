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
import com.uabn.gss.uabnlink.adapter.HighSchoolAdapter;
import com.uabn.gss.uabnlink.adapter.UnderGraduateAdapter;
import com.uabn.gss.uabnlink.model.HighSchoolModel;
import com.uabn.gss.uabnlink.model.UnderGraduateModel;

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

public class UnderGraduateSchoolActivity extends AppCompatActivity {
    Toolbar toolbar;
    RelativeLayout other_relative;
    ExpandableRelativeLayout expandableLayout1;
    FrameLayout progressBarHolder;
    EditText input_search,other_school;
    RecyclerView high_school_list;
    ArrayList<UnderGraduateModel> underGraduateSchoolModelArraylist;
    ArrayList<UnderGraduateModel> underGraduateSchoolModelArraylistTemp;
    UnderGraduateAdapter adapter;
    Button add;
    RecyclerView.LayoutManager layoutmanager;
    UnderGraduateAdapter.IOnItemClickListener iOnItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_graduate_school);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        input_search = findViewById(R.id.input_search);
        high_school_list = findViewById(R.id.high_school_list);
        progressBarHolder = findViewById(R.id.progressBarHolder);
        other_relative = findViewById(R.id.other_relative);
        expandableLayout1=findViewById(R.id.expandableLayout1);
        other_school = findViewById(R.id.other_school);
        add = findViewById(R.id.add);
        underGraduateSchoolModelArraylist = new ArrayList<>();
        underGraduateSchoolModelArraylistTemp = new ArrayList<>();
        iOnItemClickListener = new UnderGraduateAdapter.IOnItemClickListener() {
            @Override
            public void onItemClick(String text) {
//                Type_Comment.setText(text);
                CommonUtils.undergraduate_school_id=text;
//                String a= CommonUtils.graduate_school_id;
//                Toast.makeText(GraduateSchoolActivity.this,a,Toast.LENGTH_LONG).show();
                UnderGraduateSchoolActivity.this.finish();
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
                            Toast.makeText(UnderGraduateSchoolActivity.this,"Please enter Under Graduate School Name",Toast.LENGTH_LONG).show();
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
                    underGraduateSchoolModelArraylistTemp.clear();
                    for (int i = 0; i < underGraduateSchoolModelArraylist.size(); i++) {
                        if (underGraduateSchoolModelArraylist.get(i).getUndergraduatename().toLowerCase().startsWith(strText.toLowerCase())) {
                            underGraduateSchoolModelArraylistTemp.add(underGraduateSchoolModelArraylist.get(i));
                        }
                    }
                    adapter = new UnderGraduateAdapter(UnderGraduateSchoolActivity.this, underGraduateSchoolModelArraylistTemp,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new UnderGraduateAdapter(UnderGraduateSchoolActivity.this, underGraduateSchoolModelArraylist,iOnItemClickListener);
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
                    underGraduateSchoolModelArraylistTemp.clear();
                    for (int i = 0; i < underGraduateSchoolModelArraylist.size(); i++) {
                        if (underGraduateSchoolModelArraylist.get(i).getUndergraduatename().toLowerCase().startsWith(strText.toLowerCase())) {
                            underGraduateSchoolModelArraylistTemp.add(underGraduateSchoolModelArraylist.get(i));
                        }
                    }
                    adapter = new UnderGraduateAdapter(UnderGraduateSchoolActivity.this, underGraduateSchoolModelArraylistTemp,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new UnderGraduateAdapter(UnderGraduateSchoolActivity.this, underGraduateSchoolModelArraylist,iOnItemClickListener);
                    high_school_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        getUnderGraduateSchoolList();

    }
    private void getUnderGraduateSchoolList() {
        progressBarHolder.setVisibility(View.VISIBLE);
        underGraduateSchoolModelArraylist.clear();
        String url = (CommonUtils.BASE_URL) + "under_graduate_school_dropdown ";
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
                                    UnderGraduateModel model = new UnderGraduateModel();
                                    model.setUdergraduate_id(RegionObj.getString("id"));
                                    model.setUndergraduatename(RegionObj.getString("name"));
                                    model.setUndergraduate_status(RegionObj.getString("status"));
                                    underGraduateSchoolModelArraylist.add(model);
                                }
                                progressBarHolder.setVisibility(View.GONE);
                                adapter = new UnderGraduateAdapter(UnderGraduateSchoolActivity.this, underGraduateSchoolModelArraylist,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(UnderGraduateSchoolActivity.this);
                                high_school_list.setLayoutManager(layoutmanager);
                                high_school_list.setAdapter(adapter);
                                //    for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
//                                    final PoliticalAffiliationModel Items = PoliticalAffiliationModelArrayList.get(i);
//                                    politicalAffiliationList.add(Items.getPolitical_name());
//                                }
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