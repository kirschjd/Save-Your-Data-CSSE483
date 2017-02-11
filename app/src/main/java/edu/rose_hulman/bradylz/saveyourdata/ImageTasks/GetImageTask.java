package edu.rose_hulman.bradylz.saveyourdata.ImageTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by bradylz on 1/27/2017.
 */

public class GetImageTask extends AsyncTask<String, Void, Bitmap> {

    private LoadImage.PicConsumer mInputConsumer;
    Bitmap bitmap;

    public GetImageTask(LoadImage.PicConsumer activity) {
        mInputConsumer = activity;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlString = strings[0];

        InputStream in = null;
        try {
            in = new URL(urlString).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        this.mInputConsumer.onImageLoaded(bitmap);
    }
}
