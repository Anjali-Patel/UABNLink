package com.uabn.gss.uabnlink.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.MyViewHolder> {

    Context context;
    Context actcontext;
    private ArrayList<MemberDataModel> MemberArrayList;

    public ConnectionsAdapter(Context context, ArrayList<MemberDataModel> MemberModelArrayList, Context actcontext)
    {
        this.context=context;
        MemberArrayList = MemberModelArrayList;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView MemberName;
        LinearLayout ConnectionLinear;
        ImageView MemberImage;


        public MyViewHolder(View view) {
            super(view);
            MemberName= view.findViewById(R.id.MemberName);
            ConnectionLinear = view.findViewById(R.id.ConnectionLinear);
            MemberImage= view.findViewById(R.id.MemberImage);


        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MemberDataModel Items = MemberArrayList.get(position);

        holder.MemberName.setText(Items.getMemberName());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(context).load(Items.getMemberImage()).apply(options).into(holder.MemberImage);


        holder.ConnectionLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,UserProfileData.class);
                i.putExtra("UserId", Items.getMemberId());
                context.startActivity(i);
            }
        });



    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.connctions_list,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return MemberArrayList.size();
    }
}
