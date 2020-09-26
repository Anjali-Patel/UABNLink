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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.ChatMessages;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.MemberDataModel;

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

public class ChatMemberAdapter extends RecyclerView.Adapter<ChatMemberAdapter.MyViewHolder> {

    Context context;
    Context actcontext;
    private ArrayList<MemberDataModel> MemberArrayList;
    String MemberCategory;
    private SharedPreferenceUtils preferances;
    String UserId;

    public ChatMemberAdapter(Context context, ArrayList<MemberDataModel> MemberModelArrayList, Context actcontext, String Category)
    {
        this.context=context;
        MemberArrayList = MemberModelArrayList;
        MemberCategory = Category;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView MemberName;
        RelativeLayout MemberLinear;
        ImageView MemberImage;


        public MyViewHolder(View view) {
            super(view);
            MemberName= view.findViewById(R.id.MemberName);
            MemberLinear = view.findViewById(R.id.MemberLinear);
            MemberImage= view.findViewById(R.id.MemberImage);


        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MemberDataModel Items = MemberArrayList.get(position);


        holder.MemberName.setText(Items.getMemberName());
//        holder.MemberImage.setImageResource(R.drawable.ic_person_black_24dp);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(context).load(Items.getMemberImage()).apply(options).into(holder.MemberImage);


        holder.MemberLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,ChatMessages.class);

                i.putExtra("ToUserId", Items.getMemberId());
                i.putExtra("ToUserName", Items.getMemberName());
                i.putExtra("UserImage", Items.getMemberImage());
                context.startActivity(i);
            }
        });





    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.contact_list_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return MemberArrayList.size();
    }



}
