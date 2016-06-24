package com.org.thn.videocall;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.org.thn.videocall.com.org.thn.videocall.service.ClientThread;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements ClientThread.LayOutListener{
    private ClientThread clientThread ;

    SurfaceView surfaceView;
    VideoView videoHolder;
    MediaPlayer mp;
    private SurfaceHolder holder;
    int count = 0;
    boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        clientThread = ClientThread.getInstance();
        clientThread.setLayOutListener(this);
        videoHolder = (VideoView)findViewById(R.id.videoView);
//        videoHolder.setMediaController(new MediaController(this));
        holder = videoHolder.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mp = new MediaPlayer();
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.mov_bbb);
        videoHolder.setVideoURI(video);
//        videoHolder.start();
        new Thread(clientThread).start();
    }
    public void send(View v){
        EditText et = (EditText) findViewById(R.id.ed_sms);
        String str = et.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            clientThread.sendData(str);
        }
    }
    public void quit(View v){
        clientThread.sendData("QUIT");
    }
    public void connect(View v){
        if (!clientThread.isConnect()) {
            new Thread(clientThread).start();
        }
    }


    @Override
    public void onUpdateLayout(Object object) {
//        TextView textView = (TextView)findViewById(R.id.view_sms);
//        textView.setText((String)object);

            if (object instanceof ParcelFileDescriptor) {
                if (first) {
                    first = false;
                    try {
                        ParcelFileDescriptor pfd = (ParcelFileDescriptor) object;
                        mp.setDataSource(pfd.getFileDescriptor());
                        pfd.close();
                        mp.setDisplay(holder);
//                        mp.prepareAsync();
                        mp.start();
                    } catch (IOException e) {
                        android.util.Log.e("socket:", "_line81" + e.getMessage());
                    }
                }
            }

    }

}
