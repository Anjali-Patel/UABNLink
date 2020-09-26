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
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.ChatListModel;

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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    Context context;
    Context actcontext;
    private ArrayList<ChatListModel> ChatListArrayList;
    private SharedPreferenceUtils preferances;
    String UserId;

    public ChatListAdapter(Context context, ArrayList<ChatListModel> ChatModelArrayList, Context actcontext)
    {
        this.context=context;
        ChatListArrayList = ChatModelArrayList;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView MemberName, Message, DateTime, NewMsgCount;
        RelativeLayout MemberLinear;
        ImageView MemberImage, RemoveChat;


        public MyViewHolder(View view) {
            super(view);
            MemberName= view.findViewById(R.id.MemberName);
            Message= view.findViewById(R.id.Message);
            DateTime= view.findViewById(R.id.DateTime);
            MemberLinear = view.findViewById(R.id.MemberLinear);
            MemberImage= view.findViewById(R.id.MemberImage);
            RemoveChat= view.findViewById(R.id.RemoveChat);
            NewMsgCount= view.findViewById(R.id.NewMsgCount);
        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ChatListModel Items = ChatListArrayList.get(position);


        holder.MemberName.setText(Items.getToUserName());
        holder.Message.setText(Items.getMessage());
        holder.DateTime.setText(Items.getDate());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(context).load(Items.getFromUserImage()).apply(options).into(holder.MemberImage);


        holder.MemberLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,ChatMessages.class);
                i.putExtra("ToUserId", Items.getToUserId());
                i.putExtra("ToUserName", Items.getToUserName());
                i.putExtra("UserImage", Items.getFromUserImage());
                context.startActivity(i);
            }
        });




        if (!Items.getUnreadMessages().equalsIgnoreCase("0")){
            holder.NewMsgCount.setVisibility(View.VISIBLE);
            holder.NewMsgCount.setText(Items.getUnreadMessages());
        }
        else{
            holder.NewMsgCount.setVisibility(View.GONE);
        }


    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.chat_list_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return ChatListArrayList.size();
    }


    public void deleteMyConnection(String user_id, String friend_id) {
        String url = (CommonUtils.BASE_URL) + "remove_connection";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("friend_id", friend_id)
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

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
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

