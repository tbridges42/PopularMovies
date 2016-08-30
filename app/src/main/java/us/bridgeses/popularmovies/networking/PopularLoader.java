package us.bridgeses.popularmovies.networking;

import android.support.annotation.NonNull;

import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/6/2016.
 */
public interface PopularLoader {

    void getPosters(@NonNull PosterLoaderCallback callback, @Poster.SortMode int mode, int page);
    void getDetails(@NonNull DetailsLoaderCallback callback, long id);
    void getTrailers(@NonNull TrailerLoaderCallback callback, long id);
    void cancel();
}
