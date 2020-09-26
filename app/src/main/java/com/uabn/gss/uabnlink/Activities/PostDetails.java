package com.uabn.gss.uabnlink.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.LinePagerIndicatorDecoration;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.adapter.CommentAdapter;
import com.uabn.gss.uabnlink.adapter.PostDetailImageAdapter;
import com.uabn.gss.uabnlink.fragment.HomeFragment;
import com.uabn.gss.uabnlink.model.CommentDataModel;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.model.Parcabe_Imagelist;
import com.uabn.gss.uabnlink.model.PostDetailImageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.uabn.gss.uabnlink.Utility.ExpandableTextView.makeTextViewResizable;


public class PostDetails extends AppCompatActivity implements CommentAdapter.UpdatePost, View.OnClickListener{
String videoId;
    String PostBase, UserProfileBase, DocBase, user_Id, str_comment,Update_video;
    TextView Name, Date, Description, LikeCount, CommentorName, YouTubeLink, CommentDate,Url, DocsName1, DocsName2, DocsName3, DocsName4, DocsName5;
    ImageView UserImage, CommentorImage, news_img, Like, delete;
    RelativeLayout LikeCommentInfoRelative;
    String CurrentPostId, PostUploaderID, PostID,PostIsLiked="",PostLikeCount;
    EditText type_comment;
    ImageView Comment_Button,video_url_image,play_bt;
    SharedPreferenceUtils preferances;
    CommentAdapter adapter;
    RecyclerView commentlist;
    String html;

    RecyclerView.LayoutManager layoutmanager;
    ArrayList<CommentDataModel> CommentDataModelArrayList = new ArrayList<>();
    ArrayList<String> ImageArray = new ArrayList<>();
    ArrayList<String> DocArray = new ArrayList<>();
    ArrayList<String> DocURLArray = new ArrayList<>();
    int myposition;
    RelativeLayout camera_videoLayout;
    ImageView camera_image,play_bt1;
    String img;
    String ImgURL;
    String video_type;
    int current_image;
    FrameLayout progressBarHolder;
    WebView WebVideo;
    ProgressBar imageprogress;
    String URL_Link;
RelativeLayout video_url_image_layout;

