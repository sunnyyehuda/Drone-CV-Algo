package com.example.nativetest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.arthenica.mobileffmpeg.Config;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import com.arthenica.mobileffmpeg.FFmpeg;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static java.lang.System.loadLibrary;


public class MainActivity extends AppCompatActivity
{
    //############################//
    // static permissions vars    //
    //############################//
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_ALL =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };

    //#########################//
    // loading native library  //
    //#########################//
    private static String TAG="MainActivity";
    static
    {
        loadLibrary("native-lib");
        loadLibrary("opencv_java4");
    }

    //####################//
    // global variables   //
    //####################//
    String  videoRtspUrl = "out.mov";
    private SurfaceHolder sh;
    private SurfaceView sfv;
    private MediaPlayer player;
    InetAddress receiverAddress;
    DatagramSocket datagramSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //###############################//
        // setting the fields of tha app //
        //###############################//
        sfv = (SurfaceView)findViewById(R.id.surfaceView);
        TextView tv = findViewById(R.id.sample_text);
        TextView textView=(TextView) findViewById(R.id.sample_text2);
        ImageView imv=(ImageView) findViewById(R.id.imageView);
        player = new MediaPlayer();

        //####################//
        // VERIFY PERMISSIONS //
        //####################//
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_ALL,
                    REQUEST_EXTERNAL_STORAGE);
        }
        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_ALL,
                    REQUEST_EXTERNAL_STORAGE);
        }
        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_ALL,
                    REQUEST_EXTERNAL_STORAGE);
        }

        //#############################//
        // making FFMPEG pipe          //
        //#############################//
        Toast.makeText(getApplicationContext(), "ffmpeg version: "+Config.getFFmpegVersion(), Toast.LENGTH_LONG).show();
        String pipe = Config.registerNewFFmpegPipe(getApplicationContext());

        //###############################################//
        // making socket for initialize the tello        //
        //###############################################//
        String addressString = "192.168.10.1";
        try {
            receiverAddress = InetAddress.getByName(addressString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            datagramSocket = new DatagramSocket(8889);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        new TelloCommand().execute("command");
        new TelloCommand().execute("streamon");
        new TelloCommand().execute("takeoff");
        new TelloCommand().execute("up 100");
        new TelloCommand().execute("forward 200");
        new Grabber().execute("/storage/emulated/0/Movies/drones/%03d.jpg");
        new TelloCommand().execute("cw 180");
        new TelloCommand().execute("forward 200");
        new TelloCommand().execute("land");
        new TelloCommand().execute("streamoff");



        FFmpeg.cancel();
        Config.closeFFmpegPipe(pipe);

        try {
            player.setDataSource(this, Uri.parse(videoRtspUrl));
            sh =sfv.getHolder();
            sh.addCallback(new MyCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    player.setLooping(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }




        //######################################//
        // call the native code written in c++  //
        //######################################//
        textView.setText(stringFromJNI());
        tv.setText(stringFromJNI());

        //###################################//
        // init opencv precompiled libraries //
        //###################################//
        if (OpenCVLoader.initDebug()) {
            textView.setText(textView.getText()+"\n OPENCV LOADED SUCCESSFULLY");
            textView.setText(textView.getText()+"\n"+validate(500,500));

        } else {
            Log.d(TAG, "OPENCV DID NOT LOAD");

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Grabber extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            int rc = FFmpeg.execute("-i udp://192.168.10.1:11111 -r 6 -f image2 "+params[0]);

            if (rc == RETURN_CODE_SUCCESS) {
                Toast.makeText(getApplicationContext(), "ffmpeg succeed:", Toast.LENGTH_LONG).show();
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "ffmpeg failed:", Toast.LENGTH_LONG).show();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TelloCommand extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            byte[] sendBuffer = params[0].getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            DatagramPacket sendPacket = new DatagramPacket(
                    sendBuffer, sendBuffer.length, receiverAddress, 8889);
            try {
                datagramSocket.send(sendPacket);
                datagramSocket.receive(receivePacket);
                return new String(receiveBuffer, 0, receivePacket.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public native String stringFromJNI();
    public native String validate(long madAddrGr,long matAddrRgba);
}