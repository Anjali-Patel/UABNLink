package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.UpdateRequests;
import com.uabn.gss.uabnlink.model.FriendRequestDataModel;

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

public class FriendReqAdapter extends RecyclerView.Adapter<FriendReqAdapter.MyViewHolder> {
    Context context;
    private ArrayList<FriendRequestDataModel> RequestArrayList;
    UpdateRequests requpdate;


    public FriendReqAdapter(Context context, ArrayList<FriendRequestDataModel> ReqModelArrayList,  UpdateRequests requpdate)
    {
        this.context=context;
        RequestArrayList = ReqModelArrayList;
        this.requpdate = requpdate;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Name, CommentDate;
        LinearLayout ReqLinear;
        ImageView MemberImage;
        Button AccptReq, RejectReq;


        public MyViewHolder(View view) {
            super(view);
            Name= view.findViewById(R.id.Name);
            CommentDate = view.findViewById(R.id.CommentDate);
            MemberImage= view.findViewById(R.id.MemberImage);
            ReqLinear= view.findViewById(R.id.ReqLinear);
            AccptReq= view.findViewById(R.id.AccptReq);
            RejectReq= view.findViewById(R.id.RejectReq);

        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FriendRequestDataModel Items = RequestArrayList.get(position);

        holder.Name.setText(Items.getReqSenderName());
        holder.CommentDate.setText(Items.getReqDate());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(context).load(Items.getReqenderImage()).apply(options).into(holder.MemberImage);

        holder.ReqLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,UserProfileData.class);
                i.putExtra("UserId", Items.getReqSenderId());
                context.startActivity(i);
            }
        });

        holder.AccptReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to accept this friend request?");
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AcceptRequest(Items.getReqSenderId(), Items.getReqReceiverId());

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

        holder.RejectReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to reject this friend request?");
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        RejectRequest(Items.getReqSenderId(), Items.getReqReceiverId());

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

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.friend_req_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return RequestArrayList.size();
    }

    public void AcceptRequest(String ReqId, String UserId) {

        String url = (CommonUtils.BASE_URL)+"friend_request_action";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("request_id", ReqId)
                .add("action", "accept")
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
                                requpdate.UpdateRequests();
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




    public void RejectRequest(String ReqId, String UserId) {

        String url = (CommonUtils.BASE_URL)+"friend_request_action";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("request_id", ReqId)
                .add("action", "reject")
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
                                requpdate.UpdateRequests();
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


}
