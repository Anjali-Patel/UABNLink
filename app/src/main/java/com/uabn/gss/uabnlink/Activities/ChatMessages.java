package com.uabn.gss.uabnlink.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.RefreshChat;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.ChatMessagesAdapter;
import com.uabn.gss.uabnlink.fragment.PostFragment;
import com.uabn.gss.uabnlink.model.ChatMessageModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatMessages extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener, RefreshChat{

    SharedPreferenceUtils preferances;
    EditText Message;
    ImageView Send, userImage, AddMedia;
    String FriendId, UserName, UserImage;
    String UserId;
    ProgressBar MessageProgress;
    TextView user_name, SelectedDoc;
    RelativeLayout ChatRelative;

    BSImagePicker pickerDialog;
    AlertDialog alertDialog;

    ChatMessagesAdapter adapter;
    RecyclerView chatmessageslist, Images_listview;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<ChatMessageModel> MessageDataModelArrayList = new ArrayList<>();

    AddPhotoBottomDialogFragment addPhotoBottomDialogFragment;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<String> AttatchmentList = new ArrayList<String>();
    String AttatchmentPath =  "";
   static Context mContext;
   public static RefreshChat refreshchat;
    private RecyclerView.Adapter mAdapter;
    int PageNo = 0;
    int lastVisible = 0;
    boolean isKeyboardShowing = false, ischatDataLoading = false;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        refreshchat = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        preferances= SharedPreferenceUtils.getInstance(this);

        mContext = ChatMessages.this;
        refreshchat = this;
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        FriendId = getIntent().getStringExtra("ToUserId");
//        UserId = "35";
//        FriendId = "40";
        UserName = getIntent().getStringExtra("ToUserName");
        UserImage = getIntent().getStringExtra("UserImage");



        chatmessageslist = findViewById(R.id.chatmessageslist);
        Message = findViewById(R.id.Message);
        Send = findViewById(R.id.Send);

        user_name = findViewById(R.id.user_name);
        userImage = findViewById(R.id.userImage);
        AddMedia = findViewById(R.id.AddMedia);
        ChatRelative = findViewById(R.id.ChatRelative);
        Images_listview = findViewById(R.id.Images_listview);
        SelectedDoc = findViewById(R.id.SelectedDoc);

        Images_listview.setHasFixedSize(true);
        Images_listview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(PBCRCommentActivity.this, 2));
        Images_listview.setItemAnimator(new DefaultItemAnimator());


        user_name.setText(UserName);
         RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(this).load(UserImage).apply(options).into(userImage);

        Message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isKeyboardShowing = hasFocus;
            }
        });



        chatmessageslist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                 lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 1  >= totalItemCount;
                if (!ischatDataLoading && totalItemCount == (lastVisible + 1)) {
                    PageNo = PageNo + 1;
                    ischatDataLoading = true;
                    GetAllMessages(PageNo);
                }
            }
        });


//        adapter.setOnLoadMoreListener(new ChatMessagesAdapter.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if (MessageDataModelArrayList.size() <= 10) {
//
//                    PageNo = PageNo + 1;
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            GetAllMessages(PageNo);
//                        }
//                    }, 5000);
//                } else {
//                    Toast.makeText(ChatMessages.this, "Loading data completed", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Constants.hideKeyboard(ChatMessages.this);
                SelectedDoc.setVisibility(View.GONE);
                Images_listview.setVisibility(View.GONE);

                String StrMessage=Message.getText().toString().trim();
                if(StrMessage.equalsIgnoreCase("")){
                    Toast.makeText(ChatMessages.this,"Please enter some text",Toast.LENGTH_LONG).show();
                }else{
                    //progressBarHolder.setVisibility(View.VISIBLE);
                    //PostMessage(UserId, FriendId, StrMessage);
                    new PostChatMsgAsync().execute(UserId, FriendId, StrMessage);
                    Message.setText("");
                }
            }
        });

        AddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isKeyboardShowing) {
//                    Constants.hideKeyboard(ChatMessages.this);
                }

                ImagesList.clear();

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatMessages.this, android.R.style.Theme_Translucent_NoTitleBar);
                ViewGroup viewGroup = findViewById(android.R.id.content);

                LinearLayout tv_btn_add_photo_camera, tv_btn_add_video, tv_btn_add_music, tv_btn_add_document;
                String ChatFriendId;
                RelativeLayout MediaSelectionRelative;

                View dialogView = LayoutInflater.from(ChatMessages.this).inflate(R.layout.media_selection_sheet, viewGroup, false);

                tv_btn_add_photo_camera = dialogView.findViewById(R.id.tv_btn_add_photo_camera);
                tv_btn_add_video = dialogView.findViewById(R.id.tv_btn_add_video);
                tv_btn_add_music = dialogView.findViewById(R.id.tv_btn_add_music);
                tv_btn_add_document = dialogView.findViewById(R.id.tv_btn_add_document);
                MediaSelectionRelative = dialogView.findViewById(R.id.MediaSelectionRelative);


                MediaSelectionRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();

                    }
                });

                tv_btn_add_photo_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                                .setMaximumDisplayingImages(Integer.MAX_VALUE)
                                .isMultiSelect()
                                .setMinimumMultiSelectCount(1)
                                .setMaximumMultiSelectCount(5)
                                .build();

                        pickerDialog.show(getSupportFragmentManager(), "Picker");

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
                        startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);


                    }
                });

                tv_btn_add_music.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent_upload = new Intent();
                        intent_upload.setType("audio/*");
                        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent_upload,1);
                    }
                });

                tv_btn_add_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent_upload = new Intent();
                        intent_upload.setType("video/*");
                        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent_upload,"Select Video"),2);                }
                });




                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

