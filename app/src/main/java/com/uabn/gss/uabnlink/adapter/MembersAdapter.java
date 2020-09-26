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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.Activities.WalltoWallMessaging;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
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

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

    Context context;
    Context actcontext;
    private ArrayList<MemberDataModel> MemberArrayList;
    String MemberCategory;
    private SharedPreferenceUtils preferances;
    String UserId;

    public MembersAdapter(Context context, ArrayList<MemberDataModel> MemberModelArrayList, Context actcontext, String Category)
    {
        this.context=context;
        MemberArrayList = MemberModelArrayList;
        MemberCategory = Category;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView MemberName, MemberAddress;
        RelativeLayout MemberLinear;
        ImageView MemberImage, RemoveFriend;
        Button WallToWall;


        public MyViewHolder(View view) {
            super(view);
            MemberName= view.findViewById(R.id.MemberName);
            MemberAddress = view.findViewById(R.id.MemberAddress);
            MemberLinear = view.findViewById(R.id.MemberLinear);
            MemberImage= view.findViewById(R.id.MemberImage);
            WallToWall= view.findViewById(R.id.WallToWall);
            RemoveFriend= view.findViewById(R.id.RemoveFriend);


        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MemberDataModel Items = MemberArrayList.get(position);

        if (MemberCategory.equalsIgnoreCase("MyConnections")){
            holder.WallToWall.setVisibility(View.VISIBLE);
            holder.RemoveFriend.setVisibility(View.VISIBLE);
        }

        holder.MemberName.setText(Items.getMemberName());
        holder.MemberAddress.setText(Items.getMemberAddress());
//        holder.MemberImage.setImageResource(R.drawable.ic_person_black_24dp);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        Glide.with(context).load(Items.getMemberImage()).apply(options).into(holder.MemberImage);
        //Glide.with(context).load(Items.getMemberImage()).error(context.getResources().getDrawable(R.drawable.ic_person_black_24dp)).into(holder.MemberImage);


        holder.MemberLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,UserProfileData.class);
                i.putExtra("UserId", Items.getMemberId());
                context.startActivity(i);
            }
        });

        holder.WallToWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,WalltoWallMessaging.class);
                i.putExtra("FriendId", Items.getMemberId());
                context.startActivity(i);
            }
        });

        holder.RemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Delete Connection");
                alertDialogBuilder.setMessage("Are you sure you want to delete?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteMyConnection(UserId, Items.getMemberId());

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
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.members_list_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return MemberArrayList.size();
    }


    public void deleteMyConnection(String user_id, String friend_id) {
        String url = (CommonUtils.BASE_URL) + "remove_connection";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("friend_id", friend_id)
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

        });

    }

}
