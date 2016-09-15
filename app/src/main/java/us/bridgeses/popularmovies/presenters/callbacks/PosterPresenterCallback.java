package us.bridgeses.popularmovies.presenters.callbacks;

import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.persistence.networking.ServiceCallback;

/**
 * Created by tbrid on 8/29/2016.
 */
public interface PosterPresenterCallback extends ServiceCallback {
    void setAdapter(PosterAdapter adapter);

    void loadMovieDetails(long id);
}
