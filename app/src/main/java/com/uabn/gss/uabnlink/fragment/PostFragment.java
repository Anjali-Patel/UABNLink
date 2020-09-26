package com.uabn.gss.uabnlink.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.Activities.CertifiedBusinessDetailActivity;
import com.uabn.gss.uabnlink.Activities.ChatMessages;
import com.uabn.gss.uabnlink.Activities.DocumentResourceDetailActivity;
import com.uabn.gss.uabnlink.Activities.MainActivity;
import com.uabn.gss.uabnlink.Activities.PathUtil;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.model.PostCategoryDataModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
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

import static android.app.Activity.RESULT_OK;


public class PostFragment extends Fragment implements BSImagePicker.OnMultiImageSelectedListener {
    ImageView link1,video,gallery,doc,profile_pic;
    TextView user_name;
    RelativeLayout link_relative,video_relative,photo_relative,document_relative;
    LinearLayout img_video;
    String filename="";
    String imageurl="",video_type="";
    public static int VIDEO_CAPTURED = 1;
    Uri videoFileUri;
    Toolbar toolbar;
    private static final int   CAMERA=5;
    private static final int SELECT_VIDEO = 2;
    RelativeLayout camera_gallery_vid;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<PostCategoryDataModel> PostCategoryArrayList = new ArrayList<>();
    String SelectedCategory;
    String selectedCategoryId;
    Dialog Cameradialog;
    ImageView play_bt;
    SharedPreferenceUtils preferances;

    ArrayList<String> PostCategoriesList;
    Spinner post_category;
    AlertDialog alertDialog;
    ImageView camera_gallery_video;
    BSImagePicker pickerDialog;
    EditText Link, EmbeddedVideo, Message,you_tube_link;
    TextView AddLinkButton, AddVideoButton, AddPhotoButton, AddDocButton, SelectedDoc;
    Button ShareButton;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<String> DocumentList = new ArrayList<>();
    String message = "", link = "", embedded_code = "",you_tube_string="";
    RelativeLayout camera_layout;
    static AddPhotoBottomDialogFragment addPhotoBottomDialogFragment;
    View PostView;
    ProgressBar ViewProgress;
    String UserId;
    private static final String TAG = "PostFragment";
    String DocPath = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;

    ArrayList<Uri> imagesUriList;
    ArrayList<String> encodedImageList;
    String imageURI;


    public PostFragment() {
    }


    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        PostView = inflater.inflate(R.layout.fragment_post, container, false);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        link1 = PostView.findViewById(R.id.link);
        user_name=PostView.findViewById(R.id.user_name);
        profile_pic=PostView.findViewById(R.id.profile_pic);
        link_relative = PostView.findViewById(R.id.link_relative);
        video_relative = PostView.findViewById(R.id.video_relative);
        photo_relative = PostView.findViewById(R.id.photo_relative);
        document_relative = PostView.findViewById(R.id.document_relative);
        video = PostView.findViewById(R.id.video);
        user_name.setText(preferances.getStringValue(CommonUtils.NAME,""));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        if (!preferances.getStringValue(CommonUtils.MEMBER_IMAGE,"").equalsIgnoreCase("")) {
            Glide.with(getContext()).load(preferances.getStringValue(CommonUtils.MEMBER_IMAGE, "")).apply(options).into(profile_pic);
        }
        you_tube_link=PostView.findViewById(R.id.you_tube_link);
        camera_gallery_video=PostView.findViewById(R.id.camera_gallery_video);
//        img_video = PostView.findViewById(R.id.img_video);
//        doc = PostView.findViewById(R.id.doc);
        play_bt=PostView.findViewById(R.id.play_bt);
//        gallery = PostView.findViewById(R.id.gallery);
        camera_gallery_vid=PostView.findViewById(R.id.camera_gallery_vid);
        post_category = PostView.findViewById(R.id.post_category);
        Link = PostView.findViewById(R.id.Link);
        EmbeddedVideo = PostView.findViewById(R.id.EmbeddedVideo);
        Message = PostView.findViewById(R.id.Message);
        ViewProgress = PostView.findViewById(R.id.ViewProgress);
//        play_bt.setEnabled(false);
//        AddLinkButton = PostView.findViewById(R.id.AddLinkButton);
//        AddVideoButton = PostView.findViewById(R.id.AddVideoButton);
//        AddPhotoButton = PostView.findViewById(R.id.AddPhotoButton);
//        AddDocButton = PostView.findViewById(R.id.AddDocButton);
        mRecyclerView = PostView.findViewById(R.id.Images_listview);
        SelectedDoc = PostView.findViewById(R.id.SelectedDoc);
        ShareButton = PostView.findViewById(R.id.ShareButton);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        PostCategoriesList = new ArrayList<>();
        PostCategoriesList.add("Select Category");
        video_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EmbeddedVideo.setVisibility(View.VISIBLE);

                Cameradialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
                Cameradialog.setContentView(R.layout.custom_dialog);

                TextView tv_gallery = Cameradialog.findViewById(R.id.tv_gallery);

              final  RelativeLayout gallery_layout = Cameradialog.findViewById(R.id.gallery_layout);
                RelativeLayout you_tube = Cameradialog.findViewById(R.id.you_tube);

                TextView tv_camera = Cameradialog.findViewById(R.id.tv_camera);
                RelativeLayout  embed_video  = Cameradialog.findViewById(R.id.embed_code);
                camera_layout = Cameradialog.findViewById(R.id.camera_layout);

                you_tube.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        embedded_code = EmbeddedVideo.getText().toString();
                        EmbeddedVideo.setVisibility(View.GONE);
                        EmbeddedVideo.setText("");
                        imageurl="";
                        camera_gallery_vid.setVisibility(View.GONE);
                        video_type="video_url";
                        you_tube_link.setVisibility(View.VISIBLE);
                            Cameradialog.dismiss();


                    }
                });
                gallery_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        embedded_code = EmbeddedVideo.getText().toString();
                        you_tube_string = you_tube_link.getText().toString();
                        you_tube_link.setText("");
                        EmbeddedVideo.setText("");
                        EmbeddedVideo.setVisibility(View.GONE);
                        you_tube_link.setVisibility(View.GONE);
                        video_type="upload_video";


                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, SELECT_VIDEO);
                            Cameradialog.dismiss();

                    }
                });
                camera_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        you_tube_string = you_tube_link.getText().toString();
                        embedded_code = EmbeddedVideo.getText().toString();
                        you_tube_link.setText("");
                        EmbeddedVideo.setText("");
                        EmbeddedVideo.setVisibility(View.GONE);
                        you_tube_link.setVisibility(View.GONE);
                        video_type="upload_video";
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            startActivityForResult(intent, CAMERA);
                            Cameradialog.dismiss();


                    }
                });
                embed_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        embedded_code = EmbeddedVideo.getText().toString();
                        you_tube_string = you_tube_link.getText().toString();
                        you_tube_link.setVisibility(View.GONE);
                        you_tube_link.setText("");
                        imageurl="";
                        camera_gallery_vid.setVisibility(View.GONE);

                        video_type="embed_code";
                        EmbeddedVideo.setVisibility(View.VISIBLE);
                            Cameradialog.dismiss();


                    }
                });
                RelativeLayout dialogMainLayout = Cameradialog.findViewById(R.id.dialog_main_layout);
                dialogMainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cameradialog.dismiss();
                    }
                });


//                embed_video.addTextCh(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        embedded_code = EmbeddedVideo.getText().toString();
//                        video_type="embed_code";
//                        if(video_type.equalsIgnoreCase("video_ur")||video_type.equalsIgnoreCase("upload_video")){
//                            Toast.makeText(getContext(),"One video already available",Toast.LENGTH_LONG).show();
//
//                        }else{
//
//                            EmbeddedVideo.setVisibility(View.VISIBLE);
//                            Cameradialog.dismiss();
//                        }
//
//                    }
//                });


                Cameradialog.show();
            }
        });

