package us.bridgeses.popularmovies.loaders;

import android.support.annotation.NonNull;

import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.loaders.callbacks.DetailsLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.PosterLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.TrailerLoaderCallback;

/**
 * Created by Tony on 8/6/2016.
 */
public interface MovieLoader {

    void getPosters(@NonNull PosterLoaderCallback callback, @Poster.SortMode int mode, int page);
    void getDetails(@NonNull DetailsLoaderCallback callback, long id);
    void getTrailers(@NonNull TrailerLoaderCallback callback, long id);
    void cancel();
}
