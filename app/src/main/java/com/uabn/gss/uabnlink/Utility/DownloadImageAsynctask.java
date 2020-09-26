package com.uabn.gss.uabnlink.Utility;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadImageAsynctask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageAsynctask(ImageView bmImage) {
        this.bmImage = (ImageView ) bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        Bitmap myBitmap = null;
        MediaMetadataRetriever mMRetriever = null;
        try {
            mMRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mMRetriever.setDataSource(urls[0], new HashMap<String, String>());
            else
                mMRetriever.setDataSource(urls[0]);
            myBitmap = mMRetriever.getFrameAtTime(-1);

            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);*/
        } catch (Exception e) {
            e.printStackTrace();


        } finally {
            if (mMRetriever != null) {
                mMRetriever.release();
            }
        }
        return myBitmap;
    }


    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}



