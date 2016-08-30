package us.bridgeses.popularmovies.presenters;

import android.content.Intent;

import us.bridgeses.popularmovies.adapters.TrailerAdapter;
import us.bridgeses.popularmovies.models.MovieDetail;

/**
 * Created by Tony on 8/30/2016.
 */
public interface DetailPresenterCallback {

    void setMovieDetail(MovieDetail movieDetail);

    void setAdapter(TrailerAdapter adapter);

    void setShareIntent(Intent shareIntent);
}
