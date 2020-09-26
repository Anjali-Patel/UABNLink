package com.uabn.gss.uabnlink.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.CountryWiseStateModel;
import com.uabn.gss.uabnlink.model.EventTypeModel;
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
import java.util.Calendar;
import java.util.List;

import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class AddEvent extends Fragment implements BSImagePicker.OnMultiImageSelectedListener{

    SharedPreferenceUtils preferances;
    EditText eventTitle, eventOrganizer, eventDescription, eventLocation,eventCity, eventZip, eventUrl, eventVideo;
    EditText startDate, endDate, startTime, endTime;
    CheckBox checkPhoto, checkDoc, checkVideo, checkComment, checkGuests, checkGuestList;
    TextView SelectPicture, SelectCover, Heading;
    Button CreateButton, UpdateButton;
    ImageView cover_image;
    DatePickerDialog datePickerDialog;
    BSImagePicker pickerDialog;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<String> cover_image_list = new ArrayList<String>();
    ArrayList<String> PostedImagesList = new ArrayList<String>();

    private RecyclerView mRecyclerView,cover_image_list_view;
    private RecyclerView.Adapter mAdapter;
    String CoverImagePath = "";

    ProgressBar progess_load;

    String EventStatus = "0", CountryString, StateString, EventTypeString;

    int EventTypeId = 0, CountryId = 0, StateId = 0, ActivationId = 0;

    Spinner event_type, country, state, status;
    String SelectedCountryId = "", SelectedStateId = "", SelectedEventTypeId = "";

    ArrayList<String> ListEventType;
    ArrayList<String> ListCountry;
    ArrayList<String> ListState;
    ArrayList<String> ListStatus;

    ArrayList<EventTypeModel> EventTypeModelArrayList;

    ArrayList<CountryModel> CountryModelArrayList;

    ArrayList<CountryWiseStateModel> StateModelArrayList;

    String user_id, EventId;

    private OnFragmentInteractionListener mListener;



    public AddEvent() {
    }


    public static AddEvent newInstance(String param1, String param2) {
        AddEvent fragment = new AddEvent();
        Bundle args = new Bundle();
        args.putString("Title", param1);
        args.putString("EventId", param2);

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

        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        preferances = SharedPreferenceUtils.getInstance(getActivity());
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

        Heading= view.findViewById(R.id.Heading);

        cover_image_list_view=view.findViewById(R.id.cover_image_list_view);

        event_type = view.findViewById(R.id.event_type);
        country = view.findViewById(R.id.country);
        state = view.findViewById(R.id.state);
        status = view.findViewById(R.id.status);

        eventTitle = view.findViewById(R.id.eventTitle);
        eventOrganizer = view.findViewById(R.id.eventOrganizer);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventCity = view.findViewById(R.id.eventCity);
        eventZip = view.findViewById(R.id.eventZip);
        eventUrl = view.findViewById(R.id.eventUrl);
        eventVideo = view.findViewById(R.id.eventVideo);
        cover_image = view.findViewById(R.id.cover_image );
        progess_load = view.findViewById(R.id.progess_load);

        checkPhoto = view.findViewById(R.id.checkPhoto);
        checkDoc = view.findViewById(R.id.checkDoc);
        checkVideo = view.findViewById(R.id.checkVideo);
        checkComment = view.findViewById(R.id.checkComment);
        checkGuests = view.findViewById(R.id.checkGuests);
        checkGuestList = view.findViewById(R.id.checkGuestList);

        SelectPicture = view.findViewById(R.id.SelectPicture);
        SelectCover = view.findViewById(R.id.SelectCover);
        CreateButton = view.findViewById(R.id.CreateButton);
        UpdateButton = view.findViewById(R.id.UpdateButton);
        mRecyclerView = view.findViewById(R.id.Images_listview);



        startDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);
        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);
        EventTypeModelArrayList = new ArrayList<EventTypeModel>();
        CountryModelArrayList = new ArrayList<CountryModel>();
        StateModelArrayList = new ArrayList<CountryWiseStateModel>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cover_image_list_view.setHasFixedSize(true);
        cover_image_list_view.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        cover_image_list_view.setItemAnimator(new DefaultItemAnimator());

        ListEventType = new ArrayList<>();
        ListCountry = new ArrayList<>();
        ListState = new ArrayList<>();
        ListStatus = new ArrayList<>();
        CountryModelArrayList = CommonUtils.COUNTRYARRAYLIST;
        ListCountry.add("Select Country");
        ListEventType.add("Select Event Type *");


        ListStatus.add("Select Status");
        ListStatus.add("Active");
        ListStatus.add("Inactive");



        for (int i = 0; i < CountryModelArrayList.size(); i++) {
            final CountryModel Items = CountryModelArrayList.get(i);
            ListCountry.add(Items.getCountryName());
        }



        String title = getArguments().getString("Title");
        EventId =  getArguments().getString("EventId");
        if (title.equalsIgnoreCase("Edit Event")){
            Heading.setText("Edit your Event Detail");
            UpdateButton.setVisibility(View.VISIBLE);
            CreateButton.setVisibility(View.GONE);
            getEventTypes();
            GetEventDetails(user_id, EventId);
        }
        else {
            UpdateButton.setVisibility(View.GONE);
            CreateButton.setVisibility(View.VISIBLE);
            getEventTypes();
        }



        ArrayAdapter<String> countrywadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListCountry);
        country.setAdapter(countrywadapter);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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



        event_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String SelectedType = parent.getItemAtPosition(position).toString();
                for(int i = 0; i<EventTypeModelArrayList.size();i++) {
                    if (EventTypeModelArrayList.get(i).getEventTypeName().contains(SelectedType)) {
                        SelectedEventTypeId = EventTypeModelArrayList.get(position - 1).getEventTypeId();
                    }
                }
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> statusadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListStatus);
        status.setAdapter(statusadapter);
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedStatus = parent.getItemAtPosition(position).toString();

                if (SelectedStatus.equalsIgnoreCase("Active")){
                     EventStatus = "1";
                }
                else if (SelectedStatus.equalsIgnoreCase("Inactive")){
                    EventStatus = "0";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText( selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        endTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTime.setText( selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        SelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(5)
                        .setTag("Picker")
                        .build();
                pickerDialog.setCancelable(false);

                pickerDialog.show(getChildFragmentManager(), "Picker");
            }
        });

        SelectCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(5)
                        .setTag("coverImage")
                        .build();
                pickerDialog.setCancelable(false);

                pickerDialog.show(getChildFragmentManager(), "coverImage");
            }
        });


        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "", org = "", desc = "", startdate = "", enddate = "", starttime = "", endtime = "",  loc = "", city = "", zip = "", weburl = "", video = "", allowphoto = "0", allowvideo = "0", allowcomment = "0", allowguest = "0", allowdoc = "0", allowguestlist = "0";

                title = eventTitle.getText().toString().trim();
                org = eventOrganizer.getText().toString().trim();
                desc = eventDescription.getText().toString().trim();
                startdate = startDate.getText().toString().trim();
                enddate = endDate.getText().toString().trim();
                starttime = startTime.getText().toString().trim();
                endtime = endTime.getText().toString().trim();
                //loc = eventLocation.getText().toString().trim();
                city = eventCity.getText().toString().trim();
                zip = eventZip.getText().toString().trim();
                weburl = eventUrl.getText().toString().trim();
                video = eventVideo.getText().toString().trim();

                if (checkPhoto.isChecked()){
                    allowphoto = "1";
                }if (checkVideo.isChecked()){
                    allowvideo = "1";
                }if (checkComment.isChecked()){
                    allowcomment = "1";
                }if (checkGuests.isChecked()){
                    allowguest = "1";
                }if (checkDoc.isChecked()){
                    allowdoc = "1";
                }if (checkGuestList.isChecked()){
                    allowguestlist = "1";
                }


                if (title.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event title", Toast.LENGTH_LONG).show();
                } else if (org.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Event Organizer", Toast.LENGTH_LONG).show();
                } else if (desc.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter event description", Toast.LENGTH_LONG).show();
                } else if (city.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event city", Toast.LENGTH_LONG).show();
                } else if (SelectedEventTypeId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event end date", Toast.LENGTH_LONG).show();
                } else if (starttime.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event start time", Toast.LENGTH_LONG).show();
                } else if (endtime.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event end time", Toast.LENGTH_LONG).show();
                } else {
//                    addPBCR(user_id, str_assistance, str_subject, str_appoint, str_description, SelectedIndustryId, SelectedCategoryId, SelectedRegionId, str_contact_person, str_contact_email, str_contact_email2, str_home_cell, str_tel_business);

                    new PostEventAsync(getContext()).execute(user_id, title, org, SelectedEventTypeId, desc, startdate, enddate, starttime, endtime, loc, SelectedCountryId, SelectedStateId, city, zip, weburl, video, allowphoto, allowvideo , allowcomment, allowguest, allowdoc, allowguestlist, EventStatus);

                }
            }
        });



        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "", org = "", desc = "", startdate = "", enddate = "", starttime = "", endtime = "",  loc = "", city = "", zip = "", weburl = "", video = "", allowphoto = "0", allowvideo = "0", allowcomment = "0", allowguest = "0", allowdoc = "0", allowguestlist = "0";

                title = eventTitle.getText().toString().trim();
                org = eventOrganizer.getText().toString().trim();
                desc = eventDescription.getText().toString().trim();
                startdate = startDate.getText().toString().trim();
                enddate = endDate.getText().toString().trim();
                starttime = startTime.getText().toString().trim();
                endtime = endTime.getText().toString().trim();
                //loc = eventLocation.getText().toString().trim();
                city = eventCity.getText().toString().trim();
                zip = eventZip.getText().toString().trim();
                weburl = eventUrl.getText().toString().trim();
                video = eventVideo.getText().toString().trim();

                if (checkPhoto.isChecked()){
                    allowphoto = "1";
                }if (checkVideo.isChecked()){
                    allowvideo = "1";
                }if (checkComment.isChecked()){
                    allowcomment = "1";
                }if (checkGuests.isChecked()){
                    allowguest = "1";
                }if (checkDoc.isChecked()){
                    allowdoc = "1";
                }if (checkGuestList.isChecked()){
                    allowguestlist = "1";
                }


                if (title.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event title", Toast.LENGTH_LONG).show();
                } else if (org.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Event Organizer", Toast.LENGTH_LONG).show();
                } else if (desc.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter event description", Toast.LENGTH_LONG).show();
                } else if (city.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event city", Toast.LENGTH_LONG).show();
                } else if (SelectedEventTypeId.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event end date", Toast.LENGTH_LONG).show();
                } else if (starttime.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event start time", Toast.LENGTH_LONG).show();
                } else if (endtime.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Event end time", Toast.LENGTH_LONG).show();
                } else {
//                    addPBCR(user_id, str_assistance, str_subject, str_appoint, str_description, SelectedIndustryId, SelectedCategoryId, SelectedRegionId, str_contact_person, str_contact_email, str_contact_email2, str_home_cell, str_tel_business);

                    new PostEditedEventAsync(getContext()).execute(user_id, title, org, SelectedEventTypeId, desc, startdate, enddate, starttime, endtime, loc, SelectedCountryId, SelectedStateId, city, zip, weburl, video, allowphoto, allowvideo , allowcomment, allowguest, allowdoc, allowguestlist, EventStatus, EventId);

                }
            }
        });


        return view;
    }

    public void getEventTypes(){

        String url = (CommonUtils.BASE_URL) + "getEventTypes";

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

                                JSONArray json2 = json.getJSONArray("EventsType");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("events_type");

                                    EventTypeModel model = new EventTypeModel();
                                    model.setEventTypeId(RegionObj.getString("id"));
                                    model.setEventTypeName(RegionObj.getString("title"));
                                    model.setEventTypeStatus(RegionObj.getString("status"));

                                    ListEventType.add(RegionObj.getString("title"));

                                    EventTypeModelArrayList.add(model);
                                }
                                ArrayAdapter<String> typeadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListEventType);
                                event_type.setAdapter(typeadapter);
                                //event_type.setSelection(EventTypeId);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void getCountryWiseState(String CountryId){

        StateModelArrayList.clear();

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
                                ListState.add("Select State");


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
                                state.setAdapter(statewadapter);
                                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String SelectedState = parent.getItemAtPosition(position).toString();
                                        for(int i = 0; i<StateModelArrayList.size();i++) {
                                            if (StateModelArrayList.get(i).getStateName().contains(SelectedState)) {
                                                try {
                                                    SelectedStateId = StateModelArrayList.get(position - 1).getStateId();
                                                }
                                                catch (Exception e){

                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });

                            }
                            else {
                                ListState.clear();
                                ArrayAdapter<String> statewadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListState);
                                state.setAdapter(statewadapter);

                            }


                            for(int j = 0; j < ListState.size();j++) {
                                if (ListState.get(j).equalsIgnoreCase(StateString)) {
                                    state.setSelection(j);
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
        cover_image_list.clear();
        if (tag.equalsIgnoreCase("Picker")){
            for (int i = 0; i < uriList.size(); i++) {

                Uri selectedImage = uriList.get(i);
                File imageFile = new File(getRealPathFromURI(selectedImage));
                ImagesList.add(String.valueOf(imageFile));

            }

            if (ImagesList.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                cover_image_list_view.setVisibility(View.GONE);

            } else {
//                cover_image_list_view.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter = new MultipleImagesAdapter(getActivity(), ImagesList, "Add");
                mRecyclerView.setAdapter(mAdapter);
//                cover_image_list_view.setAdapter(mAdapter);
            }
        }

        else if (tag.equalsIgnoreCase("coverImage")){

//            for (int i = 0; i < uriList.size(); i++) {
//
//                Uri selectedImage = uriList.get(i);
//                File imageFile = new File(getRealPathFromURI(selectedImage));
//                CoverImagePath = String.valueOf(imageFile);
//
//                if (CoverImagePath.contains("file:")) {
//                    cover_image.setVisibility(View.VISIBLE);
//                    Glide.with(getActivity()).load(CoverImagePath).into(cover_image);
//                } else {
//                    cover_image.setVisibility(View.VISIBLE);
//                    Glide.with(getActivity()).load("file:" + CoverImagePath).into(cover_image);
//                }
//
//
//            }
            for (int i = 0; i < uriList.size(); i++) {

                Uri selectedImage = uriList.get(i);
                File imageFile = new File(getRealPathFromURI(selectedImage));
                cover_image_list.add(String.valueOf(imageFile));

            }

            if (cover_image_list.size() == 0) {
                cover_image_list_view.setVisibility(View.GONE);
            } else {
                cover_image_list_view.setVisibility(View.VISIBLE);
                mAdapter = new MultipleImagesCoverAdapter(getActivity(), cover_image_list, "Add");
                cover_image_list_view.setAdapter(mAdapter);
            }


        }
    }

    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleImagesList;
        String EventType;

        public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList, String Type) {
            context1 = context;
            MultipleImagesList = ImagesList;
            EventType = Type;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_event_mage, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            if (EventType.equalsIgnoreCase("Add")){
                if (MultipleImagesList.get(position).contains("file:")) {
                    Glide.with(context1).load(MultipleImagesList.get(position)).into(holder.adapterImage);
                }
                else if (!MultipleImagesList.get(position).contains("file:")) {
                    Glide.with(context1).load("file:" + MultipleImagesList.get(position)).into(holder.adapterImage);
                }
            }
            else if (EventType.equalsIgnoreCase("Edit")){
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
    public class MultipleImagesCoverAdapter extends RecyclerView.Adapter<MultipleImagesCoverAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleCoverImagesList;
        String EventType;

        public MultipleImagesCoverAdapter(Context context, ArrayList<String> ImagesList, String Type) {
            context1 = context;
            MultipleCoverImagesList = ImagesList;
            EventType = Type;
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

            if (EventType.equalsIgnoreCase("Add")){
                if (MultipleCoverImagesList.get(position).contains("file:")) {
                    Glide.with(context1).load(MultipleCoverImagesList.get(position)).into(holder.adapterImage);
                }
                else if (!MultipleCoverImagesList.get(position).contains("file:")) {
                    Glide.with(context1).load("file:" + MultipleCoverImagesList.get(position)).into(holder.adapterImage);
                }
            }
            else if (EventType.equalsIgnoreCase("Edit")){
                Glide.with(getActivity()).load(MultipleCoverImagesList.get(position)).into(holder.adapterImage);
            }
        }

        @Override
        public int getItemCount() {
            return MultipleCoverImagesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adapterImage;

            public MyViewHolder(View v) {
                super(v);
                adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
            }
        }
    }

    class PostEventAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;

        public PostEventAsync(Context context) {


        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("event_title", params[1]));
                nameValuePairs.add(new BasicNameValuePair("organizer", params[2]));
                nameValuePairs.add(new BasicNameValuePair("event_type_id", params[3]));
                nameValuePairs.add(new BasicNameValuePair("description", params[4]));
                nameValuePairs.add(new BasicNameValuePair("start_date", params[5]));
                nameValuePairs.add(new BasicNameValuePair("end_date", params[6]));
                nameValuePairs.add(new BasicNameValuePair("start_time", params[7]));
                nameValuePairs.add(new BasicNameValuePair("end_time", params[8]));
                nameValuePairs.add(new BasicNameValuePair("location", params[9]));
                nameValuePairs.add(new BasicNameValuePair("country_id", params[10]));
                nameValuePairs.add(new BasicNameValuePair("state_id", params[11]));
                nameValuePairs.add(new BasicNameValuePair("city", params[12]));
                nameValuePairs.add(new BasicNameValuePair("zipcode", params[13]));
                nameValuePairs.add(new BasicNameValuePair("website_url", params[14]));
                nameValuePairs.add(new BasicNameValuePair("embedded_video", params[15]));
                nameValuePairs.add(new BasicNameValuePair("allow_photo", params[16]));
                nameValuePairs.add(new BasicNameValuePair("allow_video", params[17]));
                nameValuePairs.add(new BasicNameValuePair("allow_comments", params[18]));
                nameValuePairs.add(new BasicNameValuePair("allow_guests", params[19]));
                nameValuePairs.add(new BasicNameValuePair("allow_document", params[20]));
                nameValuePairs.add(new BasicNameValuePair("show_guests_list", params[21]));
                nameValuePairs.add(new BasicNameValuePair("status", params[22]));

//                if (!CoverImagePath.equalsIgnoreCase("")){
//                    nameValuePairs.add(new BasicNameValuePair("cover_image", CoverImagePath));
//                }




                int CoverImgCount = 0;
                for (int j = 0; j< cover_image_list.size(); j++) {
                    nameValuePairs.add(new BasicNameValuePair("cover_image" + (CoverImgCount + 1), cover_image_list.get(j)));
                    CoverImgCount++;
                }
                int ImgCount = 0;
                for (int i = 0; i < ImagesList.size(); i++) {
                    nameValuePairs.add(new BasicNameValuePair("img" + (ImgCount + 1), ImagesList.get(i)));
                    ImgCount++;
                }

                Log.d("datap", nameValuePairs.toString());
                String Url = (CommonUtils.BASE_URL)+"add_new_event";
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


    public void GetEventDetails(String user_id,String event_id){
        String url = (CommonUtils.BASE_URL) + "view_event_in_detail";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add(" event_id", event_id)
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
                                JSONObject EventDetails = json.getJSONObject("event_details");
                                JSONObject Event = EventDetails.getJSONObject("Event");
                                JSONObject Country = EventDetails.getJSONObject("Country");
                                JSONObject State = EventDetails.getJSONObject("State");
                                JSONObject EventsType = EventDetails.getJSONObject("EventsType");
                                JSONArray UserEventPhoto = EventDetails.getJSONArray("UserEventPhoto");


                                eventTitle.setText(Event.getString("event_title").replace("null",""));
                                eventOrganizer.setText(Event.getString("organizer").replace("null",""));
                                eventDescription.setText(Event.getString("description").replace("null",""));
                                eventLocation.setText(Event.getString("location").replace("null",""));
                                eventCity.setText(Event.getString("city").replace("null",""));
                                eventZip.setText(Event.getString("zipcode").replace("null",""));
                                eventUrl.setText(Event.getString("website_url").replace("null",""));


                                SelectedCountryId = Event.getString("country_id").replace("null","");
                                SelectedStateId = Event.getString("state_id").replace("null","");
                                SelectedEventTypeId = Event.getString("event_type_id").replace("null","");
                                EventStatus = Event.getString("status").replace("null","");

                                if (Event.getString("allow_photo").equalsIgnoreCase("1")){
                                    checkPhoto.setChecked(true);
                                }else {
                                    checkPhoto.setChecked(false); }

                                if (Event.getString("allow_document").equalsIgnoreCase("1")){
                                    checkDoc.setChecked(true);
                                }else {
                                    checkDoc.setChecked(false); }

                                if (Event.getString("allow_video").equalsIgnoreCase("1")){
                                    checkVideo.setChecked(true);
                                }else {
                                    checkVideo.setChecked(false); }

                                if (Event.getString("allow_comments").equalsIgnoreCase("1")){
                                    checkComment.setChecked(true);
                                }else {
                                    checkComment.setChecked(false); }

                                if (Event.getString("allow_guests").equalsIgnoreCase("1")){
                                    checkGuests.setChecked(true);
                                }else {
                                    checkGuests.setChecked(false); }

                                if (Event.getString("show_guests_list").equalsIgnoreCase("1")){
                                    checkGuestList.setChecked(true); }
                                else {
                                    checkGuestList.setChecked(false); }


                                startDate.setText(Event.getString("start_date").replace("null",""));
                                endDate.setText(Event.getString("end_date").replace("null",""));
                                startTime.setText(Event.getString("start_time").replace("null",""));
                                endTime.setText(Event.getString("end_time").replace("null",""));



                                CountryString = Country.getString("name");
                                for(int i = 0; i < ListCountry.size();i++) {
                                    if (ListCountry.get(i).equalsIgnoreCase(CountryString)) {
                                        country.setSelection(i);
                                        getCountryWiseState(Country.getString("id"));
                                        StateString = State.getString("name");

                                    }
                                }

                                EventTypeString = EventsType.getString("title");
                                for(int i = 0; i < ListEventType.size();i++) {
                                    if (ListEventType.get(i).equalsIgnoreCase(EventTypeString)) {
                                        event_type.setSelection(i);
                                    }
                                }

                                if (Event.getString("status").equalsIgnoreCase("0")){
                                    status.setSelection(2);
                                }
                                else if (Event.getString("status").equalsIgnoreCase("1")){
                                    status.setSelection(1);
                                }

                                if (Event.getString("status").equalsIgnoreCase("0")){
                                    status.setSelection(2);
                                }
                                else if (Event.getString("status").equalsIgnoreCase("1")){
                                    status.setSelection(1);
                                }

                                

                                if (!Event.getString("image").equalsIgnoreCase("null")){
                                    cover_image.setVisibility(View.VISIBLE);
                                    String EventCoverImg = json.getString("cover_image_url") +Event.getString("id")+ "/" +Event.getString("image");
                                    Glide.with(getActivity()).load(EventCoverImg).into(cover_image);

                                }


                                if (UserEventPhoto.length() >= 1){
                                    for (int j = 0; j < UserEventPhoto.length(); j++) {
                                        JSONObject EventPhotos = UserEventPhoto.getJSONObject(j);
                                        String EventImg = json.getString("event_images_url") +EventPhotos.getString("id")+ "/" +EventPhotos.getString("path");
                                        PostedImagesList.add(EventImg);
                                    }
                                    if (PostedImagesList.size() == 0) {
                                        mRecyclerView.setVisibility(View.GONE);
                                    } else {
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        mAdapter = new MultipleImagesAdapter(getActivity(), PostedImagesList, "Edit");
                                        mRecyclerView.setAdapter(mAdapter);
                                    }

                                }
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


    class PostEditedEventAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;



        public PostEditedEventAsync(Context context) {


        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("event_title", params[1]));
                nameValuePairs.add(new BasicNameValuePair("organizer", params[2]));
                nameValuePairs.add(new BasicNameValuePair("event_type_id", params[3]));
                nameValuePairs.add(new BasicNameValuePair("description", params[4]));
                nameValuePairs.add(new BasicNameValuePair("start_date", params[5]));
                nameValuePairs.add(new BasicNameValuePair("end_date", params[6]));
                nameValuePairs.add(new BasicNameValuePair("start_time", params[7]));
                nameValuePairs.add(new BasicNameValuePair("end_time", params[8]));
                nameValuePairs.add(new BasicNameValuePair("location", params[9]));
                nameValuePairs.add(new BasicNameValuePair("country_id", params[10]));
                nameValuePairs.add(new BasicNameValuePair("state_id", params[11]));
                nameValuePairs.add(new BasicNameValuePair("city", params[12]));
                nameValuePairs.add(new BasicNameValuePair("zipcode", params[13]));
                nameValuePairs.add(new BasicNameValuePair("website_url", params[14]));
                nameValuePairs.add(new BasicNameValuePair("embedded_video", params[15]));
                nameValuePairs.add(new BasicNameValuePair("allow_photo", params[16]));
                nameValuePairs.add(new BasicNameValuePair("allow_video", params[17]));
                nameValuePairs.add(new BasicNameValuePair("allow_comments", params[18]));
                nameValuePairs.add(new BasicNameValuePair("allow_guests", params[19]));
                nameValuePairs.add(new BasicNameValuePair("allow_document", params[20]));
                nameValuePairs.add(new BasicNameValuePair("show_guests_list", params[21]));
                nameValuePairs.add(new BasicNameValuePair("status", params[22]));
                nameValuePairs.add(new BasicNameValuePair("event_id", params[23]));


                if (!CoverImagePath.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("cover_image", CoverImagePath));
                }


                int ImgCount = 0;
                for (int i = 0; i < ImagesList.size(); i++) {
                    nameValuePairs.add(new BasicNameValuePair("img" + (ImgCount + 1), ImagesList.get(i)));
                    ImgCount++;
                }


                Log.d("datap", nameValuePairs.toString());
                String Url = (CommonUtils.BASE_URL)+"update_event";
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
                        mListener.ShowEvents();
                    }

                } else {
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public interface OnFragmentInteractionListener {
        void ShowEvents();
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

}
