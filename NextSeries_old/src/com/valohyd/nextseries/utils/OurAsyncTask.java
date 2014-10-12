package com.valohyd.nextseries.utils;

import java.util.ArrayList;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class OurAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private ArrayList<AsyncTask<?, ?, ?>> taches = new ArrayList<AsyncTask<?,?,?>>();
    
    public OurAsyncTask(){
    }
    
    public OurAsyncTask(ArrayList<AsyncTask<?, ?, ?>> tach){
        taches = tach;
    }
    
    @Override
    protected void onPreExecute(){
        Log.d("NextSeries","ajout de la tache");
        taches.add(this);
    }
    
    @Override
    protected void onPostExecute(Result res) {
        Log.d("NextSeries","sup de la tache");
        taches.remove(this);
    }

    @Override
    protected Result doInBackground(Params... params) {
        return null;
    }
    
    public AsyncTask<Params, Progress, Result> ourExecute(Params... params){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // lancer les tâches en parallèle
            return executeOnExecutor(THREAD_POOL_EXECUTOR, params);
        } else {
            // lancer les tâches en faux parallèle
            return execute(params);
        }
    }
}
