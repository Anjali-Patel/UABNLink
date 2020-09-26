package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
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
import com.uabn.gss.uabnlink.Activities.PostDetails;
import com.uabn.gss.uabnlink.Activities.VideoPlayerActivity;
import com.uabn.gss.uabnlink.Activities.YouTubePlayerActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.model.PostsDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    String videoId;
    Bitmap bm;
    String html;
    Context context;
    Context actcontext;
    String P_PostBase, P_UserProfileBase, P_DocBase, Update_video;
    private ArrayList<PostsDataModel> NewsArrayList;
    private SharedPreferenceUtils preferences;

    public NewsAdapter(Context context, ArrayList<PostsDataModel> NewsModelArrayList, Context actcontext, String PostBase, String UserProfileBase, String DocBase) {
//        this.Update_video=Update_video;
        this.context = context;
        NewsArrayList = NewsModelArrayList;
        preferences = SharedPreferenceUtils.getInstance(context);
        P_PostBase = PostBase;
        P_UserProfileBase = UserProfileBase;
        P_DocBase = DocBase;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PostsDataModel Items = NewsArrayList.get(position);

        holder.WebVideo.getSettings().setJavaScriptEnabled(true);


        holder.Name.setText(Items.getPostUserName());
        holder.Date.setText(Items.getPostdate());
        holder.Description.setText(Items.getPostMessage());
        holder.LikeCount.setText(Items.getPostLikeCount());

        if (Items.getPostIsLiked().equalsIgnoreCase("1")) {
            holder.Like.setImageResource(R.drawable.ic_dislike);
        } else {
            holder.Like.setImageResource(R.drawable.ic_like);
        }
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        if (Items.getPostUserImage() == null || Items.getPostUserImage().equalsIgnoreCase("null") || Items.getPostUserImage().equalsIgnoreCase("")) {
            Glide.with(context).load(R.drawable.ic_person_black_24dp).apply(options).into(holder.UserImage);
        } else {
            String ImgURL = P_UserProfileBase + "/" + Items.getPostUserId() + "/" + Items.getPostUserImage().replace(" ", "%20");
//            Picasso.with(context).load(ImgURL).error(context.getResources().getDrawable(R.drawable.ic_person_black_24dp)).into(holder.UserImage);
            Glide.with(context).load(ImgURL).apply(options).into(holder.UserImage);

        }
        if (Items.getPostImage()!=null&&Items.getPostMediaType().equalsIgnoreCase("1")) {
            holder.news_img.setVisibility(View.VISIBLE);
            final String ImgURL = P_PostBase + "/" + Items.getPostId() + "/" + Items.getPostImage().replace(" ", "%20");
            Glide.with(context).asBitmap().load(ImgURL).into(holder.news_img);
//            holder.news_img.getLayoutParams().width = 500;
//            holder.news_img.getLayoutParams().height = 250;
//            holder.news_img.setAdjustViewBounds(true);
//            scaleImage(holder.news_img);


            holder.news_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
//                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    dialog.setContentView(R.layout.zoom_profile_pic);
                    ZoomImageView img = dialog.findViewById(R.id.img);
                    final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                    ImageProgress.setVisibility(View.VISIBLE);
                    Glide.with(context).load(ImgURL).into(img);
                    ImageProgress.setVisibility(View.GONE);


                    dialog.show();
                }
            });

        }else{
            holder.news_img.setVisibility(View.GONE);
        }

//        if (Items.getPostMediaType().equalsIgnoreCase("2")) {
//            holder.news_img.setVisibility(View.GONE);
//
//
//        }


        if (!Items.getPostEmbeddedVideo().isEmpty() && Items.getVideo_type() != null && Items.getVideo_type().equalsIgnoreCase("upload_video")) {
            holder.video_url_image_layout.setVisibility(View.VISIBLE);
            String video_url = Items.getUpdate_video() + Items.getPostEmbeddedVideo();
            Glide.with(context).load(video_url).into(holder.video_url_image);
            holder.play_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VideoPlayerActivity.class);
                    String ur = Items.getUpdate_video() + Items.getPostEmbeddedVideo();
                    i.putExtra("url", ur);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        } else  if (!Items.getPostEmbeddedVideo().isEmpty() && Items.getVideo_type() != null && Items.getVideo_type().equalsIgnoreCase("video_url")) {
            holder.video_url_image_layout.setVisibility(View.VISIBLE);
            String uri = Items.getPostEmbeddedVideo();
            videoId = extractYoutubeId(Items.getPostEmbeddedVideo());
            String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video
            // picasso jar file download image for u and set image in imagview
            Glide.with(context)
                    .load(img_url)
                    .into(holder.video_url_image);
            holder.play_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoId = extractYoutubeId(Items.getPostEmbeddedVideo());
                    Intent i = new Intent(context, YouTubePlayerActivity.class);
                    i.putExtra("url", videoId);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        } else {
            holder.video_url_image_layout.setVisibility(View.GONE);
        }