    ArrayList<Parcabe_Imagelist> ImageModelArraylist;
    RecyclerView recycler_view;
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Post Details");
        setSupportActionBar(toolbar);
        PostUploaderID = getIntent().getStringExtra("Id");
        video_url_image=findViewById(R.id.video_url_image);
        play_bt=findViewById(R.id.play_bt);
        video_url_image_layout=findViewById(R.id.video_url_image_layout);
        PostID = getIntent().getStringExtra("PostId");
        type_comment = findViewById(R.id.type_comment);
        Comment_Button = findViewById(R.id.Comment_Button);
        Name = findViewById(R.id.Name);
        Date = findViewById(R.id.Date);
        Description = findViewById(R.id.Description);
        LikeCount = findViewById(R.id.LikeCount);
        CommentorName = findViewById(R.id.CommentorName);
        CommentDate = findViewById(R.id.CommentDate);
        Like = findViewById(R.id.Like);
        camera_videoLayout=findViewById(R.id.camera_videoLayout);
        camera_image=findViewById(R.id.camera_image);
        play_bt1=findViewById(R.id.play_bt1);
        delete = findViewById(R.id.delete);
        commentlist = findViewById(R.id.commentlist);
        UserImage = findViewById(R.id.UserImage);
        CommentorImage = findViewById(R.id.CommentorImage);
        preferances = SharedPreferenceUtils.getInstance(PostDetails.this);
        user_Id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        news_img = findViewById(R.id.news_img);
        LikeCommentInfoRelative = findViewById(R.id.LikeCommentInfoRelative);
        Url = findViewById(R.id.Url);
        YouTubeLink = findViewById(R.id.YouTubeLink);
        WebVideo = findViewById(R.id.WebVideo);
        recycler_view = findViewById(R.id.recycler_view);
        progressBarHolder = findViewById(R.id.progressBarHolder);
        imageprogress = findViewById(R.id.imageprogress);


//        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
//        pagerSnapHelper.attachToRecyclerView(recycler_view);
//        recycler_view.addItemDecoration(new LinePagerIndicatorDecoration());
        ImageModelArraylist= new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recycler_view.setLayoutManager(linearLayoutManager);
        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.noimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                final Dialog dialog=new Dialog(PostDetails.this, R.style.DialogAnimation_2);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.setContentView(R.layout.zoom_profile_pic);
                ZoomImageView img = dialog.findViewById(R.id.img);
                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                ImageProgress.setVisibility(View.VISIBLE);
                Glide.with(PostDetails.this).asBitmap().load(ImgURL).into(img);
                ImageProgress.setVisibility(View.GONE);
                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                dialog.show();
            }
        });

        DocsName1 = findViewById(R.id.DocsName1);
        DocsName2 = findViewById(R.id.DocsName2);
        DocsName3 = findViewById(R.id.DocsName3);
        DocsName4 = findViewById(R.id.DocsName4);
        DocsName5 = findViewById(R.id.DocsName5);

        DocsName1.setOnClickListener(this);
        DocsName2.setOnClickListener(this);
        DocsName3.setOnClickListener(this);
        DocsName4.setOnClickListener(this);
        DocsName5.setOnClickListener(this);

        WebVideo.getSettings().setJavaScriptEnabled(true);



        Comment_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_comment = type_comment.getText().toString();
                if (str_comment.equalsIgnoreCase("")) {
                    Toast.makeText(PostDetails.this, "Please enter some Comment", Toast.LENGTH_LONG).show();
                } else {
                    progressBarHolder.setVisibility(View.VISIBLE);
                    CommentOnPost(user_Id, PostID, str_comment);
                    type_comment.setText("");

                }
            }
        });


        if(user_Id.equalsIgnoreCase(PostUploaderID)){
            delete.setVisibility(View.VISIBLE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetails.this);

                    alertDialogBuilder.setMessage("Are you sure you want to remove?");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            deleteSharePost(user_Id, PostID);
                            progressBarHolder.setVisibility(View.VISIBLE);

                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });


        }

        GetPostsDetails(user_Id, PostID);

    }

    public void CommentOnPost(String user_Id, final String PostID, String str_comment) {


        String url = (CommonUtils.BASE_URL) + "write_comment";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_Id)
                .add("update_id", PostID)
                .add("update_comment", str_comment)
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

                PostDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(PostDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                GetPostsDetails(PostUploaderID, PostID);
                                Constants.hideKeyboard(PostDetails.this);
                            } else {
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(PostDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                Constants.hideKeyboard(PostDetails.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });



}





    public void GetPostsDetails(String UserId, String PostId) {

        progressBarHolder.setVisibility(View.VISIBLE);


        CommentDataModelArrayList.clear();
        ImageArray.clear();
        DocArray.clear();
        final StringBuilder fileName = new StringBuilder();
        String DocName;


        String url = (CommonUtils.BASE_URL) + "view_update_in_detail";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("update_id", PostId)
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
                                PostBase = json.getString("user_update_image");
                                UserProfileBase = json.getString("profile_pic_url");
                                DocBase = json.getString("user_update_doc");
                                Update_video = json.getString("user_update_video");
                                JSONArray update = json.getJSONArray("post_details");
                                final JSONObject PostDetails = update.getJSONObject(0);
                                final JSONObject UserUpdate = PostDetails.getJSONObject("UserUpdate");
                                if(!UserUpdate.getString("video_type").equalsIgnoreCase("")&&!UserUpdate.getString("video_type").equalsIgnoreCase("null")){
                                    video_type=UserUpdate.getString("video_type");

                                }/*else{
                                    video_type=UserUpdate.getString("");

                                }*/
                                JSONObject user = PostDetails.getJSONObject("User");
                                JSONArray userComments = PostDetails.getJSONArray("UserUpdatesComment");

                                //JSONObject comments = UpdateDetails.getJSONObject("UserUpdatesComment");
                                JSONArray Docs = PostDetails.getJSONArray("UpdateDocument");

                                CurrentPostId = (UserUpdate.getString("id"));


                                if (UserUpdate.has(("islike"))) {
                                    Like.setImageResource(R.drawable.ic_dislike);
                                    PostIsLiked=UserUpdate.getString("islike");

                                } else {
                                    Like.setImageResource(R.drawable.ic_like);
                                }
                                Like.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (PostIsLiked.equalsIgnoreCase("1")) {
                                            int count = Integer.parseInt(PostLikeCount);
                                            SetDisLike(CurrentPostId);
                                            String totallike = String.valueOf(count - 1);
                                            LikeCount.setText(totallike);
                                            Like.setImageResource(R.drawable.ic_like);
                                        }  else {
                                            int count = Integer.parseInt(PostLikeCount);
                                            SetLike(CurrentPostId);
                                            String totallike = String.valueOf(count + 1);
                                            LikeCount.setText(totallike);
                                            Like.setImageResource(R.drawable.ic_dislike);
                                        }


                                    }
                                });
                                PostLikeCount=UserUpdate.getString("UserUpdatesLike_count");
                                Description.setText(UserUpdate.getString("message"));

                                if(UserUpdate.getString("message").length() > 200){
                                    makeTextViewResizable(Description, 3, "View More", true);
                                }

                                LikeCount.setText(UserUpdate.getString("UserUpdatesLike_count"));

                                if (!UserUpdate.getString("link").equalsIgnoreCase("") && !UserUpdate.getString("link").equalsIgnoreCase("null") && !UserUpdate.getString("link").equalsIgnoreCase("http://")){
                                    Url.setVisibility(View.VISIBLE);
                                    URL_Link = UserUpdate.getString("link");

                                    Url.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {
                                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_Link));
                                                startActivity(myIntent);
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(PostDetails.this, "No application can handle this request." + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UserUpdate.getString("created_date"));
                                String newString = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(date); // 25-03-2019

                                 ImgURL = UserProfileBase + "/" + user.getString("id") + "/" + user.getString("profile_picture").replace(" ", "%20");