//
//        camera_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                you_tube_string = you_tube_link.getText().toString();
//                embedded_code = EmbeddedVideo.getText().toString();
//                video_type="upload_video";
//
//                if(video_type.equalsIgnoreCase("embed_code")||video_type.equalsIgnoreCase("video_ur")){
//                    Toast.makeText(getContext(),"One video already available",Toast.LENGTH_LONG).show();
//
//                }else{
//                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    startActivityForResult(intent, CAMERA);
//                    Cameradialog.dismiss();
//                }
//
//            }
//        });

        getUpdateCategory();

        ArrayAdapter<String> Regionadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, PostCategoriesList);
        post_category.setAdapter(Regionadapter);



//        AddLinkButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Link.setVisibility(View.VISIBLE);
//            }
//        });


//        AddVideoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EmbeddedVideo.setVisibility(View.VISIBLE);
//            }
//        });


//        AddPhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
//                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
//                        .isMultiSelect()
//                        .setMinimumMultiSelectCount(1)
//                        .setMaximumMultiSelectCount(5)
//                        .build();
//
//                pickerDialog.show(getChildFragmentManager(), "Picker");
//
//            }
//        });
//
//        AddDocButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("*/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf"};
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);
//            }
//        });

        link_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Link.setVisibility(View.VISIBLE);
            }
        });
        //        video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EmbeddedVideo.setVisibility(View.VISIBLE);
//            }
//        });
        photo_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(5)
                        .setTag("Picker")
                        .build();

                pickerDialog.show(getChildFragmentManager(), "Picker");

            }
        });

        document_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, 100);

                /*Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*//**");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);*/


            }
        });

        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //(String UserId, String category, String message, String link, String embedded_code, String doc) {

//                String message = "", link = "", embedded_code = "",you_tube_string="";

                if ((Link.getText().toString().equalsIgnoreCase("http://"))) {
                    link = "";
                }
                else {
                    link = Link.getText().toString();
                }

                if (!(EmbeddedVideo.getText().toString().equalsIgnoreCase(""))) {
                    embedded_code = EmbeddedVideo.getText().toString();
                }
                if (!(you_tube_link.getText().toString().equalsIgnoreCase(""))) {
                    you_tube_string = you_tube_link.getText().toString();
                }
                if (!(Message.getText().toString().equalsIgnoreCase(""))) {
                    message = Message.getText().toString();
                }
                if ((Message.getText().toString().equalsIgnoreCase(""))) {
                    Toast.makeText(getContext(), "Please enter Post information", Toast.LENGTH_LONG).show();
                } else if (post_category.getSelectedItem().toString().equalsIgnoreCase("Select Category")) {
                    Toast.makeText(getContext(), "Please select post category", Toast.LENGTH_LONG).show();
                } else {
                    ViewProgress.setVisibility(View.VISIBLE);
                    Log.e(TAG,"video_Type:"+video_type);

                    new PostUpdateAsync().execute(UserId, selectedCategoryId, message, link, embedded_code,imageurl,you_tube_string,video_type);
                    //PostUpdate(UserId, selectedCategoryId, message, link, embedded_code,DocPath );
                }


            }
        });


        return PostView;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DocumentList.clear();

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
                            DocumentList.add(a);
                            Log.i("Path", "onActivityResult: "+a);
                        }