//         && Items.getPostEmbeddedVideo().contains("iframe")
        if (!Items.getPostEmbeddedVideo().isEmpty() && Items.getVideo_type() != null && Items.getVideo_type().equalsIgnoreCase("embed_code")) {
            holder.VideoLinear.setVisibility(View.VISIBLE);
            holder.YouTubeLink.setVisibility(View.GONE);
            html = Items.getPostEmbeddedVideo();
            if (!html.contains("https")) {
                Scanner scanner = new Scanner(html);
                boolean isWidhtFound = false, isHeightFound = false, isSrcFound = false;
                StringBuilder htmlUrl = new StringBuilder();
                while (scanner.hasNext()) {
                    String possibleUrl = scanner.next();
                    Log.i("Scan", "onCreate: possibleUrl:-" + possibleUrl);
                    if (possibleUrl.contains("width")) {
                        isWidhtFound = true;
                        isHeightFound = false;
                        isSrcFound = false;

                    } else if (possibleUrl.contains("height")) {
                        isHeightFound = true;
                        isWidhtFound = false;
                        isSrcFound = false;

                    } else if (possibleUrl.startsWith("src")) {
                        isSrcFound = true;
                        isWidhtFound = false;
                        isHeightFound = false;
                    }
                    if (isWidhtFound) {
                        htmlUrl.append(" ").append("width=\"350\" ");
                        isWidhtFound = false;
                    } else if (isHeightFound) {
                        htmlUrl.append(" ").append("height=\"200\" ");
                        isHeightFound = false;
                    } else if (isSrcFound) {
                        String[] srcUrl = possibleUrl.split("//");

                        if (srcUrl.length > 0) {
                            htmlUrl.append(" ").append(srcUrl[0]);
                        }
                        if (srcUrl.length >1) {
                            htmlUrl.append("https://").append(srcUrl[1]);
                        }
                        isSrcFound = false;
                    } else {
                        htmlUrl.append(possibleUrl);

                    }

                }
                Log.i("htmlUrl", "onCreate: htmlUrl" + htmlUrl);
                holder.WebVideo.loadData(htmlUrl.toString(), "text/html", null);
            } else {
                Scanner scanner = new Scanner(html);
                boolean isWidhtFound = false, isHeightFound = false, isSrcAttached = false;

                StringBuilder htmlUrl = new StringBuilder();
                while (scanner.hasNext()) {
                    String possibleUrl = scanner.next();
                    Log.i("Scan", "onCreate: possibleUrl:-" + possibleUrl);
                    if (possibleUrl.contains("width")) {
                        isWidhtFound = true;
                        isHeightFound = false;

                    } else if (possibleUrl.contains("height") &&possibleUrl.contains("src") ) {
                        isSrcAttached = true;
                        isWidhtFound = false;
                        isHeightFound = false;

                    }else if (possibleUrl.contains("height")) {
                        isHeightFound = true;
                        isWidhtFound = false;

                    }

                    if (isWidhtFound) {
                        htmlUrl.append(" ").append(" width=\"" + "350" + "\" ");
                        isWidhtFound = false;
                    } else if (isHeightFound) {
                        htmlUrl.append(" ").append(" height=\"" + "200" + "\"").append(" ");
                        isHeightFound = false;
                    } else if (isSrcAttached) {

                        String[] data = possibleUrl.split("src");



                        if (data.length > 1) {
                            htmlUrl.append(" ").append(" height=\"" + "200" + "\"").append(" src").append(data[1]).append(" ");
                        }else{
                            htmlUrl.append(" ").append(" height=\"" + "200" + "\"").append(" ");

                        }

                        isSrcAttached = false;
                    } else {
                        htmlUrl.append(possibleUrl);

                    }

                }
                Log.i("htmlUrl", "onCreate: htmlUrl" + htmlUrl);
                holder.WebVideo.loadData(htmlUrl.toString(), "text/html", null);
            }
        } else {
            holder.VideoLinear.setVisibility(View.GONE);

        }

        //  String html="<iframe width=\"400\" height=\"300\" src=\"https://www.youtube.com/embed/PHXxKZkeFmc\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";


        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Items.getPostIsLiked().equalsIgnoreCase("1")) {
                    int count = Integer.parseInt(Items.getPostLikeCount());
                    final String PostId = (Items.getPostId());
                    SetDisLike(PostId);
                    String totallike = String.valueOf(count - 1);
                    holder.LikeCount.setText(totallike);
                    holder.Like.setImageResource(R.drawable.ic_like);

                } else {

                    final String PostId = (Items.getPostId());
                    SetLike(Items.getPostId());
                    int count = Integer.parseInt(Items.getPostLikeCount());
                    String totallike = String.valueOf(count + 1);
                    holder.LikeCount.setText(totallike);

                    holder.Like.setImageResource(R.drawable.ic_dislike);
                }


            }
        });


        holder.Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostDetails.class);
                i.putExtra("Id", Items.getPostUserId());
                i.putExtra("PostId", Items.getPostId());
                context.startActivity(i);
            }
        });


        holder.PostLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostDetails.class);
                i.putExtra("Id", Items.getPostUserId());
                i.putExtra("PostId", Items.getPostId());
                context.startActivity(i);
            }
        });

        holder.More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostDetails.class);
                i.putExtra("Id", Items.getPostUserId());
                i.putExtra("PostId", Items.getPostId());
                context.startActivity(i);
            }
        });


        //holder.CommentCount.setText(Items.getCommentCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.news_layout, viewgroup, false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount() {

        return NewsArrayList.size();
    }

    public void SetLike(String PostId) {

        String url = (CommonUtils.BASE_URL) + "like_update";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", preferences.getStringValue(CommonUtils.MEMBER_ID, ""))
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

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);

                            String success = json.getString("status");

                            if (success.equalsIgnoreCase("success")) {


//                                Toast.makeText(context, "Like added successfully!", Toast.LENGTH_LONG).show();

                            } else if (success.equalsIgnoreCase("failure")) {

                                Toast.makeText(context, "Opps! Some problem occured while liking the post, please try again after some time.",
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
                .add("user_id", preferences.getStringValue(CommonUtils.MEMBER_ID, ""))
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

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);

                            String success = json.getString("status");

                            if (success.equalsIgnoreCase("success")) {


                            } else if (success.equalsIgnoreCase("failure")) {

                                Toast.makeText(context, "Opps! Some problem occured while liking the post, please try again after some time.",
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

    private void scaleImage(ImageView view) {
        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int xBounding = ((View) view.getParent()).getWidth();//EXPECTED WIDTH
        int yBounding = ((View) view.getParent()).getHeight();//EXPECTED HEIGHT

        float xScale = ((float) xBounding) / width;
        float yScale = ((float) yBounding) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();
        BitmapDrawable result = new BitmapDrawable(context.getResources(), scaledBitmap);

        view.setImageDrawable(result);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Name, Date, Description, LikeCount, CommentCount, More, YouTubeLink;
        LinearLayout LikeButton, CommentButton, PostLinear, VideoLinear;
        RelativeLayout video_url_image_layout;
        ImageView UserImage, news_img, Like, Comment, video_url_image, play_bt;
        WebView WebVideo;


        public MyViewHolder(View view) {
            super(view);
            Name = view.findViewById(R.id.Name);
            video_url_image_layout = view.findViewById(R.id.video_url_image_layout);
            video_url_image = view.findViewById(R.id.video_url_image);
            play_bt = view.findViewById(R.id.play_bt);
            Date = view.findViewById(R.id.Date);
            Description = view.findViewById(R.id.Description);
            LikeCount = view.findViewById(R.id.LikeCount);
            PostLinear = view.findViewById(R.id.PostLinear);
            LikeButton = view.findViewById(R.id.LikeButton);
            CommentButton = view.findViewById(R.id.CommentButton);
            More = view.findViewById(R.id.More);
            UserImage = view.findViewById(R.id.UserImage);
            news_img = view.findViewById(R.id.news_img);
            Like = view.findViewById(R.id.Like);
            Comment = view.findViewById(R.id.Comment);
            VideoLinear = view.findViewById(R.id.VideoLinear);
            WebVideo = view.findViewById(R.id.WebVideo);
            YouTubeLink = view.findViewById(R.id.YouTubeLink);

        }
    }

    public static class CommentPost extends DialogFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.comment_post, null);

            final TextView SearchText = (TextView) dialogView.findViewById(R.id.searchtext);
            ImageView close = (ImageView) dialogView.findViewById(R.id.search_close_btn);
            final ProgressBar progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            Button dialogButton = (Button) dialogView.findViewById(R.id.send);


            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            return dialogView;
        }


    }
}



