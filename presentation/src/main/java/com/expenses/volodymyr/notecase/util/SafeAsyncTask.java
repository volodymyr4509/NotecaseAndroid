package com.expenses.volodymyr.notecase.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.data.volodymyr.notecase.util.AuthenticationException;
import com.expenses.volodymyr.notecase.activity.LoginActivity;

import java.net.SocketTimeoutException;
import java.util.logging.SocketHandler;


/**
 * Handle authentication exception
 */
public abstract class SafeAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final String TAG = "SafeAsyncTask";
    private Context context;

    public SafeAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doInBackgroundSafe();
        } catch (AuthenticationException e) {
            Log.w(TAG, "AuthenticationException. " + e.getMessage() + " Start LoginActivity");
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        } catch (SocketTimeoutException e){
            Log.i(TAG, "No connection");
        } catch (Exception e) {
            Log.wtf(TAG, "Cool exception. Check it", e);
        }
        return null;
    }

    public abstract Result doInBackgroundSafe() throws AuthenticationException, SocketTimeoutException;

}
