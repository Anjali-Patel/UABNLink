package com.uabn.gss.uabnlink.Activities;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.model.PBCR_DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.uabn.gss.uabnlink.Utility.ExpandableTextView.makeTextViewResizable;

public class PBCRDetailActivity extends AppCompatActivity {
TextView reason,name,description,created_by,pbcr_industry,date_created,contact_person,response_text,category,region,status;
String pbcr_id,user_id;
LinearLayout MainView;
FrameLayout progressBarHolder;
ImageView iv;
    SharedPreferenceUtils preferances;
    ProgressBar pb;
    String img_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbcrdetail);
        reason=findViewById(R.id.reason);
        name=findViewById(R.id.name);
        iv=findViewById(R.id.pbcrImg);
        description=findViewById(R.id.description);
        created_by=findViewById(R.id.created_by);
        pbcr_industry=findViewById(R.id.pbcr_industry);
        date_created=findViewById(R.id.date_created);
        contact_person=findViewById(R.id.contact_person);
        response_text=findViewById(R.id.response);
        category=findViewById(R.id.category);
        region=findViewById(R.id.region);
        pb=findViewById(R.id.pb);
        status=findViewById(R.id.status);
        MainView = findViewById(R.id.MainView);
        progressBarHolder = findViewById(R.id.progressBarHolder);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pbcr_id = extras.getString("pbcr_id");
        }
//        pbcr_id=getIntent().getStringExtra("pbcr_id");
        preferances = SharedPreferenceUtils.getInstance(PBCRDetailActivity.this);
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
       pbcrDetails(user_id,pbcr_id);
       pb.setVisibility(View.VISIBLE);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.noimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                final Dialog dialog=new Dialog(PBCRDetailActivity.this, R.style.DialogAnimation_2);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.setContentView(R.layout.zoom_profile_pic);
                ZoomImageView img = dialog.findViewById(R.id.img);
                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                ImageProgress.setVisibility(View.VISIBLE);
                Glide.with(PBCRDetailActivity.this).asBitmap().load(img_url).apply(options).into(img);
                ImageProgress.setVisibility(View.GONE);

                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                dialog.show();
            }
        });


    }
    public void pbcrDetails(String user_id,String pbcr_id){
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);


        String url = (CommonUtils.BASE_URL) + "view_pbcr_post";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add(" pbcr_id", pbcr_id)
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
                                JSONArray update = json.getJSONArray("pbcr_details");
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                    JSONObject regionText = UpdateDetails.getJSONObject("Region");
                                    JSONObject job=UpdateDetails.getJSONObject("Mar");
                                    img_url="http://demo1.geniesoftsystem.com/newweb/icomuabn/app/webroot//files/mar/image/"+job.getString("id")+"/"+job.getString("image");                                    Glide.with(PBCRDetailActivity.this).load("http://demo1.geniesoftsystem.com/newweb/icomuabn/app/webroot//files/mar/image/"+job.getString("id")+"/"+job.getString("image")).into(iv);
                                    Glide.with(PBCRDetailActivity.this).asBitmap().load(img_url).apply(options).into(iv);
                                    reason.setText(job.getString("reason_for_pbcr").replace("null",""));
                                    name.setText(job.getString("item_title").replace("null",""));
                                    description.setText(job.getString("item_desc").replace("null",""));

                                    if(job.getString("item_desc").length() > 100){
                                        makeTextViewResizable(description, 5, "View More", true);
                                    }

                                    created_by.setText(job.getString("created_by_name").replace("null",""));
                                    //pbcr_industry.setText(job.getString("pbcr_industry").replace("null",""));
                                    date_created.setText(job.getString("display_date").replace("null",""));
                                    contact_person.setText(job.getString("contact_person").replace("null",""));
                                    response_text.setText(job.getString("Response_needed_no_later_than").replace("null",""));
                                    category.setText(job.getString("pbcr_category").replace("null",""));
                                    region.setText(regionText.getString("name").replace("null",""));

                                    if (job.getString("status").equalsIgnoreCase("0")){
                                        status.setText("Active");
                                    }
                                    else if (job.getString("status").equalsIgnoreCase("1")){
                                        status.setText("Inactive");
                                    }

                                }

                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                            } else {

                                MainView.setVisibility(View.GONE);
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(PBCRDetailActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
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
