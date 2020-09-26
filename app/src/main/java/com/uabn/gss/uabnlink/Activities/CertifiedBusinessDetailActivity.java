package com.uabn.gss.uabnlink.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CheckNetwork;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.adapter.BannerAdapter;
import com.uabn.gss.uabnlink.model.CertifiedBannerModel;
import com.uabn.gss.uabnlink.model.CertifiedBusinessModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.uabn.gss.uabnlink.Utility.ExpandableTextView.makeTextViewResizable;

public class CertifiedBusinessDetailActivity extends AppCompatActivity {
    TextView UserName,description,certified_business,region,city,managing,key_industry,country,state,primary_contact,core_services,year_certified,reason_for_joining,website;
    String str_cb_id,user_id,businessname,date,str_img,str_country,banner_base_url,str_facebook,str_you_tube,str_insta,str_linkedin,str_tweeter;
  ImageView userImg,bg_img;
    SharedPreferenceUtils preferances;
    String CertifiedBase;
    RelativeLayout r1;
    String type="Certified Business";
    String ImgURL,str_bg_img;
    ImageView twitter,youtube,linkedin,instagram,facebook;
    FrameLayout progressBarHolder;
    LinearLayout MainView;
    CircleIndicator indicator;
    TextView Comment, Share, Email;
    static ShareGroupDialogue share;
    private static int currentPage = 0;
    ViewPager mPager;
    ArrayList<CertifiedBannerModel> banner_list;
    String web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certified_business_detail);
        UserName=findViewById(R.id.UserName);
        description=findViewById(R.id.description);
        certified_business=findViewById(R.id.certified_business);
        region=findViewById(R.id.region);
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        facebook=findViewById(R.id.facebook);
        instagram=findViewById(R.id.instagram);
        banner_list= new ArrayList<>();
        linkedin=findViewById(R.id.linkedin);
        r1=findViewById(R.id.r1);
        youtube=findViewById(R.id.youtube);

        twitter=findViewById(R.id.twitter);

        city=findViewById(R.id.city);
        bg_img=findViewById(R.id.bg_img);
        userImg=findViewById(R.id.userImg);
        managing=findViewById(R.id.managing);
        year_certified=findViewById(R.id.year_certified);
        reason_for_joining=findViewById(R.id.reason_for_joining);
        website=findViewById(R.id.website);
        key_industry=findViewById(R.id.key_industry);
        country=findViewById(R.id.country);
        state=findViewById(R.id.state);
        progressBarHolder=findViewById(R.id.progressBarHolder);
        MainView=findViewById(R.id.MainView);
        primary_contact=findViewById(R.id.primary_contact);
        Comment=findViewById(R.id.Comment);
        Share=findViewById(R.id.Share);
        Email=findViewById(R.id.Email);
        str_cb_id=getIntent().getStringExtra("cb_id");
        core_services=findViewById(R.id.core_services);
        preferances = SharedPreferenceUtils.getInstance(CertifiedBusinessDetailActivity.this);
        user_id=preferances.getStringValue(CommonUtils.MEMBER_ID, "");
