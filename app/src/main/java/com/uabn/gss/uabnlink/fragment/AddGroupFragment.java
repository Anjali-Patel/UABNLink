package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.uabn.gss.uabnlink.Activities.GroupDetails;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.CountryWiseStateModel;
import com.uabn.gss.uabnlink.model.GroupDocModel;
import com.uabn.gss.uabnlink.model.GroupTypeModel;
import com.uabn.gss.uabnlink.model.GroupaudioModel;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class AddGroupFragment extends Fragment implements BSImagePicker.OnMultiImageSelectedListener {
    private OnFragmentInteractionListener mListener;
    EditText groupname, groupdesc, city, zip, website, embedded_video;
    Spinner group_type, group_country, group_state, access_type, group_status;
    CheckBox check_photo, check_comment, check_doc;
    TextView SelectPicture, Heading, SelectBanner;
    Button SaveButton, UpdateButton;
    ImageView banner_image;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<String> PostedImagesList = new ArrayList<String>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<String> ListGroupType;
    ArrayList<String> GroupStatus;
    ArrayList<String> ListCountry;
    ArrayList<String> ListState;
    ArrayList<String> AccessType;
    ArrayList<GroupTypeModel> GroupTypeModelArrayList;
    ArrayList<CountryModel> CountryModelArrayList;
    ArrayList<CountryWiseStateModel> StateModelArrayList;

    String SelectedCountryId = "", SelectedStateId = "", GruopTypeId = "", Status = "", AccessTypeSelected = "";

    BSImagePicker pickerDialog;
    ProgressBar progess_load;

    SharedPreferenceUtils preferances;
    String UserId, GroupType, GroupId;

    String CountryString, StateString,  GroupTypeString, GroupImagePath = "";
    String BannerImagePath = "";



    public AddGroupFragment() {
        // Required empty public constructor
    }

    public static AddGroupFragment newInstance(String param1, String param2) {
        AddGroupFragment fragment = new AddGroupFragment();
        Bundle args = new Bundle();
        args.putString("Title", param1);
        args.putString("GroupId", param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add_group, container, false);

        preferances= SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");

        Heading= view.findViewById(R.id.Heading);

        groupname = view.findViewById(R.id.groupname);
        groupdesc = view.findViewById(R.id.groupdesc);
        city = view.findViewById(R.id.city);
        zip = view.findViewById(R.id.zip);
        website = view.findViewById(R.id.website);
        embedded_video = view.findViewById(R.id.embedded_video);

        group_type = view.findViewById(R.id.group_type);
        group_country = view.findViewById(R.id.group_country);
        group_state = view.findViewById(R.id.group_state);
        access_type = view.findViewById(R.id.access_type);
        group_status = view.findViewById(R.id.group_status);

        check_photo = view.findViewById(R.id.check_photo);
        check_comment = view.findViewById(R.id.check_comment);
        check_doc = view.findViewById(R.id.check_doc);

        SelectPicture = view.findViewById(R.id.SelectPicture);
        SelectBanner = view.findViewById(R.id.SelectBanner);
        SaveButton = view.findViewById(R.id.SaveButton);
        mRecyclerView = view.findViewById(R.id.Images_listview);
        banner_image = view.findViewById(R.id.banner_image);
        progess_load = view.findViewById(R.id.progess_load);
        UpdateButton = view.findViewById(R.id.UpdateButton);

        GroupTypeModelArrayList = new ArrayList<GroupTypeModel>();
        CountryModelArrayList = new ArrayList<CountryModel>();
        StateModelArrayList = new ArrayList<CountryWiseStateModel>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ListGroupType = new ArrayList<>();
        ListGroupType.add("Select Type *");
        ListCountry = new ArrayList<>();
        ListState = new ArrayList<>();
        GroupStatus = new ArrayList<>();
        AccessType = new ArrayList<>();
        CountryModelArrayList = CommonUtils.COUNTRYARRAYLIST;
        ListCountry.add("Select Country");
        ListState.add("Select State");

        GroupStatus.add("Select Status");
        GroupStatus.add("Active");
        GroupStatus.add("Inactive");

        AccessType.add("Select Access Type");
        AccessType.add("Public");
        AccessType.add("Friends");
        AccessType.add("Private");

        String title = getArguments().getString("Title");
        GroupId =  getArguments().getString("GroupId");
        if (title.equalsIgnoreCase("Edit Group")){
            Heading.setText("Edit your Group Detail");
            UpdateButton.setVisibility(View.VISIBLE);
            SaveButton.setVisibility(View.GONE);
            getGroupTypes();
            GetGroupDetails(UserId, GroupId);
        }
        else {
            UpdateButton.setVisibility(View.GONE);
            SaveButton.setVisibility(View.VISIBLE);
            getGroupTypes();
        }


        if (CountryModelArrayList.size() > 0){
            for (int i = 0; i < CountryModelArrayList.size(); i++) {
                final CountryModel Items = CountryModelArrayList.get(i);
                ListCountry.add(Items.getCountryName());
            }
        }
        else{
            getCountryDetails();
        }



        group_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedType = parent.getItemAtPosition(position).toString();
                if (SelectedType.equalsIgnoreCase("Select Type")){
                    GruopTypeId = "";
                }
                else {
                    for(int i = 0; i < GroupTypeModelArrayList.size();i++) {
                        if (GroupTypeModelArrayList.get(i).getGroupName().contains(SelectedType)) {
                            GruopTypeId = GroupTypeModelArrayList.get(position - 1).getGroupId();
                        }
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        ArrayAdapter<String> countrywadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListCountry);
        group_country.setAdapter(countrywadapter);
        group_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for(int i = 0; i < CountryModelArrayList.size();i++) {
                    if (CountryModelArrayList.get(i).getCountryName().contains(SelectedCountry)) {
                        SelectedCountryId = CountryModelArrayList.get(position - 1).getCountryId();
                        getCountryWiseState(SelectedCountryId);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> statusadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,GroupStatus);
        group_status.setAdapter(statusadapter);
        group_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedStatus = parent.getItemAtPosition(position).toString();

                if (SelectedStatus.equalsIgnoreCase("Active")){
                    Status = "1";
                }
                else if (SelectedStatus.equalsIgnoreCase("Inactive")){
                    Status = "0";
                }
                else if (SelectedStatus.equalsIgnoreCase("Select Status")){
                    Status = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayAdapter<String> accessadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,AccessType);
        access_type.setAdapter(accessadapter);
        access_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedStatus = parent.getItemAtPosition(position).toString();

                if (SelectedStatus.equalsIgnoreCase("Public")){
                    AccessTypeSelected = "";
                }
                else if (SelectedStatus.equalsIgnoreCase("Friends")){
                    AccessTypeSelected = "";
                }
                else if (SelectedStatus.equalsIgnoreCase("Private")){
                    AccessTypeSelected = "";
                }
                else if (SelectedStatus.equalsIgnoreCase("Access Type")){
                    AccessTypeSelected = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(1)
                        .setTag("ImagePicker")
                        .build();

                pickerDialog.show(getChildFragmentManager(), "ImagePicker");
            }
        });

        SelectBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(1)
                        .setTag("BannerPicker")
                        .build();

                pickerDialog.show(getChildFragmentManager(), "BannerPicker");
            }
        });


        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String group_name = "", group_desc = "", groupcity = "", zipcode = "", groupwebsite = "",  groupoptions = "", embed_video = "",  allow_audio = "0",  allow_comments = "0", allow_documents = "0";

                group_name = groupname.getText().toString().trim();
                group_desc = groupdesc.getText().toString().trim();
                groupcity = city.getText().toString().trim();
                zipcode = zip.getText().toString().trim();
                groupwebsite = website.getText().toString().trim();
                embed_video = embedded_video.getText().toString().trim();

                if (check_photo.isChecked()){
                    allow_audio = "1";
                }if (check_doc.isChecked()){
                    allow_comments = "1";
                }if (check_comment.isChecked()){
                    allow_documents = "1";
                }

                if (group_name.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Group name", Toast.LENGTH_LONG).show();
                } else if (group_desc.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Group Description", Toast.LENGTH_LONG).show();
                } else if (GruopTypeId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select group type", Toast.LENGTH_LONG).show();
                } else {
                    new PostGroupAsync(getContext()).execute(UserId, group_name, group_desc, GruopTypeId, SelectedCountryId, SelectedStateId, groupcity, zipcode, groupwebsite, AccessTypeSelected, allow_audio,allow_comments,allow_documents,embed_video, Status);

                }
            }
        });


        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String group_name = "", group_desc = "", groupcity = "", zipcode = "", groupwebsite = "",  groupoptions = "", embed_video = "",  allow_audio = "0",  allow_comments = "0", allow_documents = "0";

                group_name = groupname.getText().toString().trim();
                group_desc = groupdesc.getText().toString().trim();
                groupcity = city.getText().toString().trim();
                zipcode = zip.getText().toString().trim();
                groupwebsite = website.getText().toString().trim();
                embed_video = embedded_video.getText().toString().trim();

                if (check_photo.isChecked()){
                    allow_audio = "1";
                }if (check_doc.isChecked()){
                    allow_comments = "1";
                }if (check_comment.isChecked()){
                    allow_documents = "1";
                }

                if (group_name.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Group name", Toast.LENGTH_LONG).show();
                } else if (group_desc.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Group Description", Toast.LENGTH_LONG).show();
                } else if (GruopTypeId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select group type", Toast.LENGTH_LONG).show();
                } else {
                    new UpdateGroupAsync(getContext()).execute(UserId, group_name, group_desc, GruopTypeId, SelectedCountryId, SelectedStateId, groupcity, zipcode, groupwebsite, AccessTypeSelected, allow_audio,allow_comments,allow_documents,embed_video, Status);

                }
            }
        });


        return  view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void ReloadGroups();
    }

    public void getCountryWiseState(String CountryId){

        String url = (CommonUtils.BASE_URL) + "getStates";

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("country_id", CountryId)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
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

                                JSONArray json2 = json.getJSONArray("states");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject obj = RegionList.getJSONObject("state");

                                    CountryWiseStateModel model = new CountryWiseStateModel();
                                    model.setStateId(obj.getString("id"));
                                    model.setStateName(obj.getString("name"));
                                    model.setStateStatus(obj.getString("status"));

                                    ListState.add(obj.getString("name"));


                                    StateModelArrayList.add(model);
                                }

                                ArrayAdapter<String> statewadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListState);
                                group_state.setAdapter(statewadapter);
                                group_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String SelectedState = parent.getItemAtPosition(position).toString();
                                        for(int i = 0; i<StateModelArrayList.size();i++) {
                                            if (StateModelArrayList.get(i).getStateName().contains(SelectedState)) {
                                                SelectedStateId = StateModelArrayList.get(position - 1).getStateId();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });

                            }

                            for(int j = 0; j < ListState.size();j++) {
                                if (ListState.get(j).equalsIgnoreCase(StateString)) {
                                    group_state.setSelection(j);
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

                getActivity().runOnUiThread(new Runnable() {
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

                                    ListCountry.add(RegionObj.getString("name"));
                                    CountryModelArrayList.add(model);

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



    public void getGroupTypes(){

        GroupTypeModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getGroupTypeDropdown";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).get().build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
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

                                JSONArray json2 = json.getJSONArray("group_types");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("group_type");

                                    GroupTypeModel model = new GroupTypeModel();
                                    model.setGroupId(RegionObj.getString("id"));
                                    model.setGroupName(RegionObj.getString("type_title"));
                                    model.setGroupStatus(RegionObj.getString("status"));

                                    ListGroupType.add(RegionObj.getString("type_title"));

                                    GroupTypeModelArrayList.add(model);
                                }
                                ArrayAdapter<String> typewrapper = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListGroupType);
                                group_type.setAdapter(typewrapper);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

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
        if (tag.equalsIgnoreCase("ImagePicker")) {
            for (int i = 0; i < uriList.size(); i++) {

                Uri selectedImage = uriList.get(i);
                File imageFile = new File(getRealPathFromURI(selectedImage));
                ImagesList.add(String.valueOf(imageFile));
                GroupImagePath = String.valueOf(imageFile);

            }

            if (ImagesList.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter = new MultipleImagesAdapter(getActivity(), ImagesList, "Add");
                mRecyclerView.setAdapter(mAdapter);
            }
        } else if (tag.equalsIgnoreCase("BannerPicker")) {

            for (int i = 0; i < uriList.size(); i++) {

                Uri selectedImage = uriList.get(i);
                File imageFile = new File(getRealPathFromURI(selectedImage));
                BannerImagePath = String.valueOf(imageFile);

                if (BannerImagePath.contains("file:")) {
                    banner_image.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(BannerImagePath).memoryPolicy(MemoryPolicy.NO_CACHE).into(banner_image);
                } else {
                    banner_image.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load("file:" + BannerImagePath).memoryPolicy(MemoryPolicy.NO_CACHE).into(banner_image);
                }


            }

        }
    }


    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleImagesList;
        String GroupType;

        public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList, String Type) {
            context1 = context;
            MultipleImagesList = ImagesList;
            GroupType = Type;
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

            if (GroupType.equalsIgnoreCase("Add")){
                if (MultipleImagesList.get(position).contains("file:")) {
                    Glide.with(context1).load(MultipleImagesList.get(position)).into(holder.adapterImage);
                }
                else if (!MultipleImagesList.get(position).contains("file:")) {
                    Glide.with(context1).load("file:" + MultipleImagesList.get(position)).into(holder.adapterImage);
                }
            }
            else if (GroupType.equalsIgnoreCase("Edit")){
                Glide.with(getActivity()).load(MultipleImagesList.get(position)).into(holder.adapterImage);
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

    class PostGroupAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;

        public PostGroupAsync(Context context) {


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            try {



                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("group_name", params[1]));
                nameValuePairs.add(new BasicNameValuePair("group_desc", params[2]));
                nameValuePairs.add(new BasicNameValuePair("group_type_id", params[3]));
                nameValuePairs.add(new BasicNameValuePair("country_id", params[4]));
                nameValuePairs.add(new BasicNameValuePair("state_id", params[5]));
                nameValuePairs.add(new BasicNameValuePair("city", params[6]));
                nameValuePairs.add(new BasicNameValuePair("zipcode", params[7]));
                nameValuePairs.add(new BasicNameValuePair("website", params[8]));
                nameValuePairs.add(new BasicNameValuePair("access_type", params[9]));
                nameValuePairs.add(new BasicNameValuePair("allow_audio", params[10]));
                nameValuePairs.add(new BasicNameValuePair("allow_comments", params[11]));
                nameValuePairs.add(new BasicNameValuePair("allow_documents", params[12]));
                nameValuePairs.add(new BasicNameValuePair("embed_video", params[13]));
                nameValuePairs.add(new BasicNameValuePair("status", params[14]));

                if (ImagesList.size() > 0){
                    nameValuePairs.add(new BasicNameValuePair("image", ImagesList.get(0)));
                }
                if (!BannerImagePath.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("bg_image", BannerImagePath));
                }


                Log.d("datap", nameValuePairs.toString());
                String Url = (CommonUtils.BASE_URL)+"add_group";
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
                    if (mListener != null) {
                        mListener.ReloadGroups();
                    }

                } else {
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void GetGroupDetails(String user_id,String group_id){



        ImagesList.clear();

        String url = (CommonUtils.BASE_URL) + "view_group_in_details";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add(" group_id", group_id)
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
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {


                                JSONObject GroupDetails = json.getJSONObject("group_details");
                                JSONObject Group = GroupDetails.getJSONObject("Group");
                                JSONObject User = GroupDetails.getJSONObject("User");
                                JSONObject GroupType = GroupDetails.getJSONObject("GroupType");
                                JSONObject Country = GroupDetails.getJSONObject("Country");
                                JSONObject State = GroupDetails.getJSONObject("State");


                                groupname.setText(Group.getString("title").replace("null",""));
                                groupdesc.setText(Group.getString("description").replace("null",""));
                                city.setText(Group.getString("city").replace("null",""));
                                zip.setText(Group.getString("zipcode").replace("null",""));
                                website.setText(Group.getString("website").replace("null",""));
                                embedded_video.setText(Group.getString("video").replace("null",""));

                                String GroupBannerURl = json.getString("bg_image_url") +Group.getString("id")+ "/" +Group.getString("bg_image").replace(" ","%20");
                                banner_image.setVisibility(View.VISIBLE);
                                Glide.with(getActivity()).load(GroupBannerURl).into(banner_image);



                                if (!Group.getString("image").equalsIgnoreCase("null")){
                                    String GroupImg = json.getString("image_url") +Group.getString("id")+ "/" +Group.getString("image");
                                    ImagesList.add(GroupImg);
                                }

                                if (ImagesList.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                } else {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mAdapter = new MultipleImagesAdapter(getActivity(), ImagesList, "Edit");
                                    mRecyclerView.setAdapter(mAdapter);
                                }

                                CountryString = Country.getString("name");
                                for(int i = 0; i < ListCountry.size();i++) {
                                    if (ListCountry.get(i).equalsIgnoreCase(CountryString)) {
                                        group_country.setSelection(i);
                                        getCountryWiseState(Country.getString("id"));
                                        StateString = State.getString("name");

                                    }
                                }


                                GroupTypeString = GroupType.getString("type_title");
                                for(int i = 0; i < ListGroupType.size();i++) {
                                    if (ListGroupType.get(i).equalsIgnoreCase(GroupTypeString)) {
                                        group_type.setSelection(i);
                                    }
                                }

                                if (Group.getString("allow_audio").equalsIgnoreCase("1")){
                                    check_photo.setChecked(true);
                                }else {
                                    check_photo.setChecked(false); }
                                if (Group.getString("allow_comments").equalsIgnoreCase("1")){
                                    check_comment.setChecked(true);
                                }else {
                                    check_comment.setChecked(false); }
                                if (Group.getString("allow_documents").equalsIgnoreCase("1")){
                                    check_doc.setChecked(true);
                                }else {
                                    check_doc.setChecked(false); }


//                                progress_load.setVisibility(View.GONE);


                            } else {

                                Toast.makeText(getActivity(),json.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    class UpdateGroupAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;

        public UpdateGroupAsync(Context context) {


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            try {



                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("group_name", params[1]));
                nameValuePairs.add(new BasicNameValuePair("group_desc", params[2]));
                nameValuePairs.add(new BasicNameValuePair("group_type_id", params[3]));
                nameValuePairs.add(new BasicNameValuePair("country_id", params[4]));
                nameValuePairs.add(new BasicNameValuePair("state_id", params[5]));
                nameValuePairs.add(new BasicNameValuePair("city", params[6]));
                nameValuePairs.add(new BasicNameValuePair("zipcode", params[7]));
                nameValuePairs.add(new BasicNameValuePair("website", params[8]));
                nameValuePairs.add(new BasicNameValuePair("access_type", params[9]));
                nameValuePairs.add(new BasicNameValuePair("allow_audio", params[10]));
                nameValuePairs.add(new BasicNameValuePair("allow_comments", params[11]));
                nameValuePairs.add(new BasicNameValuePair("allow_documents", params[12]));
                nameValuePairs.add(new BasicNameValuePair("embed_video", params[13]));
                nameValuePairs.add(new BasicNameValuePair("status", params[14]));
                nameValuePairs.add(new BasicNameValuePair("group_id", GroupId));
               // nameValuePairs.add(new BasicNameValuePair("image", GroupImagePath ));

                if (!GroupImagePath.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("image", GroupImagePath));
                }


                if (!BannerImagePath.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("bg_image", BannerImagePath));
                }


                Log.d("datap", nameValuePairs.toString());
                String Url = (CommonUtils.BASE_URL)+"update_group";
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
                    if (mListener != null) {
                        mListener.ReloadGroups();
                    }

                } else {
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
