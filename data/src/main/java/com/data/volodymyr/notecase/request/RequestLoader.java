package com.data.volodymyr.notecase.request;

import com.data.volodymyr.notecase.util.RequestMethod;

import java.io.IOException;

/**
 * Created by volodymyr on 06.02.16.
 */
public interface RequestLoader {
    String downloadUrl(String myurl, Enum<RequestMethod> method) throws IOException;

}
