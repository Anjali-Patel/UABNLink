package com.uabn.gss.uabnlink.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.uabn.gss.uabnlink.Activities.CertifiedBusinessDetailActivity;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.model.CertifiedBannerModel;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {

    private ArrayList<CertifiedBannerModel> images;
    private LayoutInflater inflater;
    private Context context;

    public BannerAdapter(Context context, ArrayList<CertifiedBannerModel> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = (View) inflater.inflate(R.layout.banner_image_layout ,view, false);
        CertifiedBannerModel Items=images.get(position);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.banner_photo);
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.detailbannerbg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
//        myImage.setImageResource(images.get(position).getBanner_image());
        if(!Items.getBanner_image().equalsIgnoreCase("null")&&!Items.getBanner_image().equalsIgnoreCase("")){
            String img_url=Items.getBanner_image();
            Glide.with(context).asBitmap().load(img_url).into(myImage);


        }else{
            Glide.with(context).asBitmap().load(R.drawable.detailbannerbg).into(myImage);

        }


//        TextView tvDesc=(TextView)myImageLayout.findViewById(R.id.tvDesc);
//        if(position==0){
////            tvDesc.setText("Protect Your Child");
//        }
//        else if(position==1){
////            tvDesc.setText("Safe Your Parents");
//        }else if(position==2){
////            tvDesc.setText("Track Your Location");
//        }else if(position==3){
////            tvDesc.setText("Connect To Family");
//        }
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}