if(CheckNetwork.isInternetAvailable(CertifiedBusinessDetailActivity.this)){
    getCertifiedBusinessDetail(user_id,str_cb_id);
    }else{
    Toast.makeText(CertifiedBusinessDetailActivity.this, "No Internet Connection.Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                }


        Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CertifiedBusinessDetailActivity.this, CertifiedBusinessCommentActivity.class);
                intent.putExtra("cb_id",str_cb_id);
                intent.putExtra("name",businessname);
                intent.putExtra("img",ImgURL);
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share= new ShareGroupDialogue();
                share.setContext(CertifiedBusinessDetailActivity.this);
                share.setCertifiedBusinessId(str_cb_id);
                share.setCertifiedBusinessUserId(user_id);
                share.show(getSupportFragmentManager(),ShareGroupDialogue.class.getSimpleName());
            }
        });

        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(CertifiedBusinessDetailActivity.this, R.style.DialogAnimation_2);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.setContentView(R.layout.zoom_profile_pic);
                ZoomImageView img = dialog.findViewById(R.id.img);
                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                ImageProgress.setVisibility(View.VISIBLE);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.noimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);


                Glide.with(CertifiedBusinessDetailActivity.this).load(ImgURL).apply(options).into(img);
                 ImageProgress.setVisibility(View.GONE);


                dialog.show();

            }
        });

    }
    public void getCertifiedBusinessDetail(String user_id,String str_cb_id){
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        progressBarHolder.setVisibility(View.VISIBLE);
        MainView.setVisibility(View.GONE);

        String url = (CommonUtils.BASE_URL)+"view_certified_business";

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("cb_id", str_cb_id)
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
                            CertifiedBase=json.getString("image_url");
                            banner_base_url=json.getString("bg_mage_url");
                            if (success.equalsIgnoreCase("success")) {
                                JSONArray update = json.getJSONArray("cb_data");
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject UpdateDetails = update.getJSONObject(i);
                                    final JSONObject job = UpdateDetails.getJSONObject("Agency");
                                    JSONObject json_country = UpdateDetails.getJSONObject("Country");
                                    JSONObject json_region = UpdateDetails.getJSONObject("Region");
                                    JSONObject json_state = UpdateDetails.getJSONObject("State");
                                    if(type.equalsIgnoreCase("Certified Business")) {
                                        final JSONArray banner_photo = UpdateDetails.getJSONArray("BannerPhoto");
                                        for (int j = 0; j < banner_photo.length(); j++) {
                                            JSONObject banner_photo_details = banner_photo.getJSONObject(j);
                                            final CertifiedBannerModel certifiedBannerModel = new CertifiedBannerModel();
                                            final String banner_id = banner_photo_details.getString("banner_type_id");
                                            final String path = banner_photo_details.getString("path");
                                            String banner_img_url =banner_base_url +banner_id+"/" + path;
//                                        Glide.with(CertifiedBusinessDetailActivity.this).asBitmap().load(banner_img_url).into(bg_img);
                                            certifiedBannerModel.setBanner_image(banner_img_url);
                                            banner_list.add(certifiedBannerModel);
                                            mPager.setAdapter(new BannerAdapter(CertifiedBusinessDetailActivity.this, banner_list));
                                            mPager.setCurrentItem(1);
                                            indicator.setViewPager(mPager);
                                            // Auto start of viewpager
//                                        final Handler handler = new Handler();
//                                        final Runnable Update = new Runnable() {
//                                            public void run() {
//                                                if (currentPage == banner_list.size()) {
//                                                    currentPage = 0;
//                                                }
//                                                mPager.setCurrentItem(currentPage++, true);
//                                            }
//                                        };
//                                        init();
//                                        Glide.with(CertifiedBusinessDetailActivity.this).asBitmap().load(banner_img_url).into(bg_img);

                                        }
                                    }else{
                                        r1.setVisibility(View.GONE);
                                        bg_img.setVisibility(View.VISIBLE);
                                        Glide.with(CertifiedBusinessDetailActivity.this).load(R.drawable.detailbannerbg).into(bg_img);
//                                        bg_img.setImageResource(R.drawable.detailbannerbg);
                                    }
                                   str_linkedin=job.getString("linkedin_id");
                                    str_facebook=job.getString("facebook_id");
                                            str_insta=job.getString("instagram_id");
                                                    str_tweeter=job.getString("twitter_id");
                                                            str_you_tube=job.getString("youtube_id");


                                    if(!str_linkedin.equalsIgnoreCase("")&&!str_linkedin.equalsIgnoreCase("null")){
                                       linkedin.setVisibility(View.VISIBLE);
                                       linkedin.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent myIntent = new Intent(CertifiedBusinessDetailActivity.this,SocialWebViewActivity.class);
                                               myIntent.putExtra("url",str_linkedin);
                                               startActivity(myIntent);
                                           }
                                       });
                                   }else{
                                        linkedin.setVisibility(View.GONE);

                                    }
                                    if(!str_facebook.equalsIgnoreCase("")&&!str_facebook.equalsIgnoreCase("null")){
                                        facebook.setVisibility(View.VISIBLE);
                                        facebook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent myIntent = new Intent(CertifiedBusinessDetailActivity.this,SocialWebViewActivity.class);
                                                myIntent.putExtra("url",str_facebook);
                                                startActivity(myIntent);
                                            }
                                        });

                                    }else{
                                        facebook.setVisibility(View.GONE);

                                    }
                                    if(!str_tweeter.equalsIgnoreCase("")&&!str_tweeter.equalsIgnoreCase("null")){
                                        twitter.setVisibility(View.VISIBLE);
                                        twitter.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent myIntent = new Intent(CertifiedBusinessDetailActivity.this,SocialWebViewActivity.class);
                                                myIntent.putExtra("url",str_tweeter);
                                                startActivity(myIntent);
                                            }
                                        });
                                    }else{
                                        twitter.setVisibility(View.GONE);

                                    }
                                    if(!str_insta.equalsIgnoreCase("")&&!str_insta.equalsIgnoreCase("null")){
                                        instagram.setVisibility(View.VISIBLE);
               instagram.setOnClickListener(new View.OnClickListener() {
               @Override
              public void onClick(View v) {


        Intent myIntent = new Intent(CertifiedBusinessDetailActivity.this,SocialWebViewActivity.class);
        myIntent.putExtra("url",str_insta);
        startActivity(myIntent);
         }
         });
                                    }else{
                                        instagram.setVisibility(View.GONE);
                                    }
                                    if(!str_you_tube.equalsIgnoreCase("")&&!str_you_tube.equalsIgnoreCase("null")){
                                        youtube.setVisibility(View.VISIBLE);

                                        youtube.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent myIntent = new Intent(CertifiedBusinessDetailActivity.this,SocialWebViewActivity.class);
                                                myIntent.putExtra("url",str_you_tube);
                                                startActivity(myIntent);
                                            }
                                        });

                                    }else{
                                        youtube.setVisibility(View.GONE);

                                    }
                                    businessname = job.getString("item_title").replace("null", "");

