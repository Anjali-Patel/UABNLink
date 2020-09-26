package com.uabn.gss.uabnlink.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.GroupRepliesAdapter;
import com.uabn.gss.uabnlink.model.CommentDataModel;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class GroupcommentReply extends AppCompatActivity implements GroupRepliesAdapter.UpdateGroupReplies , BSImagePicker.OnMultiImageSelectedListener{
   String  update_groupcomment_reply="0";
    static EditText Type_Comment;
    static EditText you_tube_link;
    static String video_type="";
    static EditText Link;
    private static final int SELECT_VIDEO = 2;
    static Dialog Cameradialog;
    RelativeLayout camera_layout;
    static TextView SelectedDoc;
//    private static RecyclerView Images_listview;
    static String AttatchmentPath =  "";
    static AddPhotoBottomDialogFragment addPhotoBottomDialogFragment;
    static ArrayList<String> ImagesList = new ArrayList<String>();
   FrameLayout progressBarHolder;
   ImageView AddMedia;
    private static RecyclerView mRecyclerView;
    String str_comment;
RelativeLayout camera_gallery_vid;
    String CommentId, UserId, CurrentGroupId;
    SharedPreferenceUtils preferances;
//    EditText Type_Comment;
    String Update_id;
    ImageView CommentorImage,Comment_Button;
    TextView CommentDate,CommentText, CommentorName;
    GroupRepliesAdapter.IOnItemClickListener iOnItemClickListener;
    String update_group_comment_id;
    GroupRepliesAdapter adapter;
    RecyclerView commentreplylist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<CommentDataModel> CommentDataModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupcomment_reply);
        AddMedia=findViewById(R.id.AddMedia);
        you_tube_link=findViewById(R.id.you_tube_link);
        Link=findViewById(R.id.Link);
        mRecyclerView = findViewById(R.id.Images_listview);
        SelectedDoc=findViewById(R.id.SelectedDoc);
        AddMedia=findViewById(R.id.AddMedia);
