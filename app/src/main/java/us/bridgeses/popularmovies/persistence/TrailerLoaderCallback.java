package us.bridgeses.popularmovies.persistence;

import java.util.List;

import us.bridgeses.popularmovies.models.Trailer;
import us.bridgeses.popularmovies.persistence.networking.ServiceCallback;

/**
 * Created by tbrid on 8/27/2016.
 */
public interface TrailerLoaderCallback extends ServiceCallback {

    void onReturnTrailers(List<Trailer> trailers);
}