package com.uabn.gss.uabnlink.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.BannerAdapter;
import com.uabn.gss.uabnlink.adapter.MyAdapter;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.IndustryModel;
import com.uabn.gss.uabnlink.model.RegionModel;
import com.uabn.gss.uabnlink.model.UserTypeModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Register extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener {
    int position = 0;
    String UserImgPath = "";
    ImageView profile_pic;
    Button upload_profilepic;
    ArrayList<String> SearchRegion;
    ArrayList<String> SearchCountry;
    ArrayList<String> SearchPinRegion;
    ArrayList<RegionModel> RegionModelArrayList=new ArrayList<>();
    ArrayList<RegionModel> RegionModelArrayListTmp = new ArrayList<>();
    ArrayList<CountryModel> CountryModelArrayList= new ArrayList<>();
    ArrayList<CountryModel> CountryModelArrayListTmp = new ArrayList<>();
    ArrayList<UserTypeModel> UserTypeModelArrayList= new ArrayList<>();
    ArrayList<UserTypeModel> UserTypeModelArrayListTmp = new ArrayList<>();
    Spinner search_region, search_country, search_corporation;
    ProgressBar ProgessLoad;
    String SelectedRegionId = "", SelectedCountryId = "", SelectedUserTypeId = "", fcmTocken;
    TextView Fname, Lname, Email, Password, CfmPassword, Zipcode,already_member;
    Button SignUpButton;
    SharedPreferenceUtils preferances;
    private static  ViewPager mPager;
    CircleIndicator indicator;
    private static final Integer[] XMEN= {R.drawable.viewpager_img};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
//    private static ViewPager pager;
    private static int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferenceUtils.getInstance(getApplicationContext()).setValue(CommonUtils.FCMTOCKEN, refreshedToken);
        getRegionDetails();
        getCountryDetails();
        getUserTypeDetails();
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        preferances= SharedPreferenceUtils.getInstance(this);
        already_member=findViewById(R.id.already_member);
        upload_profilepic=findViewById(R.id.upload_profilepic);
        profile_pic=findViewById(R.id.profile_pic);
        // RegionModelArrayList = CommonUtils.REGIONARRAYLIST;
//        CountryModelArrayList = CommonUtils.COUNTRYARRAYLIST;
//        UserTypeModelArrayList = CommonUtils.USERTYPEARRAYLIST;
        RegionModelArrayList = new ArrayList<>();
        CountryModelArrayList = new ArrayList<>();
        UserTypeModelArrayList = new ArrayList<>();
        init();
        /*  mPager.setAdapter(new BannerAdapter(Register.this, banner_list));
        mPager.setCurrentItem(1);
        indicator.setViewPager(mPager);*/
        Fname = findViewById(R.id.Fname);
        Lname = findViewById(R.id.Lname);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        CfmPassword = findViewById(R.id.CfmPassword);
        Zipcode = findViewById(R.id.Zipcode);
        SignUpButton = findViewById(R.id.SignUpButton);
        search_region = findViewById(R.id.search_region);
        search_country = findViewById(R.id.search_country);
        search_corporation = findViewById(R.id.search_corporation);
        ProgessLoad = findViewById(R.id.ProgessLoad);
        upload_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(1)
                        .setTag("Picker")
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "Picker");
            }
        });
        SearchRegion = new ArrayList<>();
        SearchRegion.add("Select Region *");
        SearchCountry = new ArrayList<>();
        SearchCountry.add("Select Country *");
        SearchPinRegion = new ArrayList<>();
        SearchPinRegion.add("Select Type");
        already_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

