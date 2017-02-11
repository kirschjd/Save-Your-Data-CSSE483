package edu.rose_hulman.bradylz.saveyourdata.ImageTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

import edu.rose_hulman.bradylz.saveyourdata.File;

/**
 * Created by bradylz on 1/27/2017.
 */

public class LoadImage extends AsyncTask<String, Void, File> {

    private PicConsumer mPicConsumer;

    public LoadImage(PicConsumer activity) {
        mPicConsumer = activity;
    }

    @Override
    protected File doInBackground(String... strings) {
        String urlString = strings[0];
        File pic = null;
        try {
            pic = new ObjectMapper().readValue(new URL(urlString), File.class);
        } catch (IOException e) {
            Log.d("PicLoader", e.getMessage());
        }
        return pic;
    }

    @Override
    protected void onPostExecute(File pic) {
        super.onPostExecute(pic);
        mPicConsumer.onPicLoaded(pic);
    }

    public interface PicConsumer {
        public void onPicLoaded(File Pic);
        public void onImageLoaded(Bitmap bitmap);
    }
}
