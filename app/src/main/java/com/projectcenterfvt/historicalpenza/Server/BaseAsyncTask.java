package com.projectcenterfvt.historicalpenza.Server;

import android.os.AsyncTask;

/**
 * Created by Dmitry on 01.04.2018.
 */

public abstract class BaseAsyncTask<P, R> extends AsyncTask<P, Void, R> {

    protected static final String SERVER_ADDR = "http://hpenza.creativityprojectcenter.ru/api.request.php";
    OnResponseListener listener;
    protected Exception mException;

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
