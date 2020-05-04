package com.example.nativetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //###############################//
        // setting the fields of tha app //
        //###############################//
        sfv = (SurfaceView)findViewById(R.id.big_screen);
        TextView tv = findViewById(R.id.sample_text);
        TextView textView=(TextView) findViewById(R.id.sample_text);
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

        int rc = FFmpeg.execute("-rtsp_transport udp -i rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov -c:va copy out.mov");

        if (rc == RETURN_CODE_SUCCESS) {
            Toast.makeText(getApplicationContext(), "ffmpeg succeed:", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "ffmpeg failed:", Toast.LENGTH_LONG).show();
        }


        FFmpeg.cancel();
        Config.closeFFmpegPipe(pipe);

        //ffmpeg = FFmpeg.getInstance(getApplicationContext());
        //String[] cmd = "-rtsp_transport udp -i rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov -r 10 -f image2 /storage/emulated/0/Pictures/img%03d.png".split(" ");

        //ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

            //@Override
            //public void onStart() {}

            //@Override
            //public void onProgress(String message) {
                //Toast.makeText(getApplicationContext(), "ffmpeg ongoing: "+message, Toast.LENGTH_SHORT).show();
            //}

            //@Override
            //public void onFailure(String message) {
                //Toast.makeText(getApplicationContext(), "ffmpeg failed:"+message, Toast.LENGTH_LONG).show();
            //}

            //@Override
           // public void onSuccess(String message) {
                //T//oast.makeText(getApplicationContext(), "ffmpeg succeed", Toast.LENGTH_SHORT).show();
         //   }

           // @Override
          //  public void onFinish() {}

      //  });
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