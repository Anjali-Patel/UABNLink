package com.uabn.gss.uabnlink.fragment;


import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.uabn.gss.uabnlink.Activities.PathUtil;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.CategoryModel;
import com.uabn.gss.uabnlink.model.IndustryModel;
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


public class AddPBCRFragment extends Fragment implements BSImagePicker.OnMultiImageSelectedListener {
    DatePickerDialog datePickerDialog;
    ArrayList<String> SearchRegion = new ArrayList<>();
    ArrayList<String> searchIndustry = new ArrayList<>();
    ArrayList<String> searchCategories = new ArrayList<>();
    String str_assistance, str_subject, str_description, str_appoint, str_contact_person = "", str_contact_email = "", str_contact_email2 = "", str_home_cell = "", str_tel_business = "";
    ArrayList<RegionModel> RegionModelArrayList;
    ArrayList<RegionModel> RegionModelArrayListTmp = new ArrayList<>();
    String user_id;
    ImageView img;
    LinearLayout assistance_pbcr, new_business_req;
    RadioGroup RadiGroupPBCR;

    private RecyclerView.Adapter mAdapter;
    BSImagePicker pickerDialog;
    TextView document, picture, SelectedDoc;
    String DocPath = "";
    ArrayList<String> ImagesList = new ArrayList<String>();
    ProgressBar progess_load;
    private RecyclerView mRecyclerView;
    ArrayList<String> DocumentList = new ArrayList<String>();
    SharedPreferenceUtils preferances;
    ArrayList<CategoryModel> CategoryModelArrayList = new ArrayList<>();
    ArrayList<CategoryModel> CategoryModelArrayListTmp = new ArrayList<>();
    ArrayList<IndustryModel> IndustryModelArrayList = new ArrayList<>();
    ArrayList<IndustryModel> IndustryModelArrayListTmp = new ArrayList<>();
    Spinner search_region, search_industry, search_category;
    EditText description, subject, appointment_date;
    RadioButton assistance, new_business;
    Button ShareButton;
    EditText contact_person, contact_email, contact_email2, home_cell, tel_business;
    String SelectedRegionId = "", SelectedCategoryId = "", SelectedIndustryId = "";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AddPBCRFragment() {
    }

    public static AddPBCRFragment newInstance(String param1, String param2) {
        AddPBCRFragment fragment = new AddPBCRFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pbcr, container, false);
        search_region = view.findViewById(R.id.search_region);
        search_industry = view.findViewById(R.id.search_industry);
        search_category = view.findViewById(R.id.search_category);
        description = view.findViewById(R.id.description);
        subject = view.findViewById(R.id.subject);
        contact_person = view.findViewById(R.id.contact_person);
        contact_email = view.findViewById(R.id.contact_email);

        contact_email2 = view.findViewById(R.id.contact_email2);

        home_cell = view.findViewById(R.id.home_cell);
        tel_business = view.findViewById(R.id.tel_business);
//        contact_person=view.findViewById(R.id.contact_person);
        assistance = view.findViewById(R.id.assistance);
        new_business = view.findViewById(R.id.new_business);
        ShareButton = view.findViewById(R.id.ShareButton);
        SelectedDoc = view.findViewById(R.id.SelectedDoc);
        progess_load = view.findViewById(R.id.progess_load);
        mRecyclerView = view.findViewById(R.id.Images_listview);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        appointment_date = view.findViewById(R.id.appointment_date);
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        picture = view.findViewById(R.id.picture);
        assistance_pbcr = view.findViewById(R.id.assistance_pbcr);
        new_business_req = view.findViewById(R.id.new_business_req);
        RadiGroupPBCR = view.findViewById(R.id.RadiGroupPBCR);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        RadiGroupPBCR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){

                    case R.id.assistance:
                        assistance_pbcr.setVisibility(View.VISIBLE);
                        new_business_req.setVisibility(View.GONE);
                        break;

                    case R.id.new_business:
                        assistance_pbcr.setVisibility(View.GONE);
                        new_business_req.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });



