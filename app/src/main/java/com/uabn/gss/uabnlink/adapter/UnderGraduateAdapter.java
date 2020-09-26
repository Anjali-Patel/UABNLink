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
import com.uabn.gss.uabnlink.model.UnderGraduateModel;

import java.util.ArrayList;

public class UnderGraduateAdapter extends  RecyclerView.Adapter<UnderGraduateAdapter.MyViewHolder> {
    private IOnItemClickListener iClickListener;
    Context context;
    Context actcontext;

    private ArrayList<UnderGraduateModel> HighSchoolArrayList;
    public interface IOnItemClickListener {
        void onItemClick(String text);

    }

    public UnderGraduateAdapter(Context context, ArrayList<UnderGraduateModel> HighSchoolArrayList, IOnItemClickListener iClickListener) {
        this.context = context;
        this.HighSchoolArrayList = HighSchoolArrayList;
        this.iClickListener=iClickListener;

    }

    @Override
    public void onBindViewHolder(final UnderGraduateAdapter.MyViewHolder holder, int position) {
        final UnderGraduateModel Items = HighSchoolArrayList.get(position);
        holder.highschool_list.setText(Items.getUndergraduatename());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onItemClick(CommonUtils.undergraduate_school_id=Items.getUdergraduate_id());
            }
        });
    }

    @Override
    public UnderGraduateAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.high_school_list, viewgroup, false);
        UnderGraduateAdapter.MyViewHolder holder = new UnderGraduateAdapter.MyViewHolder(layout);
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
