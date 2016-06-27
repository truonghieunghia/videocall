package com.org.thn.videocall.com.org.thn.videocall.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by tnghia on 6/27/16.
 */
public class ClientService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ClientService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