//        img = view.findViewById(R.id.iv);
        document = view.findViewById(R.id.document);
        document.setOnClickListener(new View.OnClickListener() {
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
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(1)
                        .build();
                pickerDialog.setCancelable(false);

                pickerDialog.show(getChildFragmentManager(), "Picker");
            }
        });
        getCategoriesDetails();
        getIndustriesDetails();
        RegionModelArrayList = CommonUtils.REGIONARRAYLIST;
        SearchRegion = new ArrayList<>();
        SearchRegion.add("Select Region *");
        searchIndustry = new ArrayList<>();
        searchIndustry.add("Select Industry *");
        searchCategories = new ArrayList<>();
        searchCategories.add("Select Categories *");
        for (int i = 0; i < RegionModelArrayList.size(); i++) {
            final RegionModel Items = RegionModelArrayList.get(i);
            SearchRegion.add(Items.getRegionName());
        }
        ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, SearchRegion);
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


        ArrayAdapter<String> CategoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, searchCategories);
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


        ArrayAdapter<String> IndustryAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, searchIndustry);
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
            str_assistance = "assistance";
        } else {
            str_assistance = "New Business";
        }
        appointment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
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


        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addPBCR(user_id, str_assistance, str_subject, str_appoint, str_description, SelectedIndustryId, SelectedCategoryId, SelectedRegionId);

