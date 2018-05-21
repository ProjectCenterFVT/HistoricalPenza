package com.projectcenterfvt.historicalpenza.Server;

import android.os.AsyncTask;

/**
 * Created by Dmitry on 01.04.2018.
 */

public abstract class BaseAsyncTask<P, R> extends AsyncTask<P, Void, R> {

    public static final String UML_ADDR = "http://hpenza.creativityprojectcenter.ru/img/";
    protected static final String SERVER_ADDR = "http://hpenza.creativityprojectcenter.ru/api.request.php";
    protected Exception mException;
    OnResponseListener listener;

    @Override
    abstract protected R doInBackground(P... from);

    public void setOnResponseListener(OnResponseListener listener) {
        this.listener = listener;
    }

    protected void onPostExecute(R result) {
        if (listener != null) {
            if (mException == null) {
                listener.onSuccess(result);
            } else {
                listener.onFailure(mException);
            }
        }
    }

    public interface OnResponseListener<R> {
        void onSuccess(R result);

        void onFailure(Exception e);
    }
}
