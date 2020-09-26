package com.uabn.gss.uabnlink.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CheckNetwork;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.adapter.BannerAdapter;
import com.uabn.gss.uabnlink.adapter.MembersAdapter;
import com.uabn.gss.uabnlink.model.CertifiedBannerModel;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.uabn.gss.uabnlink.Utility.ExpandableTextView.makeTextViewResizable;

public class EventDetails extends AppCompatActivity {
    ImageView  eventimage,event_cover_image, AddDoc;
    TextView title, description, startdate, enddate, type, country, Interested, GuestList, Comments;
    LinearLayout ImageLinear, DocumentLinear, ConfirmedLinear, MayAttendLinear, DeclineLinear;
    TextView noDeclineList,noMayList,noCfmList,noDoc,noImage;
    FrameLayout progressBarHolder;
    LinearLayout MainView;
//    FrameLayout progress_load;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<MemberDataModel> MembersAttendingArrayList = new ArrayList<>();
    ArrayList<MemberDataModel> MembersMaybeAttendingArrayList = new ArrayList<>();
    ArrayList<MemberDataModel> MembersDeclinedArrayList = new ArrayList<>();
    ViewPager mPager;
    CircleIndicator indicator;
    ArrayList<CertifiedBannerModel> banner_list;
    RecyclerView.LayoutManager layoutmanager;
    MembersAdapter adapter;
    private RecyclerView mRecyclerView, declined_listview, may_attend_listview, confirmed_listview;
    private RecyclerView.Adapter mAdapter;
    String CoverImagePath = "";

    String EventId, UserId;
    String EventCoverImg, Date, Eventtitle ,EventImage;
    String DocumetPath;

    ExpandableRelativeLayout EventImages, EventVideo, EventConfirmList, EventMayAttendList, EventDeclinedList, EventDocument;
    Button EventImagesExpand, EventVideoExpand, EventConfirmInterestExpand, EventMayAttendExpand, EventDeclinedExpand, EventDocumentExpand;

    WebView WebVideo;
    LinearLayout videoLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
//        event_cover_image=findViewById(R.id.event_cover_image);
        mRecyclerView = findViewById(R.id.Images_listview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        banner_list= new ArrayList<>();
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
//        eventimage = findViewById(R.id.eventimage);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        type = findViewById(R.id.type);
        country = findViewById(R.id.country);
        Interested = findViewById(R.id.Interested);
        ImageLinear = findViewById(R.id.ImageLinear);
        DocumentLinear = findViewById(R.id.DocumentLinear);
        ConfirmedLinear = findViewById(R.id.ConfirmedLinear);
        MayAttendLinear = findViewById(R.id.MayAttendLinear);
        DeclineLinear = findViewById(R.id.DeclineLinear);
        GuestList = findViewById(R.id.GuestList);
        Comments = findViewById(R.id.Comments);
        declined_listview = findViewById(R.id.declined_listview);
        may_attend_listview = findViewById(R.id.may_attend_listview);
        confirmed_listview = findViewById(R.id.confirmed_listview);
        AddDoc = findViewById(R.id.AddDoc);
        noDeclineList = findViewById(R.id.noDeclineList);
        noMayList = findViewById(R.id.noMayList);
        noCfmList = findViewById(R.id.noCfmList);
        noDoc = findViewById(R.id.noDoc);
        noImage = findViewById(R.id.noImage);
        videoLinear = findViewById(R.id.videoLinear);
        WebVideo = findViewById(R.id.WebVideo);
        MainView = findViewById(R.id.MainView);
        progressBarHolder = findViewById(R.id.progressBarHolder);
        EventImages = findViewById(R.id.EventImages);
        EventVideo = findViewById(R.id.EventVideo);
        EventConfirmList = findViewById(R.id.EventConfirmList);
        EventMayAttendList = findViewById(R.id.EventMayAttendList);
        EventDeclinedList = findViewById(R.id.EventDeclinedList);
        EventDocument = findViewById(R.id.EventDocument);
        EventImagesExpand = findViewById(R.id.EventImagesExpand);
        EventVideoExpand = findViewById(R.id.EventVideoExpand);
        EventConfirmInterestExpand = findViewById(R.id.EventConfirmInterestExpand);
        EventMayAttendExpand = findViewById(R.id.EventMayAttendExpand);
        EventDeclinedExpand = findViewById(R.id.EventDeclinedExpand);
        EventDocumentExpand = findViewById(R.id.EventDocumentExpand);



        WebVideo.getSettings().setJavaScriptEnabled(true);

        EventId=getIntent().getStringExtra("EventId");
        UserId = getIntent().getStringExtra("UserId");
        if(CheckNetwork.isInternetAvailable(EventDetails.this)){
            GetEventDetails(UserId, EventId);
        }else{
            Toast.makeText(EventDetails.this, "No Internet Connection.Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }



        AddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);


            }
        });


        Interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog alertDialog = new AlertDialog.Builder(EventDetails.this).create();

                alertDialog.setTitle("Set Interest");

                alertDialog.setMessage("Set your interest for this event!");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Attending", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        SetInterest(EventId,"1");

                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Maybe Attending", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        SetInterest(EventId,"2");

                    }});

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Not Attending", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        SetInterest(EventId,"3");

                    }});
                alertDialog.show();


            }
        });

        Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(EventDetails.this, PBCRCommentActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", "event");
                intent.putExtra("img", EventCoverImg);
                intent.putExtra("pbcr_id", EventId);
                intent.putExtra("name", Eventtitle);
                intent.putExtra("date", Date);

                startActivity(intent);
            }
        });

