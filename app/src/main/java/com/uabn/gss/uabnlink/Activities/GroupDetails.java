package com.uabn.gss.uabnlink.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.adapter.BannerAdapter;
import com.uabn.gss.uabnlink.model.CertifiedBannerModel;
import com.uabn.gss.uabnlink.model.EventsModel;
import com.uabn.gss.uabnlink.model.GroupDocModel;
import com.uabn.gss.uabnlink.model.GroupaudioModel;
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

public class GroupDetails extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener {

    ImageView groupimage,group_banner_img, Play, Pause;
    TextView title, description, groupby, grouptype, accesstype, date, views, LikeGroup, DisLikeGroup, Share, JoinGroup, Comment;
    TextView noImage, noMusic, noDocuemnt, country ,membercount;
    LinearLayout ImageLinear, AudioLinear, DocumentLinear, MusicPlayer, EventLinear;
    ImageView AddImage, AddDocument, AddAudio;
    ProgressBar progress_load;

    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<GroupDocModel> DocumentList = new ArrayList<>();
    ArrayList<GroupaudioModel> AudioList = new ArrayList<>();
    ArrayList<EventsModel> EventList = new ArrayList<>();
    CircleIndicator indicator;
    ViewPager mPager;
    ArrayList<CertifiedBannerModel> banner_list;
    private RecyclerView ImageRecyclerView;
    private RecyclerView AudioRecyclerView;
    private RecyclerView DocumentRecyclerView;
    private RecyclerView EventRecyclerView;

    private RecyclerView.Adapter ImageAdapter;
    private RecyclerView.Adapter AudioAdapter;
    private RecyclerView.Adapter DocumentAdapter;
    private RecyclerView.Adapter EventsAdapter;

    String UserId, GruopId,str_name, str_img, str_date, IsLiked;
    String DocumetPath, GroupImagePath, AudioPath;

    LinearLayout MainView;
    FrameLayout progressBarHolder;
    ExpandableRelativeLayout GroupEvent, GroupDocs, GroupMusic, GroupImages;
    Button GroupEventExpand, GroupDocuementdExpand, GroupMusicExpand, GroupImagesExpand;

    private static final String TAG = "GroupId";
    MediaPlayer mediaplayer;
    private SeekBar playAudioProgress;
    private boolean audioIsPlaying = false;
    private Handler mHandler = new Handler();