//        Images_listview=findViewById(R.id.Images_listview);
        progressBarHolder=findViewById(R.id.progressBarHolder);
        preferances= SharedPreferenceUtils.getInstance(this);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        Comment_Button=findViewById(R.id.Comment_Button);
        CommentorImage = findViewById(R.id.CommentorImage);
        camera_gallery_vid=findViewById(R.id.camera_gallery_vid);
        CommentDate = findViewById(R.id.CommentDate);
        CommentText = findViewById(R.id.CommentText);
        CommentorName = findViewById(R.id.CommentorName);
        commentreplylist = findViewById(R.id.commentreplylist);
        Type_Comment=findViewById(R.id.Type_Comment);
        CommentId = getIntent().getStringExtra("CommentId");
        CurrentGroupId = getIntent().getStringExtra("GroupId");
        addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        iOnItemClickListener = new GroupRepliesAdapter.IOnItemClickListener() {
            @Override
            public void onItemClick(String text,String text1,String text2) {
                Type_Comment.setText(text);
                update_group_comment_id=text1;
                update_groupcomment_reply=text2;
            }
        };
        Glide.with(this).asBitmap().load(getIntent().getStringExtra("CommentImage")).apply(options).into(CommentorImage);
        CommentDate.setText(getIntent().getStringExtra("CommentDate"));
        CommentText.setText(getIntent().getStringExtra("CommentText"));
        CommentorName.setText(getIntent().getStringExtra("CommentName"));
        GetReplyDetails(UserId,CommentId,CurrentGroupId);
        Comment_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Str_You_TubeLink = "", UrlLink = "";
                String group_id=getIntent().getStringExtra("group_id");
                Str_You_TubeLink = you_tube_link.getText().toString();
                UrlLink = Link.getText().toString().trim();
                if (UrlLink.equalsIgnoreCase("http://")){
                    UrlLink = "";
                }
                 str_comment = Type_Comment.getText().toString();
                if(str_comment.equalsIgnoreCase("")){
                    Toast.makeText(GroupcommentReply.this,"Please enter some text",Toast.LENGTH_LONG).show();
                }else{
                    if(update_groupcomment_reply=="1"){
                        updateGroupComment( UserId,CurrentGroupId, CommentId,update_group_comment_id,str_comment);
                        progressBarHolder.setVisibility(View.VISIBLE);
                        Type_Comment.setText("");
                    }else{
                        new PostGroupcommentReplyAsync().execute(UserId,CurrentGroupId,str_comment,UrlLink , Str_You_TubeLink);
//                        PostCommentReply(UserId,CommentId,CurrentGroupId,str_comment);
                        Type_Comment.setText("");
                    }
                }
            }
            private void updateGroupComment(String userId, String currentGroupId, String commentId, String update_group_comment_id, String str_comment) {
                String url = (CommonUtils.BASE_URL)+"update_group_comment_reply";
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("user_id", userId)
                        .add("group_id", currentGroupId)
                        .add("group_comment_id", commentId)
                        .add("updated_group_comment_reply",str_comment )
                        .add("group_comment_reply_id", update_group_comment_id)
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
                                progressBarHolder.setVisibility(View.GONE);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(myResponse);
                                    String success = json.getString("status");
                                    if (success.equalsIgnoreCase("success")) {
                                        Toast.makeText(  GroupcommentReply.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                        GetReplyDetails(UserId,CommentId,CurrentGroupId);
                                        update_groupcomment_reply="0";
                                        Constants.hideKeyboard(GroupcommentReply.this); }
                                    else {
                                        Toast.makeText(  GroupcommentReply.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
            }
        });
        AddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagesList.clear();
                addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
                addPhotoBottomDialogFragment.setContext(GroupcommentReply.this);
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(), "add_photo_dialog_fragment");
            }
        });



    }

    public void PostCommentReply(final String UserId, final String CommentId, String Group_id, String str_comment) {
        CommentDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"write_comment_reply_on_group";

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id",Group_id)
                .add("comment_id", CommentId)
                .add("comment_reply", str_comment)
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
                                Toast.makeText(GroupcommentReply.this,json.getString("message"),Toast.LENGTH_LONG).show();
                                Constants.hideKeyboard(GroupcommentReply.this);
                                GetReplyDetails(UserId,CommentId,CurrentGroupId);

                            } else {
                                Toast.makeText(GroupcommentReply.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void GetReplyDetails(final String UserId, final String CommentId, final String Group_id) {

        CommentDataModelArrayList.clear();

        String url = (CommonUtils.BASE_URL)+"view_group_reply_comments";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("comment_id", CommentId)
                .add("group_id", Group_id)
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

                                String UserProfileBase = json.getString("profile_pic_url");



                                JSONArray userComments = json.getJSONArray("comments_data");

                                for (int j = 0; j < userComments.length(); j++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(j);
                                    CommentDataModel model = new CommentDataModel();
                                    model.setCommentId(CommentDetails.getString("comment_id"));
                                    model.setCommentorUserId(CommentDetails.getString("user_id"));
                                    model.setCommentatorName(CommentDetails.getString("name"));
                                    model.setCommentetorImage(CommentDetails.getString("profile_picture"));
                                    model.setCommentDate(CommentDetails.getString("display_date"));
                                    model.setCommentText(CommentDetails.getString("comment"));
                                    CommentDataModelArrayList.add(model);
                                }
                                adapter = new GroupRepliesAdapter(GroupcommentReply.this, CommentDataModelArrayList, UserProfileBase, Group_id,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(GroupcommentReply.this);
                                commentreplylist.setLayoutManager(layoutmanager);
                                commentreplylist.setAdapter(adapter);
                                commentreplylist.scrollToPosition(CommentDataModelArrayList.size()-1);
                                //ProgessLoad.setVisibility(View.GONE);



                            }

                            else {

                                adapter = new GroupRepliesAdapter(GroupcommentReply.this, CommentDataModelArrayList, "", Group_id,iOnItemClickListener);
                                layoutmanager = new LinearLayoutManager(GroupcommentReply.this);
                                commentreplylist.setLayoutManager(layoutmanager);
                                commentreplylist.setAdapter(adapter);
                                commentreplylist.scrollToPosition(CommentDataModelArrayList.size()-1);

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
    public void UpdateGroupReplyList() {
        GetReplyDetails(UserId,CommentId,CurrentGroupId);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(GroupcommentReply.this,PBCRCommentActivity.class);
        intent.putExtra("group_id",CurrentGroupId);
        intent.putExtra("type","group");
        startActivity(intent);

    }

    @SuppressLint("ValidFragment")
    public static class AddPhotoBottomDialogFragment extends BottomSheetDialogFragment {
        public static AddPhotoBottomDialogFragment newInstance() {
            return new AddPhotoBottomDialogFragment();
        }
        Context context;
        TextView tv_btn_add_photo_camera, tv_btn_add_video, add_url, tv_btn_add_document;
        String ChatFriendId;
        public void setContext(Context context) { this.context = context; }
        public Context getContext() { return context; }
        public void setChatFriendId(String user) { this.ChatFriendId = user; }
        public String getChatFriendId() { return ChatFriendId; }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.media_selection_sheet_group, container,
                    false);
            tv_btn_add_photo_camera = view.findViewById(R.id.tv_btn_add_photo_camera);
            tv_btn_add_video = view.findViewById(R.id.tv_btn_add_video);
            add_url = view.findViewById(R.id.add_url);
            tv_btn_add_document = view.findViewById(R.id.tv_btn_add_document);
            tv_btn_add_photo_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                            .setMaximumDisplayingImages(Integer.MAX_VALUE)
                            .isMultiSelect()
                            .setMinimumMultiSelectCount(1)
                            .setMaximumMultiSelectCount(5)
                            .build();
                    pickerDialog.show(getChildFragmentManager(), "Picker");
                    addPhotoBottomDialogFragment.dismiss();

                }
            });
            tv_btn_add_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);
                    addPhotoBottomDialogFragment.dismiss();

                }
            });

            add_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPhotoBottomDialogFragment.dismiss();
                    you_tube_link.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    SelectedDoc.setVisibility(View.GONE);
                    Link.setVisibility(View.VISIBLE);

                }
            });


                    tv_btn_add_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                EmbeddedVideo.setVisibility(View.VISIBLE);

                            Cameradialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
                            Cameradialog.setContentView(R.layout.custom_dialog);

                            TextView tv_gallery = Cameradialog.findViewById(R.id.tv_gallery);

                            final RelativeLayout gallery_layout = Cameradialog.findViewById(R.id.gallery_layout);
                            RelativeLayout you_tube = Cameradialog.findViewById(R.id.you_tube);

                            TextView tv_camera = Cameradialog.findViewById(R.id.tv_camera);
                            RelativeLayout  embed_video  = Cameradialog.findViewById(R.id.embed_code);
                            embed_video.setVisibility(View.GONE);
