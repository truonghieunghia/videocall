package com.org.thn.videocall;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Socket socket = null;
    Handler updateUi ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUi = new Handler();
        new Thread(new ClientThread()).start();
    }
    public void send(View v){
        EditText et = (EditText) findViewById(R.id.ed_sms);
        String str = et.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            sendData(str);
        }
    }
    public void quit(View v){
        sendData("QUIT");
    }
    public void connect(View v){
        new Thread(new ClientThread()).start();
    }
    private void sendData(String data){
        try {
            String str = data;
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(str);
        }catch (UnknownHostException e){
            android.util.Log.e("socket:",e.getMessage());
        }catch (IOException e){
            android.util.Log.e("socket:",e.getMessage());
        }catch (Exception e){
            android.util.Log.e("socket:",e.getMessage());
        }
    }
    class ClientThread implements Runnable {
        BufferedReader is=null;
        PrintWriter os=null;
        String line=null;
        @Override
        public void run() {
            try {
                System.out.println("ServerIP:"+InetAddress.getLocalHost().getHostAddress());
                InetAddress serverAddr = InetAddress.getByName("172.25.65.62");
                socket = new Socket(serverAddr,8400);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            try{
                is=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os= new PrintWriter(socket.getOutputStream());
            }catch (IOException e){
                android.util.Log.e("socket:",e.getMessage());
            }
            String response=null;
            try {
                line=is.readLine();
                while(line.compareTo("QUIT")!=0){
                    updateUi.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView)findViewById(R.id.view_sms);
                            textView.setText(line);
                        }
                    });

                    line=is.readLine();
                }
            } catch (IOException e) {
                android.util.Log.e("socket:",e.getMessage());
            } finally {
                try {
                    System.out.println("Connection Closing..");
                    if (is != null) {
                        is.close();
                        System.out.println(" Socket Input Stream Closed");
                    }

                    if (os != null) {
                        os.close();
                        System.out.println("Socket Out Closed");
                    }
                    if (socket != null) {
                        socket.close();
                    }

                } catch (IOException ie) {
                    System.out.println("Socket Close Error");
                }
            }
        }
    }
}
