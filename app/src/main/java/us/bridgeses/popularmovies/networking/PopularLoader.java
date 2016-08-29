package us.bridgeses.popularmovies.networking;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

/**
 * Created by Tony on 8/6/2016.
 */
public interface PopularLoader {

    @IntDef({MOST_POPULAR_MODE, TOP_RATED_MODE})
    @interface SortMode {}

    int MOST_POPULAR_MODE = 0;
    int TOP_RATED_MODE = 1;

    void getPosters(@NonNull PosterLoaderCallback callback, @SortMode int mode, int page);
    void getDetails(@NonNull DetailsLoaderCallback callback, long id);
    void getTrailers(@NonNull TrailerLoaderCallback callback, long id);
    void cancel();
}
