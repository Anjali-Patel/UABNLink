package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Target;
import com.uabn.gss.uabnlink.Activities.EventDetails;
import com.uabn.gss.uabnlink.Activities.EventGuestList;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.EventEdit;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.UpdateEventList;
import com.uabn.gss.uabnlink.model.EventsModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
    String title,website_url,image_path;
    Context context;
    Context actcontext;
    private ArrayList<EventsModel> EventsrrayList;
    private SharedPreferenceUtils preferences;
    UpdateEventList eventupdate;
    EventEdit eventedit;

    public EventAdapter(Context context, ArrayList<EventsModel> EventsModelArrayList, UpdateEventList eventupdate, EventEdit eventedit)
    {
        this.context=context;
        EventsrrayList = EventsModelArrayList;
        preferences = SharedPreferenceUtils.getInstance(context);
        this.eventupdate = eventupdate;
        this.eventedit = eventedit;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Title,type,start_date,location,website, date, Interested, Guest;
        LinearLayout editEventLinear;
        ImageView EventImage, edit, delete,share;
        RelativeLayout EventRelative;

        public MyViewHolder(View view) {
            super(view);
            Title= view.findViewById(R.id.Title);
            type = view.findViewById(R.id.event_type);
            start_date = view.findViewById(R.id.start_date);
            location = view.findViewById(R.id.location);
            share= view.findViewById(R.id.share);
//            date= view.findViewById(R.id.date);
//            Interested= view.findViewById(R.id.Interested);
            editEventLinear = view.findViewById(R.id.editEventLinear);
            EventImage = view.findViewById(R.id.EventImage);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
//            EventRelative = view.findViewById(R.id.EventRelative);
//            Guest = view.findViewById(R.id.Guest);
        }
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EventsModel Items = EventsrrayList.get(position);

        if (Items.getEventOccurance().equalsIgnoreCase("MyEvents")){
            holder.editEventLinear.setVisibility(View.VISIBLE);
        }
        else if (Items.getEventOccurance().equalsIgnoreCase("PastEvents")){
            holder.editEventLinear.setVisibility(View.GONE);
//            holder.Interested.setVisibility(View.INVISIBLE);
        }
        else if (Items.getEventOccurance().equalsIgnoreCase("UpcomingEvents")){
            holder.editEventLinear.setVisibility(View.GONE);
        }
        if (Items.getEventGuestList().equalsIgnoreCase("1")){
//            holder.Guest.setVisibility(View.VISIBLE);
        }
        else if (Items.getEventGuestList().equalsIgnoreCase("0")){
//            holder.Guest.setVisibility(View.INVISIBLE);
        }
        holder.Title.setText(Items.getTitle());
        holder.type.setText(Items.getType());
        holder.location.setText(Items.getWhere());
        holder.start_date.setText(Items.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,EventDetails.class);
                i.putExtra("EventId", Items.getEvent_id());
                i.putExtra("UserId", preferences.getStringValue(CommonUtils.MEMBER_ID,""));
                context.startActivity(i);
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 image_path = Items.getEventImage();
                 website_url=Items.getWebsite();
                 title=Items.getTitle();
             /*   Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),Integer.parseInt(image_path));
//                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Share.png";
                OutputStream out = null;
                File file=new File(image_path);
                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                image_path=file.getPath();
                Uri bmpUri = Uri.parse(image_path);
             Intent    shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, website_url);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                // shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +getPackageName());
                shareIntent.setType("image/png");
                context.startActivity(Intent.createChooser(shareIntent,"Share with"));
*/



                URL url = null;
                try {
                    url = new URL(image_path);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                final File f = new File(url.getFile());
                Uri bmpUri = Uri.parse("file://"+f.getPath());
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("image/png");
//
//                intent.putExtra(Intent.EXTRA_STREAM, imageUri);

//                context.startActivity(Intent.createChooser(intent , "Share"));
             /*   Uri apkURI = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", f);
                Intent install = new Intent(Intent.ACTION_SEND);
                install.setDataAndType(apkURI, mimeType);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
// End New Approach
                context.startActivity(install);*/
             Intent i = new Intent(Intent.ACTION_SEND);
             i.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        i.putExtra(Intent.EXTRA_TEXT, website_url);
                        i.putExtra(Intent.EXTRA_SUBJECT, title);
                        i.setType("image/*");
                        i.setType("text/plain");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(Intent.createChooser(i, "Share Image"));
            }
        });
//        if(!Items.getWebsite().equalsIgnoreCase("")){
//            holder.website.setVisibility(View.VISIBLE);
////        holder.website.setText(Items.getWebsite());
//            holder.website.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Items.getWebsite()));
//                        context.startActivity(myIntent);
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }  else {
//            holder.website.setVisibility(View.GONE);
//        }

//        holder.date.setText(Items.getDate());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        if (!Items.getEventImage().equalsIgnoreCase("")){
            Glide.with(context).asBitmap().load(Items.getEventImage()).apply(options).into(holder.EventImage);
        }





//        holder.Interested.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//
//                alertDialog.setTitle("Set Interest");
//
//                alertDialog.setMessage("Set your interest for this event!");
//
//                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Attending", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        SetInterest(Items.getEvent_id(),"1");
//
//                    } });
//
//                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Maybe Attending", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        SetInterest(Items.getEvent_id(),"2");
//
//                    }});
//
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Not Attending", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        SetInterest(Items.getEvent_id(),"3");
//
//                    }});
//                alertDialog.show();
//
//
//            }
//        });

//        holder.EventRelative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context,EventDetails.class);
//                i.putExtra("EventId", Items.getEvent_id());
//                i.putExtra("UserId", preferences.getStringValue(CommonUtils.MEMBER_ID,""));
//                context.startActivity(i);
//
//            }
//        });
//
//        holder.Guest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context,EventGuestList.class);
//                i.putExtra("EventId", Items.getEvent_id());
//                context.startActivity(i);
//
//            }
//        });
//
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventedit.EditEvents("Edit Event", Items.getEvent_id());

            }
        });
//
//
//
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                alertDialog.setTitle("Delete Event");

                alertDialog.setMessage("Are you sure you want to delete this event?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        DeletePost(Items.getEvent_id());

                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alertDialog.dismiss();

                    }});

                alertDialog.show();

            }
        });




//        holder.PostLinear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context,PostDetails.class);
//                i.putExtra("Id", Items.getPostUserId());
//                i.putExtra("PostId", Items.getPostId());
//
//
//
//                context.startActivity(i);
//            }
//        });



        //holder.CommentCount.setText(Items.getCommentCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.event_list_layout,viewgroup,false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount()
    {

        return EventsrrayList.size();
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


    public void SetInterest(String EventId, String Status) {

        String url = (CommonUtils.BASE_URL)+"is_interested_in_event";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", preferences.getStringValue(CommonUtils.MEMBER_ID,""))
                .add("event_id", EventId)
                .add("event_intrested_status", Status)

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
                                eventupdate.UpdateEvents();

                            }

                            else if (success.equalsIgnoreCase("failed")){

                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                eventupdate.UpdateEvents();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });




    }



    public void DeletePost(String EventId) {

        String url = (CommonUtils.BASE_URL)+"delete_event";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", preferences.getStringValue(CommonUtils.MEMBER_ID,""))
                .add("event_id", EventId)
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
                                eventupdate.UpdateEvents();

                            }

                            else if (success.equalsIgnoreCase("failed")){
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
    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //////// this method share your image
    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    }

