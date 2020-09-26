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
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.FindFreinds;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.HighSchoolModel;
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

public class HighSchoolAdapter  extends RecyclerView.Adapter<HighSchoolAdapter.MyViewHolder> {
    Context context;
    Context actcontext;
    private IOnItemClickListener iClickListener;
    private ArrayList<HighSchoolModel> HighSchoolArrayList;
    public interface IOnItemClickListener {
        void onItemClick(String text);

    }
    public HighSchoolAdapter(Context context, ArrayList<HighSchoolModel> HighSchoolArrayList,IOnItemClickListener iClickListener) {
        this.iClickListener=iClickListener;
        this.context = context;
        this.HighSchoolArrayList = HighSchoolArrayList;
    }
    @Override
    public void onBindViewHolder(final HighSchoolAdapter.MyViewHolder holder, int position) {
        final HighSchoolModel Items = HighSchoolArrayList.get(position);
        holder.highschool_list.setText(Items.getHighschool_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onItemClick(CommonUtils.high_school_id=Items.getHigh_school_id());
            }
        });
    }
    @Override
    public HighSchoolAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.high_school_list, viewgroup, false);
        HighSchoolAdapter.MyViewHolder holder = new HighSchoolAdapter.MyViewHolder(layout);
        return holder;
    }
    @Override
    public int getItemCount() {
        return HighSchoolArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView highschool_list;
        public MyViewHolder(View view) {
            super(view);
            highschool_list = view.findViewById(R.id.highschool_list);
        }
    }
}