//                                Picasso.with(PostDetails.this).load(ImgURL)
//                                        .error(R.drawable.ic_person_black_24dp).into(UserImage);

                                Glide.with(getApplicationContext()).asBitmap().load(ImgURL).into(UserImage);
                                Name.setText(user.getString("name"));
                                Date.setText(newString);
                                if(UserUpdate.getString("embed_code")!=null&&!UserUpdate.getString("embed_code").isEmpty()&&/*!video_type.equalsIgnoreCase("")&& !*/video_type!=null&& video_type.equalsIgnoreCase("video_url")) {

                                    video_url_image_layout.setVisibility(View.VISIBLE);


                                    videoId = extractYoutubeId(UserUpdate.getString("embed_code"));

                                    String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video
                                    Glide.with(PostDetails.this)
                                            .load(img_url)
                                            .into(video_url_image);
                                    play_bt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent i = new Intent(PostDetails.this, YouTubePlayerActivity.class);
                                            i.putExtra("url", videoId);
                                         startActivity(i);

                                        }
                                    });
                                } else if (UserUpdate.getString("embed_code")!=null&&!UserUpdate.getString("embed_code").isEmpty()&&video_type!=null&&video_type.equalsIgnoreCase("upload_video")) {
                                    video_url_image_layout.setVisibility(View.VISIBLE);
                                    img=Update_video+UserUpdate.getString("embed_code");
                                    Glide.with(PostDetails.this).load(img).into(video_url_image);
                                    play_bt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(PostDetails.this, VideoPlayerActivity.class);
                                            i.putExtra("url", img);
                                            startActivity(i);

                                        }
                                    });


                                }

