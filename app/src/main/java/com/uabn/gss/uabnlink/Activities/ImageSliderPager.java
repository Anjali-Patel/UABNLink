package com.uabn.gss.uabnlink.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.model.Parcabe_Imagelist;
import com.uabn.gss.uabnlink.model.PostDetailImageModel;

import java.util.ArrayList;

public class ImageSliderPager extends AppCompatActivity {

    ViewPager pager;
    ImagePagerAdapter adapter;
    ArrayList<Parcabe_Imagelist> imageList = new ArrayList<Parcabe_Imagelist>();
    int position_pager;
    String ImageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider_pager);

        position_pager = getIntent().getIntExtra("position",0);
        ImageSelected = (getIntent().getStringExtra("ImageSelected"));
        imageList = getIntent().getExtras().getParcelableArrayList("image_list");


        pager = (ViewPager) findViewById(R.id.pager);


        adapter = new ImagePagerAdapter(ImageSliderPager.this, imageList);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position_pager);


    }


    public class ImagePagerAdapter extends android.support.v4.view.PagerAdapter {
        ArrayList<Parcabe_Imagelist> ImageArrayList;

        RelativeLayout profileselect;
        Context context;
        LayoutInflater inflater;
        View item;
        BitmapDrawable drawable;

        ImagePagerAdapter(Context context, ArrayList<Parcabe_Imagelist> ImageArray) {
            this.context = context;
            this.ImageArrayList = ImageArray;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @NonNull
        public Object instantiateItem(ViewGroup container, final int position) {
            item = inflater.inflate(R.layout.zoom_profile_pic, container, false);
            container.addView(item);
            ImageView adapterImageview;

            adapterImageview = (ImageView) item.findViewById(R.id.img);


            String image = ImageArrayList.get(position).getImage();



            if (image == null || image.isEmpty() || image.equalsIgnoreCase("null") || image.equalsIgnoreCase(" ") ) {
                adapterImageview.setImageResource(R.drawable.noimage);
            } else {
                try {
                    String loaderimage = ImageArrayList.get(position).getImage();

                    Glide.with(ImageSliderPager.this).load(loaderimage).into(adapterImageview);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return item;
        }

        @Override
        public int getCount() {
            return ImageArrayList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int x = item.getItemId();

        switch (item.getItemId()) {

            case R.id.homeAsUp:
                this.onBackPressed();
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
