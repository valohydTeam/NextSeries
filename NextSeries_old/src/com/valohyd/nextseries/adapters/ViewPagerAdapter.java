package com.valohyd.nextseries.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {

	private View[] views;

	public ViewPagerAdapter(Context context, View[] viewTab) {
		views = viewTab;
	}

	@Override
	public int getCount() {
		return views.length;
	}

	@Override
	public Object instantiateItem(View pager, int position) {
	    if(pager != null && views != null && views[position] != null){
	        ((ViewPager) pager).addView(views[position],0);
	        return views[position];
	    }
	    return null;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable p, ClassLoader c) {
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View)object);
	}

}