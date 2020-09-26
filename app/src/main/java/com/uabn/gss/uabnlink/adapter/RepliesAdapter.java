package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.CommentDataModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.MyViewHolder> {
    Context context;
    Context actcontext;
    private ArrayList<CommentDataModel> CommentArrayList;
    private SharedPreferenceUtils preferences;
    String P_UserProfileBase, CurrentPostId;
    UpdatePost postupdate;
    String user_id;

    public RepliesAdapter(Context context, ArrayList<CommentDataModel> CommetModelArrayList, String UserProfileBase, String PostId)
    {
        this.context=context;
        CommentArrayList = CommetModelArrayList;
        preferences = SharedPreferenceUtils.getInstance(context);
        P_UserProfileBase =UserProfileBase;
        CurrentPostId = PostId;
        postupdate = (UpdatePost) context;
        user_id = preferences.getStringValue(CommonUtils.MEMBER_ID, "");
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView CommentorName,CommentDate,CommentText,LikeCount;
        ImageView CommentorImage, Like, Comment, delete_img,edit;
        public MyViewHolder(View view) {
            super(view);
            CommentorName= view.findViewById(R.id.CommentorName);
            CommentDate = view.findViewById(R.id.CommentDate);
            CommentText = view.findViewById(R.id.CommentText);
            LikeCount= view.findViewById(R.id.LikeCount);
            edit=view.findViewById(R.id.edit);
            CommentorImage = view.findViewById(R.id.CommentorImage);
            Like = view.findViewById(R.id.Like);
            delete_img=view.findViewById(R.id.delete_img);

        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CommentDataModel Items = CommentArrayList.get(position);
        holder.CommentorName.setText(Items.getCommentatorName());
        holder.CommentDate.setText(Items.getCommentDate());
        holder.CommentText.setText(Items.getCommentText());
        holder.LikeCount.setText(Items.getCommentLikeCount());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        if (Items.getCommentISLiked().equalsIgnoreCase("0")){
            holder.Like.setImageResource(R.drawable.ic_like);
        }
        else if (Items.getCommentISLiked().equalsIgnoreCase("1")){
            holder.Like.setImageResource(R.drawable.ic_dislike);
        }


        if (Items.getCommentetorImage() == null || Items.getCommentetorImage().equalsIgnoreCase("null") || Items.getCommentetorImage().equalsIgnoreCase("")){
            Glide.with(context).load(R.drawable.ic_person_black_24dp).apply(options).into(holder.CommentorImage);
        }
        else{

            String ImgURL = P_UserProfileBase+"/" +Items.getCommentorUserId()+ "/" +Items.getCommentetorImage().replace(" ","%20");
            Glide.with(context).load(ImgURL).apply(options).into(holder.CommentorImage);
        }




        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Items.getCommentISLiked().equalsIgnoreCase("0")){
                    int count = Integer.parseInt(Items.getCommentLikeCount());
                    final String CommentId = (Items.getCommentId());
                    SetLike(CurrentPostId,CommentId);
                }


                else if (Items.getCommentISLiked().equalsIgnoreCase("1")){
                    int count = Integer.parseInt(Items.getCommentLikeCount());
                    final String CommentId = (Items.getCommentId());
                    SetDisLike(CurrentPostId,CommentId);

                }


            }
        });

        if (user_id.equalsIgnoreCase(Items.getCommentorUserId())){
            holder.delete_img.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ( holder. CommentText);

                }
            });

            holder.delete_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Delete  Post Reply");

                    alertDialogBuilder.setMessage("Are you sure you want to remove?");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            deletePostReply(Items.getCommentId());
//                            pb.setVisibility(View.VISIBLE);


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

        }else{
            holder.delete_img.setVisibility(View.GONE);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.reply_list,viewgroup,false);
        MyViewHolder holder = new MyViewHolder(layout);
        return holder;
    }

    @Override
    public int getItemCount()
    {

        return CommentArrayList.size();
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


    public void SetLike(String PostId, String ReplyId) {

        String url = (CommonUtils.BASE_URL)+"like_comment_reply";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", preferences.getStringValue(CommonUtils.MEMBER_ID,""))
                .add("update_id", PostId)
                .add("reply_id", ReplyId)
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

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);

                            String success = json.getString("status");

                            if (success.equalsIgnoreCase("success")) {

                                postupdate.UpdateComments();


                            }

                            else if (success.equalsIgnoreCase("failed")){

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



    public void SetDisLike(String PostId, String ReplyId) {

        String url = (CommonUtils.BASE_URL)+"unlike_comment_reply";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", preferences.getStringValue(CommonUtils.MEMBER_ID,""))
                .add("update_id", PostId)
                .add("reply_id", ReplyId)
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

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);

                            String success = json.getString("status");

                            if (success.equalsIgnoreCase("success")) {
                                postupdate.UpdateComments();

                            }

                            else if (success.equalsIgnoreCase("failure")){

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

    public void deletePostReply(String comment_id){
        String url = (CommonUtils.BASE_URL)+"remove_comments_reply";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("reply_comment_id", comment_id)
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

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                                        pb.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                postupdate.UpdateComments();

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

    public interface UpdatePost{
        void UpdateComments();

    }

}
