package com.valohyd.nextseries.models;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Gallery;

import com.valohyd.nextseries.adapters.IndicatorSeasonsAdapter;

/**
 * Widget changant en mm temps que le ViewPager, affiche le titre au dessus
 * 
 * @author Valohyd Team
 * 
 */
public class ViewPagerSeasonIndicator extends Gallery {
    /**
     * les titres des pages (donne aussi le nb de pages)
     */
    private String[] titles;

    private final Context ctxt;

    /**
     * default
     * @param context
     */
    public ViewPagerSeasonIndicator(Context context) {
        super(context);

        ctxt = context;
    }

    /**
     * init widget direct après la création
     * @param context
     * @param titres
     */
    public ViewPagerSeasonIndicator(Context context, String[] titres) {
        super(context);

        ctxt = context;
        titles = titres;
        init();
    }
    
    public ViewPagerSeasonIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctxt = context;
    }

    public ViewPagerSeasonIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctxt = context;
    }

    /**
     * permet de "setter" les titres et lance l'init du widget
     * @param titles
     */
    public void setTitles(String[] titles) {
        this.titles = titles;
        init();
    }

    /**
     * mets les textView correspondantes aux titres
     */
    private void init() {
        this.setAdapter(new IndicatorSeasonsAdapter(ctxt, titles));
    }

}
