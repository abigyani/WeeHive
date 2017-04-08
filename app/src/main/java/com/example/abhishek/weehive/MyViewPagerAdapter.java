package com.example.abhishek.weehive;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Abhishek on 18-Dec-16.
 */
public class MyViewPagerAdapter extends PagerAdapter{

    LayoutInflater layoutInflater;
    Context mContext;
    int[] layouts = new int[]{
        R.layout.slide1,
                R.layout.slide2,
                R.layout.slide3,
                R.layout.slide4
    };


    public MyViewPagerAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layouts[position],container,false);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