//                str_contact_person=contact_person.getText().toString().trim();
//                str_contact_email=contact_email.getText().toString().trim();
//                str_contact_email2=contact_email2.getText().toString().trim();
//                str_home_cell=home_cell.getText().toString().trim();
//                str_tel_business=tel_business.getText().toString().trim();
                str_description = description.getText().toString().trim();
                str_subject = subject.getText().toString().trim();
                str_appoint = appointment_date.getText().toString().trim();

                if (!(contact_person.getText().toString().equalsIgnoreCase(""))) {
                    str_contact_person = contact_person.getText().toString();
                }
                if (!(contact_email.getText().toString().equalsIgnoreCase(""))) {
                    str_contact_email = contact_email.getText().toString();
                }
                if (!(contact_email2.getText().toString().equalsIgnoreCase(""))) {
                    str_contact_email2 = contact_email2.getText().toString();
                }
                if (!(home_cell.getText().toString().equalsIgnoreCase(""))) {
                    str_home_cell = home_cell.getText().toString();
                }
                if (!(tel_business.getText().toString().equalsIgnoreCase(""))) {
                    str_tel_business = tel_business.getText().toString();
                }

                if (str_assistance.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select any one item", Toast.LENGTH_LONG).show();
                } else if (str_subject.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Subject field", Toast.LENGTH_LONG).show();
                } else if (str_description.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Description Field", Toast.LENGTH_LONG).show();
                } else if (SelectedIndustryId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select any one Industry", Toast.LENGTH_LONG).show();
                } else if (SelectedCategoryId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select any one Category", Toast.LENGTH_LONG).show();
                } else if (SelectedRegionId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select any one Region", Toast.LENGTH_LONG).show();
                } else if (str_appoint.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select Appointment Date", Toast.LENGTH_LONG).show();
                } else {
//                    addPBCR(user_id, str_assistance, str_subject, str_appoint, str_description, SelectedIndustryId, SelectedCategoryId, SelectedRegionId, str_contact_person, str_contact_email, str_contact_email2, str_home_cell, str_tel_business);


                    new PostPBCRAsync(getContext()).execute(user_id, str_assistance, str_appoint, SelectedIndustryId, str_subject, SelectedCategoryId, str_description, SelectedRegionId, str_contact_person, str_contact_email, str_contact_email2, str_home_cell, str_tel_business);


                }
            }
        });

        return view;
    }

    public void getCategoriesDetails() {


        String url = (CommonUtils.BASE_URL) + "getServices";


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

                getActivity().runOnUiThread(new Runnable() {
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

                getActivity().runOnUiThread(new Runnable() {
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

    public void addPBCR(String user_id, String str_assistance, String str_subject, String str_appoint, String str_description, String SelectedIndustryId, String SelectedCategoryId, String SelectedRegionId,String str_contact_person,String str_contact_email,String str_contact_email2,String str_home_cell,String str_tel_business)
     {
        String url = (CommonUtils.BASE_URL) + "add_pbcr_post";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("mar_reason", str_assistance)
                .add("appoinment_date", str_appoint)
                .add("mar_industry", SelectedIndustryId)
                .add("item_title", str_subject)
                .add("core_expertise", SelectedCategoryId)
                .add("item_desc", str_description)
                .add("region", SelectedRegionId)
                .add("contact_person", str_contact_person)
                .add("contact_email", str_contact_email)
                .add("contact_email2", str_contact_email2)
                .add("contact_phone", str_home_cell)
                .add("contact_business", str_tel_business)
                .add("image", "")
                .add("path", "")
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
                                                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }


                                                    }
                                                });
                                            }
                                        }
        );
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

                        try {
                            String mypath = PathUtil.getPath(getActivity(), uri);
                            DocumentList.add(mypath);

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }


                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
                        String mypath = PathUtil.getPath(getActivity(), uri);
                        DocumentList.add(mypath);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }


                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
        }   else if (requestCode == 200) {

            String imageEncoded="";
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    ImagesList.add(imageEncoded);
                    cursor.close();

                }
            }


            //File file = new File(getRealPathFromURI(selectedImage));
            // ImagesList.add(imgDecodableString);


            if (ImagesList.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter = new MultipleImagesAdapter(getActivity(), ImagesList);
                mRecyclerView.setAdapter(mAdapter);

            }



        }




        super.onActivityResult(requestCode, resultCode, data);


    }
    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
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

        ImagesList.clear();

        for (int i = 0; i < uriList.size(); i++) {

            Uri selectedImage = uriList.get(i);
            File imageFile = new File(getRealPathFromURI(selectedImage));
            ImagesList.add(String.valueOf(imageFile));

        }

        if (ImagesList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new MultipleImagesAdapter(getActivity(), ImagesList);
            mRecyclerView.setAdapter(mAdapter);
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
                Picasso.with(context1).load(MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
            } else {
                Picasso.with(context1).load("file:" + MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
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
        class PostPBCRAsync extends AsyncTask<String, String, JSONObject> {
            JSONObject json;

            public PostPBCRAsync(Context context) {


            }

            @Override
            protected JSONObject doInBackground(String... params) {
                try {


                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                    nameValuePairs.add(new BasicNameValuePair("mar_reason", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("appoinment_date", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("mar_industry", params[3]));
                    nameValuePairs.add(new BasicNameValuePair("item_title", params[4]));
                    nameValuePairs.add(new BasicNameValuePair("core_expertise", params[5]));
                    nameValuePairs.add(new BasicNameValuePair("item_desc", params[6]));
                    nameValuePairs.add(new BasicNameValuePair("region", params[7]));
                    nameValuePairs.add(new BasicNameValuePair("contact_person", params[8]));
                    nameValuePairs.add(new BasicNameValuePair("contact_email", params[9]));
                    nameValuePairs.add(new BasicNameValuePair("contact_email2", params[10]));
                    nameValuePairs.add(new BasicNameValuePair("contact_phone", params[11]));
                    nameValuePairs.add(new BasicNameValuePair("contact_business", params[12]));
                    nameValuePairs.add(new BasicNameValuePair("image", ImagesList.get(0)));

//                    nameValuePairs.add(new BasicNameValuePair("image", params[13]));
//                    nameValuePairs.add(new BasicNameValuePair("path", params[14]));

                    int DocCount = 0;
                    for (int i = 0; i < DocumentList.size(); i++) {
                        nameValuePairs.add(new BasicNameValuePair("path" + (DocCount + 1), DocumentList.get(i)));
                        DocCount++;
                    }


                    Log.d("datap", nameValuePairs.toString());

                    String Url = (CommonUtils.BASE_URL)+"add_pbcr_post";
                    json = RestJsonClient.post2(Url, nameValuePairs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return json;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            progess_load.setVisibility(View.VISIBLE);
            }




            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                progess_load.setVisibility(View.GONE);

                try {
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

