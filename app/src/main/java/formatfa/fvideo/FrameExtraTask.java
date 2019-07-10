package formatfa.fvideo;

import android.os.AsyncTask;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.File;

public class FrameExtraTask extends AsyncTask<String, FrameExtraTask.ExtraMessage,String> {

//    frame extra task
    public static class ExtraMessage
    {
        public int count;
        public int now;
        public String message;

        public ExtraMessage(int count, int now, String message) {
            this.count = count;
            this.now = now;
            this.message = message;
        }

        @Override
        public String toString() {
            return "ExtraMessage{" +
                    "count=" + count +
                    ", now=" + now +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    private File outPath;

    private FrameExtraListener listener;
    public FrameExtraTask(File outpath,FrameExtraListener listener) {
        this.outPath = outpath;
        this.listener=listener;

        if(!outpath.exists())outpath.mkdirs();

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(ExtraMessage... values) {
        if(listener!=null)listener.update(values[0]);
    }

    @Override
    protected String doInBackground(String... strings) {


        VideoCapture video = new VideoCapture(strings[0]);

        Mat frame = new Mat();

        int index = 0;
        while(video.read(frame))
        {
            File out = new File(this.outPath,String.format("%d.png",index));
            Imgcodecs.imwrite(out.getAbsolutePath(),frame);
            index+=1;

            publishProgress(new ExtraMessage(0,index,out.getAbsolutePath()));

        }

        return null;
    }
}
