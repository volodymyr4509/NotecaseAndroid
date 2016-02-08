package com.data.volodymyr.notecase.request;

import java.io.IOException;

/**
 * Created by volodymyr on 06.02.16.
 */
public interface RequestLoader {
    String makeGet(String url) throws IOException;
    String makePut(String url, byte[] data) throws IOException;
    String makePost(String url, byte[] data) throws IOException;
    String makeDelete(String url) throws IOException;
}
