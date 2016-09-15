package us.bridgeses.popularmovies.persistence.implementations;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import us.bridgeses.popularmovies.persistence.ImageSaver;

/**
 * Created by Tony on 9/3/2016.
 */
public class DiskImageSaver implements ImageSaver {

    @SuppressWarnings("unused")
    private static final String TAG = "DiskImageSaver";

    private Uri rootDir;
    public DiskImageSaver(Uri rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public Uri saveImage(Uri uri) {
        new ImageSaveTask(rootDir).execute(uri);
        return rootDir.buildUpon().appendPath(uri.getLastPathSegment()).scheme("file").build();
    }

    @Override
    public void deleteImage(Uri uri) {
        File image = new File(uri.toString());
        image.delete();
    }

    private void handleException() {
    }

    private class ImageSaveTask extends AsyncTask<Uri, Void, Void> {

        private Uri rootdir;
        private boolean exception = false;

        public ImageSaveTask(Uri rootdir) {
            this.rootdir = rootdir;
        }

        @Override
        protected Void doInBackground(Uri... params) {
            Uri source = params[0];
            File target = new File(rootdir.toString() + "/" + source.getLastPathSegment());
            InputStream is = null;
            OutputStream os = null;
            Log.d(TAG, "doInBackground: target:" + target.toString());
            try {
                is = new URL(source.toString()).openConnection().getInputStream();
                target.createNewFile();
                Log.d(TAG, "doInBackground: creating file");
                if (target.exists()) {
                    Log.d(TAG, "doInBackground: saving file");
                    os = new FileOutputStream(target);
                    byte [] buffer = new byte[256];
                    int bytesRead;
                    while((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        Log.d(TAG, "doInBackground: Read " + bytesRead + " bytes");
                    }
                }
            }
            catch (IOException e) {
                exception = true;
                e.printStackTrace();
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }

                    if (os != null) {
                        os.close();
                    }
                }
                catch (IOException e) {
                    exception = true;
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (exception) {
                Log.d(TAG, "onPostExecute: Exception");
            }
        }
    }
}
