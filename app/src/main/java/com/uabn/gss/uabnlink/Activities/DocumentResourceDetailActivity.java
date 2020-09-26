package com.uabn.gss.uabnlink.Activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.uabn.gss.uabnlink.Utility.ExpandableTextView.makeTextViewResizable;


public class DocumentResourceDetailActivity extends AppCompatActivity {
    static ShareDocumentDialog shareDocumentDialog;
    ImageView DocsImage;
    String web;
    LinearLayout linear_layout;
    FrameLayout progressBarHolder;
    //    static InviteDocumentDialog inviteDocumentDialog;
    TextView posted, detail, click_here, share1;
    SharedPreferenceUtils preferances;
    String docId, user_id, imgae_url, str_media;
    CardView MainCard;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_resource_detail);
        DocsImage = findViewById(R.id.DocsImage);
        posted = findViewById(R.id.posted);
        detail = findViewById(R.id.detail);
        linear_layout=findViewById(R.id.linear_layout);
        click_here = findViewById(R.id.click_here);
        progressBarHolder = findViewById(R.id.progressBarHolder);
        MainCard = findViewById(R.id.MainCard);
        share1 = findViewById(R.id.share);
        preferances = SharedPreferenceUtils.getInstance(this);
        user_id = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        docId = getIntent().getStringExtra("docId");

        getDocumentlist();
        share1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDocumentDialog = new ShareDocumentDialog();
                shareDocumentDialog.setContext(DocumentResourceDetailActivity.this);
                shareDocumentDialog.setGroupId(docId);
                shareDocumentDialog.setGroupUserId(user_id);

                shareDocumentDialog.show(DocumentResourceDetailActivity.this.getFragmentManager(), ShareDocumentDialog.class.getSimpleName());

            }
        });

//        invite_friends.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                inviteDocumentDialog = new InviteDocumentDialog();
//                inviteDocumentDialog.setContext(DocumentResourceDetailActivity.this);
//                inviteDocumentDialog.setDocument_id(doc_id);
//                inviteDocumentDialog.setDocument_userid(user_id);
//
//                inviteDocumentDialog.show(DocumentResourceDetailActivity.this.getFragmentManager(), ShareDocumentDialog.class.getSimpleName());
//
//            }
//        });
        DocsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.noimage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                final Dialog dialog = new Dialog(DocumentResourceDetailActivity.this, R.style.DialogAnimation_2);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.setContentView(R.layout.zoom_profile_pic);
                ZoomImageView img = dialog.findViewById(R.id.img);
                final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                ImageProgress.setVisibility(View.VISIBLE);

                 Glide.with(DocumentResourceDetailActivity.this).load(imgae_url).apply(options).into(img);
                ImageProgress.setVisibility(View.GONE);
//
//                Picasso.with(DocumentResourceDetailActivity.this).load(imgae_url)
//                        .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
//                        .into(img, new com.squareup.picasso.Callback() {
//                            @Override
//                            public void onSuccess() {
//                                //do something when picture is loaded successfully
//                                ImageProgress.setVisibility(View.GONE);
//
//                            }
//
//                            @Override
//                            public void onError() {
//                                //do something when there is picture loading error
//                            }
//                        });

                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
//                dialog.show();
//                Picasso.with(DocumentResourceDetailActivity.this).load(imgae_url).error(R.drawable.noimage).into(img);

                /*(View) Glide.with(context).load("http://demo1.geniesoftsystem.com/newweb/community/uploads/events/" + person.get(position).getImage()).into(holder.image)*/
                dialog.show();
            }
        });
    }

    public void getDocumentlist() {
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
//        progressBarHolder.setVisibility(View.GONE);
        progressBarHolder.setVisibility(View.VISIBLE);
        //ProgessLoad.setVisibility(View.VISIBLE);
        String url = (CommonUtils.BASE_URL) + "view_resource";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("resource_id", docId)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            String baseimgae_url = json.getString("imgae_url");

                            if (success.equalsIgnoreCase("success")) {
                                JSONArray update = json.getJSONArray("knowledgebase_data");
                                for (int i = 0; i < update.length(); i++) {
                                    JSONObject Details = update.getJSONObject(i);
                                    JSONObject UpdateDetails = Details.getJSONObject("Knowledgebase");
                                    str_media = UpdateDetails.getString("media_type").replace("null", "");
                                    Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_date"));
                                    String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                    posted.setText(CommentPostDate);
                                    imgae_url = baseimgae_url+UpdateDetails.getString("id") + "/" + UpdateDetails.getString("media_name");
                                    Glide.with(DocumentResourceDetailActivity.this).load(imgae_url).apply(options).into(DocsImage);
                                    detail.setText(UpdateDetails.getString("description").replace("null", ""));

                                    if(UpdateDetails.getString("description").length() > 100){
                                        makeTextViewResizable(detail, 5, "View More", true);
                                    }
                                     web=UpdateDetails.getString("website");
                                    if(!web.equalsIgnoreCase("")){
                                        linear_layout.setVisibility(View.VISIBLE);
//                                        click_here.setText(web.replace("null", ""));
                                        click_here.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(web));
                                                    startActivity(myIntent);
                                                } catch (ActivityNotFoundException e) {
                                                    Toast.makeText(DocumentResourceDetailActivity.this, "No application can handle this request." + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }else{
                                        linear_layout.setVisibility(View.GONE);
                                    }
                                }

                                if (str_media.equalsIgnoreCase("1")) {
                                    Glide.with(DocumentResourceDetailActivity.this).load(imgae_url).into(DocsImage);

                                    DocsImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String path = (imgae_url);
                                            Intent i = new Intent(DocumentResourceDetailActivity.this, ViewPDF.class);
                                            i.putExtra("doc_path", path);
                                            startActivity(i);
                                        }
                                    });

                                } else {
                                    Glide.with(DocumentResourceDetailActivity.this).load(imgae_url).apply(options).into(DocsImage);

                                }

                                MainCard.setVisibility(View.VISIBLE);
                                progressBarHolder.setVisibility(View.GONE);
                            } else {
                                progressBarHolder.setVisibility(View.GONE);

                                Toast.makeText(DocumentResourceDetailActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static class ShareDocumentDialog extends DialogFragment {
        Context context;
        String GroupUserId, GroupId;

        ProgressBar progess_load;

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public String getGroupUserId() {
            return GroupUserId;
        }

        public void setGroupUserId(String user) {
            this.GroupUserId = user;
        }

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String group) {
            this.GroupId = group;
        }

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
            Button submit = (Button) dialogView.findViewById(R.id.submit);
            final EditText To_text, subject_text, message_text;
            To_text = dialogView.findViewById(R.id.To_text);
            subject_text = dialogView.findViewById(R.id.subject_text);
            message_text = dialogView.findViewById(R.id.message_text);
            progess_load = dialogView.findViewById(R.id.progess_load);

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
                        ShareDocument(getGroupUserId(), str_to, str_subject, str_message, getGroupId());
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

        public void ShareDocument(String UserId, String str_to, String str_subject, String str_message, String resource_id) {
            String url = (CommonUtils.BASE_URL) + "share_resources";
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", UserId)
                    .add("to_email", str_to)
                    .add("subject", str_subject)
                    .add("message", str_message)
                    .add("resource_id", resource_id)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progess_load.setVisibility(View.GONE);

                            JSONObject json = null;
                            try {
                                json = new JSONObject(myResponse);
                                String success = json.getString("status");
                                if (success.equalsIgnoreCase("success")) {
                                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                    shareDocumentDialog.dismiss();

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

}


