package com.valohyd.nextseries.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.valohyd.nextseries.R;

public class IndicatorSeasonsAdapter extends BaseAdapter {

	String[] titres;
	LayoutInflater inflater;

	public IndicatorSeasonsAdapter(Context context, String[] list) {
		inflater = LayoutInflater.from(context);
		this.titres = list;
	}

	public int getCount() {
		return titres.length;
	}

	public Object getItem(int itemPos) {
		return titres[itemPos];
	}

	public long getItemId(int pos) {
		return pos;
	}

	private class ViewHolder {
		TextView titre;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.indicator_renderer, null);
			holder.titre = (TextView) convertView.findViewById(R.id.titreIndicator);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.titre.setText(titres[position]);
		return convertView;
	}

}