//                            camera_layout = Cameradialog.findViewById(R.id.camera_layout);

                            you_tube.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

//                                    imageurl="";
//                                    camera_gallery_vid.setVisibility(View.GONE);
                                    video_type="video_url";
                                    you_tube_link.setVisibility(View.VISIBLE);
                                    Cameradialog.dismiss();


                                }
                            });
                            gallery_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    you_tube_string = you_tube_link.getText().toString();
//                                    you_tube_link.setText("");
//                                    you_tube_link.setVisibility(View.GONE);
//                                    video_type="upload_video";


                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, SELECT_VIDEO);
                                    Cameradialog.dismiss();

                                }
                            });
//                            camera_layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    you_tube_string = you_tube_link.getText().toString();
//                                    embedded_code = EmbeddedVideo.getText().toString();
//                                    you_tube_link.setText("");
//                                    you_tube_link.setVisibility(View.GONE);
//                                    video_type="upload_video";
//                                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                                    startActivityForResult(intent, CAMERA);
//                                    Cameradialog.dismiss();
//
//
//                                }
//                            });

                            RelativeLayout dialogMainLayout = Cameradialog.findViewById(R.id.dialog_main_layout);
                            dialogMainLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Cameradialog.dismiss();
                                }
                            });




                            Cameradialog.show();
                        }
                    });


            // get the views and attach the listener

            return view;

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
                                String a =  Environment.getExternalStorageDirectory() + "/" + split[1];
                                //DocumetPath = a;
                                Log.i("Path", "onActivityResult: "+a);
                            }

                            try {
                                String mypath = PathUtil.getPath(getActivity(), uri);
                                AttatchmentPath = mypath;

                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }




                            if (uriString.startsWith("content://")) {
                                Cursor cursor = null;
                                try {
                                    cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

//                                        addPhotoBottomDialogFragment.dismiss();
                                        you_tube_link.setVisibility(View.GONE);
                                        Link.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.GONE);
                                        SelectedDoc.setText(displayName);
                                        SelectedDoc.setVisibility(View.VISIBLE);

                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile.getName();

//                                addPhotoBottomDialogFragment.dismiss();
                                you_tube_link.setVisibility(View.GONE);
                                Link.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.GONE);
                                SelectedDoc.setText(displayName);
                                SelectedDoc.setVisibility(View.VISIBLE);

                            }

                            Log.d("Path", myFile.getAbsolutePath());
                            fileName.append(displayName).append("\n");
                            Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
                        }

                    } else {

                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        String path = myFile.getAbsolutePath();


                        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
                        String selection = null;
                        String[] selectionArgs = null;
                        Cursor cursor = null;
                        String Pdfpath = "";

                        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                            if (isExternalStorageDocument(uri)) {
                                final String docId = DocumentsContract.getDocumentId(uri);
                                final String[] split = docId.split(":");
                                String p =  Environment.getExternalStorageDirectory() + "/" + split[1];
                            } else if (isDownloadsDocument(uri)) {
                                try {
                                    cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                                    if (cursor != null && cursor.moveToFirst())
                                    {
                                        String Name = cursor.getString(0);
                                        Pdfpath = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                        AttatchmentPath = Pdfpath;

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
                                cursordoc = getActivity().getContentResolver().query(uri, null, null, null, null);
                                if (cursordoc != null && cursordoc.moveToFirst()) {
                                    displayName = cursordoc.getString(cursordoc.getColumnIndex(OpenableColumns.DISPLAY_NAME));

//                                    addPhotoBottomDialogFragment.dismiss();
                                    you_tube_link.setVisibility(View.GONE);
                                    Link.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.GONE);
                                    SelectedDoc.setText(displayName);
                                    SelectedDoc.setVisibility(View.VISIBLE);

                                }
                            } finally {
                                cursordoc.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                            AttatchmentPath =  uri.getPath();

//                            addPhotoBottomDialogFragment.dismiss();
                            you_tube_link.setVisibility(View.GONE);
                            Link.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.GONE);
                            SelectedDoc.setText(displayName);
                            SelectedDoc.setVisibility(View.VISIBLE);
                        }

                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));

                        Log.d("fileUri: ", String.valueOf(uri));
                    }
                }
            }

            else if(requestCode == 1){

                if(resultCode == RESULT_OK){

                    Uri audioFileUri = data.getData();
                    try {
                        String MP3Path = PathUtil.getPath(getActivity(), audioFileUri);
                        AttatchmentPath = MP3Path;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
            }

            else if(requestCode == 2){

                if(resultCode == RESULT_OK){

                    Uri videoFileUri = data.getData();
                    try {
                        String video = PathUtil.getPath(getActivity(), videoFileUri);
                        AttatchmentPath = video;
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
    }
    class PostGroupcommentReplyAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
        String GroupId;
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("group_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("comment_id", params[2]));
                nameValuePairs.add(new BasicNameValuePair("comment_reply", params[3]));
                nameValuePairs.add(new BasicNameValuePair("reply_link", params[4]));
                nameValuePairs.add(new BasicNameValuePair("video_type", params[4]));
                nameValuePairs.add(new BasicNameValuePair("video_url", params[4]));
                nameValuePairs.add(new BasicNameValuePair("video", params[4]));
                int Imagecount = 0;
                for (int i=0;i<ImagesList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("photo1"+(Imagecount+1), ImagesList.get(i)));
                    Imagecount++;
                }
                if (!AttatchmentPath.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("document1", AttatchmentPath));
                }
                Log.d("datap", nameValuePairs.toString());
                GroupId = params[1];
                String Url = (CommonUtils.BASE_URL) + "write_comment_reply_on_group_new";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ViewProgress.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //{"success":"1","message":"Data saved successfully!"}
            try {
                String success = json.getString("status");
                if (success.equalsIgnoreCase("success")) {
                    ImagesList.clear();
                    Type_Comment.setText("");
                    you_tube_link.setText("");
                    Link.setText("");
                    AttatchmentPath = "";
//                    addPhotoBottomDialogFragment.dismiss();
                    you_tube_link.setVisibility(View.GONE);
                    Link.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    SelectedDoc.setVisibility(View.GONE);
                    Toast.makeText(  GroupcommentReply.this, json.getString("message"), Toast.LENGTH_LONG).show();
                    GetReplyDetails(UserId,CommentId,CurrentGroupId);
                    Constants.hideKeyboard(GroupcommentReply.this);
                    //ViewProgress.setVisibility(View.GONE);
                } else {
                    Toast.makeText(GroupcommentReply.this, "Opps! Some problem occured while posting your update, please ensure your location services are on at the highest accuracy.", Toast.LENGTH_LONG).show();
                    //ViewProgress.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