//                                    str_bg_img=CertifiedBase+job.getString("id")+"/"+job.getString("bg_image");
//                                    Glide.with(CertifiedBusinessDetailActivity.this).asBitmap().load(str_bg_img).into(bg_img);

                                    ImgURL = CertifiedBase + job.getString("id") + "/" + job.getString("image");
                                    Glide.with(CertifiedBusinessDetailActivity.this).load(ImgURL).apply(options).into(userImg);
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(job.getString("created_on"));
                                    String BussinessDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate);
                                    date = BussinessDate;
                                    year_certified.setText(job.getString("no_years").replace("null",""));
                                    web=job.getString("website_url");
                                    if(!web.equalsIgnoreCase("")){
                                        website.setVisibility(View.VISIBLE);
//                                        click_here.setText(web.replace("null", ""));
                                        website.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(web));
                                                    startActivity(myIntent);
                                                } catch (ActivityNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }else{
                                        website.setVisibility(View.VISIBLE);
                                        website.setText("-");

                                    }

                                    country.setText(json_country.getString("name").replace("null",""));
                                    city.setText(job.getString("city").replace("null", ""));
                                    description.setText(job.getString("item_desc").replace("null", ""));

                                    if(job.getString("item_desc").length() > 100){
                                        makeTextViewResizable(description, 5, "View More", true);
                                    }

                                    key_industry.setText(job.getString("key_industry").replace("null", ""));
                                    region.setText(json_region.getString("name").replace("null", ""));
                                    state.setText(json_state.getString("name").replace("null", ""));
                                    primary_contact.setText(job.getString("contact_phone").replace("null", ""));
                                    UserName.setText(job.getString("item_title").replace("null", ""));
                                    description.setText(job.getString("item_desc").replace("null", ""));
                                    certified_business.setText(job.getString("item_title").replace("null", ""));
                                    JSONArray jsonArray = UpdateDetails.getJSONArray("Category");
                               for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject CommentDetails = jsonArray.getJSONObject(j);
                                       key_industry.setText(CommentDetails.getString("name").replace("/", ","));
                                   core_services.setText(CommentDetails.getString("name").replace("/", ","));


                            }
                                }

                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                            } else {

                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(CertifiedBusinessDetailActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();

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


    public static class ShareGroupDialogue extends DialogFragment {
        Context context;
        String CertifiedBusinessUserId, CertifiedBusinessId;

        ProgressBar progess_load;

        public void setContext(Context context) { this.context = context; }
        public Context getContext() { return context; }

        public void setCertifiedBusinessUserId(String user) { this.CertifiedBusinessUserId = user; }
        public String getCertifiedBusinessUserId() { return CertifiedBusinessUserId; }

        public void setCertifiedBusinessId(String id) { this.CertifiedBusinessId = id; }
        public String getCertifiedBusinessId() { return CertifiedBusinessId; }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.share, null);
            ImageView search_close_btn=dialogView.findViewById(R.id.search_close_btn);
            final TextView SearchText = (TextView) dialogView.findViewById(R.id.searchtext);
            Button submit = (Button) dialogView.findViewById(R.id.submit);
            final EditText To_text,subject_text,message_text;
            To_text=dialogView.findViewById(R.id.To_text);
            subject_text=dialogView.findViewById(R.id.subject_text);
            message_text=dialogView.findViewById(R.id.message_text);
            progess_load = dialogView.findViewById(R.id.progess_load);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str_to = To_text.getText().toString().trim();
                    String str_subject = subject_text.getText().toString().trim();
                    String str_message = message_text.getText().toString().trim();
                    if (str_to.equalsIgnoreCase("")) {
                        Toast.makeText(context, "Please enter your mail address", Toast.LENGTH_LONG).show();
                    }else if(str_subject.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter your Subject", Toast.LENGTH_LONG).show();
                    }else if(!isValidEmail(To_text.getText().toString().trim())) {
                        Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    } else if(str_message.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter your Message", Toast.LENGTH_LONG).show();
                    }else{
                        ShareGroup(getCertifiedBusinessUserId(),str_to,str_subject,str_message,getCertifiedBusinessId());
                        progess_load.setVisibility(View.VISIBLE);
                    }
                }
            });





            search_close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            return dialogView;
        }

        public void  ShareGroup(String UserId, String str_to, String str_subject, String str_message, String group_id){
            String url = (CommonUtils.BASE_URL)+"share_cb";
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", UserId)
                    .add("to_email", str_to)
                    .add("subject", str_subject)
                    .add("message", str_message)
                    .add("cb_id", group_id)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progess_load.setVisibility(View.GONE);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(myResponse);
                                String success = json.getString("status");
                                if (success.equalsIgnoreCase("success")) {
                                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                    share.dismiss();

                                } else {
                                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
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

    private static boolean isValidEmail(CharSequence target) {
        boolean emailflag = false;
        String emailArr[] = target.toString().split("[,]");
        for (int i = 0; i < emailArr.length; i++) {
            emailflag = Patterns.EMAIL_ADDRESS.matcher(
                    emailArr[i].trim()).matches();
        }
        return emailflag;
    }

}
