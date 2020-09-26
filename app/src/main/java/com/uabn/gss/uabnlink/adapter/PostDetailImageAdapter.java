package com.uabn.gss.uabnlink.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.model.PostDetailImageModel;

import java.util.ArrayList;

public class PostDetailImageAdapter extends  RecyclerView.Adapter<PostDetailImageAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<PostDetailImageModel> person = new ArrayList<>();


    public PostDetailImageAdapter(Context context, ArrayList<PostDetailImageModel> person) {
        this.context = context;
        this.person = person;

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Context context;
        public ImageView image;
        public MyViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_detail_image, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PostDetailImageModel Items=person.get(position);

//            holder.webView.setVisibility(View.GONE);
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
            Glide.with(context).load(  Items.getImage()).apply(options).into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
//                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    dialog.setContentView(R.layout.zoom_profile_pic);
                    ZoomImageView img = dialog.findViewById(R.id.img);
                    final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                    ImageProgress.setVisibility(View.VISIBLE);
                    Glide.with(context).load(Items.getImage()).apply(options).into(img);
                    ImageProgress.setVisibility(View.GONE);
                    /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                    dialog.show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return person.size();
    }
}
