package us.bridgeses.popularmovies.persistence;

import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.persistence.networking.ServiceCallback;

/**
 * Created by Tony on 8/6/2016.
 */
public interface DetailsLoaderCallback extends ServiceCallback {
    void onReturnDetails(MovieDetail detail);
}
