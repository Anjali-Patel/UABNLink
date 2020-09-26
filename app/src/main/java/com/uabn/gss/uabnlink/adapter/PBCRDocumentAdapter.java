package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uabn.gss.uabnlink.Activities.EditPBCRActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.model.DocumentDataModel;
import com.uabn.gss.uabnlink.model.DocumentModel;

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

public class PBCRDocumentAdapter extends RecyclerView.Adapter<PBCRDocumentAdapter.MyViewHolder> {

    Context context;
    String user_id,pbcr_id;
    Context actcontext;
    private ArrayList<DocumentModel> DocumentArrayList;

    public PBCRDocumentAdapter(Context context, ArrayList<DocumentModel> DocumentModelArrayList,String user_id,String pbcr_id)
    {
        this.context=context;

        this.pbcr_id=pbcr_id;
        this.user_id=user_id;
        DocumentArrayList = DocumentModelArrayList;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView doc2;

        ImageView cross2;


        public MyViewHolder(View view) {
            super(view);
            doc2= view.findViewById(R.id.doc2);
            cross2 = view.findViewById(R.id.cross2);




        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DocumentModel Items = DocumentArrayList.get(position);

        holder.doc2.setText(Items.getDoc_name());
        holder.doc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDocument(user_id,pbcr_id,Items.getDoc_id());
                holder.cross2.setVisibility(View.GONE);
                holder.doc2.setVisibility(View.GONE);


            }
        });


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.pbce_document,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }


    @Override
    public int getItemCount()
    {

        return DocumentArrayList.size();
    }
    public void  deleteDocument(String user_id,String pbcr_id,String doc_id){
        String url = (CommonUtils.BASE_URL)+"remove_pbcr_documents";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("pbcr_id", pbcr_id)
                .add("doc_id", doc_id)
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

                                                ((Activity)context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        JSONObject json = null;
                                                        try {
                                                            json = new JSONObject(myResponse);
                                                            String success = json.getString("status");
                                                            if (success.equalsIgnoreCase("success")) {
                                                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();

//                                                                postupdate.GetPBCRDetails();
                                                            } else {
                                                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }


                                                    }
                                                });
                                            }

                                        }
        );

    }


}
