package com.uabn.gss.uabnlink.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.Activities.GroupDetails;
import com.uabn.gss.uabnlink.Activities.PBCRCommentActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.GroupEdit;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.UpdateGroup;
import com.uabn.gss.uabnlink.model.GroupDataModel;
import com.squareup.picasso.Picasso;

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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

        static Context context;
        Context actcontext;
        private ArrayList<GroupDataModel> GroupArrayList;
        static String UserId;
        private  SharedPreferenceUtils preferances;
        static ShareGroupDialogue share;
        static String GroupIdSelected;
        GroupEdit groupedit;
        UpdateGroup updategroup;



    public GroupAdapter(Context context, ArrayList<GroupDataModel> GroupModelArrayList, Context actcontext, GroupEdit groupedit, UpdateGroup updategroup)
    {
        this.context=context;
        GroupArrayList = GroupModelArrayList;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        this.groupedit = groupedit;
        this.updategroup = updategroup;


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Title, by, type, date, views, comment;
        Button CommentButton;
        LinearLayout GroupLinear, editGroupLinear;
        ImageView GroupImage, JoinGroup, LikePost, PostComments, Share, delete, DisLikePost, LeaveButton;


        public MyViewHolder(View view) {
            super(view);
            Title= view.findViewById(R.id.Title);
            by = view.findViewById(R.id.by);
            type= view.findViewById(R.id.type);
            date = view.findViewById(R.id.date);
            views= view.findViewById(R.id.views);
            comment= view.findViewById(R.id.comment);
            LeaveButton= view.findViewById(R.id.LeaveButton);

            CommentButton= view.findViewById(R.id.CommentButton);
            GroupImage= view.findViewById(R.id.GroupImage);
            JoinGroup= view.findViewById(R.id.JoinGroup);
            LikePost= view.findViewById(R.id.LikePost);
            DisLikePost = view.findViewById(R.id.DisLikePost);
            PostComments= view.findViewById(R.id.PostComments);
            Share= view.findViewById(R.id.Share);
//            GroupLinear= view.findViewById(R.id.GroupLinear);
            editGroupLinear = view.findViewById(R.id.editGroupLinear);
            delete = view.findViewById(R.id.delete);

        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GroupDataModel Items = GroupArrayList.get(position);
        if (Items.getGroupCreatorUserId().equalsIgnoreCase(preferances.getStringValue(CommonUtils.MEMBER_ID,""))){
           holder.editGroupLinear.setVisibility(View.VISIBLE);
        }
        else{
            holder.JoinGroup.setVisibility(View.GONE);
        }
        if (Items.getIsJoined().equalsIgnoreCase("1")){
            holder.JoinGroup.setVisibility(View.GONE);
            holder.PostComments.setVisibility(View.VISIBLE);
            holder.LeaveButton.setVisibility(View.VISIBLE);
            holder.PostComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentPost post = new CommentPost();
                    post.show(((Activity)context).getFragmentManager(), CommentPost.class.getSimpleName());
                }
            });
        }
        else{
            holder.PostComments.setVisibility(View.GONE);
            holder.JoinGroup.setVisibility(View.VISIBLE);
        }
        holder.Title.setText(Items.getGroupName());
        holder.by.setText(Items.getGroupBy());
        holder.type.setText(Items.getGroupType());
        holder.date.setText(Items.getGroupDate());
        holder.views.setText(Items.getGroupViews());
        holder.comment.setText(Items.getGroupComments());
        if (Items.getIsGroupLiked().equalsIgnoreCase("1")){
            holder.LikePost.setVisibility(View.GONE);
            holder.DisLikePost.setVisibility(View.VISIBLE);
        }
        else if (Items.getIsGroupLiked().equalsIgnoreCase("0")){
            holder.LikePost.setVisibility(View.VISIBLE);
            holder.DisLikePost.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        if (!Items.getGroupIcon().equalsIgnoreCase("")){
            Glide.with(context).asBitmap().load(Items.getGroupIcon()).apply(options).into(holder.GroupImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,GroupDetails.class);
                i.putExtra("GroupId", Items.getGroupId());
                i.putExtra("img", Items.getGroupIcon());
                i.putExtra("GroupId", Items.getGroupId());
                i.putExtra("name", Items.getGroupName());
                i.putExtra("date", Items.getGroupDate());
                i.putExtra("IsLiked", Items.getIsGroupLiked());
                i.putExtra("UserId", (preferances.getStringValue(CommonUtils.MEMBER_ID,"")));
                context.startActivity(i);
            }
        });

        holder.PostComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, PBCRCommentActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", "group");
                intent.putExtra("img", Items.getGroupIcon());
                intent.putExtra("group_id", Items.getGroupId());
                intent.putExtra("name", Items.getGroupName());
                intent.putExtra("date", Items.getGroupDate());
                context.startActivity(intent);
            }
        });

        holder.CommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, PBCRCommentActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", "group");
                intent.putExtra("img", Items.getGroupIcon());
                intent.putExtra("group_id", Items.getGroupId());
                intent.putExtra("name", Items.getGroupName());
                intent.putExtra("date", Items.getGroupDate());
                context.startActivity(intent);

            }
        });


        holder.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share= new ShareGroupDialogue();
                GroupIdSelected = Items.getGroupId();
                share.show(((Activity)context).getFragmentManager(),ShareGroupDialogue.class.getSimpleName());
                }
        });

        holder.LikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetLike(Items.getGroupId());
                holder.LikePost.setVisibility(View.GONE);
                holder.DisLikePost.setVisibility(View.VISIBLE);
            }
        });

        holder.DisLikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDisLike(Items.getGroupId());
                holder.LikePost.setVisibility(View.VISIBLE);
                holder.DisLikePost.setVisibility(View.GONE);
            }
        });

        holder.JoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                alertDialog.setTitle("Join Group");

                alertDialog.setMessage("Are you sure you want to join this group?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        JoinGroup(UserId, Items.getGroupId());
                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alertDialog.dismiss();

                    }});

                alertDialog.show();
            }
        });

        holder.LeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                alertDialog.setTitle("Leave Group");

                alertDialog.setMessage("Are you sure you want to leave this group?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        LeaveGroup(UserId, Items.getGroupId());
                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alertDialog.dismiss();

                    }});

                alertDialog.show();
            }
        });

        holder.editGroupLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupedit.EditGroup("Edit Group", Items.getGroupId());

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                alertDialog.setTitle("Delete Group");

                alertDialog.setMessage("Are you sure you want to delete this group?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        DeleteGroup(Items.getGroupId());

                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alertDialog.dismiss();

                    }});

                alertDialog.show();
            }
        });




    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.group_list_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return GroupArrayList.size();
    }

    public static class CommentPost extends DialogFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.comment_post, null);

            final TextView SearchText = (TextView) dialogView.findViewById(R.id.searchtext);
            ImageView close = (ImageView) dialogView.findViewById(R.id.search_close_btn);
            final ProgressBar progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            Button dialogButton = (Button) dialogView.findViewById(R.id.send);




            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            return dialogView;
        }


    }

    public void JoinGroup (String UserId, String GroupId){
        String url = (CommonUtils.BASE_URL) + "join_group";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                updategroup.GroupUpdate();

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


    public void LeaveGroup (String UserId, String GroupId){
        String url = (CommonUtils.BASE_URL) + "leave_group";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                updategroup.GroupUpdate();

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


    @SuppressLint("ValidFragment")
    public static class ShareGroupDialogue extends DialogFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.share, null);
            ImageView search_close_btn=dialogView.findViewById(R.id.search_close_btn);
            final TextView SearchText = (TextView) dialogView.findViewById(R.id.searchtext);
            Button submit = (Button) dialogView.findViewById(R.id.submit);
            final EditText To_text,subject_text,message_text;
            To_text=dialogView.findViewById(R.id.To_text);
            subject_text=dialogView.findViewById(R.id.subject_text);
            message_text=dialogView.findViewById(R.id.message_text);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str_to = To_text.getText().toString().trim();
                    String str_subject = subject_text.getText().toString().trim();
                    String str_message = message_text.getText().toString().trim();
                    if (str_to.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Please enter your mail address", Toast.LENGTH_LONG).show();
                    }else if(str_subject.equalsIgnoreCase("")){
                        Toast.makeText(getActivity(), "Please enter your Subject", Toast.LENGTH_LONG).show();
                    }else if(!isValidEmail(To_text.getText().toString().trim())) {
                        Toast.makeText(getActivity(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                    } else if(str_message.equalsIgnoreCase("")){
                        Toast.makeText(getActivity(), "Please enter your Message", Toast.LENGTH_LONG).show();
                    }else{
                        ShareGroup(UserId,str_to,str_subject,str_message,GroupIdSelected);
                    }
                }
            });


            search_close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            return dialogView;
        }


    }

    private static boolean isValidEmail(CharSequence target) {
        boolean emailflag = false;
        String emailArr[] = target.toString().split("[,]");
        for (int i = 0; i < emailArr.length; i++) {
            emailflag = Patterns.EMAIL_ADDRESS.matcher(
                    emailArr[i].trim()).matches();
        }
        return emailflag;
    }

    public static void  ShareGroup(String UserId, String str_to, String str_subject, String str_message, String group_id){
        String url = (CommonUtils.BASE_URL)+"share_group";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("to_email", str_to)
                .add("subject", str_subject)
                .add("message", str_message)
                .add("group_id", group_id)
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
                                share.dismiss();

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

    public void SetLike(String GroupId) {

        String url = (CommonUtils.BASE_URL)+"add_like_to_group";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                                updategroup.GroupUpdate();
                            }
                            else{
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void SetDisLike(String GroupId) {

        String url = (CommonUtils.BASE_URL)+"remove_like_to_group";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                                updategroup.GroupUpdate();
                            }
                            else{
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void DeleteGroup(String GroupId) {

        String url = (CommonUtils.BASE_URL)+"delete_group";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("group_id", GroupId)
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
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
                                updategroup.GroupUpdate();
                            }
                            else{
                                Toast.makeText(context,json.getString("message"),Toast.LENGTH_LONG).show();
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