//                                if (!UserUpdate.getString("embed_code").equalsIgnoreCase("") && !UserUpdate.getString("embed_code").equalsIgnoreCase("null") && UserUpdate.getString("embed_code").contains("iframe")&&!video_type.equalsIgnoreCase("")&& !video_type.equalsIgnoreCase("null")&&video_type.equalsIgnoreCase("embed_code")){

                                    if (UserUpdate.getString("embed_code")!=null&&!UserUpdate.getString("embed_code").isEmpty()&&/*!video_type.equalsIgnoreCase("")&& !*/video_type!=null&&video_type.equalsIgnoreCase("embed_code")){
                                  //  String html="<iframe width=\"400\" height=\"300\" src=\"https://www.youtube.com/embed/PHXxKZkeFmc\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
                                    WebVideo.setVisibility(View.VISIBLE);
                                     html=UserUpdate.getString("embed_code");
                                    if(!html.contains("https")){
                                        Scanner scanner = new Scanner(html);
                                        boolean isWidhtFound = false, isHeightFound = false,str_src=false;
                                        StringBuilder htmlUrl = new StringBuilder();
                                        while (scanner.hasNext()) {
                                            String possibleUrl = scanner.next();
                                            Log.i("Scan", "onCreate: possibleUrl:-" + possibleUrl);
                                            if (possibleUrl.contains("width")) {
                                                isWidhtFound = true;
                                                isHeightFound = false;
                                                str_src=false;

                                            }
                                            else if (possibleUrl.contains("height")) {
                                                isHeightFound = true;
                                                isWidhtFound = false;
                                                str_src=false;

                                            }
                                            else if (possibleUrl.startsWith("src")) {
                                                isHeightFound = false;
                                                isWidhtFound = false;
                                                str_src=true;
                                            }

                                            if (isWidhtFound){
                                                htmlUrl.append(" ").append("width=\"350\" ");
                                                isWidhtFound = false;
                                            }
                                            else if (isHeightFound){
                                                htmlUrl.append(" ").append("height=\"200\" ");
                                                isHeightFound = false;
                                            }  else if (str_src){
                                                String[] srcUrl = possibleUrl.split("//");

                                                if (srcUrl.length > 0) {
                                                    htmlUrl.append(" ").append(srcUrl[0]);
                                                }
                                                if (srcUrl.length >1) {
                                                    htmlUrl.append("https://").append(srcUrl[1]);
                                                }
                                                str_src = false;
                                            }
                                            else{
                                                htmlUrl.append(possibleUrl);

                                            }

                                        }
                                        Log.i("htmlUrl", "onCreate: htmlUrl"+htmlUrl);
                                        WebVideo.loadData(htmlUrl.toString(), "text/html", null);
                                    }else{
                                        Scanner scanner = new Scanner(html);
                                        boolean isWidhtFound = false, isHeightFound = false, isSrcAttached = false;
                                        StringBuilder htmlUrl = new StringBuilder();
                                        while (scanner.hasNext()) {
                                            String possibleUrl = scanner.next();
                                            Log.i("Scan", "onCreate: possibleUrl:-" + possibleUrl);
                                            if (possibleUrl.contains("width")) {
                                                isWidhtFound = true;
                                                isHeightFound = false;

                                            }else if (possibleUrl.contains("height") &&possibleUrl.contains("src") ) {
                                                isSrcAttached = true;
                                                isWidhtFound = false;
                                                isHeightFound = false;

                                            } else if (possibleUrl.contains("height")) {
                                                isHeightFound = true;
                                                isWidhtFound = false;

                                            }

                                            if (isWidhtFound){
                                                htmlUrl.append(" ").append("width=\"350\" ");
                                                isWidhtFound = false;
                                            }
                                            else if (isHeightFound){
                                                htmlUrl.append(" ").append("height=\"200\" ");
                                                isHeightFound = false;
                                            } else if (isSrcAttached) {

                                                String[] data = possibleUrl.split("src");



                                                if (data.length > 1) {
                                                    htmlUrl.append(" ").append(" height=\"" + "200" + "\"").append(" src").append(data[1]).append(" ");
                                                }else{
                                                    htmlUrl.append(" ").append(" height=\"" + "200" + "\"").append(" ");

                                                }

                                                isSrcAttached = false;
                                            } else{
                                                htmlUrl.append(possibleUrl);

                                            }

                                        }
                                        Log.i("htmlUrl", "onCreate: htmlUrl"+htmlUrl);
                                        WebVideo.loadData(htmlUrl.toString(), "text/html", null);
                                    }
                                    } else{
                                        WebVideo.setVisibility(View.GONE);
                                }



                                for (int i = 0; i < Docs.length(); i++) {
                                    JSONObject UploadedDocs = Docs.getJSONObject(i);

                                    if (UploadedDocs.getString("type").equalsIgnoreCase("1")) {

                                        imageprogress.setVisibility(View.VISIBLE);

                                        recycler_view.setVisibility(View.VISIBLE);
                                        Parcabe_Imagelist postDetailImageModel= new Parcabe_Imagelist();
                                        String my_url=PostBase + "/" + UploadedDocs.getString("update_id") + "/" + UploadedDocs.getString("path");
//                                        (.replace(" ", "%20"))
                                        postDetailImageModel.setImage(my_url);
                                        ImageModelArraylist.add(postDetailImageModel);
                                        mAdapter = new MultipleImagesAdapter(PostDetails.this, ImageModelArraylist);
                                        recycler_view.setAdapter(mAdapter);


                                        imageprogress.setVisibility(View.GONE);

                                       // ImageArray.add(PostBase + "/" + UploadedDocs.getString("update_id") + "/" + UploadedDocs.getString("path").replace(" ", "%20"));
                                    } else if (UploadedDocs.getString("type").equalsIgnoreCase("2")) {

                                        DocURLArray.add(DocBase + "/" + UploadedDocs.getString("update_id") + "/" + UploadedDocs.getString("path").replace(" ", "%20"));
                                        DocArray.add(UploadedDocs.getString("path"));

                                    }
                                }



                                if (DocArray.size()>= 1) {

                                    StringBuilder name = new StringBuilder();
                                    for (int i = 0; i < DocArray.size(); i++) {
                                        name.append(DocArray.get(i)).append("\n");

                                        switch (i) {
                                            case 0:
                                                DocsName1.setVisibility(View.VISIBLE);
                                                DocsName1.setText(DocArray.get(i));
                                                break;
                                            case 1:
                                                DocsName2.setVisibility(View.VISIBLE);
                                                DocsName2.setText(DocArray.get(i));
                                                break;
                                            case 2:
                                                DocsName3.setVisibility(View.VISIBLE);
                                                DocsName3.setText(DocArray.get(i));
                                                break;
                                            case 3:
                                                DocsName4.setVisibility(View.VISIBLE);
                                                DocsName4.setText(DocArray.get(i));
                                                break;
                                            case 4:
                                                DocsName5.setVisibility(View.VISIBLE);
                                                DocsName5.setText(DocArray.get(i));
                                                break;
                                            default:
                                                break;

                                        }


                                    }


                                }

                                LikeCommentInfoRelative.setVisibility(View.VISIBLE);

                                for (int j = 0; j < userComments.length(); j++) {
                                    JSONObject CommentDetails = userComments.getJSONObject(j);

                                    CommentDataModel model = new CommentDataModel();
                                    model.setCommentId(CommentDetails.getString("id"));
                                    model.setCommentorUserId(CommentDetails.getString("user_id"));
                                    model.setCommentatorName(CommentDetails.getString("name"));
                                    model.setCommentetorImage(CommentDetails.getString("profile_picture"));


                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UserUpdate.getString("created_date"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    model.setCommentDate(CommentPostDate);

                                    model.setCommentText(CommentDetails.getString("comment"));


                                    if (CommentDetails.has("like_count")) {
                                        model.setCommentLikeCount(CommentDetails.getString("like_count"));
                                    } else {
                                        model.setCommentLikeCount("0");
                                    }

                                    if (CommentDetails.has("reply_count")) {
                                        model.setCommentReplyCount(CommentDetails.getString("reply_count"));
                                    }

                                    model.setCommentISLiked(CommentDetails.getString("islike"));


                                    CommentDataModelArrayList.add(model);

                                }

                                progressBarHolder.setVisibility(View.GONE);

                                adapter = new CommentAdapter(PostDetails.this, CommentDataModelArrayList, UserProfileBase, CurrentPostId);
                                layoutmanager = new LinearLayoutManager(PostDetails.this);
                                commentlist.setLayoutManager(layoutmanager);
                                commentlist.setAdapter(adapter);



                            } else {

                                adapter = new CommentAdapter(PostDetails.this, CommentDataModelArrayList, UserProfileBase, CurrentPostId);
                                layoutmanager = new LinearLayoutManager(PostDetails.this);
                                commentlist.setLayoutManager(layoutmanager);
                                commentlist.setAdapter(adapter);
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(PostDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }





    @Override
    public void UpdateComments() {
        GetPostsDetails(PostUploaderID, PostID);
    }



    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.DocsName1:
                callDoc(0);
                break;
            case R.id.DocsName2:
                callDoc(1);
                break;
            case R.id.DocsName3:
                callDoc(2);
                break;
            case R.id.DocsName4:
                callDoc(3);
                break;
            case R.id.DocsName5:
                callDoc(4);
                break;
            default:
                break;
        }

    }

    public void callDoc(int myposition) {
        String path = DocURLArray.get(myposition);
        Intent i = new Intent(this,ViewPDF.class);
        i.putExtra("doc_path",path);
        startActivity(i);

    }


    public void deleteSharePost(String user_id,String update_id) {
        String url = (CommonUtils.BASE_URL)+"remove_post";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add(" update_id", update_id)
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
                                progressBarHolder.setVisibility(View.GONE);
                                Toast.makeText(PostDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                Intent home = new Intent(PostDetails.this, MainActivity.class);
                                startActivity(home);
                            } else {
                                Toast.makeText(PostDetails.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                progressBarHolder.setVisibility(View.GONE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressBarHolder.setVisibility(View.GONE);
                    }
                });

            }
        });
    }


    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        ArrayList<Parcabe_Imagelist> ImageArrayList;

        public MultipleImagesAdapter(Context context, ArrayList<Parcabe_Imagelist> ImagesList) {
            context1 = context;
            ImageArrayList = ImagesList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_listitem, parent, false);
            return new MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            final Parcabe_Imagelist Items = ImageArrayList.get(position);
            final RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.noimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(context1).asBitmap().load(Items.getImage()).into(holder.adapterImage);

