package com.uabn.gss.uabnlink.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.EventCommentModel;

import java.util.ArrayList;

public class EventCommentAdapter  extends RecyclerView.Adapter<EventCommentAdapter.MyViewHolder> {

    Context context;
    Context actcontext;
    private ArrayList<EventCommentModel> CommentArrayList;
    private SharedPreferenceUtils preferences;

    public EventCommentAdapter(Context context, ArrayList<EventCommentModel> CommentModelArrayList)
    {
        this.context=context;
        CommentArrayList = CommentModelArrayList;
        preferences = SharedPreferenceUtils.getInstance(context);
//        P_UserProfileBase =UserProfileBase;
//        CurrentPostId = PostId;
//        postupdate = (CommentAdapter.UpdatePost) context;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView CommentorName,CommentDate,CommentText;
        ImageView CommentorImage;


        public MyViewHolder(View view) {
            super(view);
            CommentorName= view.findViewById(R.id.CommentorName);
            CommentDate = view.findViewById(R.id.CommentDate);
            CommentText = view.findViewById(R.id.CommentText);
            CommentorImage = view.findViewById(R.id.CommentorImage);


        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EventCommentModel Items = CommentArrayList.get(position);

        holder.CommentorName.setText(Items.getName());
        holder.CommentDate.setText(Items.getDate());
        holder.CommentText.setText(Items.getComment());

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

}