//                        try {
//                            String mypath = PathUtil.getPath(getActivity(), uri);
//                            DocumentList.add(mypath);
//
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }

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
                    if (DocumentList.size() == 0) {
                        DocumentList.add(DocPath);
                    }

                } else {

                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();

                    //String result = myFile.getPath();


//                    final String docId = DocumentsContract.getDocumentId(uri);
//                    final String[] split = docId.split(":");
//                    final String type = split[0];

//                    if ("primary".equalsIgnoreCase(type)) {
//                        String a = Environment.getExternalStorageDirectory() + "/" + split[1];
//
//                        Log.i("Path", "onActivityResult: " + a);
//                    }

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
                    DocPath = path + "/" + displayName;
                    if (DocPath.contains(".pdf")) {
                        SelectedDoc.setVisibility(View.VISIBLE);

                        Log.d("Path", myFile.getAbsolutePath());
                        fileName.append(displayName);
                        SelectedDoc.setText(fileName);

                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));

                        Log.d("fileUri: ", String.valueOf(uri));

                        if (DocumentList.size() == 0) {
                            DocumentList.add(DocPath);
                        }
                    }else {
                        DocPath = "";
                        Toast.makeText(getContext(), "Invalid file type", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            videoFileUri = data.getData();

        }
        else if (requestCode == CAMERA) {
            Uri selectedImageUri =data.getData();
            imageurl = getPath(getContext(),selectedImageUri).replaceAll(" ","%20");
            camera_gallery_vid.setVisibility(View.VISIBLE);
            if(imageurl.equalsIgnoreCase("18 MB")){
                Toast.makeText(getContext(),"Video size is too large",Toast.LENGTH_SHORT).show();
            }else{
                Bitmap bitmap = BitmapFactory.decodeFile(imageurl);
                camera_gallery_video.setImageBitmap(bitmap);


            }



            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imageurl, MediaStore.Video.Thumbnails.MINI_KIND);
            camera_gallery_video.setImageBitmap(thumb);

        }
