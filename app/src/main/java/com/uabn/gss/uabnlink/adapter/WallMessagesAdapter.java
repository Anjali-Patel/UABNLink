package com.uabn.gss.uabnlink.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.WallMessagesModel;

import java.util.ArrayList;

public class WallMessagesAdapter extends RecyclerView.Adapter<WallMessagesAdapter.MyViewHolder> {
    Context context;
    Context actcontext;
    private ArrayList<WallMessagesModel> MessagesArrayList;
    private SharedPreferenceUtils preferances;
    String UserId;

    public WallMessagesAdapter(Context context, ArrayList<WallMessagesModel> MessagesModelArrayList)
    {
        this.context=context;
        MessagesArrayList = MessagesModelArrayList;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView FriendName, MessageText, MessageDate;
        ImageView FriendImage;
        LinearLayout WallDateLinear, WallLinear;
        RelativeLayout WallRelative;


        public MyViewHolder(View view) {
            super(view);
            FriendName= view.findViewById(R.id.FriendName);
            MessageText = view.findViewById(R.id.MessageText);
            MessageDate = view.findViewById(R.id.MessageDate);
            FriendImage= view.findViewById(R.id.FriendImage);
            WallDateLinear= view.findViewById(R.id.WallDateLinear);
            WallLinear= view.findViewById(R.id.WallLinear);
            WallRelative= view.findViewById(R.id.WallRelative);



        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final WallMessagesModel Items = MessagesArrayList.get(position);

        if (Items.getFromUserId().equalsIgnoreCase(UserId)){
            holder.WallDateLinear.setGravity(Gravity.END);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.WallRelative.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            holder.WallRelative.setLayoutParams(params);

            }
        else {
            holder.WallDateLinear.setGravity(Gravity.START);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.WallRelative.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            holder.WallRelative.setLayoutParams(params);
        }
//
//        android:layout_alignParentEnd="true"
//        android:layout_alignParentTop="true"

        holder.FriendName.setText(Items.getUserName());
        holder.MessageText.setText(Items.getMessage());
        holder.MessageDate.setText(Items.getMessageDate());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
//        holder.MemberImage.setImageResource(R.drawable.ic_person_black_24dp);
        Glide.with(context).load(Items.getProfilePicture()).apply(options).into(holder.FriendImage);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.wall_messages_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return MessagesArrayList.size();
    }
}

