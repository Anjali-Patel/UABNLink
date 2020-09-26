package com.uabn.gss.uabnlink.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.model.GraduateModel;

import java.util.ArrayList;

public class GraduateAdapter extends  RecyclerView.Adapter<GraduateAdapter.MyViewHolder> {
    Context context;
    Context actcontext;
    private ArrayList<GraduateModel> HighSchoolArrayList;
    private IOnItemClickListener iClickListener;
    public interface IOnItemClickListener {
        void onItemClick(String text);

    }
    public GraduateAdapter(Context context, ArrayList<GraduateModel> HighSchoolArrayList,IOnItemClickListener iClickListener) {
        this.context = context;
        this.HighSchoolArrayList = HighSchoolArrayList;
        this.iClickListener=iClickListener;
    }
    @Override
    public void onBindViewHolder(final GraduateAdapter.MyViewHolder holder, int position) {
        final GraduateModel Items = HighSchoolArrayList.get(position);
        holder.highschool_list.setText(Items.getGraduateschool_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onItemClick(CommonUtils.graduate_school_id=Items.getGraduateschool_name());



            }
        });

    }

    @Override
    public GraduateAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.high_school_list, viewgroup, false);
        GraduateAdapter.MyViewHolder holder = new GraduateAdapter.MyViewHolder(layout);
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