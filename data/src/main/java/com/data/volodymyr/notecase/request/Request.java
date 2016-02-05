package com.data.volodymyr.notecase.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vkret on 05.02.16.
 */
public class Request {

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String content = null;
        HttpURLConnection urlCon = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

    public String readIt(InputStream stream) throws IOException {
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
