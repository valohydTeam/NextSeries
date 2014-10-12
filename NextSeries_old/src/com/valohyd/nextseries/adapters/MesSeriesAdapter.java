package com.valohyd.nextseries.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class MesSeriesAdapter extends PagerAdapter {

    View[] vues;
    LayoutInflater inflater;

    public MesSeriesAdapter(Context context, View[] vu) {
        inflater = LayoutInflater.from(context);
        vues = vu;
    }

    @Override
    public int getCount() {
        return vues.length;
    }

    @Override
    public Object instantiateItem(View pager, int position) {
        Log.d("NextSeries", "instantiateItem n°"+position+"/"+vues.length);
        //Log.d("NextSeries", "vue voulue : "+vues[position].toString());
        ((ViewPager) pager).addView(vues[position]);
        return vues[position];
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        // ViewPager
        ViewPager viewPager = (ViewPager) container;
        // View
        View view = (View) object;

        // View löschen
        viewPager.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void finishUpdate(View view) {
    }

    @Override
    public void restoreState(Parcelable p, ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {
    }
}
