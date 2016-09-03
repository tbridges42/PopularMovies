package us.bridgeses.popularmovies.persistence;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by Tony on 9/3/2016.
 */
public class DiskImageSaver implements ImageSaver {

    private Uri rootDir;

    public DiskImageSaver(Uri rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public Uri saveImage(Uri uri) {
        return null;
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
            File target = new File(rootdir.toString()
                    + File.pathSeparator + source.getLastPathSegment());
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new URL(source.toString()).openConnection().getInputStream();
                target.createNewFile();
                if (target.exists()) {
                    os = new FileOutputStream(target);
                    byte [] buffer = new byte[256];
                    int bytesRead = 0;
                    while((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }
            catch (IOException e) {
                exception = true;
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
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
