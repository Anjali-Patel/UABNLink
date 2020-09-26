package com.uabn.gss.uabnlink.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.MyAdapter;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.RegionModel;
import com.uabn.gss.uabnlink.model.UserTypeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Login extends AppCompatActivity {

    TextView UserEmail, UserPassword, forget_password;
    Button RegisterButton, LoginButton;
    ProgressBar ProgessLoad;
    String UserId, reset_key, fcmTocken;
    private static ViewPager mPager;
    CircleIndicator indicator;
    private static final Integer[] XMEN= {R.drawable.viewpager_img};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    //    private static ViewPager pager;
    private static int currentPage = 0;

    SharedPreferenceUtils preferances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferances= SharedPreferenceUtils.getInstance(this);
        RegisterButton = findViewById(R.id.RegisterButton);
        LoginButton = findViewById(R.id.LoginButton);
        UserEmail = findViewById(R.id.UserEmail);
        UserPassword = findViewById(R.id.UserPassword);
        ProgessLoad = findViewById(R.id.ProgessLoad);
        forget_password = findViewById(R.id.forget_password);
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        checkPermissionForSDK();
        init();

        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");

        getRegionDetails();
        getCountryDetails();
        getUserTypeDetails();

        if (!UserId.equalsIgnoreCase("")){
            startActivity(new Intent(Login.this,MainActivity.class));
        }

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,Register.class);
                startActivity(i);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = UserEmail.getText().toString().trim();
                password = UserPassword.getText().toString().trim();
                fcmTocken = preferances.getStringValue(CommonUtils.FCMTOCKEN,"");

                if (email.equalsIgnoreCase("")){
                    Toast.makeText(Login.this, "Please enter your email address", Toast.LENGTH_LONG).show();
                }else if (password.equalsIgnoreCase("")){
                    Toast.makeText(Login.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                }
                else{
                    LoginUser(email, password);
                }
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str_email=UserEmail.getText().toString();
////                if(str_email.equalsIgnoreCase("")){
////                    Toast.makeText(Login.this, "Please enter your email address", Toast.LENGTH_LONG).show();
////
////                }else{
//                    forgetPassword(str_email);
//                    ProgessLoad.setVisibility(View.VISIBLE);
//
//
                Intent intent= new Intent(Login.this,ForgetPasswordActivity.class);
               startActivity(intent);

            }
        });

    }
    private void init() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter(Login.this,XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

    }

    public void getRegionDetails() {

        CommonUtils.REGIONARRAYLIST.clear();

        String url = (CommonUtils.BASE_URL)+"getRegion";


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

                                JSONArray json2 = json.getJSONArray("regions");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("region");

                                    RegionModel model = new RegionModel();
                                    model.setRegionId(RegionObj.getString("id"));
                                    model.setRegionName(RegionObj.getString("name"));
                                    model.setRegionStatus(RegionObj.getString("status"));

                                    CommonUtils.REGIONARRAYLIST.add(model);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void getCountryDetails() {

        CommonUtils.COUNTRYARRAYLIST.clear();

        String url = (CommonUtils.BASE_URL)+"getCountry";


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

                                JSONArray json2 = json.getJSONArray("countries");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("country");

                                    CountryModel model = new CountryModel();
                                    model.setCountryId(RegionObj.getString("id"));
                                    model.setCountryName(RegionObj.getString("name"));
                                    model.setCountryStatus(RegionObj.getString("status"));

                                    CommonUtils.COUNTRYARRAYLIST.add(model);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void getUserTypeDetails() {

        CommonUtils.USERTYPEARRAYLIST.clear();

        String url = (CommonUtils.BASE_URL)+"getUserTypes";


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

                                JSONArray json2 = json.getJSONArray("usetypes");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("user_type");

                                    UserTypeModel model = new UserTypeModel();
                                    model.setUserTypeId(RegionObj.getString("id"));
                                    model.setUserTypeName(RegionObj.getString("name"));
                                    model.setUserTypeStatus(RegionObj.getString("status"));

                                    CommonUtils.USERTYPEARRAYLIST.add(model);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void LoginUser(String Email, String Password) {


        ProgessLoad.setVisibility(View.VISIBLE);

        String url = (CommonUtils.BASE_URL)+"user_login";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("email", Email)
                .add("password", Password)
                .add("fcm_token", fcmTocken)

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

                                ProgessLoad.setVisibility(View.GONE);

                                String ImageUrl = json.getString("profile_pic_url") + json.getString("id") + "/" + json.getString("profile_picture");
                                preferances.setValue(CommonUtils.MEMBER_IMAGE, ImageUrl);
                                preferances.setValue(CommonUtils.MEMBER_ID, json.getString("id"));
                                preferances.setValue(CommonUtils.NAME, json.getString("fname") + " " +json.getString("lname"));
                                preferances.setValue(CommonUtils.MEMBEREMAIL, json.getString("email"));
                                startActivity(new Intent(Login.this,MainActivity.class));

                            }

                            else {

                                ProgessLoad.setVisibility(View.GONE);

                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Login.this);
                                builder.create();
                                builder.setMessage("Opps! Some problem occured while login, please provide valid Email and Password");

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void forgetPassword(final String email){
        String url = (CommonUtils.BASE_URL) + "forgot_password";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
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

                        Login.this.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = null;

                                        try {
                                            json = new JSONObject(myResponse);
                                            String success = json.getString("status");
                                            if (success.equalsIgnoreCase("success")) {
                                                ProgessLoad.setVisibility(View.GONE);
                                                Toast.makeText(Login.this,json.getString("message"),Toast.LENGTH_LONG).show();

//                                                reset_key=json.getString("reset_key");
                                                Intent intent= new Intent(Login.this,ForgetPasswordActivity.class);
//                                                intent.putExtra("reset_key",reset_key);
//                                                intent.putExtra("mail",email);
//                                                startActivity(intent);


                                            } else {
                                                Toast.makeText(Login.this,json.getString("message"),Toast.LENGTH_LONG).show();

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
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setMessage("Want to Exit ?");

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });


        builder.show();
    }

    private void checkPermissionForSDK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int Permission1 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.CAMERA);
            Log.e("LogPermission", Permission1 + "/m");
            int Permission2 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.PROCESS_OUTGOING_CALLS);
            Log.e("LogPermission", Permission2 + "/m");
            int Permission3 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            Log.e("LogPermission", Permission3 + "/m");
            int Permission4 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION);
            Log.e("LogPermission", Permission4 + "/m");
            int Permission5 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.READ_PHONE_STATE);
            Log.e("LogPermission", Permission5 + "/m");
            int Permission6 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("LogPermission", Permission6 + "/m");
            int Permission7 = ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.e("LogPermission", Permission7 + "/m");
            ArrayList<String> list = new ArrayList<>();

            if (Permission1 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.CAMERA);
            }
            if (Permission2 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
            }
            if (Permission3 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (Permission4 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (Permission5 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (Permission6 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (Permission7 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (list.size() > 0) {
                ActivityCompat.requestPermissions(Login.this, list.toArray(new String[list.size()]), Constants.MULTI_PERMISSION);
            }

        }
    }
}