//        eventimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog dialog=new Dialog(EventDetails.this, R.style.DialogAnimation_2);
////                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
//                dialog.setContentView(R.layout.zoom_profile_pic);
//                ZoomImageView img = dialog.findViewById(R.id.img);
//                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
//                RequestOptions options = new RequestOptions()
//                        .centerCrop()
//                        .error(R.drawable.noimage)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .priority(Priority.HIGH);
//                ImageProgress.setVisibility(View.VISIBLE);
//                Glide.with(EventDetails.this).load(EventCoverImg).apply(options).into(img);
//                ImageProgress.setVisibility(View.GONE);
//
//                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
//                dialog.show();
//            }
//        });


        EventImagesExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventImages.toggle();
            }
        });


        EventVideoExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventVideo.toggle();
            }

        });

        EventDocumentExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDocument.toggle();
            }
        });


        EventConfirmInterestExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventConfirmList.toggle();
            }
        });


        EventMayAttendExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventMayAttendList.toggle();
            }
        });


        EventDeclinedExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDeclinedList.toggle();
            }
        });

    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {


            String displayName = "";
            StringBuilder fileName = new StringBuilder();

            if (data != null) {

                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();


                final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
                String selection = null;
                String[] selectionArgs = null;
                Cursor cursor = null;
                String Pdfpath = "";

                if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
                    if (isExternalStorageDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        String p =  Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else if (isDownloadsDocument(uri)) {
                        try {
                            cursor = getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                            if (cursor != null && cursor.moveToFirst())
                            {
                                String Name = cursor.getString(0);
                                Pdfpath = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                DocumetPath = Pdfpath;
                                noDoc.setVisibility(View.VISIBLE);
                                noDoc.setText(Name);

                                new AddMediaInEventAsync(this).execute(UserId, EventId, DocumetPath);


                                if (!TextUtils.isEmpty(path)) {
                                    String p =  Pdfpath;
                                }
                            }
                        }
                        catch (Exception e){
                            Log.e("Doc", "onActivityResult: ", e);
                        }

                    } else if (isMediaDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("image".equals(type)) {
                            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        selection = "_id=?";
                        selectionArgs = new String[]{ split[1] };
                    }
                }

                if ("file".equalsIgnoreCase(uri.getScheme())) {
                }



                if (uriString.startsWith("content://")) {
                    Cursor cursordoc = null;
                    try {
                        cursordoc = getContentResolver().query(uri, null, null, null, null);
                        if (cursordoc != null && cursordoc.moveToFirst()) {
                            displayName = cursordoc.getString(cursordoc.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                        }
                    } finally {
                        cursordoc.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    DocumetPath =  uri.getPath();
                    noDoc.setVisibility(View.VISIBLE);
                    noDoc.setText(displayName);
                    new AddMediaInEventAsync(this).execute(UserId, EventId, DocumetPath);

                }

            }
        }
    }



        public void GetEventDetails(final String user_id, final String event_id){
        MainView.setVisibility(View.GONE);
            progressBarHolder.setVisibility(View.VISIBLE);

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
                                JSONObject EventDetails = json.getJSONObject("event_details");
                                JSONObject Event = EventDetails.getJSONObject("Event");
                                JSONObject Country = EventDetails.getJSONObject("Country");
                                JSONObject State = EventDetails.getJSONObject("State");
                                JSONObject EventsType = EventDetails.getJSONObject("EventsType");
//                                try {
//                                    JSONArray user_event_banner_photo = EventDetails.getJSONArray("UserEventBannerPhoto");
//                                    JSONObject banner_photo = user_event_banner_photo.getJSONObject(0);
//                                    EventCoverImg =json.getString("cover_image_url")+banner_photo.getString("event_id")+"/"+banner_photo.getString("banner_path");
//                                    Glide.with(EventDetails.this).asBitmap().load(EventCoverImg).into(event_cover_image);
//                                }catch(Exception e){
//                                    e.printStackTrace();
//                                }
                                JSONArray user_event_banner_photo = EventDetails.getJSONArray("UserEventBannerPhoto");
//                                EventCoverImg =json.getString("cover_image_url")+banner_photo_details.getString("event_id")+"/"+banner_photo_details.getString("banner_path");
                                for (int j = 0; j < user_event_banner_photo.length(); j++) {
                                    JSONObject banner_photo_details = user_event_banner_photo.getJSONObject(j);
                                    final CertifiedBannerModel certifiedBannerModel= new CertifiedBannerModel();
                                    EventCoverImg =json.getString("cover_image_url")+banner_photo_details.getString("event_id")+"/"+banner_photo_details.getString("banner_path");
//                                        Glide.with(CertifiedBusinessDetailActivity.this).asBitmap().load(banner_img_url).into(bg_img);
                                    certifiedBannerModel.setBanner_image(EventCoverImg);
                                    banner_list.add(certifiedBannerModel);
                                    mPager.setAdapter(new BannerAdapter(EventDetails.this, banner_list));
                                    mPager.setCurrentItem(1);
                                    indicator.setViewPager(mPager);
                                }
                                JSONArray UserEventPhoto = EventDetails.getJSONArray("UserEventPhoto");
                                JSONArray UserEventDocument = EventDetails.getJSONArray("UserEventDocument");
                                JSONArray EventGuest = EventDetails.getJSONArray("EventGuest");
                                title.setText(Event.getString("event_title").replace("null",""));
                                description.setText(Event.getString("description").replace("null",""));
                                if(Event.getString("description").length() > 100){
                                    makeTextViewResizable(description, 5, "View More", true);
                                }
                                startdate.setText(Event.getString("start_date").replace("null",""));
                                enddate.setText(Event.getString("end_date").replace("null",""));
                                type.setText(EventsType.getString("title").replace("null",""));
                                country.setText(Country.getString("name").replace("null",""));
                                String Date  = Event.getString("start_date");
                                Eventtitle = Event.getString("event_title");

//                                if (!Event.getString("image").equalsIgnoreCase("null")||!Event.getString("image").equalsIgnoreCase("")){
//
//                                    EventImage = json.getString("cover_image_url") +Event.getString("id")+ "/" +Event.getString("image");
//                                    final RequestOptions options = new RequestOptions()
//                                            .centerCrop()
//                                            .error(R.drawable.noimage)
//                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                            .priority(Priority.HIGH);
////                                    EventCoverImg = json.getString("cover_image_url") +Event.getString("id")+ "/" +Event.getString("image");
//                                    Glide.with(EventDetails.this).load(EventImage).apply(options).into(eventimage);
//
//
//                                }


                                if (!Event.getString("embedded_video").equalsIgnoreCase("") && !Event.getString("embedded_video").equalsIgnoreCase("null")){
                                    //  String html="<iframe width=\"400\" height=\"300\" src=\"https://www.youtube.com/embed/PHXxKZkeFmc\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
//                                                   <iframe width="560" height="315" src="https://www.youtube.com/embed/hy_FA_YLrP0" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                                    EventVideoExpand.setVisibility(View.VISIBLE);
                                    String html=Event.getString("embedded_video");
                                    Scanner scanner = new Scanner(html);
                                    boolean isWidhtFound = false, isHeightFound = false;
                                    StringBuilder htmlUrl = new StringBuilder();
                                    while (scanner.hasNext()) {
                                        String possibleUrl = scanner.next();
                                        Log.i("Scan", "onCreate: possibleUrl:-" + possibleUrl);
                                        if (possibleUrl.contains("width")) {
                                            isWidhtFound = true;
                                            isHeightFound = false;

                                        }
                                        else if (possibleUrl.contains("height")) {
                                            isHeightFound = true;
                                            isWidhtFound = false;

                                        }
                                        if (isWidhtFound){
                                            htmlUrl.append(" ").append("width=\"300\" ");
                                            isWidhtFound = false;
                                        }
                                        else if (isHeightFound){
                                            htmlUrl.append(" ").append("height=\"200\" ");
                                            isHeightFound = false;
                                        }
                                        else{
                                            htmlUrl.append(possibleUrl);

                                        }

                                    }
                                    Log.i("htmlUrl", "onCreate: htmlUrl"+htmlUrl);
                                    String iframeurl = String.valueOf(htmlUrl);
                                    if (iframeurl.contains("allowfullscreen></iframe>")){
                                        WebVideo.loadData(htmlUrl.toString(), "text/html", null);
                                    }
                                    else{
                                        WebVideo.loadData(htmlUrl.toString() + "allowfullscreen></iframe>", "text/html", null);
                                    }
                                }
                                else{
                                    EventVideoExpand.setVisibility(View.GONE);
                                }



                                if (Event.getString("allow_document").equalsIgnoreCase("1")){
                                    EventDocumentExpand.setVisibility(View.VISIBLE);
                                    DocumentLinear.setVisibility(View.VISIBLE);
                                }
                                if (Event.getString("allow_photo").equalsIgnoreCase("1")){
                                    EventImagesExpand.setVisibility(View.VISIBLE);
                                }
                                if (Event.getString("allow_guests").equalsIgnoreCase("1")){
                                    Interested.setVisibility(View.VISIBLE);
                                }
                                if (Event.getString("show_guests_list").equalsIgnoreCase("1")){
                                    GetEventGuestList( user_id, event_id);
                                }
                                if (Event.getString("allow_comments").equalsIgnoreCase("1")){
                                    Comments.setVisibility(View.VISIBLE);
                                }


                                if (UserEventPhoto.length() >= 1){
                                    for (int j = 0; j < UserEventPhoto.length(); j++) {
                                        JSONObject EventPhotos = UserEventPhoto.getJSONObject(j);
                                        String EventImg = json.getString("event_images_url") +EventPhotos.getString("id")+ "/" +EventPhotos.getString("path");
                                        ImagesList.add(EventImg);
                                        }

                                    if (ImagesList.size() == 0) {
                                        mRecyclerView.setVisibility(View.GONE);
                                        ImageLinear.setVisibility(View.GONE);
                                    } else {
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        ImageLinear.setVisibility(View.VISIBLE);
                                        mAdapter = new MultipleImagesAdapter(EventDetails.this, ImagesList);
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                }
                                else {
                                    noImage.setVisibility(View.VISIBLE);
                                }


                                if (UserEventDocument.length() >= 1){
//                                    for (int j = 0; j < UserEventPhoto.length(); j++) {
//                                        JSONObject EventPhotos = UserEventPhoto.getJSONObject(j);
//                                        String EventImg = json.getString("event_images_url") +EventPhotos.getString("id")+ "/" +EventPhotos.getString("path");
//                                        ImagesList.add(EventImg);
//                                    }
//
//                                    if (ImagesList.size() == 0) {
//                                        mRecyclerView.setVisibility(View.GONE);
//                                        ImageLinear.setVisibility(View.GONE);
//                                    } else {
//                                        mRecyclerView.setVisibility(View.VISIBLE);
//                                        ImageLinear.setVisibility(View.VISIBLE);
//                                        mAdapter = new MultipleImagesAdapter(EventDetails.this, ImagesList);
//                                        mRecyclerView.setAdapter(mAdapter);
//                                    }
                                }
                                else {
                                    noDoc.setVisibility(View.VISIBLE);
                                }


                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                            } else {
                                MainView.setVisibility(View.GONE);
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(EventDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    public void GetEventGuestList(String user_id,String event_id){
        String url = (CommonUtils.BASE_URL) + "event_guestlist";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add(" event_id", event_id)
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
                                JSONArray EventGuest = json.getJSONArray("EventGuest");

                                if (EventGuest.length() >= 1){

                                    String InterestType = "";
                                    for (int j = 0; j < EventGuest.length(); j++) {
                                        JSONObject Guest = EventGuest.getJSONObject(j);

                                        if (Guest.getString("rsvp_status").equalsIgnoreCase("1")){


                                            MemberDataModel model = new MemberDataModel();
                                            model.setMemberName(Guest.getString("name").replace("null", ""));
                                            model.setMemberAddress("Attending");
                                            model.setInterestType(Guest.getString("rsvp_status"));
                                            model.setMemberId(Guest.getString("user_id").replace("null", ""));
                                            model.setMemberImage(json.getString("profile_pic_url") + Guest.getString("user_id")+ "/" +Guest.getString("profile_picture"));
                                            MembersAttendingArrayList.add(model);
                                        }
                                        else if (Guest.getString("rsvp_status").equalsIgnoreCase("2")){
                                            MemberDataModel model = new MemberDataModel();
                                            model.setMemberName(Guest.getString("name").replace("null", ""));
                                            model.setMemberAddress("May be Attending");
                                            model.setInterestType(Guest.getString("rsvp_status"));
                                            model.setMemberId(Guest.getString("user_id").replace("null", ""));
                                            model.setMemberImage(json.getString("profile_pic_url") + Guest.getString("user_id")+ "/" +Guest.getString("profile_picture"));
                                            MembersMaybeAttendingArrayList.add(model);
                                        }
                                        else if (Guest.getString("rsvp_status").equalsIgnoreCase("3")){
                                            MemberDataModel model = new MemberDataModel();
                                            model.setMemberName(Guest.getString("name").replace("null", ""));
                                            model.setMemberAddress("Not Attending");
                                            model.setInterestType(Guest.getString("rsvp_status"));
                                            model.setMemberId(Guest.getString("user_id").replace("null", ""));
                                            model.setMemberImage(json.getString("profile_pic_url") + Guest.getString("user_id")+ "/" +Guest.getString("profile_picture"));
                                            MembersDeclinedArrayList.add(model);
                                        }

                                    }

                                    if (MembersAttendingArrayList.size() == 0){

                                        EventConfirmInterestExpand.setVisibility(View.VISIBLE);
                                        noCfmList.setVisibility(View.VISIBLE);
                                        ConfirmedLinear.setVisibility(View.VISIBLE);


                                    }
                                    if (MembersMaybeAttendingArrayList.size() == 0){
                                        EventMayAttendExpand.setVisibility(View.VISIBLE);
                                        noMayList.setVisibility(View.VISIBLE);
                                        MayAttendLinear.setVisibility(View.VISIBLE);

                                    }
                                    if (MembersDeclinedArrayList.size() == 0){
                                        EventDeclinedExpand.setVisibility(View.VISIBLE);
                                        noDeclineList.setVisibility(View.VISIBLE);
                                        DeclineLinear.setVisibility(View.VISIBLE);

                                    }


                                        adapter = new MembersAdapter(EventDetails.this, MembersAttendingArrayList, EventDetails.this,"");
                                        layoutmanager = new LinearLayoutManager(EventDetails.this);
                                        confirmed_listview.setLayoutManager(layoutmanager);
                                        confirmed_listview.setAdapter(adapter);

                                        adapter = new MembersAdapter(EventDetails.this, MembersMaybeAttendingArrayList, EventDetails.this,"");
                                        layoutmanager = new LinearLayoutManager(EventDetails.this);
                                        may_attend_listview.setLayoutManager(layoutmanager);
                                        may_attend_listview.setAdapter(adapter);

                                        adapter = new MembersAdapter(EventDetails.this, MembersDeclinedArrayList, EventDetails.this,"");
                                        layoutmanager = new LinearLayoutManager(EventDetails.this);
                                        declined_listview.setLayoutManager(layoutmanager);
                                        declined_listview.setAdapter(adapter);


                                }

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

            Glide.with(context1).load(MultipleImagesList.get(position)).into(holder.adapterImage);

            holder.adapterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog=new Dialog(EventDetails.this, R.style.DialogAnimation_2);
//                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    dialog.setContentView(R.layout.zoom_profile_pic);
                    ZoomImageView img = dialog.findViewById(R.id.img);
                    final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                    ImageProgress.setVisibility(View.VISIBLE);
                    Glide.with(EventDetails.this).load(MultipleImagesList.get(position)).into(img);
                    ImageProgress.setVisibility(View.GONE);

                    /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                    dialog.show();
                }
            });
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


    public void SetInterest(String EventId, String Status) {

        String url = (CommonUtils.BASE_URL)+"is_interested_in_event";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("event_id", EventId)
                .add("event_intrested_status", Status)

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
                                Toast.makeText(EventDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            else if (success.equalsIgnoreCase("failed")){
                                Toast.makeText(EventDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
    class AddMediaInEventAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;

        public AddMediaInEventAsync(Context context) {


        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("event_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("img1", params[2]));


                Log.d("datap", nameValuePairs.toString());

                String Url = (CommonUtils.BASE_URL)+"add_event_document";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHolder.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    progressBarHolder.setVisibility(View.GONE);
                    Toast.makeText(EventDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                    GetEventDetails( UserId, EventId);
                } else {
                    progressBarHolder.setVisibility(View.GONE);
                    Toast.makeText(EventDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
