package com.data.volodymyr.notecase.request;

import com.data.volodymyr.notecase.util.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by volodymyr on 06.02.16.
 */
public class RequestLoaderImpl implements RequestLoader {
    private static final int REQUEST_TIMEOUT = 5000;

    public String downloadUrl(String myurl, Enum<RequestMethod> method) throws IOException {
        InputStream is = null;
        String content = null;
        HttpURLConnection urlCon = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toString());
            conn.setReadTimeout(REQUEST_TIMEOUT);
            conn.connect();
            int response = conn.getResponseCode();

            System.out.println("--- " + response + " ---");
            is = conn.getInputStream();

            content = readIt(is);
        } finally {
            urlCon.disconnect();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    private String readIt(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = null;
        String result = "";
        try {
            String line;
            bufferedReader = new BufferedReader(reader);
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }
}
