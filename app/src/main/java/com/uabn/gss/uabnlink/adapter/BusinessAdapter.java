package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
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
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.CertifiedBusinessCommentActivity;
import com.uabn.gss.uabnlink.Activities.CertifiedBusinessDetailActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.BusinessdataModel;

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

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.MyViewHolder> {
    public static ProgressBar progess_load;
    static Context context;
    static Share share;
    static String cb_id;
    //    Context context;
    String profile_pic_url;
    Context actcontext;
    private ArrayList<BusinessdataModel> BusinessArrayList;

    public BusinessAdapter(Context context, ArrayList<BusinessdataModel> BusinessModelArrayList, Context activityContext) {
        this.context = context;
        this.actcontext = activityContext;
        BusinessArrayList = BusinessModelArrayList;

    }

    public static void sharePbcr(String UserId, String str_to, String str_subject, String str_message, String cb_id) {
        String url = (CommonUtils.BASE_URL) + "share_cb";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("to_email", str_to)
                .add("subject", str_subject)
                .add("message", str_message)
                .add("cb_id", cb_id)
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

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final BusinessdataModel Items = BusinessArrayList.get(position);
        holder.Title.setText(Items.getBusinesName());
        holder.country.setText(Items.getBusinesCountry());
        holder.services.setText(Items.getBusinesServices());
        holder.date.setText(Items.getBusinesDate());
        holder.comment.setText(Items.getBusinesComment());
        holder.address.setText(Items.getBusinesAddress());
        holder.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share = new Share();
                cb_id = Items.getCb_id();
                share.show(((Activity) context).getFragmentManager(), Share.class.getSimpleName());
            }
        });
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(context).load(Items.getBusinesImage()).apply(options).into(holder.BusinessImage);
        holder.CommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CertifiedBusinessCommentActivity.class);
                intent.putExtra("cb_id", Items.getCb_id());
                intent.putExtra("name", Items.getBusinesName());
                intent.putExtra("img", Items.getBusinesImage());
                intent.putExtra("date", Items.getBusinesDate());
                context.startActivity(intent);
            }
        });

        holder.ViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CertifiedBusinessCommentActivity.class);
                intent.putExtra("cb_id", Items.getCb_id());
                intent.putExtra("name", Items.getBusinesName());
                intent.putExtra("img", Items.getBusinesImage());
                intent.putExtra("date", Items.getBusinesDate());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CertifiedBusinessDetailActivity.class);
                intent.putExtra("cb_id", Items.getCb_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.business_list_layout, viewgroup, false);
        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount() {

        return BusinessArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Title, country, services, date, comment, address;
        Button CommentButton;
        LinearLayout BusinessLinear;
        ImageView BusinessImage, AttatchDoc, ViewComments, Share;


        public MyViewHolder(View view) {
            super(view);
            Title = view.findViewById(R.id.Title);
            country = view.findViewById(R.id.country);
            services = view.findViewById(R.id.services);
            date = view.findViewById(R.id.date);
            comment = view.findViewById(R.id.comment);
            address = view.findViewById(R.id.address);
            CommentButton = view.findViewById(R.id.CommentButton);
            BusinessImage = view.findViewById(R.id.BusinessImage);
            AttatchDoc = view.findViewById(R.id.AttatchDoc);
            ViewComments = view.findViewById(R.id.ViewComments);
            Share = view.findViewById(R.id.Share);
        }
    }

    public static class Share extends DialogFragment {

        private static String user_id;
        private static SharedPreferenceUtils preferances;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.share, null);
            ImageView search_close_btn = dialogView.findViewById(R.id.search_close_btn);
            final TextView SearchText = (TextView) dialogView.findViewById(R.id.searchtext);
            progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            Button submit = (Button) dialogView.findViewById(R.id.submit);
            preferances = SharedPreferenceUtils.getInstance(getActivity());
            user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
            final EditText To_text, subject_text, message_text;
            To_text = dialogView.findViewById(R.id.To_text);
            subject_text = dialogView.findViewById(R.id.subject_text);
            message_text = dialogView.findViewById(R.id.message_text);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str_to = To_text.getText().toString().trim();
                    String str_subject = subject_text.getText().toString().trim();
                    String str_message = message_text.getText().toString().trim();
                    if (str_to.equalsIgnoreCase("")) {
                        Toast.makeText(context, "Please enter your mail address", Toast.LENGTH_LONG).show();
                    } else if (str_subject.equalsIgnoreCase("")) {
                        Toast.makeText(context, "Please enter your Subject", Toast.LENGTH_LONG).show();
                    } else if (!isValidEmail(To_text.getText().toString().trim())) {
                        Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    } else if (str_message.equalsIgnoreCase("")) {
                        Toast.makeText(context, "Please enter your Message", Toast.LENGTH_LONG).show();
                    } else {
                        sharePbcr(user_id, str_to, str_subject, str_message, cb_id);
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
}

