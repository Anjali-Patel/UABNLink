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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.FindFreinds;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.MemberDataModel;
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


public class FindContactAdapter extends RecyclerView.Adapter<FindContactAdapter.MyViewHolder> {

    Context context;
    Context actcontext;
    String UserId, ProfiulePicBaseURL;
    FindFreinds update_List;

    private ArrayList<MemberDataModel> MemberArrayList;

    public FindContactAdapter(Context context,
                              ArrayList<MemberDataModel> MemberModelArrayList,
                              String ProfiulePicURL, FindFreinds ListFriends) {
        this.context = context;
        MemberArrayList = MemberModelArrayList;
        ProfiulePicBaseURL = ProfiulePicURL;
        update_List = ListFriends;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MemberDataModel Items = MemberArrayList.get(position);


        holder.MemberName.setText(Items.getMemberName());
        holder.gender.setText(Items.getMemberGender());
        holder.location.setText(Items.getMemberLocation());
        holder.joined.setText(Items.getMemberJoined());
        holder.status.setText(Items.getMemberStatus());
        holder.send.setText(Items.getRequestStatus());

        String ReqStatus  = Items.getRequestStatus();
        if (ReqStatus.equalsIgnoreCase("send connection request")){
            holder.send.setBackground(context.getResources().getDrawable(R.drawable.btn_background));
        }
        else if (ReqStatus.equalsIgnoreCase("pending")){
            holder.send.setBackground(context.getResources().getDrawable(R.drawable.yellow_btn_background));
        }
        else if (ReqStatus.equalsIgnoreCase("connected")){
            holder.send.setBackground(context.getResources().getDrawable(R.drawable.green_btn_background));
        }
        else if (ReqStatus.equalsIgnoreCase("rejected")){
            holder.send.setBackground(context.getResources().getDrawable(R.drawable.red_btn_background));
        }
        else{
            holder.send.setBackground(context.getResources().getDrawable(R.drawable.btn_background));
        }


        holder.MemberImage.setImageResource(R.drawable.sample);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        String ProfileURL = ProfiulePicBaseURL+ Items.getMemberId() + "/" + Items.getMemberImage().replace(" ","%20");
        Glide.with(context).load(ProfileURL).apply(options).into(holder.MemberImage);


        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Items.getRequestStatus().equalsIgnoreCase("send connection request")) {

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setMessage("Are you sure you want to send request?");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            sendConnectionRequest(Items.getMemberId());

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
                }            }
        });

        holder.MemberLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,UserProfileData.class);
                i.putExtra("UserId", Items.getMemberId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.member_detail_list, viewgroup, false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount() {

        return MemberArrayList.size();
    }

    private void sendConnectionRequest( String ReceiverId) {
        String url = (CommonUtils.BASE_URL) + "sendConnectionRequest";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("receiver_id", ReceiverId)
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

                    ((Activity) context).runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(myResponse);
                                        String success = json.getString("status");
                                        if (success.equalsIgnoreCase("success")) {
                                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                            update_List.FreindListLoad();


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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView MemberName, gender, joined, location, status;
        LinearLayout MemberLinear;
        ImageView MemberImage;
        Button send;
        SharedPreferenceUtils preferances;


        public MyViewHolder(View view) {
            super(view);
            MemberName = view.findViewById(R.id.MemberName);
            gender = view.findViewById(R.id.gender);
            joined = view.findViewById(R.id.joined);
            location = view.findViewById(R.id.location);
            status = view.findViewById(R.id.status);
            MemberLinear = view.findViewById(R.id.MemberLinear);
            MemberImage = view.findViewById(R.id.MemberImage);
            send = view.findViewById(R.id.send);
            preferances = SharedPreferenceUtils.getInstance(context);
            UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");


        }
    }


}
