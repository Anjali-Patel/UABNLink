package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.CertifiedBusinessModel;

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

public class CertifiedBusinessAdapter extends RecyclerView.Adapter<CertifiedBusinessAdapter.MyViewHolder> {
    Context context;
    String baseURL, UserId, cb_id;
    String comment_id;
    Context actcontext;
    private ArrayList<CertifiedBusinessModel> CommentArrayList;
    private SharedPreferenceUtils preferances;

    public CertifiedBusinessAdapter(Context context, ArrayList<CertifiedBusinessModel> CommentModelArrayList, String cb_id) {
        this.cb_id = cb_id;
        this.context = context;
        CommentArrayList = CommentModelArrayList;

        this.baseURL = baseURL;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

    }

//    public CertifiedBusinessAdapter(CertifiedBusinessCommentActivity context, ArrayList<CertifiedBusinessModel> commentDataModelArrayList, String cb_id, Runnable runnable) {
//    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CertifiedBusinessModel Items = CommentArrayList.get(position);

        holder.CommentorName.setText(Items.getName());
        holder.CommentDate.setText(Items.getDate());
        holder.CommentText.setText(Items.getComment());
        comment_id = Items.getComment_id();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        if (Items.getImage() == null || Items.getImage().equalsIgnoreCase("null") || Items.getImage().equalsIgnoreCase("")) {
            Glide.with(context).load(R.drawable.ic_person_black_24dp).apply(options).into(holder.CommentorImage);
        } else {
            Glide.with(context).load(Items.getImage()).apply(options).into(holder.CommentorImage);
        }
        if (UserId.equalsIgnoreCase(Items.getUser_id())) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Are you sure you want to remove?");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            deleteCertifiedBusinessComment(UserId, comment_id, cb_id);

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

        } else {
            holder.delete.setVisibility(View.GONE);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.certified_business_commentlist, viewgroup, false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount() {

        return CommentArrayList.size();
    }

    public void deleteCertifiedBusinessComment(String user_id, String comment_id, String cb_id) {
        String url = (CommonUtils.BASE_URL) + "remove_cb_comment";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("comment_id", comment_id)
                .add("cb_id", cb_id)
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
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }

    }
        );

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView CommentorName, CommentDate, CommentText;
        ImageView CommentorImage, delete;


        public MyViewHolder(View view) {
            super(view);
            CommentorName = view.findViewById(R.id.CommentorName);
            CommentDate = view.findViewById(R.id.CommentDate);
            CommentText = view.findViewById(R.id.CommentText);
            CommentorImage = view.findViewById(R.id.CommentorImage);
            delete = view.findViewById(R.id.delete);


        }
    }


}

