package com.uabn.gss.uabnlink.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.uabn.gss.uabnlink.Activities.PathUtil;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.MembersAdapter;
import com.uabn.gss.uabnlink.adapter.PBCRDocumentAdapter;
import com.uabn.gss.uabnlink.fragment.PostFragment;
import com.uabn.gss.uabnlink.model.CategoryModel;
import com.uabn.gss.uabnlink.model.DocumentModel;
import com.uabn.gss.uabnlink.model.IndustryModel;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.uabn.gss.uabnlink.model.RegionModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EditPBCRActivity extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener {
    DatePickerDialog datePickerDialog;
    BSImagePicker pickerDialog;
    String doc_id,str_assistance;
    ProgressBar pb;
    String str_subject, str_description, str_appoint, str_contact_person = "", str_contact_email = "", str_contact_email2 = "", str_home_cell = "", str_tel_business = "";
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<DocumentModel> DocumentModelArrayList;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<String> DocumentList = new ArrayList<String>();
    ArrayList<RegionModel> RegionModelArrayList = new ArrayList<>();
    ArrayList<RegionModel> RegionModelArrayListTmp = new ArrayList<>();
    ArrayList<String> SearchRegion = new ArrayList<>();
    ArrayList<String> searchIndustry = new ArrayList<>();
    ArrayList<String> searchCategories = new ArrayList<>();
    ArrayList<CategoryModel> CategoryModelArrayList = new ArrayList<>();
    ArrayList<CategoryModel> CategoryModelArrayListTmp = new ArrayList<>();
    ArrayList<IndustryModel> IndustryModelArrayList = new ArrayList<>();
    ArrayList<IndustryModel> IndustryModelArrayListTmp = new ArrayList<>();
    RadioButton assistance, new_business;
    ImageView pbcr_img,cover_pic;
    Button save, Cancel;
    SharedPreferenceUtils preferances;
    TextView pbcr_text, select_img, select_doc, SelectedDoc;
    EditText  contact_person, contact_email, contact_email2, home_cell, tel_business, description, subject, appointment_date;
    String user_id, pbcr_id;
    Spinner search_region, search_industry, search_category;
    String SelectedRegionId = "", SelectedCategoryId = "", SelectedIndustryId = "", DocPath = "";
    private RecyclerView cover_img,pbcr_doc_list;
    private RecyclerView.Adapter mAdapter,pbcrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pbcr);
        pbcr_img = findViewById(R.id.pbcr_img);
        cover_img = findViewById(R.id.cover_img);
        save = findViewById(R.id.save);
        Cancel = findViewById(R.id.Cancel);
        SelectedDoc = findViewById(R.id.SelectedDoc);
        search_region = findViewById(R.id.search_region);
        search_industry = findViewById(R.id.search_industry);
        search_category = findViewById(R.id.search_category);
        assistance = findViewById(R.id.assistance);
        new_business = findViewById(R.id.new_business);
        pbcr_text = findViewById(R.id.pbcr_text);
        select_img = findViewById(R.id.select_img);
        select_doc = findViewById(R.id.select_doc);
        pbcr_doc_list=findViewById(R.id.pbcr_doc);
        pb=findViewById(R.id.pb);
        contact_person = findViewById(R.id.contact_person);
        contact_email = findViewById(R.id.contact_email);
        contact_email2 = findViewById(R.id.contact_email2);
        home_cell = findViewById(R.id.home_cell);
        tel_business = findViewById(R.id.tel_business);
        description = findViewById(R.id.description);
        subject = findViewById(R.id.subject);
        cover_pic=findViewById(R.id.cover_pic);
        appointment_date = findViewById(R.id.appointment_date);
        preferances = SharedPreferenceUtils.getInstance(this);
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        pbcr_id = getIntent().getStringExtra("pbcr_id");
        DocumentModelArrayList=new ArrayList<>();
        cover_img.setHasFixedSize(true);
        cover_img.setLayoutManager(new GridLayoutManager(EditPBCRActivity.this, 1));
        cover_img.setItemAnimator(new DefaultItemAnimator());
