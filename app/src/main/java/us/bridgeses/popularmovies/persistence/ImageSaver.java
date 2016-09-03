package us.bridgeses.popularmovies.persistence;

import android.net.Uri;

/**
 * Created by Tony on 9/3/2016.
 */
public interface ImageSaver {

    Uri saveImage(Uri uri);

    void deleteImage(Uri uri);
}