//        String path = "";
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 100) {
//                Uri uri = data.getData();
//                path = uri.getPath();
//                String[] parts = path.split("[./]");
//                filename = parts[parts.length-1];
//
//                /*ClipData clipData = data.getClipData();
//                //null and not null path
//                if (clipData == null) {
//                    path = data.getData().toString();
//                    String[] parts = path.split("[./]");
//                    filename = parts[parts.length-1];
//                } else {
//                    for (int i = 0; i < clipData.getItemCount(); i++) {
//                        ClipData.Item item = clipData.getItemAt(i);
//                        Uri uri = item.getUri();
//                        path += uri.toString() + "\n";
//                    }
////                    filename=path.substring(path.lastIndexOf("/")+1);
//
//                }*/
//            }
//            SelectedDoc.setVisibility(View.VISIBLE);
//            SelectedDoc.setText(filename);
//        }
        if (data != null) {
            if (requestCode == SELECT_VIDEO) {
                Uri selectedImageUri =data.getData();
                imageurl = getPath(getContext(),selectedImageUri).replaceAll(" ","%20");
                camera_gallery_vid.setVisibility(View.VISIBLE);
                if(imageurl.equalsIgnoreCase("18 MB")){
                    Toast.makeText(getContext(),"Video size is too large",Toast.LENGTH_SHORT).show();
                }else{
                    Bitmap bitmap = BitmapFactory.decodeFile(imageurl);
                    camera_gallery_video.setImageBitmap(bitmap);

                }



                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imageurl, MediaStore.Video.Thumbnails.MINI_KIND);
                camera_gallery_video.setImageBitmap(thumb);

//                video.setImageResource(R.drawable.pla);

//                byte[] byteArrayFromFile = new byte[0];
//                try {
//                    String path = getVideoPath(selectedImageUri);
//                    byteArrayFromFile = getByteArrayFromPath(path);
////                    byteArrayFromFile = getByteArrayFromFile(file);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                // Get length of file in bytes
//                long fileSizeInBytes = byteArrayFromFile.length;
//                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
//                long fileSizeInKB =fileSizeInBytes/1024;
//                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
//               long  fileSizeInMB = fileSizeInKB/1024;
////               if(fileSizeInMB>16){
////                   Toast.makeText(PostMessageActivity.this,"invalid file Size",Toast.LENGTH_SHORT).show();
////
////                }
////                encodedImageData = Base64.encodeToString(byteArrayFromFile, Base64.DEFAULT);
            }
        }
            else if (requestCode == 200) {


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
            //  if (uriString.startsWith("content://")) {
//                    Cursor cursor = null;
//                    try {
//                        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//                        if (cursor != null && cursor.moveToFirst()) {
//                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                        }
//                    } finally {
//                        cursor.close();
//                    }
//                }
//                else if (uriString.startsWith("file://")) {
//                    displayName = myFile.getName();
//                }
//                SelectedDoc.setVisibility(View.VISIBLE);
//                SelectedDoc.setText(displayName);
//
//                DocPath = path+"/"+displayName;
//                Log.d("Path", myFile.getAbsolutePath());
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

//    public String getRealPathFromURI(Uri uri) {
//        String[] projection = {MediaStore.Files.FileColumns.DATA};
//        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
//        int column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void getUpdateCategory() {
        PostCategoryArrayList.clear();
        String url = (CommonUtils.BASE_URL) +"get_updates_category";
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
                                JSONArray json2 = json.getJSONArray("updates_categories");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject Category = json2.getJSONObject(i);
                                    JSONObject CategoryList = Category.getJSONObject("category");
                                    PostCategoryDataModel model = new PostCategoryDataModel();
                                    model.setPostCategoryId(CategoryList.getString("id"));
                                    model.setPostCategoryName(CategoryList.getString("name"));

                                    PostCategoryArrayList.add(model);
                                    PostCategoriesList.add(CategoryList.getString("name"));
                                }
                            }
                            post_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    SelectedCategory = parent.getItemAtPosition(position).toString();


                                    for (int i = 0; i < PostCategoryArrayList.size(); i++) {
                                        if (PostCategoryArrayList.get(i).getPostCategoryName().contains(SelectedCategory)) {
                                            selectedCategoryId = PostCategoryArrayList.get(position - 1).getPostCategoryId();

                                        }
                                    }


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public void PostUpdate(String UserId, String category, String message, String link, String embedded_code, String doc) {

        String url = (CommonUtils.BASE_URL) + "upload_post";

        OkHttpClient client = new OkHttpClient();

        if (ImagesList.size() == 0) {
            ImagesList.add("");
        }
//        else {
//            for (int i = 0; i < ImagesList.size(); i++) {
//                .add(("img" + (i + 1), ImagesListSelected.get(i)));
//            }
//        }


        ViewProgress.setVisibility(View.GONE);


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("updates_category", category)
                .add("message", message)
                .add("link", link)
                .add("embeded_code", embedded_code)
                .add("photo", ImagesList.get(0))
                .add("document", doc)
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

                                ViewProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                ViewProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    class PostUpdateAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("updates_category", params[1]));
                nameValuePairs.add(new BasicNameValuePair("message", params[2]));
                nameValuePairs.add(new BasicNameValuePair("link", params[3]));
//                nameValuePairs.add(new BasicNameValuePair("embeded_code", params[4]));
                if(!params[5].isEmpty()) {
                    nameValuePairs.add(new BasicNameValuePair("video1", params[5]));
                    nameValuePairs.add(new BasicNameValuePair("video_type", params[7]));
                }else if(!params[6].isEmpty()){
                    nameValuePairs.add(new BasicNameValuePair("video_url", params[6]));
                    nameValuePairs.add(new BasicNameValuePair("video_type", params[7]));
                }else if(!params[4].isEmpty()){
                    nameValuePairs.add(new BasicNameValuePair("embeded_code", params[4]));
                    nameValuePairs.add(new BasicNameValuePair("video_type", params[7]));
                }else{
                    nameValuePairs.add(new BasicNameValuePair("video_type",""));
                }
                int Imagecount = 0, DocCount = 0;
                for (int i=0;i<ImagesList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("photo"+(Imagecount+1), ImagesList.get(i)));
                    Imagecount++;
                }