//            holder.adapterImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    final Dialog dialog=new Dialog(PostDetails.this, R.style.DialogAnimation_2);
////                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
//                    dialog.setContentView(R.layout.zoom_profile_pic);
//                    ZoomImageView img = dialog.findViewById(R.id.img);
//                    final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
//                    ImageProgress.setVisibility(View.VISIBLE);
//
//                    // Glide.with(context).load(ImgURL).thumbnail(0.1f).into(img);
//
//                    Picasso.with(PostDetails.this).load(Items.getImage())
//                            .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
//                            .into(img, new com.squareup.picasso.Callback() {
//                                @Override
//                                public void onSuccess() {
//                                    //do something when picture is loaded successfully
//                                    ImageProgress.setVisibility(View.GONE);
//
//                                }
//
//                                @Override
//                                public void onError() {
//                                    //do something when there is picture loading error
//                                }
//                            });
//
//                    dialog.show();
//
//
//                }
//            });


            holder.adapterImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PostDetails.this, ImageSliderPager.class);
                    i.putExtra("ImageSelected",  ImageArrayList.get(position).getImage());
                    i.putParcelableArrayListExtra("image_list", ImageArrayList);
                    i.putExtra("position", holder.getAdapterPosition());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ImageArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adapterImage;

            public MyViewHolder(View v) {
                super(v);
                adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
            }
        }
    }
    public void SetLike(String PostId) {

        String url = (CommonUtils.BASE_URL) + "like_update";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id",user_Id)
                .add("update_id", PostId)
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

                                Toast.makeText(PostDetails.this, "Like added successfully!",
                                        Toast.LENGTH_LONG).show();

                            } else if (success.equalsIgnoreCase("failure")) {

                                Toast.makeText(PostDetails.this, "Opps! Some problem occured while liking the post, please try again after some time.",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }


    public void SetDisLike(String PostId) {

        String url = (CommonUtils.BASE_URL) + "unlike_update";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_Id)
                .add("update_id", PostId)
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


                            } else if (success.equalsIgnoreCase("failure")) {

                                Toast.makeText(PostDetails.this, "Opps! Some problem occured while liking the post, please try again after some time.",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }
    public String extractYoutubeId(String inUrl) {
        inUrl = inUrl.replace("&feature=youtu.be", "");
        if (inUrl.toLowerCase().contains("youtu.be")) {
            return inUrl.substring(inUrl.lastIndexOf("/") + 1);
        }
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(inUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}