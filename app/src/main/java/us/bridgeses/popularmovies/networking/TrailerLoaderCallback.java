package us.bridgeses.popularmovies.networking;

import java.util.List;

import us.bridgeses.popularmovies.models.Trailer;

/**
 * Created by tbrid on 8/27/2016.
 */
public interface TrailerLoaderCallback extends ServiceCallback {

    void onReturnTrailers(List<Trailer> trailers);
}