//                if (ImagesList.size() < 5){
//                    int sizeLeft = 5 - ImagesList.size();
//                    for (int i=0;i<sizeLeft;i++){
//                        nameValuePairs.add(new BasicNameValuePair("photo"+(Imagecount+1),ImagesList.get(0)));
//                        Imagecount++;
//                    }
//                }


                for (int i=0;i<DocumentList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("document"+(DocCount+1), DocumentList.get(i)));
                    DocCount++;
                }
//                if (DocumentList.size() < 5){
//                    int sizeLeft = 5 - DocumentList.size();
//                    for (int i=0;i<sizeLeft;i++){
//                        nameValuePairs.add(new BasicNameValuePair("document" + (DocCount + 1), DocumentList.get(0)));
//                        DocCount++;
//                    }
//                }
                Log.d("datap", nameValuePairs.toString());

                String Url =(CommonUtils.BASE_URL)+"upload_post";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ViewProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //{"success":"1","message":"Data saved successfully!"}
if(jsonObject.has("status")) {
    try {
        String success = jsonObject.getString("status");


        if (success.equalsIgnoreCase("success")) {
            ViewProgress.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Update posted successfully!", Toast.LENGTH_LONG).show();


            Fragment fragment2 = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contentContainer, fragment2);
            fragmentTransaction.addToBackStack(null);
//                    toolbar.setTitle("UABN Home");
            fragmentTransaction.commit();


        } else {

            Toast.makeText(getActivity(), "Opps! Some problem occured while posting your update, please ensure your location services are on at the highest accuracy.",
                    Toast.LENGTH_LONG).show();

            ViewProgress.setVisibility(View.GONE);

        }

    } catch (JSONException e) {
        e.printStackTrace();
    }
}else{
    Toast.makeText(getActivity(), "Update post failed", Toast.LENGTH_LONG).show();
    ViewProgress.setVisibility(View.GONE);


}

        }
    }
    public static class AddPhotoBottomDialogFragment extends BottomSheetDialogFragment {

        public static AddPhotoBottomDialogFragment newInstance() {
            return new  AddPhotoBottomDialogFragment();
        }

        Context context;
        TextView  gallery_video,embed_video,camera_video;
        String ChatFriendId;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.upload_video_section, container,
                    false);

            gallery_video = view.findViewById(R.id.gallery_video);
            camera_video = view.findViewById(R.id.camera_video);
            embed_video = view.findViewById(R.id.embed_video);

//            gallery_video.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Intent intent_upload = new Intent();
//                    intent_upload.setType("video/*");
//                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent_upload,"Select Video"),2);
//                }
//
//
//            });
//
//
//            camera_video.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent();
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("*/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                    startActivityForResult(Intent.createChooser(intent, "Choose file"), 100);
//
//
//                }
//            });
//
//
//
//            embed_video.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    EmbeddedVideo.setVisibility(View.VISIBLE);
//                }
//            });

            // get the views and attach the listener

            return view;

        }


    }
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Log.i("URI", uri + "");
        String result = uri + "";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {
            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length - 1];
            final String[] dat = imgary.split("%3A");
            final String docId = dat[1];
            final String type = dat[0];
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    dat[1]
            };
            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }



    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);


                return cursor.getString(column_index);
            }


            /*String[] mediaColumns = {MediaStore.Video.Media.SIZE};
            Cursor cursor = getContext().getContentResolver().query(videoUri, mediaColumns, null, null, null);
            cursor.moveToFirst();
            int sizeColInd = cursor.getColumnIndex(mediaColumns[0]);
            long fileSize = cursor.getLong(sizeColInd);
            cursor.close();*/
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    }
