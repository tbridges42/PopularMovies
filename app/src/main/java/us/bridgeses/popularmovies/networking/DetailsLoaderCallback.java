package us.bridgeses.popularmovies.networking;

import us.bridgeses.popularmovies.models.MovieDetail;

/**
 * Created by Tony on 8/6/2016.
 */
public interface DetailsLoaderCallback extends ServiceCallback {
    void onReturnDetails(MovieDetail detail);
}
