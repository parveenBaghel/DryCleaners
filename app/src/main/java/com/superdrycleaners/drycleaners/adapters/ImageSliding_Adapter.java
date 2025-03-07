package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.beans.Banner;

import java.util.List;

public class ImageSliding_Adapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Banner> sliderImg;

    public ImageSliding_Adapter(List sliderImg, Context context) {
        this.sliderImg = sliderImg;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slidingimage_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        String BANNER_URL = ConfigClass.GET_IMAGE_URL;
        Banner banners = sliderImg.get(position);
        Picasso.with(context).load(BANNER_URL + banners.getName()).into(imageView);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}


