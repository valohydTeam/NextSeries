package com.valohyd.nextseries.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class ViewPagerSerieAdapter extends PagerAdapter {
	private static String[] titles = new String[] { "Page 1", "Page 2" };

	private View v1;
	private View v2;
	
	public ViewPagerSerieAdapter(Context context, View vu, View vd) {
		v1 = vu;
		v2 = vd;
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object instantiateItem(View pager, int position) {
		switch (position) {

		case 0:
		    if(v1.getParent() == null)
		        ((ViewPager) pager).addView(v1, 0);
		    return v1;

		case 1:
		    if(v2.getParent() == null)
		        ((ViewPager) pager).addView(v2, 0);
		    return v2;  

		}
		return null;
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
	public void destroyItem(View container, int position, Object object) {
		// ViewPager
		ViewPager viewPager = (ViewPager) container;
		// View
		View view = (View) object;

		// View löschen
		viewPager.removeView(view);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View view) {
		Log.d("UPDATE","NOTIFY");
	}

}