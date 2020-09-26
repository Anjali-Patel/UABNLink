package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.CertifiedBusinessDetailActivity;
import com.uabn.gss.uabnlink.Activities.EventDetails;
import com.uabn.gss.uabnlink.Activities.PBCRDetailActivity;
import com.uabn.gss.uabnlink.Activities.PostDetails;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.FindFreinds;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.uabn.gss.uabnlink.model.NotificationModel;

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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    Context context;
    String a="";
    Context actcontext;
    String UserId, ProfiulePicBaseURL;
    FindFreinds update_List;
    private SharedPreferenceUtils preferances;

    private ArrayList<NotificationModel> MemberArrayList;
    public NotificationAdapter(Context context, ArrayList<NotificationModel> MemberModelArrayList) {
        this.context = context;
        this.MemberArrayList = MemberModelArrayList;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final NotificationModel Items = MemberArrayList.get(position);

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.primaryTextColor));
        }
        else{
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationbg));
        }
        holder.title.setText(Items.getDisplay_text());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a=MemberArrayList.get(position).getType();
                if(a.equalsIgnoreCase("1")){
                    notificationStatus(Items.getId(),UserId);
                    Intent i = new Intent(context,PostDetails.class);
                    i.putExtra("Id", UserId);
                    i.putExtra("PostId", Items.getNotfn_id());
                    context.startActivity(i);

                }  else{

                }if(a.equalsIgnoreCase("2")){
                    notificationStatus(Items.getId(),UserId);
                    Intent intent= new Intent(context, PBCRDetailActivity.class);
                    intent.putExtra("pbcr_id", Items.getNotfn_id());
                    context.startActivity(intent);
                } else{

                }if(a.equalsIgnoreCase("3")){
                    notificationStatus(Items.getId(),UserId);
                    Intent intent = new Intent(context, CertifiedBusinessDetailActivity.class);
                    intent.putExtra("cb_id", Items.getNotfn_id());
                    context.startActivity(intent);
                }  else{

                } if(a.equalsIgnoreCase("4")){
                    notificationStatus(Items.getId(),UserId);
                    Intent intent= new Intent(context, EventDetails.class);
                    intent.putExtra("EventId", Items.getNotfn_id());
                    intent.putExtra("UserId",UserId);
                    context.startActivity(intent);
                }else{

                }

            }
        });


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.notification_layout, viewgroup, false);
        MyViewHolder holder = new MyViewHolder(layout);
        return holder;
    }
    @Override
    public int getItemCount() {

        return MemberArrayList.size();
    }
      public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
          public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.my_notification);

          }
    }


    public void notificationStatus(String notification_id, String user_id) {

        String url = (CommonUtils.BASE_URL)+"update_notificaton_status";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("notification_id", notification_id)

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
//                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
//                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();

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