package com.data.volodymyr.notecase.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.data.volodymyr.notecase.daosqlite.UserSQLiteDAO;
import com.data.volodymyr.notecase.daosqlite.UserSQLiteDAOImpl;
import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.data.volodymyr.notecase.util.RequestMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by volodymyr on 06.02.16.
 */
public class RequestLoaderImpl implements RequestLoader {
    private static final String TAG = "RequestLoaderImpl";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHENTICATION_TOKEN = "AuthToken";
    private static final int REQUEST_TIMEOUT = 1000;
    private static final int AUTHENTICATION_REQUEST_TIMEOUT = 5000;

    private Context context;

    public RequestLoaderImpl(Context context) {
        this.context = context;
    }

    public String makeGet(String myurl) throws IOException {
        return makeRequest(myurl, null, RequestMethod.GET);
    }

    public String makePut(String url, byte[] data) throws IOException {
        return makeRequest(url, data, RequestMethod.PUT);
    }

    @Override
    public String makePost(String url, byte[] data) throws IOException {
        return makeRequest(url, data, RequestMethod.POST);
    }

    @Override
    public String makeDelete(String url) throws IOException {
        return makeRequest(url, null, RequestMethod.DELETE);
    }

    private String makeRequest(String requestUrl, byte[] body, RequestMethod method) throws IOException {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()){
            Log.i(TAG, "No network connection");
            return null;
        }

        InputStream is = null;
        String content = null;
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toString());
            conn.setDoInput(true);
            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            conn.setRequestProperty(AUTHENTICATION_TOKEN, getAuthToken());
            conn.setReadTimeout(REQUEST_TIMEOUT);
            conn.setConnectTimeout(REQUEST_TIMEOUT);
            if (requestUrl.contains("authenticate")){
                conn.setReadTimeout(AUTHENTICATION_REQUEST_TIMEOUT);
                conn.setConnectTimeout(AUTHENTICATION_REQUEST_TIMEOUT);
            }
            if (body!=null){
                conn.setDoOutput(true);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.write(body);
            }
            conn.connect();
            int response = conn.getResponseCode();
            if (response == 401){
                throw new AuthenticationException("Wrong AuthToken.");
            }

            Log.i(TAG, "Request: " + method + ", url: " + requestUrl + ", response code: " + response);

            is = conn.getInputStream();

            content = readIS(is);
        } finally {
            conn.disconnect();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }
    private String readIS(InputStream stream) throws IOException {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return builder.toString();
    }

    private String getAuthToken(){
        UserSQLiteDAO userSQLiteDAO = new UserSQLiteDAOImpl(context);
        User owner = userSQLiteDAO.getOwner();
        if (owner != null){
            return owner.getAuthToken();
        }else {
            return null;
        }
    }

}
