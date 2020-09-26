package com.uabn.gss.uabnlink.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;

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

public class UserProfileData extends AppCompatActivity {

    ImageView userImg;
    TextView UserName, UserBio, country, state, city, org, proff_status,industry,business, services, status, Connections;
    Button send_req;
    LinearLayout seeConnections;
    String UserId, MemberId;
    ProgressBar progess_load;
    SharedPreferenceUtils preferances;
    String ImageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_data);
        UserId = getIntent().getStringExtra("UserId");
        userImg = findViewById(R.id.userImg);
        UserName = findViewById(R.id.UserName);
        UserBio = findViewById(R.id.UserBio);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        org = findViewById(R.id.org);
        proff_status = findViewById(R.id.proff_status);
        industry = findViewById(R.id.industry);
        business = findViewById(R.id.business);
        services = findViewById(R.id.services);
        status = findViewById(R.id.status);
        send_req = findViewById(R.id.send_req);
        seeConnections = findViewById(R.id.seeConnections);
        Connections = findViewById(R.id.Connections);
        progess_load = findViewById(R.id.progess_load);
        preferances= SharedPreferenceUtils.getInstance(this);
        MemberId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        send_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send_req.getText().toString().equalsIgnoreCase("send connection request")) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileData.this);
                    alertDialogBuilder.setMessage("Are you sure you want to send request?");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            sendConnectionRequest(UserId);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });


        seeConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(UserProfileData.this,ConnectionsList.class);
            i.putExtra("ConncetionId", UserId);
            startActivity(i);
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog=new Dialog(UserProfileData.this, R.style.DialogAnimation_2);
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

                Glide.with(UserProfileData.this).asBitmap().load(ImageURL).apply(options).into(img);
                ImageProgress.setVisibility(View.GONE);
                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                dialog.show();
            }
        });



        getUserData(UserId, MemberId);

    }



    public void getUserData(String UserId, String MemberId) {

        progess_load.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL)+"viewUserInfo";

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("view_id", UserId)
                .add("user_id", MemberId)

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

                                JSONObject UserDataObj = json.getJSONObject("userinfo");

                                String ProfileBase = json.getString("profile_pic_url");
                                final RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .error(R.drawable.noimage)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .priority(Priority.HIGH);

                                JSONObject UserData = UserDataObj.getJSONObject("User");
                                JSONObject Region = UserDataObj.getJSONObject("Region");
                                JSONObject UserType = UserDataObj.getJSONObject("UserType");
                                JSONObject Country = UserDataObj.getJSONObject("Country");
                                JSONObject Nation = UserDataObj.getJSONObject("Nation");
                                JSONObject State = UserDataObj.getJSONObject("State");
                                JSONObject Category = UserDataObj.getJSONObject("Category");
                                JSONObject Agency = UserDataObj.getJSONObject("Agency");
                                ImageURL = ProfileBase + UserData.getString("id") + "/" + UserData.getString("profile_picture");
                                Glide.with(UserProfileData.this).asBitmap().load(ImageURL).apply(options).into(userImg);
                                UserName.setText(UserData.getString("name").replace("null", "-"));
                                UserBio.setText(UserData.getString("email").replace("null", "-"));
                                country.setText(Country.getString("name").replace("null", "-"));
                                state.setText(State.getString("name").replace("null", "-"));
                                city.setText(UserData.getString("city").replace("null", "-"));
                                org.setText(UserData.getString("organization").replace("null", "-"));
                                proff_status.setText(UserData.getString("profession").replace("null", "-"));
                                industry.setText(UserData.getString("industry_focus").replace("null", "-"));
                                business.setText(Agency.getString("item_title").replace("null", "-"));
                                services.setText(UserData.getString("core_services").replace("null", "-"));
                                status.setText(UserType.getString("status").replace("null", "-"));
                                Connections.setText("Connections ("+UserData.getString("connection_count")+ ")".replace("null", "0"));

                                progess_load.setVisibility(View.GONE);
                                //Picasso.with(UserProfileData.this).load(ImageURL).error(R.drawable.ic_person_black_24dp).into(MainActivity.user_profile);
//                                SharedPreferenceUtils.getInstance(UserProfileData.this).setValue(CommonUtils.MEMBER_IMAGE, ImageURL);


                                String ReqStatus = UserData.getString("request_status");
                                    send_req.setText(UserData.getString("request_status"));

                                if (ReqStatus.equalsIgnoreCase("send connection request")){
                                    send_req.setBackground(getDrawable(R.drawable.btn_background));
                                }
                                else if (ReqStatus.equalsIgnoreCase("pending")){
                                    send_req.setBackground(getDrawable(R.drawable.yellow_btn_background));
                                }
                                else if (ReqStatus.equalsIgnoreCase("connected")){
                                    send_req.setBackground(getDrawable(R.drawable.green_btn_background));
                                }
                                else if (ReqStatus.equalsIgnoreCase("rejected")){
                                    send_req.setBackground(getDrawable(R.drawable.red_btn_background));
                                }
                                else{
                                    send_req.setBackground(getDrawable(R.drawable.btn_background));
                                }





                            } else {
                                progess_load.setVisibility(View.GONE);
                                Toast.makeText(UserProfileData.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }

    private void sendConnectionRequest( String ReceiverId) {
        String url = (CommonUtils.BASE_URL) + "sendConnectionRequest";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("receiver_id", ReceiverId)
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

                runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(UserProfileData.this, json.getString("message"), Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(UserProfileData.this, json.getString("message"), Toast.LENGTH_LONG).show();
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