    static ShareGroupDialogue share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        UserId = getIntent().getStringExtra("UserId");
        GruopId = getIntent().getStringExtra("GroupId");
        Log.d(TAG ,"GroupId:"+GruopId);
        str_img=getIntent().getStringExtra("img");
        str_name = getIntent().getStringExtra("name");
        str_date = getIntent().getStringExtra("date");
        IsLiked = getIntent().getStringExtra("IsLiked");
        mPager=findViewById(R.id.mPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        banner_list=new ArrayList<>();
        ImageRecyclerView = findViewById(R.id.Images_listview);
        ImageRecyclerView.setHasFixedSize(true);
        ImageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        ImageRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        AudioRecyclerView = findViewById(R.id.Audio_listview);
//        AudioRecyclerView.setHasFixedSize(true);
//        AudioRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
//        AudioRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DocumentRecyclerView = findViewById(R.id.document_listview);
        DocumentRecyclerView.setHasFixedSize(true);
        DocumentRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        DocumentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        EventRecyclerView = findViewById(R.id.event_listview);
        EventRecyclerView.setHasFixedSize(true);
        EventRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        EventRecyclerView.setItemAnimator(new DefaultItemAnimator());

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        groupby = findViewById(R.id.groupby);
        grouptype = findViewById(R.id.grouptype);
        accesstype = findViewById(R.id.accesstype);
        date = findViewById(R.id.date);
        country = findViewById(R.id.country);
        membercount = findViewById(R.id.membercount);
        views = findViewById(R.id.views);
        groupimage = findViewById(R.id.groupimage);
        group_banner_img = findViewById(R.id.group_banner_img);
        ImageLinear = findViewById(R.id.ImageLinear);
        ImageLinear = findViewById(R.id.ImageLinear);
        EventLinear = findViewById(R.id.EventLinear);
        AudioLinear = findViewById(R.id.AudioLinear);
        DocumentLinear = findViewById(R.id.DocumentLinear);
        MusicPlayer = findViewById(R.id.MusicPlayer);
//        Play = findViewById(R.id.playaudio);
        Pause = findViewById(R.id.pauseaudio);
        playAudioProgress = findViewById(R.id.play_audio_progressbar);
        AddImage = findViewById(R.id.AddPhoto);
        AddDocument = findViewById(R.id.AddDoc);
//        AddAudio = findViewById(R.id.AddAudio);
        progress_load = findViewById(R.id.progress_load);
        LikeGroup = findViewById(R.id.LikeGroup);
        DisLikeGroup = findViewById(R.id.DisLikeGroup);
        Share = findViewById(R.id.Share);
        JoinGroup = findViewById(R.id.JoinGroup);
        Comment = findViewById(R.id.Comment);
        noImage = findViewById(R.id.noImage);
//        noMusic = findViewById(R.id.noMusic);
        noDocuemnt = findViewById(R.id.noDocuemnt);
        MainView = findViewById(R.id.MainView);
        progressBarHolder = findViewById(R.id.progressBarHolder);

        GroupEventExpand = findViewById(R.id.GroupEventExpand);
        GroupDocuementdExpand = findViewById(R.id.GroupDocuementdExpand);
//        GroupMusicExpand = findViewById(R.id.GroupMusicExpand);
        GroupImagesExpand = findViewById(R.id.GroupImagesExpand);

        GroupEvent = findViewById(R.id.GroupEvent);
        GroupDocs = findViewById(R.id.GroupDocs);
//        GroupMusic = findViewById(R.id.GroupMusic);
        GroupImages = findViewById(R.id.GroupImages);


        GroupEventExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupEvent.toggle();
            }
        });


        GroupDocuementdExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupDocs.toggle();
            }
        });

//        GroupMusicExpand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GroupMusic.toggle();
//            }
//        });

        GroupImagesExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupImages.toggle();
            }
        });



        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (IsLiked.equalsIgnoreCase("1")){
            LikeGroup.setVisibility(View.GONE);
            DisLikeGroup.setVisibility(View.VISIBLE);
        }
        else if (IsLiked.equalsIgnoreCase("0")){
            LikeGroup.setVisibility(View.VISIBLE);
            DisLikeGroup.setVisibility(View.GONE);
        }



//        Pause.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                mediaplayer.stop();
//            }
//        });

//        Pause.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                    mediaplayer.pause();
//                    audioIsPlaying = false;
//            }
//        });

//        Play.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mediaplayer.start();
//            }
//        });


