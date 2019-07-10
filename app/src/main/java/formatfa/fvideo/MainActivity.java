package formatfa.fvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static{
        System.loadLibrary("opencv_java4");

    }

    private void log(Object obj)
    {
        Log.d("OpenCv",obj+"");
    }
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status) {
            if(status== LoaderCallbackInterface.SUCCESS)
            {
                Toast.makeText(MainActivity.this," connect success",Toast.LENGTH_SHORT).show();            }
            else {
                Toast.makeText(MainActivity.this," connect fail",Toast.LENGTH_SHORT).show();

                super.onManagerConnected(status);
            }
        }
    };


    private EditText videoPath;
    private TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1)
        {


        }
    }

    private void initView() {
        videoPath = findViewById(R.id.videoPath);
        info = findViewById(R.id.info);
        findViewById(R.id.getinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                videoTest(videoPath.getText().toString());

                new FrameExtraTask(new File("/sdcard/frame_out/"), new FrameExtraListener() {
                    @Override
                    public void update(FrameExtraTask.ExtraMessage msg) {
                        info.setText(msg.toString());

                    }


                }).execute(videoPath.getText().toString());
            }
        });
        findViewById(R.id.choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            new LFilePicker()
                    .withActivity(MainActivity.this)
                    .withRequestCode(1)
                    .start();
            }
        });
    }

    private void videoTest(String path) {

        File in = new File(path);
        if(!in.exists())
        {
            Toast.makeText(this, "not exists",Toast.LENGTH_LONG).show();
            return;
        }
        VideoCapture video =  new VideoCapture(path);

        Mat mat = new Mat();
        boolean result = video.read(mat);

        log("video frame count:"+  result+" test:"+mat);
        log("video frame count:"+  video.get( Videoio.CAP_PROP_FRAME_COUNT));
        log("video frame width:"+  video.get( Videoio.CAP_PROP_FRAME_WIDTH));
        log("video frame height:"+  video.get( Videoio.CAP_PROP_FRAME_HEIGHT));
        log("video frame fps:"+  video.get( Videoio.CAP_PROP_FPS));


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!OpenCVLoader.initDebug())
        {
        Toast.makeText(this,"init success",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode)
        {
            case 1:
                if(resultCode== Activity.RESULT_OK&&data!=null)
                {
                    List<String> files = data.getStringArrayListExtra(Constant.RESULT_INFO);
                    videoPath.setText(files.get(0));
                }
                break;


        }

    }
}
