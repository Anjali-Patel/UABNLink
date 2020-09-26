package com.uabn.gss.uabnlink.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.DocumentResourceDetailActivity;
import com.uabn.gss.uabnlink.Activities.VideoWebViewIframe;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.RefreshChat;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.Utility.ZoomImageView;
import com.uabn.gss.uabnlink.model.ChatMessageModel;

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

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.MyViewHolder> {
    Context context;
    Context actcontext;
    String UserId;
    private ArrayList<ChatMessageModel> MessagesArrayList;
    private SharedPreferenceUtils preferances;
    RefreshChat chatrefresh;


    MediaPlayer mediaplayer;
    private boolean audioIsPlaying = false;
    private Handler mHandler = new Handler();




    public ChatMessagesAdapter(Context context, ArrayList<ChatMessageModel> MessagesModelArrayList, RefreshChat chatrefresh, RecyclerView recyclerView) {
        this.context = context;
        MessagesArrayList = MessagesModelArrayList;
        preferances = SharedPreferenceUtils.getInstance(context);
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");

        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        this.chatrefresh = chatrefresh;




    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


            final ChatMessageModel Items = MessagesArrayList.get(position);


            holder.MessageDate.setText(Items.getMessageDate());

            if (Items.getFromUserId().equalsIgnoreCase(UserId)) {
                holder.ChatLinear.setGravity(Gravity.END);

//            LinearLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.setMargins(100, 5, 5, 15);
                holder.ChatLinear.setLayoutParams(params);
                holder.ChatLinear.setBackground(context.getResources().getDrawable(R.drawable.white));
                holder.MessageText.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.MessageDate.setTextColor(context.getResources().getColor(R.color.grey_text));


                holder.ChatLinear.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {



                        Context wrapper = new ContextThemeWrapper(context, R.style.popup);
                        PopupMenu popup = new PopupMenu(wrapper, view);
                        popup.getMenuInflater().inflate(R.menu.delete_chat, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.delete:

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.create();
                                        builder.setMessage("Are you sure you want to delete this message?");

                                        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });
                                        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DeleteChat(UserId, Items.getChatId());
                                            }
                                        });


                                        builder.show();

                                        return true;


                                    default:
                                        return false;
                                }

                            }
                        });

                        popup.show();



                        return true;
                    }
                });

            } else {
                holder.ChatLinear.setGravity(Gravity.START);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.setMargins(5, 5, 100, 15);
                holder.ChatLinear.setLayoutParams(params);
                holder.ChatLinear.setBackground(context.getResources().getDrawable(R.drawable.green));
                holder.MessageText.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                holder.MessageDate.setTextColor(context.getResources().getColor(R.color.grey));

                holder.ChatLinear.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });


            }

            if (Items.getMessage() != null && !Items.getMessage().equalsIgnoreCase("")){
                holder.MessageText.setVisibility(View.VISIBLE);
                holder.MessageText.setText(Items.getMessage());
            }
            else{
                holder.MessageText.setVisibility(View.GONE);
            }



            holder.MessageImage1.setVisibility(View.GONE);
            holder.MessageImage2.setVisibility(View.GONE);
            holder.MessageImage3.setVisibility(View.GONE);
            holder.MessageImage4.setVisibility(View.GONE);
            holder.MessageImage5.setVisibility(View.GONE);

            if (Items.getAttatchment1() != null){
                holder.MessageImage1.setVisibility(View.VISIBLE);


                Glide.with(context).load(Items.getAttatchment1()).thumbnail(0.1f).into(holder.MessageImage1);
                //Picasso.with(context).load(Items.getAttatchment1()).resize(20,20).placeholder(R.drawable.uabnsplash).error(R.drawable.noimage).into(holder.MessageImage1);

                holder.MessageImage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
                        dialog.setContentView(R.layout.zoom_profile_pic);
                        ZoomImageView img = dialog.findViewById(R.id.img);
                        final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                        ImageProgress.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .error(R.drawable.noimage)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .priority(Priority.HIGH);
                        Picasso.with(context).load(Items.getAttatchment1())
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(img, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        ImageProgress.setVisibility(View.GONE);

                                    }
                                    @Override
                                    public void onError() {
                                    }
                                });
                        dialog.show();
                    }
                });