//        for (int i = 0; i < RegionModelArrayList.size(); i++) {
//            final RegionModel Items = RegionModelArrayList.get(i);
//            SearchRegion.add(Items.getRegionName());
//        }
//
//        for (int i = 0; i < CountryModelArrayList.size(); i++) {
//            final CountryModel Items = CountryModelArrayList.get(i);
//            SearchCountry.add(Items.getCountryName());
//        }
//
//        for (int i = 0; i < UserTypeModelArrayList.size(); i++) {
////            final UserTypeModel Items = UserTypeModelArrayList.get(i);
////            SearchPinRegion.add(Items.getUserTypeName());
//            final UserTypeModel Items = UserTypeModelArrayList.get(i);
//            SearchPinRegion.add(Items.getUserTypeName());
//            if(Items.getUserTypeName().equals("Career Professional")){
//                position = i;
//            }
//
//        }
//        ArrayAdapter<String> userwadapter = new ArrayAdapter<String>(this, R.layout.spinner_text, SearchPinRegion);
//        search_corporation.setAdapter(userwadapter);
//        search_corporation.setSelection(position);
        ArrayAdapter<String> wadapter = new ArrayAdapter<String>(Register.this,R.layout.text,SearchRegion);
        search_region.setAdapter(wadapter);
        ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(this, R.layout.spinner_text, SearchRegion);
        search_region.setAdapter(Regionadapter);
        search_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedRegion = parent.getItemAtPosition(position).toString();

                for(int i = 0; i<RegionModelArrayList.size();i++) {
                    if (RegionModelArrayList.get(i).getRegionName().contains(SelectedRegion)) {
                        RegionModelArrayListTmp.add(RegionModelArrayList.get(i));
                        SelectedRegionId = RegionModelArrayList.get(position - 1).getRegionId();

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> countrywadapter = new ArrayAdapter<String>(Register.this,R.layout.text,SearchCountry);
        search_country.setAdapter(countrywadapter);
        ArrayAdapter<String> Countryadapter = new ArrayAdapter<String>(this, R.layout.spinner_text, SearchCountry);
        search_country.setAdapter(Countryadapter);
        search_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();

                for(int i = 0; i<CountryModelArrayList.size();i++) {
                    if (CountryModelArrayList.get(i).getCountryName().contains(SelectedCountry)) {
                        CountryModelArrayListTmp.add(CountryModelArrayList.get(i));
                        SelectedCountryId = CountryModelArrayList.get(position - 1).getCountryId();

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        ArrayAdapter<String> userwadapter = new ArrayAdapter<String>(Register.this,R.layout.text,SearchPinRegion);
//        search_corporation.setAdapter(userwadapter);
        ArrayAdapter<String> Pinadapter = new ArrayAdapter<String>(this, R.layout.spinner_text, SearchPinRegion);
        search_corporation.setAdapter(Pinadapter);

        search_corporation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedType = parent.getItemAtPosition(position).toString();

                for(int i = 0; i<UserTypeModelArrayList.size();i++) {
                    if (UserTypeModelArrayList.get(i).getUserTypeName().contains(SelectedType)) {
                        UserTypeModelArrayListTmp.add(UserTypeModelArrayList.get(i));
                        SelectedUserTypeId = UserTypeModelArrayList.get(position - 1).getUserTypeId();

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fcmTocken=SharedPreferenceUtils.getInstance(Register.this).getStringValue(CommonUtils.FCMTOCKEN, "");

                String UserFname, UserLname,UserEmail, Userzip, UserPassword, ConfirmPswd;
                UserFname = Fname.getText().toString().trim();
                UserLname = Lname.getText().toString().trim();
                UserEmail = Email.getText().toString().trim();
                Userzip = Zipcode.getText().toString().trim();
                UserPassword = Password.getText().toString().trim();
                ConfirmPswd = CfmPassword.getText().toString().trim();
                if (UserFname.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter your First name", Toast.LENGTH_LONG).show();
                }else if (UserLname.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter your Last name", Toast.LENGTH_LONG).show();
                }else if (UserEmail.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                }else if (UserPassword.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter Password", Toast.LENGTH_LONG).show();
                }else if (ConfirmPswd.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter Confirm Password", Toast.LENGTH_LONG).show();
                }/*else if (Userzip.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter your Zipcode", Toast.LENGTH_LONG).show();
                }*/else if (SelectedRegionId.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please select Region", Toast.LENGTH_LONG).show();
                }else if (SelectedCountryId.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please select Country", Toast.LENGTH_LONG).show();
                }else if (SelectedUserTypeId.equalsIgnoreCase("")){
                    Toast.makeText(Register.this, "Please enter select User Type", Toast.LENGTH_LONG).show();
                }else if (!UserPassword.equalsIgnoreCase(ConfirmPswd)){
                    Toast.makeText(Register.this, "Password and Confirm Password do not match!", Toast.LENGTH_LONG).show();
                }else if(UserPassword.length()<6){
                    Toast.makeText(Register.this, "Minimum length of password should be 6", Toast.LENGTH_LONG).show();
                } else{
                    new PostUpdateAsync().execute(UserFname, UserLname, SelectedRegionId, UserEmail, SelectedCountryId, UserPassword, SelectedUserTypeId,fcmTocken);
//                     RegisterUser(UserFname, UserLname, SelectedRegionId, UserEmail, SelectedCountryId, UserPassword, SelectedUserTypeId,fcmTocken,UserImgPath);


                }

            }
        });
    }

    class PostUpdateAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("fname", params[0]));
                nameValuePairs.add(new BasicNameValuePair("lname", params[1]));
                nameValuePairs.add(new BasicNameValuePair("region_id", params[2]));
                nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                nameValuePairs.add(new BasicNameValuePair("country_id", params[4]));
                nameValuePairs.add(new BasicNameValuePair("password", params[5]));
                nameValuePairs.add(new BasicNameValuePair("confirm_password", params[5]));
                nameValuePairs.add(new BasicNameValuePair("user_type_id", params[6]));
                nameValuePairs.add(new BasicNameValuePair("fcm_token", params[7]));
                if(!UserImgPath.isEmpty()){
                    nameValuePairs.add(new BasicNameValuePair("profile_picture",UserImgPath) );
                }else{
                    nameValuePairs.add(new BasicNameValuePair("profile_picture", ""));
                }
                String Url = (CommonUtils.BASE_URL)+"user_registration";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgessLoad.setVisibility(View.VISIBLE);

        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //{"success":"1","message":"Data saved successfully!"}
            if(jsonObject.has("status")){
                try {
                    String success = jsonObject.getString("status");
                    if (success.equalsIgnoreCase("success")) {
                        ProgessLoad.setVisibility(View.GONE);
                        preferances.setValue(CommonUtils.MEMBER_ID, json.getString("userid"));
                        preferances.setValue(CommonUtils.NAME, json.getString("fname") + " " +json.getString("lname"));
                        preferances.setValue(CommonUtils.MEMBEREMAIL, json.getString("email"));
                        startActivity(new Intent(Register.this,MainActivity.class));
                    } else {
                        ProgessLoad.setVisibility(View.GONE);
                        Toast.makeText(Register.this, json.getString("message"), Toast.LENGTH_LONG).show();
//                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Register.this);
//                        builder.create();
//                        builder.setMessage("Opps! Some problem occured while registration, please try again after some time.");
//                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                startActivity(new Intent(Register.this,Register.class));
//                            }
//                        });
//                        builder.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                ProgessLoad.setVisibility(View.GONE);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Register.this);
                        builder.create();
                        builder.setMessage("Opps! Some problem occured while registration, please try again after some time.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(Register.this,Register.class));
                            }
                        });
                        builder.show();
            }

            }

        }

    private void init() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter(Register.this,XMENArray));
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
//    public void RegisterUser(String Fname, String LName, String Region, String Email, String Country, String Password, String UserType) {
//        ProgessLoad.setVisibility(View.VISIBLE);
//        fcmTocken = preferances.getStringValue(CommonUtils.FCMTOCKEN,"");
//        String url = (CommonUtils.BASE_URL)+"user_registration";
//        OkHttpClient client = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("fname", Fname)
//                .add("lname", LName)
//                .add("region_id", Region)
//                .add("country_id", Country)
//                .add("email", Email)
//                .add("password", Password)
//                .add("confirm_password", Password)
//                .add("user_type_id", UserType)
//                .add("fcm_token", fcmTocken)
//                .add(" profile_picture",UserImgPath)
//                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(formBody)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                call.cancel();
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                ResponseBody responseBody = response.body();
//                final String myResponse = responseBody.string();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject json = null;
//                        try {
//                            json = new JSONObject(myResponse);
//
//                            String success = json.getString("status");
//
//                            if (success.equalsIgnoreCase("success")) {
//                                ProgessLoad.setVisibility(View.GONE);
//                                preferances.setValue(CommonUtils.MEMBER_ID, json.getString("userid"));
//                                preferances.setValue(CommonUtils.NAME, json.getString("fname") + " " +json.getString("lname"));
//                                preferances.setValue(CommonUtils.MEMBEREMAIL, json.getString("email"));
//                                startActivity(new Intent(Register.this,MainActivity.class));
//                            }
//                            else {
//                                ProgessLoad.setVisibility(View.GONE);
//                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Register.this);
//                                builder.create();
//                                builder.setMessage("Opps! Some problem occured while registration, please try again after some time.");
//                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        startActivity(new Intent(Register.this,Register.class));
//                                    }
//                                });
//                                builder.show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }
    public void getRegionDetails() {

        RegionModelArrayList.clear();

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
                                    RegionModelArrayList.add(model);
                                }
                                for (int i = 0; i < RegionModelArrayList.size(); i++) {
                                    final RegionModel Items = RegionModelArrayList.get(i);
                                    SearchRegion.add(Items.getRegionName());

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

        CountryModelArrayList.clear();

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
                                    CountryModelArrayList.add(model);
                                }
                                for (int i = 0; i < CountryModelArrayList.size(); i++) {
                                    final CountryModel Items = CountryModelArrayList.get(i);
                                    SearchCountry.add(Items.getCountryName());

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

        UserTypeModelArrayList.clear();

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
                                    UserTypeModelArrayList.add(model);
                                }
                                for (int i = 0; i < UserTypeModelArrayList.size(); i++) {
                                    final UserTypeModel Items = UserTypeModelArrayList.get(i);
                                    SearchPinRegion.add(Items.getUserTypeName());
                                    if(Items.getUserTypeName().equals("Career Professional")){
                                        position=i+1;
                                    }
                                }

                                ArrayAdapter<String> Pinadapter = new ArrayAdapter<String>(Register.this, R.layout.spinner_text, SearchPinRegion);
                                search_corporation.setAdapter(Pinadapter);
                                search_corporation.setSelection(position);

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
    public void onMultiImageSelected(List<Uri> uriList, String tag) {


        for (int i = 0; i < uriList.size(); i++) {

            Uri selectedImage = uriList.get(i);
            File imageFile = new File(getRealPathFromURI(selectedImage));
            UserImgPath = String.valueOf(imageFile);

            if (UserImgPath.contains("file:")) {
                Glide.with(Register.this).load(UserImgPath).into(profile_pic);
            } else if (!UserImgPath.contains("file:")) {
                Glide.with(Register.this).load("file:"+ UserImgPath).into(profile_pic);
            }

        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = getApplicationContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }





}
