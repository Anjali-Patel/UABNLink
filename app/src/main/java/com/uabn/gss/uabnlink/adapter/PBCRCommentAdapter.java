package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.GroupcommentReply;
import com.uabn.gss.uabnlink.Activities.ViewPDF;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommentListRefresh;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.fragment.GroupFragment;
import com.uabn.gss.uabnlink.model.PBCRCommentModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PBCRCommentAdapter extends RecyclerView.Adapter<PBCRCommentAdapter.MyViewHolder> {
    String group_comment_post="0";
    private IOnItemClickListener iClickListener;
    public interface IOnItemClickListener {
        void onItemClick(String text,String text1,String text2);

    }

    Activity context;
    String UserId;
    Context actcontext;
    private ArrayList<PBCRCommentModel> CommentArrayList;
    private SharedPreferenceUtils preferences;
    String P_UserProfileBase, CurrentPostId;
    CommentListRefresh refeshlist;
    private RecyclerView.Adapter mAdapter;
    //    CommentAdapter.UpdatePost postupdate;

    public PBCRCommentAdapter(Activity context, ArrayList<PBCRCommentModel> CommentModelArrayList, CommentListRefresh refeshlist,IOnItemClickListener iClickListener)
    {
        this.context=context;
        CommentArrayList = CommentModelArrayList;
        preferences = SharedPreferenceUtils.getInstance(context);
        UserId = preferences.getStringValue(CommonUtils.MEMBER_ID, "");
        this.refeshlist = refeshlist;
        this.iClickListener=iClickListener;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView CommentorName,CommentDate,CommentText, GroupReply,ReplyCount;
        ImageView CommentorImage,delete,edit,Like;

        TextView GroupCommentLink,GroupDocument;
        RecyclerView GroupcommentimageList;
        WebView WebVideo;
        LinearLayout VideoLinear;


        public MyViewHolder(View view) {
            super(view);
            ReplyCount=view.findViewById(R.id.ReplyCount);
            CommentorName= view.findViewById(R.id.CommentorName);
            CommentDate = view.findViewById(R.id.CommentDate);
            CommentText = view.findViewById(R.id.CommentText);
            CommentorImage = view.findViewById(R.id.CommentorImage);
            GroupReply  = view.findViewById(R.id.GroupReply);
            delete=view.findViewById(R.id.delete);
            edit=view.findViewById(R.id.edit);
            Like=view.findViewById(R.id.Like);
            GroupCommentLink=view.findViewById(R.id.GroupCommentLink);
            GroupDocument=view.findViewById(R.id.GroupDocument);
            GroupcommentimageList=view.findViewById(R.id.GroupcommentimageList);
            VideoLinear=view.findViewById(R.id.VideoLinear);
            WebVideo=view.findViewById(R.id.WebVideo);

        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PBCRCommentModel Items = CommentArrayList.get(position);
        holder.ReplyCount.setText(Items.getReply_count());
        holder.CommentorName.setText(Items.getName());
        holder.CommentDate.setText(Items.getDate());
        holder.CommentText.setText(Items.getComment());
        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });



        if (Items.getType().equalsIgnoreCase("group")) {
            holder.GroupReply.setVisibility(View.VISIBLE);

            holder.GroupReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, GroupcommentReply.class);
                    i.putExtra("CommentId", Items.getComment_id());
                    i.putExtra("CommentorId", Items.getUser_id());
                    i.putExtra("CommentName", Items.getName());
                    i.putExtra("CommentText", Items.getComment());
                    i.putExtra("CommentDate", Items.getDate());
                    i.putExtra("CommentImage", Items.getImage());
                    i.putExtra("GroupId", Items.getGroup_id());
                    context.startActivity(i);
                }
            });
            if (!Items.getGroupcommentLink().equalsIgnoreCase("")) {
                holder.GroupCommentLink.setVisibility(View.VISIBLE);
                holder.GroupCommentLink.setText(Items.getGroupcommentLink());
            } else {
                holder.GroupCommentLink.setVisibility(View.GONE);
            }
            if (!Items.getGroupcommentEmddedVideo().equalsIgnoreCase("")) {
                holder.VideoLinear.setVisibility(View.VISIBLE);
                holder.WebVideo.getSettings().setJavaScriptEnabled(true);
                holder.WebVideo.setVisibility(View.VISIBLE);
                String html = Items.getGroupcommentEmddedVideo();
                Scanner scanner = new Scanner(html);
                boolean isWidhtFound = false, isHeightFound = false;
                StringBuilder htmlUrl = new StringBuilder();
                while (scanner.hasNext()) {
                    String possibleUrl = scanner.next();
                    Log.i("Scan", "onCreate: possibleUrl:-" + possibleUrl);
                    if (possibleUrl.contains("width")) {
                        isWidhtFound = true;
                        isHeightFound = false;

                    } else if (possibleUrl.contains("height")) {
                        isHeightFound = true;
                        isWidhtFound = false;

                    }
                    if (isWidhtFound) {
                        htmlUrl.append(" ").append("width=\"300\" ");
                        isWidhtFound = false;
                    } else if (isHeightFound) {
                        htmlUrl.append(" ").append("height=\"200\" ");
                        isHeightFound = false;
                    } else {
                        htmlUrl.append(possibleUrl);
                    }
                }
                Log.i("htmlUrl", "onCreate: htmlUrl" + htmlUrl);
                String iframeurl = String.valueOf(htmlUrl);
                if (iframeurl.contains("allowfullscreen></iframe>")) {
                    holder.WebVideo.loadData(htmlUrl.toString(), "text/html", null);
                } else {
                    holder.WebVideo.loadData(htmlUrl.toString() + "allowfullscreen></iframe>", "text/html", null);
                }
            }
            else{
                holder.VideoLinear.setVisibility(View.GONE);
            }
            if (Items.getGroupcommentDocument() != null){
                holder.GroupDocument.setVisibility(View.VISIBLE);
                holder.GroupDocument.setText(Items.getGroupcommentDocument());
                holder.GroupDocument.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String path = Items.getGroupcommentDocument();
                        Intent i = new Intent(context,ViewPDF.class);
                        i.putExtra("doc_path",path);
                        context.startActivity(i);
                    }
                });
            }
            else{
                holder.GroupDocument.setVisibility(View.GONE);
            }
            if (Items.getGroupcommentImageList().size() > 0){
               holder.GroupcommentimageList.setVisibility(View.VISIBLE);
                holder.GroupcommentimageList.setHasFixedSize(true);
                holder.GroupcommentimageList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
                holder.GroupcommentimageList.setItemAnimator(new DefaultItemAnimator());
                mAdapter = new MultipleImagesAdapter(context, Items.getGroupcommentImageList());
                holder.GroupcommentimageList.setAdapter(mAdapter);
            }
            else{
                holder.GroupcommentimageList.setVisibility(View.GONE);
            }

        }
        else {
            holder.GroupReply.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        if (Items.getImage() == null || Items.getImage().equalsIgnoreCase("null") || Items.getImage().equalsIgnoreCase("")){
            Glide.with(context).load(R.drawable.ic_person_black_24dp).apply(options).into(holder.CommentorImage);
        }
        else{
            Glide.with(context).load(Items.getImage()).apply(options).into(holder.CommentorImage);
        }
        if (UserId.equalsIgnoreCase(Items.getUser_id())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);


            if (Items.getType().equalsIgnoreCase("pbcr")){
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Are you sure you want to remove?");
                        alertDialogBuilder.setCancelable(false);

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteCertifiedBusinessComment(UserId, Items.getComment_id(),Items.getPbcr_id());

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
            if (Items.getType().equalsIgnoreCase("group")){

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Are you sure you want to remove?");
                        alertDialogBuilder.setCancelable(false);

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteGroupComment(UserId, Items.getComment_id(),Items.getGroup_id());

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

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        group_comment_post="1";
                        iClickListener.onItemClick(Items.getComment(),Items.getComment_id(),group_comment_post);





                    }
                });

            }
            if (Items.getType().equalsIgnoreCase("event")){
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Are you sure you want to remove?");
                        alertDialogBuilder.setCancelable(false);

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteEventComment(UserId, Items.getComment_id(),Items.getEvent_Id());

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



        }else{
            holder.delete.setVisibility(View.GONE);

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.pbcr_comments_list,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {
        return CommentArrayList.size();
    }

    public void deleteCertifiedBusinessComment(String user_id, String comment_id, final String pbcr_id ){
        String url = (CommonUtils.BASE_URL)+"remove_pbcr_comment";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("pbcr_id", pbcr_id)
                .add("comment_id", comment_id)
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
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                refeshlist.RefreshList("pbcr" ,pbcr_id );

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


    public void deleteGroupComment(String user_id, String comment_id, final String group_id ){
        String url = (CommonUtils.BASE_URL)+"remove_group_comment";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("group_id", group_id)
                .add("comment_id", comment_id)
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
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                refeshlist.RefreshList("group" ,group_id );

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

    public void deleteEventComment(String user_id, String comment_id, final String event_id ){
        String url = (CommonUtils.BASE_URL)+"remove_event_comment";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("event_id", event_id)
                .add("comment_id", comment_id)
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
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                refeshlist.RefreshList("event" ,event_id );

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

// Showing Images og group comments
    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleImagesList;

        public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList) {
            context1 = context;
            MultipleImagesList = ImagesList;
        }

        @NonNull
        @Override
        public MultipleImagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_listitem, parent, false);
            return new MultipleImagesAdapter.MyViewHolder(v);
        }

        @Override

        public void onBindViewHolder(@NonNull MultipleImagesAdapter.MyViewHolder holder, final int position) {

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.noimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            Glide.with(context).load(MultipleImagesList.get(position)).apply(options).into(holder.adapterImage);

            holder.adapterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .error(R.drawable.noimage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH);
                    final Dialog dialog=new Dialog(context, R.style.DialogAnimation_2);
//                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    dialog.setContentView(R.layout.zoom_profile_pic);

                    ZoomImageView img = dialog.findViewById(R.id.img);
                    final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                    ImageProgress.setVisibility(View.VISIBLE);
                    Glide.with(context).load(MultipleImagesList.get(position)).apply(options).into(img);
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

    }