//                addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
//                addPhotoBottomDialogFragment.setContext(ChatMessages.this);
//                addPhotoBottomDialogFragment.setChatFriendId(FriendId);
//                addPhotoBottomDialogFragment.show(getSupportFragmentManager(), "add_photo_dialog_fragment");
            }
        });



        //set load more listener for the RecyclerView adapter



    GetAllMessages(PageNo);
    }

    public void GetAllMessages(final int PageNo) {

       // progressBarHolder.setVisibility(View.VISIBLE);



       if (PageNo == 0){
           MessageDataModelArrayList.clear();
       }

//        String url = "http://demo1.geniesoftsystem.com/newweb/icomuabn/index.php/apis/show_chat";
        String url = (CommonUtils.BASE_URL)+"show_chat";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("friend_id", FriendId)
                .add("page_no", String.valueOf(PageNo))
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
                                Message.setText("");

                                JSONArray Messages = json.getJSONArray("chat_data");

                                for (int j = 0; j < Messages.length(); j++) {
                                    JSONObject Chat = Messages.getJSONObject(j);
                                    JSONObject MessageDetails = Chat.getJSONObject("Chat");
                                    JSONArray ImageDetails = Chat.getJSONArray("Chat_images");


                                    ChatMessageModel model = new ChatMessageModel();
                                    model.setChatId(MessageDetails.getString("id"));
                                    model.setFromUserId(MessageDetails.getString("from_user_id"));
                                    model.setToUserId(MessageDetails.getString("to_user_id"));
                                    model.setMessageType(MessageDetails.getString("type"));
                                    model.setMessage(MessageDetails.getString("message"));


//                                    if (MessageDetails.getString("type").equalsIgnoreCase("")){
//                                        model.setMessage(MessageDetails.getString("message"));
//                                    }
                                     if (MessageDetails.getString("type").equalsIgnoreCase("image") && ImageDetails.length() > 0){



//                                         if (media.contains(",")){
                                             AttatchmentList.clear();
//                                             String[] mediaelements = media.split(",");



                                            for (int i = 0; i < ImageDetails.length(); i++) {

                                                JSONObject image = ImageDetails.getJSONObject(i);

                                                String MediaUrl = json.getString("attachment_url") + image.getString("images");
                                                AttatchmentList.add(MediaUrl);

                                            }

                                            if (AttatchmentList.size() > 0) {
                                                String img1URL = AttatchmentList.get(0);
                                                model.setAttatchment1(img1URL);
                                            }
                                            //--------------- Image 1------------//


                                            if (AttatchmentList.size() > 1) {
                                                String img1URL = AttatchmentList.get(1);
                                                model.setAttatchment2(img1URL);
                                            }
                                            //--------------- Image 2------------//

                                            if (AttatchmentList.size() > 2) {
                                                String img1URL = AttatchmentList.get(2);
                                                model.setAttatchment3(img1URL);
                                            }
                                            //--------------- Image 3------------//


                                            if (AttatchmentList.size() > 3) {
                                                String img1URL = AttatchmentList.get(3);
                                                model.setAttatchment4(img1URL);
                                            }
                                            //--------------- Image 4------------//


                                            if (AttatchmentList.size() > 4) {
                                                String img1URL = AttatchmentList.get(4);
                                                model.setAttatchment4(img1URL);
                                            }
                                            //--------------- Image 5------------//




//                                        else{
//                                            String MediaUrl = json.getString("attachment_url") + MessageDetails.getString("attachments");
//                                            model.setMessage(MediaUrl);
//                                        }



                                    }
                                    else if (MessageDetails.getString("type").equalsIgnoreCase("audio") && ImageDetails.length() > 0) {

                                         for (int i = 0; i < ImageDetails.length(); i++) {

                                             JSONObject video = ImageDetails.getJSONObject(i);
                                             String AudioURL = json.getString("attachment_url") + video.getString("images");
                                             model.setChatAudio(AudioURL);
                                         }

                                    }

                                    else if (MessageDetails.getString("type").equalsIgnoreCase("document") && ImageDetails.length() > 0) {

                                         for (int i = 0; i < ImageDetails.length(); i++) {

                                             JSONObject doc = ImageDetails.getJSONObject(i);
                                             String DocURL = json.getString("attachment_url") + doc.getString("images");
                                             model.setChatDoc(DocURL);
                                         }

                                    }

                                    else if (MessageDetails.getString("type").equalsIgnoreCase("video") && ImageDetails.length() > 0) {

                                         for (int i = 0; i < ImageDetails.length(); i++) {

                                             JSONObject video = ImageDetails.getJSONObject(i);
                                             String VideoURL = json.getString("attachment_url") + video.getString("images");
                                             model.setChatVideo(VideoURL);
                                         }

                                    }


                                    if (!MessageDetails.getString("posted_date").equalsIgnoreCase("null")) {
                                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MessageDetails.getString("posted_date"));
                                        String newString = new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019
                                        Date td_date = new Date();
                                        String todayStr = new SimpleDateFormat("dd-MM-yyyy").format(td_date); //
                                        if (newString.compareTo(todayStr) == 0) {
                                            newString = new SimpleDateFormat("hh:mm a").format(date); // 9:00
                                            model.setMessageDate(newString);
                                        } else {
                                            newString = new SimpleDateFormat("dd-MMM-yyyy").format(date); // 25-03-2019
                                            model.setMessageDate(newString);

                                        }
                                    }


                                    String ProfilePic = json.getString("profile_pic_url") + json.getString("friend_id") + "/" + json.getString("friend_profilepic");
                                    model.setProfilePicture(ProfilePic);

                                    MessageDataModelArrayList.add(model);

                                }
                               // progressBarHolder.setVisibility(View.GONE);