//        pbcr_doc_list.setHasFixedSize(true);
//        pbcr_doc_list.setLayoutManager(new GridLayoutManager(EditPBCRActivity.this, 3));
        pbcr_doc_list.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        pbcr_doc_list.setLayoutManager(linearLayoutManager);
        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(1)
                        .build();
                pickerDialog.setCancelable(false);

                pickerDialog.show(getSupportFragmentManager(), "Picker");


            }
        });

        appointment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(EditPBCRActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                appointment_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        select_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Choose file"), 100);

            }
        });

        getCategoriesDetails();
        getIndustriesDetails();
        RegionModelArrayList = CommonUtils.REGIONARRAYLIST;

        SearchRegion = new ArrayList<>();
        SearchRegion.add("Select Region");

        searchIndustry = new ArrayList<>();
        searchIndustry.add("Select Industry");

        searchCategories = new ArrayList<>();
        searchCategories.add("Select Categories");
        for (int i = 0; i < RegionModelArrayList.size(); i++) {
            final RegionModel Items = RegionModelArrayList.get(i);
            SearchRegion.add(Items.getRegionName());
        }
        ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(EditPBCRActivity.this, R.layout.spinner_text, SearchRegion);
        search_region.setAdapter(Regionadapter);
        search_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedRegion = parent.getItemAtPosition(position).toString();

                for (int i = 0; i < RegionModelArrayList.size(); i++) {
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


        ArrayAdapter<String> CategoryAdapter = new ArrayAdapter<String>(EditPBCRActivity.this, R.layout.spinner_text, searchCategories);
        search_category.setAdapter(CategoryAdapter);
        search_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCategory = parent.getItemAtPosition(position).toString();

                for (int i = 0; i < CategoryModelArrayList.size(); i++) {
                    if (CategoryModelArrayList.get(i).getCategoryName().contains(SelectedCategory)) {

                        CategoryModelArrayListTmp.add(CategoryModelArrayList.get(i));

                        SelectedCategoryId = CategoryModelArrayList.get(position - 1).getCategoryId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayAdapter<String> IndustryAdapter = new ArrayAdapter<String>(EditPBCRActivity.this, R.layout.spinner_text, searchIndustry);
        search_industry.setAdapter(IndustryAdapter);
        search_industry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedIndustry = parent.getItemAtPosition(position).toString();

                for (int i = 0; i < IndustryModelArrayList.size(); i++) {
                    if (IndustryModelArrayList.get(i).getIndustryName().contains(SelectedIndustry)) {
                        IndustryModelArrayListTmp.add(IndustryModelArrayList.get(i));
                        SelectedIndustryId = IndustryModelArrayList.get(position - 1).getIndustryId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (assistance.isChecked() == true) {
            str_assistance = "1";
        } else {
            str_assistance = "0";
        }
        editPBCRDetails(user_id, pbcr_id);
save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        str_subject=subject.getText().toString().trim();
        str_description=description.getText().toString().trim();
        str_appoint=appointment_date.getText().toString().trim();
        str_contact_person=contact_person.getText().toString().trim();
        str_contact_email=contact_email.getText().toString().trim();
        str_contact_email2=contact_email2.getText().toString().trim();
        str_home_cell=home_cell.getText().toString().trim();
        str_tel_business=tel_business.getText().toString().trim();
//        str_subject=subject.getText().toString().trim();
//        str_subject=subject.getText().toString().trim();
//        str_subject=subject.getText().toString().trim();
//        str_subject=subject.getText().toString().trim();
//        str_subject=subject.getText().toString().trim();
//








        new updatePBCRAsync().execute(user_id, str_appoint, SelectedIndustryId, str_subject, SelectedCategoryId,str_description,SelectedRegionId,str_contact_person,str_contact_email,str_contact_email2,str_home_cell,str_tel_business,pbcr_id,str_assistance);
    }
});

    }

    public void getCategoriesDetails() {


        String url = (CommonUtils.BASE_URL)+"getServices";


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

                                JSONArray json2 = json.getJSONArray("services");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("category");

                                    CategoryModel model = new CategoryModel();
                                    model.setCategoryId(RegionObj.getString("id"));
                                    model.setCategoryName(RegionObj.getString("name"));
                                    model.setCategoryStatus(RegionObj.getString("status"));

                                    CategoryModelArrayList.add(model);
                                }
                                for (int i = 0; i < CategoryModelArrayList.size(); i++) {
                                    final CategoryModel Items = CategoryModelArrayList.get(i);
                                    searchCategories.add(Items.getCategoryName());
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

    public void getIndustriesDetails() {


        String url = (CommonUtils.BASE_URL) + "getIndustries";
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

                                JSONArray json2 = json.getJSONArray("services");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("category");
                                    IndustryModel model = new IndustryModel();
                                    model.setIndustryId(RegionObj.getString("id"));
                                    model.setIndustryName(RegionObj.getString("name"));
                                    model.setIndustryStatus(RegionObj.getString("status"));
                                    IndustryModelArrayList.add(model);
                                }
                                for (int i = 0; i < IndustryModelArrayList.size(); i++) {
                                    final IndustryModel Items = IndustryModelArrayList.get(i);
                                    searchIndustry.add(Items.getIndustryName());
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

    public void editPBCRDetails(final String user_id, final String pbcr_id) {
        String url = (CommonUtils.BASE_URL) + "edit_pbcr_post";

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("pbcr_id", pbcr_id)
                .add("user_id", user_id)
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
                                            String image_path = json.getString("image_path");
                                            String doc_path = json.getString("doc_path");
                                            String success = json.getString("status");
                                            if (success.equalsIgnoreCase("success")) {
                                                JSONObject json1 = json.getJSONObject("pbcr_data");
                                                JSONObject json2 = json1.getJSONObject("Mar");

                                                JSONObject json5 = json1.getJSONObject("Region");

JSONArray userMarDocument=json1.getJSONArray("UserMarDocument");
   if(userMarDocument.length()>=0){
//       cross1.setVisibility(View.VISIBLE);
//       cross2.setVisibility(View.VISIBLE);
       for(int i=0;i<userMarDocument.length();i++){
           JSONObject UpdateDetails = userMarDocument.getJSONObject(i);
//           doc1.setText(UpdateDetails.getString("name"));
//           doc2.setText(UpdateDetails.getString("name"));
           DocumentModel doc= new DocumentModel();
           doc.setDoc_id(UpdateDetails.getString("id"));
           doc.setDoc_name(UpdateDetails.getString("name"));

           DocumentModelArrayList.add(doc);
           pbcrAdapter= new PBCRDocumentAdapter(EditPBCRActivity.this,DocumentModelArrayList,user_id,pbcr_id);

           pbcr_doc_list.setAdapter(pbcrAdapter);
           doc_id=UpdateDetails.getString("id");

       }
   }
                                                Glide.with(EditPBCRActivity.this).load(image_path+pbcr_id+"/"+json2.getString("image")).into(cover_pic);

                                                Glide.with(EditPBCRActivity.this).load(image_path+pbcr_id+"/"+json2.getString("image")).into(pbcr_img);
  if (json2.getString("reason").equalsIgnoreCase("0")) {
                                                    assistance.setChecked(true);
                                                } else {
                                                    new_business.setChecked(true);
                                                }
                                                subject.setText(json2.getString("item_title").replace("null", ""));

                                                tel_business.setText(json2.getString("contact_business").replace("null", ""));

                                                home_cell.setText(json2.getString("contact_phone").replace("null", ""));

                                                appointment_date.setText(json2.getString("appoinment_date").replace("null", ""));
//                                                subject.setText(json2.getString("appoinment_time"));
                                                description.setText(json2.getString("item_desc").replace("null", ""));
                                                contact_person.setText(json2.getString("contact_person").replace("null", ""));
//                                                appointment_date.setText(json2.getString("appoinment_time"));
                                                contact_email.setText(json2.getString("contact_email").replace("null", ""));
                                                contact_email2.setText(json2.getString("contact_email2").replace("null", ""));
//                                                home_cell.setText(json2.getString("appoinment_time"));
//                                                tel_business.setText(json2.getString("appoinment_time"));

                                                ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(EditPBCRActivity.this, R.layout.spinner_text, SearchRegion);
                                                search_region.setAdapter(Regionadapter);
                                                search_region.setSelection(Integer.parseInt(json5.getString("id")) - 1);
                                                search_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        String SelectedRegion = parent.getItemAtPosition(position).toString();

                                                        for (int i = 0; i < RegionModelArrayList.size(); i++) {
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


                                                ArrayAdapter<String> CategoryAdapter = new ArrayAdapter<String>(EditPBCRActivity.this, R.layout.spinner_text, searchCategories);
                                                search_category.setAdapter(CategoryAdapter);
                                                search_category.setSelection(Integer.parseInt(json5.getString("id")) - 1);

                                                search_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        String SelectedCategory = parent.getItemAtPosition(position).toString();

                                                        for (int i = 0; i < CategoryModelArrayList.size(); i++) {
                                                            if (CategoryModelArrayList.get(i).getCategoryName().contains(SelectedCategory)) {

                                                                CategoryModelArrayListTmp.add(CategoryModelArrayList.get(i));

                                                                SelectedCategoryId = CategoryModelArrayList.get(position - 1).getCategoryId();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {
                                                    }
                                                });


                                                ArrayAdapter<String> IndustryAdapter = new ArrayAdapter<String>(EditPBCRActivity.this, R.layout.spinner_text, searchIndustry);
                                                search_industry.setAdapter(IndustryAdapter);
                                                search_industry.setSelection(Integer.parseInt(json2.getString("key_industry")) - 1);


                                                search_industry.setSelection(Integer.parseInt(json5.getString("id")) - 1);

                                                search_industry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        String SelectedIndustry = parent.getItemAtPosition(position).toString();

                                                        for (int i = 0; i < IndustryModelArrayList.size(); i++) {
                                                            if (IndustryModelArrayList.get(i).getIndustryName().contains(SelectedIndustry)) {
                                                                IndustryModelArrayListTmp.add(IndustryModelArrayList.get(i));
                                                                SelectedIndustryId = IndustryModelArrayList.get(position - 1).getIndustryId();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }
                                                });


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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {

            String displayName = null;
            StringBuilder fileName = new StringBuilder();

            if (data != null) {

                if (data.getClipData() != null) {

                    for (int index = 0; index < data.getClipData().getItemCount(); index++) {

                        Uri uri = data.getClipData().getItemAt(index).getUri();
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        String path = myFile.getAbsolutePath();


                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        if ("primary".equalsIgnoreCase(type)) {
                            String a = Environment.getExternalStorageDirectory() + "/"
                                    + split[1];
                            DocumentList.add(a);

                            Log.i("Path", "onActivityResult: " + a);
                        }


                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = EditPBCRActivity.this.getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();

                        }
                        SelectedDoc.setVisibility(View.VISIBLE);

                        DocPath = path + "/" + displayName;
                        Log.d("Path", myFile.getAbsolutePath());
                        fileName.append(displayName).append("\n");
                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
                    }
                    SelectedDoc.setText(fileName);

                } else {

                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();

                    String result = myFile.getPath();


                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        String a = Environment.getExternalStorageDirectory() + "/"
                                + split[1];

                        Log.i("Path", "onActivityResult: " + a);
                    }

                    try {
                        String mypath = PathUtil.getPath(EditPBCRActivity.this, uri);
                        DocumentList.add(mypath);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }


                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = EditPBCRActivity.this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();

                    }
                    SelectedDoc.setVisibility(View.VISIBLE);

                    DocPath = path + "/" + displayName;
                    Log.d("Path", myFile.getAbsolutePath());
                    fileName.append(displayName);
                    SelectedDoc.setText(fileName);

                    Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));

                    Log.d("fileUri: ", String.valueOf(uri));
                }
            }
        } else if (requestCode == 200) {

            String imageEncoded = "";
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    // Get the cursor
                    Cursor cursor = EditPBCRActivity.this.getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    ImagesList.add(imageEncoded);
                    cursor.close();

                }
            }




            if (ImagesList.size() == 0) {
                cover_pic.setVisibility(View.VISIBLE);

                cover_img.setVisibility(View.GONE);
            } else {
                cover_img.setVisibility(View.VISIBLE);
                cover_pic.setVisibility(View.GONE);
                mAdapter = new MultipleImagesAdapter(EditPBCRActivity.this, ImagesList);
                cover_img.setAdapter(mAdapter);

            }



        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = EditPBCRActivity.this.getContentResolver().query(contentUri, null, null, null, null);
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


    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        for (int i = 0; i < uriList.size(); i++) {

            Uri selectedImage = uriList.get(i);
            File imageFile = new File(getRealPathFromURI(selectedImage));
            ImagesList.add(String.valueOf(imageFile));

        }

        if (ImagesList.size() == 0) {
            cover_pic.setVisibility(View.VISIBLE);
            cover_img.setVisibility(View.GONE);
        } else {
            cover_pic.setVisibility(View.GONE);
            cover_img.setVisibility(View.VISIBLE);
            mAdapter = new MultipleImagesAdapter(EditPBCRActivity.this, ImagesList);
            cover_img.setAdapter(mAdapter);

        }


    }

    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleImagesList;

        public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList) {
            context1 = context;
            MultipleImagesList = ImagesList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_listitem, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            if (MultipleImagesList.get(position).contains("file:")) {
                Glide.with(context1).load(MultipleImagesList.get(position)).into(holder.adapterImage);
            } else {
                Glide.with(context1).load("file:" + MultipleImagesList.get(position)).into(holder.adapterImage);
            }

        }

        @Override
        public int getItemCount() {
            return MultipleImagesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adapterImage;

            public MyViewHolder(View v) {
                super(v);
                adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
            }
        }
    }

    class updatePBCRAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;


        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("appoinment_date", params[1]));
                nameValuePairs.add(new BasicNameValuePair("mar_industry", params[2]));
                nameValuePairs.add(new BasicNameValuePair("item_title", params[3]));
                nameValuePairs.add(new BasicNameValuePair("core_expertise", params[4]));
                nameValuePairs.add(new BasicNameValuePair("item_desc", params[5]));
                nameValuePairs.add(new BasicNameValuePair("region", params[6]));
                nameValuePairs.add(new BasicNameValuePair("contact_person", params[7]));
                nameValuePairs.add(new BasicNameValuePair("contact_email", params[8]));
                nameValuePairs.add(new BasicNameValuePair("contact_email2", params[9]));
                nameValuePairs.add(new BasicNameValuePair("contact_phone", params[10]));
                nameValuePairs.add(new BasicNameValuePair("contact_business", params[11]));
                nameValuePairs.add(new BasicNameValuePair("pbcr_id", params[12]));
                nameValuePairs.add(new BasicNameValuePair("status", params[13]));



                int Imagecount = 0, DocCount = 0;
                for (int i=0;i<ImagesList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("image"+(Imagecount+1), ImagesList.get(i)));
                    Imagecount++;
                }


                for (int i=0;i<DocumentList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("path"+(DocCount+1), DocumentList.get(i)));
                    DocCount++;
                }

                Log.d("datap", nameValuePairs.toString());

                String Url = (CommonUtils.BASE_URL)+"update_pbcr";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //{"success":"1","message":"Data saved successfully!"}

            try {
                String success = json.getString("status");


                if (success.equalsIgnoreCase("success")) {

                    Toast.makeText(EditPBCRActivity.this, "Update posted successfully!",
                            Toast.LENGTH_LONG).show();

                    pb.setVisibility(View.GONE);
                    editPBCRDetails(user_id, pbcr_id);


                } else {

                    Toast.makeText(EditPBCRActivity.this, "Opps! Some problem occured while posting your update, please ensure your location services are on at the highest accuracy.",
                            Toast.LENGTH_LONG).show();

                    pb.setVisibility(View.GONE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    }



