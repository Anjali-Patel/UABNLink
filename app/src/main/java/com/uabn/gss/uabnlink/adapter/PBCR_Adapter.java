package com.uabn.gss.uabnlink.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.uabn.gss.uabnlink.Activities.EditPBCRActivity;
import com.uabn.gss.uabnlink.Activities.PBCRCommentActivity;
import com.uabn.gss.uabnlink.Activities.PBCRDetailActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.EditPBCR;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.UpdatePBCR;
import com.uabn.gss.uabnlink.model.PBCR_DataModel;
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

public class PBCR_Adapter extends RecyclerView.Adapter<PBCR_Adapter.MyViewHolder> {
    String UserId;
     public static ProgressBar progess_load;
     static Share share;
    UpdatePBCR postupdate;
    private  SharedPreferenceUtils preferances;
    static Context context;
    Context actcontext;
    static String selectedpbcr;
    private ArrayList<PBCR_DataModel> PBCRArrayList;
    EditPBCR editpbcr;


    public PBCR_Adapter(Context context, ArrayList<PBCR_DataModel> PBCRModelArrayList, UpdatePBCR postupdate, EditPBCR editpbcr)
    {
        this.context=context;
        this.actcontext = actcontext;
        PBCRArrayList = PBCRModelArrayList;
        this.postupdate =  postupdate;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        this.editpbcr = editpbcr;


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Title, services, date, comment;
        Button CommentButton;
        LinearLayout PBCRLinear;
        ImageView PBCRImage, AttatchDoc, ViewComments, Share,edit,delete;


        public MyViewHolder(View view) {
            super(view);
            Title= view.findViewById(R.id.Title);
            services = view.findViewById(R.id.services);
            date = view.findViewById(R.id.date);
            comment= view.findViewById(R.id.comment);
            edit=view.findViewById(R.id.edit);
            delete=view.findViewById(R.id.delete);

            CommentButton= view.findViewById(R.id.CommentButton);
            PBCRImage= view.findViewById(R.id.PBCRImage);
            AttatchDoc= view.findViewById(R.id.AttatchDoc);
            ViewComments= view.findViewById(R.id.ViewComments);
            Share= view.findViewById(R.id.Share);



        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PBCR_DataModel Items = PBCRArrayList.get(position);
//        pbcr_userid=Items.getPbcr_userId_();
        holder.Title.setText(Items.getPBCRTitle());
        if(UserId.equalsIgnoreCase(Items.getPbcr_userId_())) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
            final String pbcrId = Items.getPBCR_id();
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editpbcr.EditPBCR(Items.PBCR_id);

//                    Intent intent = new Intent(context, EditPBCRActivity.class);
//                    intent.putExtra("pbcr_id",Items.getPBCR_id());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);


                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setMessage("Are you sure you want to remove?");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            deletePBCRPost(UserId, pbcrId);

                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });
        }else{
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);

        }

        holder.services.setText(Items.getPBCRService());
        holder.date.setText(Items.getPBCRDate());
        holder.comment.setText(Items.getPBCRComments());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
          Glide.with(context).load(Items.getPBCRImage()).apply(options).into(holder.PBCRImage);


          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PBCRDetailActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pbcr_id", Items.getPBCR_id());
                context.startActivity(intent);


            }

        });

         holder.CommentButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent= new Intent(context, PBCRCommentActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", "pbcr");
        intent.putExtra("img", Items.getPBCRImage());
        intent.putExtra("pbcr_id", Items.getPBCR_id());
        intent.putExtra("name", Items.getPBCRTitle());
        intent.putExtra("date", Items.getPBCRDate());
        context.startActivity(intent);
    }
});
        holder.ViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent= new Intent(context, PBCRCommentActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", "pbcr");
                intent.putExtra("img", Items.getPBCRImage());
                intent.putExtra("pbcr_id", Items.getPBCR_id());
                intent.putExtra("name", Items.getPBCRTitle());
                intent.putExtra("date", Items.getPBCRDate());
                context.startActivity(intent);

            }
        });

        holder.Share.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        share= new Share();
        selectedpbcr = Items.getPBCR_id();
        share.show(((Activity)context).getFragmentManager(),Share.class.getSimpleName());
        }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.pbcr_list_layout,viewgroup,false);
        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return PBCRArrayList.size();
    }


    @SuppressLint("ValidFragment")
    public static class Share extends DialogFragment {

    private       String user_id;
     private    SharedPreferenceUtils preferances;
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
            progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            Button submit = (Button) dialogView.findViewById(R.id.submit);
            preferances = SharedPreferenceUtils.getInstance(getActivity());
            user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
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
                   sharePbcr(user_id,str_to,str_subject,str_message,selectedpbcr);
                        progess_load.setVisibility(View.VISIBLE);
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
 public static void  sharePbcr(String UserId, String str_to, String str_subject, String str_message, String pbcr_id){
        String url = (CommonUtils.BASE_URL)+"share_pbcr_post";
     OkHttpClient client = new OkHttpClient();
     RequestBody formBody = new FormBody.Builder()
             .add("user_id", UserId)
             .add("to_email", str_to)
             .add("subject", str_subject)
             .add("message", str_message)
             .add("pbcr_id", pbcr_id)
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
                 progess_load.setVisibility(View.GONE);

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




 private static boolean isValidEmail(CharSequence target) {
        boolean emailflag = false;
        String emailArr[] = target.toString().split("[,]");
        for (int i = 0; i < emailArr.length; i++) {
            emailflag = Patterns.EMAIL_ADDRESS.matcher(
                    emailArr[i].trim()).matches();
        }
        return emailflag;
    }
    public void deletePBCRPost(String user_id,String pbcr_id){
        String url = (CommonUtils.BASE_URL)+"delete_pbcr";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("pbcr_id", pbcr_id)
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
                                postupdate.GetPBCRDetails();
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