//                                chatmessageslist.setHasFixedSize(true);
//                                mLayoutManager = new LinearLayoutManager(ChatMessages.this);
//                                chatmessageslist.setLayoutManager(mLayoutManager);
//                                adapter = new ChatMessagesAdapter(ChatMessages.this, MessageDataModelArrayList);
//                                chatmessageslist.setAdapter(adapter);
//                                mLayoutManager.setStackFromEnd(true);
//                                chatmessageslist.scrollToPosition(MessageDataModelArrayList.size()-1);

//                                MessageProgress.setVisibility(View.GONE);

                                mLayoutManager = new LinearLayoutManager(ChatMessages.this, LinearLayoutManager.VERTICAL, true);
                                chatmessageslist.setLayoutManager(mLayoutManager);
                                adapter = new ChatMessagesAdapter(ChatMessages.this, MessageDataModelArrayList, ChatMessages.this, chatmessageslist);
                                chatmessageslist.setAdapter(adapter);
                                if (PageNo == 0){
                                    mLayoutManager.setStackFromEnd(true);
                                    chatmessageslist.scrollToPosition(0);
                                }
                                else{
//                                    scrollposition = scrollposition - 1
//                                    scrollposition = scrollposition - 1;
                                    chatmessageslist.scrollToPosition(lastVisible);
                                }

                                ischatDataLoading = false;
                            }

                            else {
                                ischatDataLoading = false;

//                                Toast.makeText(ChatMessages.this,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            ischatDataLoading = false;
                            e.printStackTrace();
                        } catch (ParseException e) {
                            ischatDataLoading = false;
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override

    public void ChatRefresh() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GetAllMessages(PageNo );

            }
        });

    }


    public static class AddPhotoBottomDialogFragment extends BottomSheetDialogFragment {

        public static AddPhotoBottomDialogFragment newInstance() {
            return new AddPhotoBottomDialogFragment();
        }
        Context context;
        LinearLayout tv_btn_add_photo_camera, tv_btn_add_video, tv_btn_add_music, tv_btn_add_document;
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

            View view = inflater.inflate(R.layout.media_selection_sheet, container,
                    false);

            tv_btn_add_photo_camera = view.findViewById(R.id.tv_btn_add_photo_camera);
            tv_btn_add_video = view.findViewById(R.id.tv_btn_add_video);
            tv_btn_add_music = view.findViewById(R.id.tv_btn_add_music);
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
                    startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);


                }
            });

            tv_btn_add_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_upload = new Intent();
                    intent_upload.setType("audio/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload,1);
                }
            });

            tv_btn_add_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_upload = new Intent();
                    intent_upload.setType("video/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent_upload,"Select Video"),2);                }
            });

            // get the views and attach the listener

            return view;

        }





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

            String displayName = null;
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

                if (needToCheckUri && DocumentsContract.isDocumentUri(ChatMessages.this, uri)) {
                    if (isExternalStorageDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        String p =  Environment.getExternalStorageDirectory() + "/" + split[1];
                        AttatchmentPath = p;
//                        new PostChatMsgAsync().execute(UserId, FriendId, "");
                    } else if (isDownloadsDocument(uri)) {
                        try {
                            cursor = ChatMessages.this.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                            if (cursor != null && cursor.moveToFirst())
                            {
                                String Name = cursor.getString(0);
                                Pdfpath = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                AttatchmentPath = Pdfpath;
                                //new PostChatMsgAsync().execute(UserId, FriendId, "");


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
                        cursordoc = ChatMessages.this.getContentResolver().query(uri, null, null, null, null);
                        if (cursordoc != null && cursordoc.moveToFirst()) {
                            displayName = cursordoc.getString(cursordoc.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                            alertDialog.dismiss();
                            SelectedDoc.setText(displayName);
                            SelectedDoc.setVisibility(View.VISIBLE);


                        }
                    } finally {
                        cursordoc.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    AttatchmentPath =  uri.getPath();
                    displayName = myFile.getName();

                    alertDialog.dismiss();
                    SelectedDoc.setText(displayName);
                    SelectedDoc.setVisibility(View.VISIBLE);

                   // new PostChatMsgAsync().execute(UserId, FriendId, "");

                }




            }

        }

        else if(requestCode == 1){

            if(resultCode == RESULT_OK){



                String displayName = "";

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

                    if (needToCheckUri && DocumentsContract.isDocumentUri(ChatMessages.this, uri)) {
                        if (isExternalStorageDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            String MP3Path =  Environment.getExternalStorageDirectory() + "/" + split[1];
                            AttatchmentPath = MP3Path;
//                            MessageProgress.setVisibility(View.VISIBLE);
                            new PostChatMsgAsync().execute(UserId, FriendId, "");
                        } else if (isDownloadsDocument(uri)) {
                            try {
                                cursor = ChatMessages.this.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                                if (cursor != null && cursor.moveToFirst())
                                {
                                    String Name = cursor.getString(0);
                                    String MP3Path = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                    AttatchmentPath = MP3Path;
//                                    MessageProgress.setVisibility(View.VISIBLE);
                                    new PostChatMsgAsync().execute(UserId, FriendId, "");


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
                            cursordoc = ChatMessages.this.getContentResolver().query(uri, null, null, null, null);
                            if (cursordoc != null && cursordoc.moveToFirst()) {
                                displayName = cursordoc.getString(cursordoc.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                            }
                        } finally {
                            cursordoc.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        AttatchmentPath =  uri.getPath();
//                        MessageProgress.setVisibility(View.VISIBLE);
                        new PostChatMsgAsync().execute(UserId, FriendId, "");

                    }




                }
//                    Uri audioFileUri = data.getData();
//                    try {
//                        String MP3Path = PathUtil.getPath(getActivity(), audioFileUri);
//                        AttatchmentPath = MP3Path;
//                        new PostChatMsgAsync().execute(UserId, getChatFriendId(), "");
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }

            }
        }

        else if(requestCode == 2) {

            if (resultCode == RESULT_OK) {



                String displayName = "";

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

                    if (needToCheckUri && DocumentsContract.isDocumentUri(ChatMessages.this, uri)) {
                        if (isExternalStorageDocument(uri)) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            String video = Environment.getExternalStorageDirectory() + "/" + split[1];
                            AttatchmentPath = video;
//                            MessageProgress.setVisibility(View.VISIBLE);
                            new PostChatMsgAsync().execute(UserId, FriendId, "");
                        } else if (isDownloadsDocument(uri)) {
                            try {
                                cursor = ChatMessages.this.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    String Name = cursor.getString(0);
                                    String video = Environment.getExternalStorageDirectory().toString() + "/Download/" + Name;
                                    AttatchmentPath = video;
//                                    MessageProgress.setVisibility(View.VISIBLE);
                                    new PostChatMsgAsync().execute(UserId, FriendId, "");


                                    if (!TextUtils.isEmpty(path)) {
                                        String p = Pdfpath;
                                    }
                                }
                            } catch (Exception e) {
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
                            selectionArgs = new String[]{split[1]};
                        }
                    }

                    if ("file".equalsIgnoreCase(uri.getScheme())) {
                    }


                    if (uriString.startsWith("content://")) {
                        Cursor cursordoc = null;
                        try {
                            cursordoc = ChatMessages.this.getContentResolver().query(uri, null, null, null, null);
                            if (cursordoc != null && cursordoc.moveToFirst()) {
                                displayName = cursordoc.getString(cursordoc.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                            }
                        } finally {
                            cursordoc.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        AttatchmentPath = uri.getPath();
//                        MessageProgress.setVisibility(View.VISIBLE);
                        new PostChatMsgAsync().execute(UserId, FriendId, "");

                    }


                }


//                    Uri videoFileUri = data.getData();
//                    try {
//                        String video = PathUtil.getPath(getActivity(), videoFileUri);
//                        AttatchmentPath = video;
//                        new PostChatMsgAsync().execute(UserId, getChatFriendId(), "");
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }


            }

        }
        super.onActivityResult(requestCode, resultCode, data);


    }


    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

        for (int i = 0; i < uriList.size(); i++) {

            Uri selectedImage = uriList.get(i);
            File imageFile = new File(getRealPathFromURI(selectedImage));
            ImagesList.add(String.valueOf(imageFile));

        }

        if (ImagesList.size() == 0) {
            pickerDialog.dismiss();
            alertDialog.dismiss();
            Images_listview.setVisibility(View.GONE);
        } else {
            pickerDialog.dismiss();
            alertDialog.dismiss();
            SelectedDoc.setVisibility(View.GONE);
            Images_listview.setVisibility(View.VISIBLE);
            mAdapter = new MultipleImagesAdapter(ChatMessages.this, ImagesList);
            Images_listview.setAdapter(mAdapter);

        }

//        if (ImagesList.size() > 0) {
//            new PostChatMsgAsync().execute(UserId, FriendId, "");
//        }



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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == 100) {
//
//            String displayName = null;
//            StringBuilder fileName = new StringBuilder();
//
//            if (data != null) {
//
//                if (data.getClipData() != null) {
//
//                    for (int index = 0; index < data.getClipData().getItemCount(); index++) {
//
//                        Uri uri = data.getClipData().getItemAt(index).getUri();
//                        String uriString = uri.toString();
//                        File myFile = new File(uriString);
//                        String path = myFile.getAbsolutePath();
//
//
//                        final String docId = DocumentsContract.getDocumentId(uri);
//                        final String[] split = docId.split(":");
//                        final String type = split[0];
//
//                        if ("primary".equalsIgnoreCase(type)) {
//                            String a =  Environment.getExternalStorageDirectory() + "/" + split[1];
//                            DocumetPath = a;
//                            Log.i("Path", "onActivityResult: "+a);
//                        }
//
//                        try {
//                            String mypath = PathUtil.getPath(this, uri);
//                            DocumetPath = mypath;
//
//
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }
//
//
//
//
//                        if (uriString.startsWith("content://")) {
//                            Cursor cursor = null;
//                            try {
//                                cursor = this.getContentResolver().query(uri, null, null, null, null);
//                                if (cursor != null && cursor.moveToFirst()) {
//                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//
//                                }
//                            } finally {
//                                cursor.close();
//                            }
//                        } else if (uriString.startsWith("file://")) {
//                            displayName = myFile.getName();
//
//                        }
//
//                        Log.d("Path", myFile.getAbsolutePath());
//                        fileName.append(displayName).append("\n");
//                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
//                    }
//
//                } else {
//
//                    Uri uri = data.getData();
//                    String uriString = uri.toString();
//                    File myFile = new File(uriString);
//                    String path = myFile.getAbsolutePath();
//                    String result = myFile.getPath();
//
//
//                    final String docId = DocumentsContract.getDocumentId(uri);
//                    final String[] split = docId.split(":");
//                    final String type = split[0];
//
//                    if ("primary".equalsIgnoreCase(type)) {
//                        String a =  Environment.getExternalStorageDirectory() + "/" + split[1];
//
//                        Log.i("Path", "onActivityResult: "+a);
//                    }
//
//                    try {
//                        String mypath = PathUtil.getPath(this, uri);
//                        DocumetPath = mypath;
//
//
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    if (uriString.startsWith("content://")) {
//                        Cursor cursor = null;
//                        try {
//                            cursor = this.getContentResolver().query(uri, null, null, null, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                            }
//                        } finally {
//                            cursor.close();
//                        }
//                    } else if (uriString.startsWith("file://")) {
//                        displayName = myFile.getName();
//
//                    }
//
//                    Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
//
//                    Log.d("fileUri: ", String.valueOf(uri));
//                }
//            }
//        }
//
//        else if(requestCode == 1){
//
//            if(resultCode == RESULT_OK){
//
//                Uri audioFileUri = data.getData();
//                try {
//                    String MP3Path = PathUtil.getPath(ChatMessages.this, audioFileUri);
//                    AudioPath = MP3Path;
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        else if(requestCode == 123){
//
//            if(resultCode == RESULT_OK){
//
//                Uri videoFileUri = data.getData();
//                try {
//                    String video = PathUtil.getPath(ChatMessages.this, videoFileUri);
//                    VideoPath = video;
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//
//
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//    }



    class PostChatMsgAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
        boolean IsMedia = false;


        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("friend_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("message", params[2]));


                int Imagecount = 0;
                if (ImagesList.size() > 0){
                    for (int i=0;i<ImagesList.size();i++){
                        IsMedia = true;
//                         progressBarHolder.setVisibility(View.VISIBLE);


                        nameValuePairs.add(new BasicNameValuePair("attachment"+(Imagecount+1), ImagesList.get(i)));
                        Imagecount++;
                    }
                }
                else if (!AttatchmentPath.equalsIgnoreCase("")){
                    IsMedia = true;
                    nameValuePairs.add(new BasicNameValuePair("attachment1", AttatchmentPath));
                }


                Log.d("datap", nameValuePairs.toString());

//                String Url = "http://demo1.geniesoftsystem.com/newweb/icomuabn/index.php/apis/send_msgs";
                String Url = (CommonUtils.BASE_URL)+"send_msgs";

                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Constants.hideKeyboard(ChatMessages.this);

            if (IsMedia){
//                MessageProgress.setVisibility(View.VISIBLE);
            }
            else{
//                MessageProgress.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            AttatchmentPath = "";
            ImagesList.clear();

            try {
                String success = json.getString("status");
               // {"status":"success","message":"message sent successfully"}

                if (success.equalsIgnoreCase("success")) {

                    PageNo = 0;
//                    MessageProgress.setVisibility(View.GONE);
//                    Intent i = new Intent(mContext, ChatMessages.class);
//                    mContext.startActivity(i);
                    Constants.hideKeyboard(ChatMessages.this);
//                    chatmessageslist.scrollToPosition(0);
                    GetAllMessages(PageNo);

                } else {

                    Constants.hideKeyboard(ChatMessages.this);

//                    MessageProgress.setVisibility(View.GONE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_delete_icon, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            if (MultipleImagesList.get(position).contains("file:")) {
                Picasso.with(context1).load(MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
            } else {
                Picasso.with(context1).load("file:" + MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
            }

            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagesList.remove(MultipleImagesList.get(position));
                    mAdapter.notifyDataSetChanged();
                }
            });



        }

        @Override
        public int getItemCount() {
            return MultipleImagesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adapterImage, deleteImage;


            public MyViewHolder(View v) {
                super(v);
                adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
                deleteImage = (ImageView) v.findViewById(R.id.deleteImage);
            }
        }
    }

}
