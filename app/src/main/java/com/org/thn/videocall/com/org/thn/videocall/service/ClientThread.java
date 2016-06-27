package com.org.thn.videocall.com.org.thn.videocall.service;

import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.SyncFailedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by tnghia on 6/24/16.
 */
public class ClientThread implements Runnable {
    private Socket socket = null;
    DataInputStream is = null;
    PrintWriter os = null;
    String line = null;
    LayOutListener mLayOutListener;
    Handler mHandler;
    private static ClientThread INSTANCE;

    private ClientThread() {
        mHandler = new Handler();
    }

    public ClientThread(LayOutListener layOutListener) {
        mLayOutListener = layOutListener;
    }

    public static ClientThread getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientThread();
        }
        return INSTANCE;
    }

    public void setLayOutListener(LayOutListener listener) {
        mLayOutListener = listener;
    }

    @Override
    public void run() {
        reConnect();
        if (socket!=null) {
            try {
                DataInputStream in = null;
                is = new DataInputStream(socket.getInputStream());
                os = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                android.util.Log.e("socket:","_line55"+ e.getMessage());
            }
            try {

                int count = is.read();
                if (!TextUtils.isEmpty(line))
                    while (count != -1) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mLayOutListener != null) {

                                    if (line.equalsIgnoreCase("a")){
                                        mLayOutListener.onUpdateLayout(line);
                                    } else {
                                        ParcelFileDescriptor pfd =  ParcelFileDescriptor.fromSocket(socket);
                                        mLayOutListener.onUpdateLayout(pfd);
                                    }
                                }
                            }
                        });
                        count = is.read();
                    }
            } catch (IOException e) {
                android.util.Log.e("socket:","_line79"+ e.getMessage());
            } finally {
                try {
                    System.out.println("Connection Closing..");
                    if (is != null) {
                        is.close();
                    }

                    if (os != null) {
                        os.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }

                } catch (IOException e) {
                    android.util.Log.e("socket:","_line90"+ e.getMessage());
                }
            }
        }
    }

    public void sendData(Object data) {
        if (socket == null)
            return;
        if (socket.isConnected()) {
            try {
                String str = (String) data;
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(str);
            } catch (UnknownHostException e) {
                android.util.Log.e("socket:", "_line103" + e.getMessage());
            } catch (IOException e) {
                android.util.Log.e("socket:", "_line105" + e.getMessage());
            } catch (Exception e) {
                android.util.Log.e("socket:", "_line107" + e.getMessage());
            }
        }
    }

    public void reConnect(){
        while (socket == null || socket.isClosed()) {
            try {
                System.out.println("ServerIP:" + InetAddress.getLocalHost().getHostAddress());
                InetAddress serverAddr = InetAddress.getByName("192.168.1.100");
                socket = new Socket(serverAddr, 8400);
                Thread.sleep(100); // simulate work
            } catch (InterruptedException x) {
                Thread.currentThread().interrupted();
            }catch (UnknownHostException e) {
                android.util.Log.e("socket:","_line122"+ e.getMessage());
            } catch (IOException e) {
                android.util.Log.e("socket:","_line124"+ e.getMessage());
            }
        }
    }
    public boolean isConnect(){
        if (socket.isClosed() || socket==null){
            return false;
        }
        return true;
    }
    public interface LayOutListener {
        void onUpdateLayout(Object object);
    }
}