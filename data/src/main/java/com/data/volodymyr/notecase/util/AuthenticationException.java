package com.data.volodymyr.notecase.util;

import java.io.IOException;

/**
 * Created by volodymyr on 08.03.16.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String detailMessage) {
        super(detailMessage);
    }
}