//            Glide.with(context)
//                    .load("http://via.placeholder.com/300.png")
//                    .override(300, 200)
//                    .into(ivImg);
            }else{
                holder.MessageImage1.setVisibility(View.GONE);
            }

            if (Items.getAttatchment2() != null){
                holder.MessageImage2.setVisibility(View.VISIBLE);
                Glide.with(context).load(Items.getAttatchment2()).thumbnail(0.1f).into(holder.MessageImage2);

                holder.MessageImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
                        dialog.setContentView(R.layout.zoom_profile_pic);
                        ZoomImageView img = dialog.findViewById(R.id.img);
                        final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                        ImageProgress.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(Items.getAttatchment2())
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(img, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        ImageProgress.setVisibility(View.GONE);

                                    }
                                    @Override
                                    public void onError() {
                                    }
                                });
                        dialog.show();
                    }
                });

            }else{
                holder.MessageImage2.setVisibility(View.GONE);
            }

            if (Items.getAttatchment3() != null){
                holder.MessageImage3.setVisibility(View.VISIBLE);
                Glide.with(context).load(Items.getAttatchment3()).thumbnail(0.1f).into(holder.MessageImage3);
                holder.MessageImage3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
                        dialog.setContentView(R.layout.zoom_profile_pic);
                        ZoomImageView img = dialog.findViewById(R.id.img);
                        final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                        ImageProgress.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(Items.getAttatchment3())
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(img, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        ImageProgress.setVisibility(View.GONE);

                                    }
                                    @Override
                                    public void onError() {
                                    }
                                });
                        dialog.show();
                    }
                });
            }else{
                holder.MessageImage3.setVisibility(View.GONE);
            }


            if (Items.getAttatchment4() != null){
                holder.MessageImage4.setVisibility(View.VISIBLE);
                Glide.with(context).load(Items.getAttatchment4()).thumbnail(0.1f).into(holder.MessageImage4);
                holder.MessageImage4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
                        dialog.setContentView(R.layout.zoom_profile_pic);
                        ZoomImageView img = dialog.findViewById(R.id.img);
                        final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                        ImageProgress.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(Items.getAttatchment4())
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(img, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        ImageProgress.setVisibility(View.GONE);

                                    }
                                    @Override
                                    public void onError() {
                                    }
                                });
                        dialog.show();
                    }
                });

            }else{
                holder.MessageImage4.setVisibility(View.GONE);
            }


            if (Items.getAttatchment5() != null){
                holder.MessageImage5.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Items.getAttatchment5())
                        .thumbnail(0.1f)
                        .into(holder.MessageImage5);

                holder.MessageImage5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context, R.style.DialogAnimation_2);
                        dialog.setContentView(R.layout.zoom_profile_pic);
                        ZoomImageView img = dialog.findViewById(R.id.img);
                        final ProgressBar ImageProgress = dialog.findViewById(R.id.ImageProgress);
                        ImageProgress.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(Items.getAttatchment5())
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(img, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        ImageProgress.setVisibility(View.GONE);

                                    }
                                    @Override
                                    public void onError() {
                                    }
                                });
                        dialog.show();
                    }
                });
            }else{
                holder.MessageImage5.setVisibility(View.GONE);
            }


            if (Items.getChatAudio() != null){


                holder.Pause.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        holder.Play.setVisibility(View.VISIBLE);
                        holder.Pause.setVisibility(View.GONE);

                        mediaplayer.stop();
                    }
                });

                holder.Pause.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        holder.Play.setVisibility(View.VISIBLE);
                        holder.Pause.setVisibility(View.GONE);
                        mediaplayer.pause();
                        audioIsPlaying = false;
                    }
                });

                holder.Play.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        holder.Play.setVisibility(View.GONE);
                        holder.Pause.setVisibility(View.VISIBLE);
                        mediaplayer.start();
                    }
                });

                holder.MessageAudio.setVisibility(View.VISIBLE);

                holder.MessageAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String URL = Items.getChatAudio();
                        try {

                            if (holder.MusicPlayer.getVisibility() == View.VISIBLE){
                                mediaplayer.stop();
                                holder.MusicPlayer.setVisibility(View.GONE);

                            }
                            else{
                                holder.MusicPlayer.setVisibility(View.VISIBLE);

                                mediaplayer.setDataSource(URL);
                                mediaplayer.prepare();
                                int totalTime = mediaplayer.getDuration();
                                holder.playAudioProgress.setMax(totalTime/1000);

                            }



                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mediaplayer.start();
                        audioIsPlaying = true;

                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(mediaplayer != null){
                                    int mCurrentPosition = mediaplayer.getCurrentPosition() / 1000;
                                    holder.playAudioProgress.setProgress(mCurrentPosition);
                                }
                                mHandler.postDelayed(this, 1000);
                            }
                        });

                    }
                });

                // holder.MessageAudio.setText(Items.getChatAudio());
            }
            else{
                holder.MusicPlayer.setVisibility(View.GONE);
                holder.MessageAudio.setVisibility(View.GONE);
            }


            if (Items.getChatDoc() != null){
                holder.MessageDoc.setVisibility(View.VISIBLE);

                holder.MessageDoc.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Items.getChatDoc()));
                            context.startActivity(myIntent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else{
                holder.MessageDoc.setVisibility(View.GONE);
            }

            if (Items.getChatVideo() != null){
                holder.videoRelative.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .asBitmap()
                        .load(Items.getChatVideo())
                        .into(holder.VideoImage);

                holder.VideoImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, VideoWebViewIframe.class);
                        i.putExtra("url", Items.getChatVideo());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                });
            }
            else{
                holder.videoRelative.setVisibility(View.GONE);
            }


    }





    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position) {
        View layout = LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.chat_message_layout, viewgroup, false);

        MyViewHolder holder = new MyViewHolder(layout);

        return holder;

    }

    @Override
    public int getItemCount() {

        return MessagesArrayList.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView MessageText, MessageDate, MessageAudio, MessageDoc;
        ImageView MessageImage1, MessageImage2, MessageImage3, MessageImage4, MessageImage5, Play, Pause, VideoImage;
        LinearLayout ChatLinear, ImageBox1, ImageBox2, MusicPlayer;
        RelativeLayout WallRelative, videoRelative;
        SeekBar playAudioProgress;



        public MyViewHolder(View view) {
            super(view);

            MessageText = view.findViewById(R.id.MessageText);
            MessageAudio = view.findViewById(R.id.MessageAudio);
            MessageDoc = view.findViewById(R.id.MessageDoc);
            MessageImage1 = view.findViewById(R.id.MessageImage1);
            MessageImage2 = view.findViewById(R.id.MessageImage2);
            MessageImage3 = view.findViewById(R.id.MessageImage3);
            MessageImage4 = view.findViewById(R.id.MessageImage4);
            MessageImage5 = view.findViewById(R.id.MessageImage5);
            VideoImage = view.findViewById(R.id.VideoImage);
            MessageDate = view.findViewById(R.id.MessageDate);
            ChatLinear = view.findViewById(R.id.ChatLinear);
            ImageBox1 = view.findViewById(R.id.ImageBox1);
            ImageBox2 = view.findViewById(R.id.ImageBox2);
            WallRelative = view.findViewById(R.id.WallRelative);
            videoRelative = view.findViewById(R.id.videoRelative);

            MusicPlayer = view.findViewById(R.id.MusicPlayer);
            Play = view.findViewById(R.id.playaudio);
            Pause = view.findViewById(R.id.pauseaudio);
            playAudioProgress = view.findViewById(R.id.play_audio_progressbar);


        }


    }


    public void DeleteChat(String UserId, String ChatId) {

        String url = (CommonUtils.BASE_URL) + "delete_chat";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("chat_id", ChatId)
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

                                Toast.makeText(context, "Chat deleted successfully!", Toast.LENGTH_LONG).show();
                                chatrefresh.ChatRefresh();
                            }

                            else {

                                Toast.makeText(context, "Opps! Some problem occured while deleting message, please try again after some time.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}