//        AddAudio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent_upload = new Intent();
//                intent_upload.setType("audio/*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload,1);
//            }
//        });

        AddDocument.setOnClickListener(new View.OnClickListener() {
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

        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(5)
                        .build();
                pickerDialog.setCancelable(false);

                pickerDialog.show(getSupportFragmentManager(), "Picker");


            }
        });

        LikeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SetLike(GruopId);
                LikeGroup.setVisibility(View.GONE);
                DisLikeGroup.setVisibility(View.VISIBLE);
            }
        });

        DisLikeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDisLike(GruopId);
                DisLikeGroup.setVisibility(View.GONE);
                LikeGroup.setVisibility(View.VISIBLE);
            }
        });

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share= new ShareGroupDialogue();
                share.setContext(GroupDetails.this);
                share.setGroupId(GruopId);
                share.setGroupUserId(UserId);

                share.show(GroupDetails.this.getFragmentManager(),ShareGroupDialogue.class.getSimpleName());
            }
        });

        JoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertDialog = new AlertDialog.Builder(GroupDetails.this).create();

                alertDialog.setTitle("Join Group");

                alertDialog.setMessage("Are you sure you want to join this group?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        JoinGroup(UserId, GruopId);
                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alertDialog.dismiss();

                    }});

                alertDialog.show();

            }
        });

        Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(GroupDetails.this, PBCRCommentActivity.class);
                intent.putExtra("type", "group");
                intent.putExtra("img", str_img);
                intent.putExtra("group_id", GruopId);
                intent.putExtra("name", str_name);
                intent.putExtra("date", str_date);
                startActivity(intent);
            }
        });

        groupimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(GroupDetails.this, R.style.DialogAnimation_2);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.noimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                dialog.setContentView(R.layout.zoom_profile_pic);
                ZoomImageView img = dialog.findViewById(R.id.img);
                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                ImageProgress.setVisibility(View.VISIBLE);
                Glide.with(GroupDetails.this).asBitmap().load(str_img).apply(options).into(img);
                ImageProgress.setVisibility(View.GONE);

                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                dialog.show();
            }
        });


        GetGroupDetails( UserId, GruopId);



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
                        DocumetPath = p;
                        new AddMediaInGroupAsync(this).execute(UserId, GruopId, DocumetPath, "addGroupDocument");

                    } else if (isDownloadsDocument(uri)) {
                        try {
                            cursor = getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                            if (cursor != null && cursor.moveToFirst())
                            {
                                String Name = cursor.getString(0);
                                Pdfpath = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                DocumetPath = Pdfpath;
                                new AddMediaInGroupAsync(this).execute(UserId, GruopId, DocumetPath, "addGroupDocument");


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
                    new AddMediaInGroupAsync(this).execute(UserId, GruopId, DocumetPath, "addGroupDocument");

                }




            }
        }

        else if(requestCode == 1){

            if(resultCode == RESULT_OK){

                Uri audioFileUri = data.getData();
                try {
                    String MP3Path = PathUtil.getPath(GroupDetails.this, audioFileUri);
                    AudioPath = MP3Path;
                    new AddMediaInGroupAsync(this).execute(UserId, GruopId, AudioPath, "addGroupAudio");
                    } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                }
        }




        super.onActivityResult(requestCode, resultCode, data);


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


    private String getAudioPath(Uri uri) {
        String[] data = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    public void GetGroupDetails(String user_id,String group_id){

        MainView.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);

        DocumentList.clear();
        AudioList.clear();
        ImagesList.clear();
        EventList.clear();

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {

//                            {"status":"success","message":"Data found","image_url":"http:\/\/demo1.geniesoftsystem.com\/newweb\/icomuabn\/app\/webroot\/\/files\/group\/image\/","group_photo_url":"http:\/\/demo1.geniesoftsystem.com\/newweb\/icomuabn\/app\/webroot\/\/files\/user_group_photo\/path\/","USER_GROUP_MUSIC":"http:\/\/demo1.geniesoftsystem.com\/newweb\/icomuabn\/app\/webroot\/\/files\/user_group_music\/path\/","USER_GROUP_DOCUMENT":"http:\/\/demo1.geniesoftsystem.com\/newweb\/icomuabn\/app\/webroot\/\/files\/user_group_document\/path\/",
//                                    "group_details":{"Group":{"id":"22","title":"test group","user_id":"40",
//                                    "description":"Testing demo","group_type_id":"7","country_id":"101","state_id":"67",
//                                    "city":"Mumbai","zipcode":"606746","image":"UABN-1570779887812.jpg","bg_image":null,
//                                    "website":"www.testy.com","access_type":"0","allow_audio":"1","allow_comments":"1",
//                                    "allow_documents":"1","video":null,"status":"1","no_comments":"0","no_views":"1",
//                                    "rating":null,"display_date":null,"created_date":"2019-10-11 13:15:11"},"User":{"id":"40","name":"vinay mistry","fname":"vinay","lname":"mistry"},"GroupType":{"id":"7","type_title":"Project Management","status":"active","created":"2019-01-28 16:30:22","updated":"2019-03-19 12:54:19"},"Country":{"id":"101","name":"India","code":"IN","status":true},"State":{"id":"67","country_id":"101","name":"Karnataka","code":"","status":true},"GroupCommentReply":[],"GroupComment":[],"GroupMember":[{"id":"12","user_id":"35","group_id":"22","join_date":"2019-10-11 16:54:21"}],"UserGroupDocument":[],"UserGroupMusic":[],"UserGroupPhoto":[{"id":"12","user_id":"35","group_id":"22","path":"542723e2-e1f7-4b58-80f7-0bc37ed47763.png","display_date":"10-11-2019  04:49 pm",
//                                    "uploaded_date":"2019-10-11 16:49:58","status":"1"}],"UserFavGroup":[]}}
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                JSONObject GroupDetails = json.getJSONObject("group_details");
                                JSONObject Group = GroupDetails.getJSONObject("Group");
                                JSONObject User = GroupDetails.getJSONObject("User");
                                JSONObject GroupType = GroupDetails.getJSONObject("GroupType");
                                JSONObject Country = GroupDetails.getJSONObject("Country");
                                JSONArray UserGroupDocument = GroupDetails.getJSONArray("UserGroupDocument");
                                JSONArray UserGroupMusic = GroupDetails.getJSONArray("UserGroupMusic");
                                JSONArray UserGroupPhoto = GroupDetails.getJSONArray("UserGroupPhoto");
                                JSONArray group_banner=GroupDetails.getJSONArray("BannerPhoto");
                                for (int j = 0; j < group_banner.length(); j++) {
                                    JSONObject banner_photo_details = group_banner.getJSONObject(j);
                                    final CertifiedBannerModel certifiedBannerModel = new CertifiedBannerModel();
                                    final String banner_id = banner_photo_details.getString("banner_type_id");
                                    final String path = banner_photo_details.getString("path");
                                    String banner_img_url = json.getString("bg_image_url")  +banner_id+"/" + path;
//                                        Glide.with(CertifiedBusinessDetailActivity.this).asBitmap().load(banner_img_url).into(bg_img);
                                    certifiedBannerModel.setBanner_image(banner_img_url);
                                    banner_list.add(certifiedBannerModel);
                                    mPager.setAdapter(new BannerAdapter(GroupDetails.this, banner_list));
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
                                JSONArray Events = json.getJSONArray("events");
//                                "Country":{"id":"122","name":"Liberia","code":"LR","status":true},

                                title.setText(Group.getString("title").replace("null",""));
                                description.setText(Group.getString("description").replace("null",""));

                                if(Group.getString("description").length() > 100){
                                    makeTextViewResizable(description, 5, "View More", true);
                                }

                                groupby.setText(User.getString("name").replace("null",""));
                                grouptype.setText(GroupType.getString("type_title").replace("null",""));
                                accesstype.setText(Group.getString("title").replace("null",""));
                                date.setText(Group.getString("created_date").replace("null",""));
                                views.setText(Group.getString("no_views").replace("null",""));
                                country.setText(Country.getString("name").replace("null",""));
                                membercount.setText(json.getString("group_members_count").replace("null",""));

                                String GroupBannerURl = json.getString("bg_image_url") +Group.getString("id")+ "/" +Group.getString("bg_image").replace(" ","%20");
                                Glide.with(GroupDetails.this).asBitmap().load(GroupBannerURl).into(group_banner_img);


                                if (!Group.getString("image").equalsIgnoreCase("null")){
                                    String GroupImg = json.getString("image_url") +Group.getString("id")+ "/" +Group.getString("image");
                                    Glide.with(GroupDetails.this).asBitmap().load(GroupImg).into(groupimage);

                                }
//                                if (Group.getString("allow_audio").equalsIgnoreCase("1")){
//                                    AudioLinear.setVisibility(View.VISIBLE);
////                                    GroupMusicExpand.setVisibility(View.VISIBLE);


                                if (Group.getString("allow_documents").equalsIgnoreCase("1")){
                                    GroupDocuementdExpand.setVisibility(View.VISIBLE);
                                    DocumentLinear.setVisibility(View.VISIBLE);

                                }

                                if (Group.getString("allow_comments").equalsIgnoreCase("1")){}




                                if (UserGroupPhoto.length() >= 1){
                                    for (int j = 0; j < UserGroupPhoto.length(); j++) {
                                        JSONObject GroupPhotos = UserGroupPhoto.getJSONObject(j);
                                        //String URL = "http://demo1.geniesoftsystem.com/newweb/icomuabn/app/webroot//files/user_group_photo/path/"   +GroupPhotos.getString("id")+ "/" +GroupPhotos.getString("path");
                                        String GroupImg = json.getString("group_photo_url") +GroupPhotos.getString("id")+ "/" +GroupPhotos.getString("path");
                                        ImagesList.add(GroupImg);
                                    }

                                    if (ImagesList.size() == 0) {
                                        ImageRecyclerView.setVisibility(View.GONE);
                                        ImageLinear.setVisibility(View.GONE);
                                    } else {
                                        GroupImagesExpand.setVisibility(View.VISIBLE);
                                        ImageLinear.setVisibility(View.VISIBLE);
                                        ImageRecyclerView.setVisibility(View.VISIBLE);
                                        ImageAdapter = new MultipleImagesAdapter(GroupDetails.this, ImagesList);
                                        ImageRecyclerView.setAdapter(ImageAdapter);
                                    }
                                }
                                else{
                                    noImage.setVisibility(View.VISIBLE);
                                }


                                if (UserGroupDocument.length() >= 1){
                                    for (int j = 0; j < UserGroupDocument.length(); j++) {
                                        JSONObject GroupDocs = UserGroupDocument.getJSONObject(j);
                                        String DocPath = json.getString("image_url") +GroupDocs.getString("group_id")+ "/" +GroupDocs.getString("path");
                                        String GroupDoc = json.getString("USER_GROUP_DOCUMENT") +GroupDocs.getString("id")+ "/" +GroupDocs.getString("path");
                                        String DocName = GroupDocs.getString("path");

                                        GroupDocModel model = new GroupDocModel();
                                        model.setDocName(GroupDocs.getString("path"));
                                        model.setDocPath(GroupDoc);

                                        DocumentList.add(model);
                                    }

                                    if (DocumentList.size() == 0) {
                                        DocumentRecyclerView.setVisibility(View.GONE);
                                        DocumentRecyclerView.setVisibility(View.GONE);
                                        DocumentLinear.setVisibility(View.GONE);
                                    } else {
                                        DocumentLinear.setVisibility(View.VISIBLE);
                                        DocumentRecyclerView.setVisibility(View.VISIBLE);
                                        DocumentAdapter = new MultipleDocumentAdapter(GroupDetails.this, DocumentList);
                                        DocumentRecyclerView.setAdapter(DocumentAdapter);
                                    }
                                }
                                else{
                                    noDocuemnt.setVisibility(View.VISIBLE);
                                }

                                if (UserGroupMusic.length() >= 1){
                                    for (int j = 0; j < UserGroupMusic.length(); j++) {
                                        JSONObject Audios = UserGroupMusic.getJSONObject(j);
                                        String AudioPath = "http://demo1.geniesoftsystem.com/newweb/icomuabn/app/webroot//files/user_group_music/path/2/" +Audios.getString("path");
                                        String GroupMusic = json.getString("USER_GROUP_MUSIC") +Audios.getString("id")+ "/" +Audios.getString("path");
                                        GroupaudioModel model = new GroupaudioModel();
                                        model.setAudioName(Audios.getString("path"));
                                        model.setAudioPath(GroupMusic);

                                        AudioList.add(model);
                                    }

                                    if (AudioList.size() == 0) {
//                                        AudioRecyclerView.setVisibility(View.GONE);
                                        AudioLinear.setVisibility(View.GONE);
                                    } else {
                                        AudioLinear.setVisibility(View.VISIBLE);
//                                        AudioRecyclerView.setVisibility(View.VISIBLE);
                                        AudioAdapter = new MultipleAudioAdapter(GroupDetails.this, AudioList);
//                                        AudioRecyclerView.setAdapter(AudioAdapter);
                                    }
                                }
                                else{
//                                    noMusic.setVisibility(View.VISIBLE);
                                }


                                if (Events.length() >= 1){
                                    for (int j = 0; j < Events.length(); j++) {
                                        JSONObject EventDetails = Events.getJSONObject(j);
                                        String EventImageBase = json.getString("EVENT_IMAGE");
                                        String EventImg = EventImageBase +EventDetails.getString("id")+ "/" + EventDetails.getString("image");

                                        EventsModel model = new EventsModel();
                                        model.setEvent_id(EventDetails.getString("id"));
                                        model.setTitle(EventDetails.getString("event_title"));
                                        model.setEventImage(EventImg);
                                        EventList.add(model);
                                    }

                                    if (EventList.size() == 0) {
                                        GroupEventExpand.setVisibility(View.GONE);
                                        EventLinear.setVisibility(View.GONE);
                                    } else {
                                        EventLinear.setVisibility(View.VISIBLE);
                                        GroupEventExpand.setVisibility(View.VISIBLE);
                                        EventsAdapter = new EventAdapter(GroupDetails.this, EventList);
                                        EventRecyclerView.setAdapter(EventsAdapter);
                                    }
                                }
                                else{
                                }

                                MainView.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);

                            } else {
                                MainView.setVisibility(View.GONE);
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
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
            GroupImagePath = String.valueOf(imageFile);
            new AddMediaInGroupAsync(this).execute(UserId, GruopId, GroupImagePath, "addGroupPhoto");


        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
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

            Glide.with(context1).asBitmap().load(MultipleImagesList.get(position)).into(holder.adapterImage);

            holder.adapterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog=new Dialog(GroupDetails.this, R.style.DialogAnimation_2);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    dialog.setContentView(R.layout.zoom_profile_pic);
                    ZoomImageView img = dialog.findViewById(R.id.img);
                    Glide.with(GroupDetails.this).asBitmap().load(MultipleImagesList.get(position)).into(img);

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


    public class MultipleDocumentAdapter extends RecyclerView.Adapter<MultipleDocumentAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<GroupDocModel> MultipleDocList;

        public MultipleDocumentAdapter(Context context, ArrayList<GroupDocModel> DocModelList) {
            context1 = context;
            MultipleDocList = DocModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_doc_list, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            final GroupDocModel Items = MultipleDocList.get(position);

            holder.DocumentName.setText(Items.getDocName());


            holder.DocumentName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URL = "http://demo1.geniesoftsystem.com/newweb/icomuabn/app/webroot//files/user_group_document/path/1/" +MultipleDocList.get(position);
                    Intent i = new Intent(GroupDetails.this,ViewPDF.class);
                    i.putExtra("doc_path",Items.getDocPath());
                    startActivity(i);
                }
            });



        }

        @Override
        public int getItemCount() {
            return MultipleDocList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView DocumentName;

            public MyViewHolder(View v) {
                super(v);
                DocumentName = (TextView) v.findViewById(R.id.adapterDocview);
            }
        }
    }


    public class MultipleAudioAdapter extends RecyclerView.Adapter<MultipleAudioAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<GroupaudioModel> MultipleAudioList;

        public MultipleAudioAdapter(Context context, ArrayList<GroupaudioModel> AudioModelList) {
            context1 = context;
            MultipleAudioList = AudioModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_audio_list, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MyViewHolder holder,  int position) {

            final GroupaudioModel Items = MultipleAudioList.get(position);

            holder.AudioName.setText(Items.getAudioName());
            holder.AudioLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URL = Items.getAudioPath();
                    try {

                        MusicPlayer.setVisibility(View.VISIBLE);

                        mediaplayer.setDataSource(URL);
                        mediaplayer.prepare();
                        int totalTime = mediaplayer.getDuration();
                        playAudioProgress.setMax(totalTime/1000);


                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mediaplayer.start();
                    audioIsPlaying = true;


                    GroupDetails.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(mediaplayer != null){
                                int mCurrentPosition = mediaplayer.getCurrentPosition() / 1000;
                                playAudioProgress.setProgress(mCurrentPosition);
                            }
                            mHandler.postDelayed(this, 1000);
                        }
                    });

                }

            });



        }

        @Override
        public int getItemCount() {
            return MultipleAudioList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView AudioName;
            LinearLayout AudioLinear;

            public MyViewHolder(View v) {
                super(v);
                AudioName = (TextView) v.findViewById(R.id.AudioName);
                AudioLinear = (LinearLayout) v.findViewById(R.id.AudioLinear);
            }
        }
    }



    class AddMediaInGroupAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;

        public AddMediaInGroupAsync(Context context) {


        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("group_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("path", params[2]));


                Log.d("datap", nameValuePairs.toString());

                String Url = (CommonUtils.BASE_URL)+params[3];
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_load.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    progress_load.setVisibility(View.GONE);
                    Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                    GetGroupDetails( UserId, GruopId);
                } else {
                    progress_load.setVisibility(View.GONE);
                    Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();

                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void SetLike(String GroupId) {

        String url = (CommonUtils.BASE_URL)+"add_like_to_group";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                GetGroupDetails( UserId, GruopId);
                                Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<EventsModel> EventList;

        public EventAdapter(Context context, ArrayList<EventsModel> EventModelList) {
            context1 = context;
            EventList = EventModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_event_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            final EventsModel Items = EventList.get(position);

            holder.Title.setText(Items.getTitle());
            Glide.with(context1).asBitmap().load(Items.getEventImage()).into(holder.EventImage);



            holder.EventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(GroupDetails.this,EventDetails.class);
                    i.putExtra("EventId", Items.getEvent_id());
                    i.putExtra("UserId", UserId);
                    startActivity(i);
                }
            });



        }

        @Override
        public int getItemCount() {
            return EventList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView Title;
            ImageView EventImage;

            public MyViewHolder(View v) {
                super(v);
                Title = (TextView) v.findViewById(R.id.Title);
                EventImage = (ImageView) v.findViewById(R.id.EventImage);
            }
        }
    }


    public void SetDisLike(String GroupId) {

        String url = (CommonUtils.BASE_URL)+"remove_like_to_group";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                GetGroupDetails( UserId, GruopId);
                                Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(GroupDetails.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public static class ShareGroupDialogue extends DialogFragment {
        Context context;
        String GroupUserId, GroupId;

        ProgressBar progess_load;

        public void setContext(Context context) { this.context = context; }
        public Context getContext() { return context; }

        public void setGroupUserId(String user) { this.GroupUserId = user; }
        public String getGroupUserId() { return GroupUserId; }

        public void setGroupId(String group) { this.GroupId = group; }
        public String getGroupId() { return GroupId; }

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
                        ShareGroup(getGroupUserId(),str_to,str_subject,str_message,getGroupId());
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
            String url = (CommonUtils.BASE_URL)+"share_group";
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", UserId)
                    .add("to_email", str_to)
                    .add("subject", str_subject)
                    .add("message", str_message)
                    .add("group_id", group_id)
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



    public void JoinGroup (String UserId, String GroupId){
        String url = (CommonUtils.BASE_URL) + "join_group";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                        progress_load.setVisibility(View.GONE);

                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(GroupDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(GroupDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
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
