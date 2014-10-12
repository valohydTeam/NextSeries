package com.valohyd.nextseries.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class TotalSeriesViewPagerAdapter extends PagerAdapter {
    //private static String[] titles = new String[] { "Page 1", "Page 2", "Page 3" };
    private final Context context;
    private View[] vues;

    public TotalSeriesViewPagerAdapter(Context context, View[] vs) {
        this.context = context;
        vues = vs;
    }

   /* public String getTitle(int position) {
        return titles[position];
    }
*/
    @Override
    public int getCount() {
        return vues.length;
    }

    /*
     * @Override public Object instantiateItem(View pager, int position) {
     * TextView v = new TextView(context); v.setText(titles[position]);
     * ((ViewPager) pager).addView(v, 0); return v; }
     */

    @Override
    public Object instantiateItem(View pager, int position) {
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ((ViewPager) pager).addView(vues[position], 0);
        return vues[position];
    }

    /*
     * @Override public void destroyItem(View pager, int position, Object view)
     * { ((ViewPager) pager).removeView((TextView) view); }
     */
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