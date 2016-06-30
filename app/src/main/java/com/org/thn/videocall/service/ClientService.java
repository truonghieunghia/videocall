package com.org.thn.videocall.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tnghia on 6/27/16.
 */
public class ClientService extends Service {
    private Socket mSocket = null;
    private InputStream mIn;
    private OutputStream mOut;
    private Context mContext;
    private Boolean mkeepGoing = false;
    private String mUserName = "acnovn";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(mContext, "Service running", Toast.LENGTH_LONG).show();
        if (!mkeepGoing) {
//            new Thread(new ClientThread()).start();
            try {
                InetAddress serverAddr = InetAddress.getByName("172.25.65.62");
                mSocket = new Socket(serverAddr, 8400);
                mkeepGoing = true;
            } catch (UnknownHostException e) {
                mkeepGoing = false;
                android.util.Log.e("ConnectError", e.getMessage());
            } catch (IOException e) {
                mkeepGoing = false;
                android.util.Log.e("ConnectError", e.getMessage());
            }
            if (!mkeepGoing) {
                super.onStartCommand(intent, flags, startId);
            }
            try {
                mOut = mSocket.getOutputStream();
                mOut.write(("00" + mUserName + "KAERB").getBytes());
                mOut.flush();
                mIn = mSocket.getInputStream();

                while (mkeepGoing) {
                    int byte_count = mIn.read();
                    if (byte_count == -1) {
                        mkeepGoing = false;
                    }
                }
                mkeepGoing = false;
                if (mSocket != null) {
                    mSocket.close();
                }
                if (mIn != null) {
                    mIn.close();
                }
                if (mOut != null) {
                    mOut.close();
                }
            } catch (IOException e) {
                mkeepGoing = false;
                android.util.Log.e("ReadDataError", e.getMessage());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mkeepGoing = false;
        super.onDestroy();
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            connect();
            if (!mkeepGoing)
                return;
            try {
                mOut = mSocket.getOutputStream();
                mOut.write(("00" + mUserName + "KAERB").getBytes());
                mOut.flush();
                mIn = mSocket.getInputStream();

                while (mkeepGoing) {
                    int byte_count = mIn.read();
                    if (byte_count == -1) {
                        mkeepGoing = false;
                    }
                }
                mkeepGoing = false;
                if (mSocket != null) {
                    mSocket.close();
                }
                if (mIn != null) {
                    mIn.close();
                }
                if (mOut != null) {
                    mOut.close();
                }
            } catch (IOException e) {
                mkeepGoing = false;
                android.util.Log.e("ReadDataError", e.getMessage());
            }

        }

        private void connect() {
            try {
                InetAddress serverAddr = InetAddress.getByName("172.25.65.62");
                mSocket = new Socket(serverAddr, 8400);
                mkeepGoing = true;
            } catch (UnknownHostException e) {
                mkeepGoing = false;
                android.util.Log.e("ConnectError", e.getMessage());
            } catch (IOException e) {
                mkeepGoing = false;
                android.util.Log.e("ConnectError", e.getMessage());
            }
        }
    }

    class ClientSender extends Thread {

    }
}
