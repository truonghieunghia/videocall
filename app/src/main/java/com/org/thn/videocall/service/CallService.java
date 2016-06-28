package com.org.thn.videocall.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tnghia on 6/27/16.
 */
public class CallService extends InputStream{

    @Override
    public int read() throws IOException {
        return 0;
    }
}
