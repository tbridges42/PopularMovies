package us.bridgeses.popularmovies.presenters;

import us.bridgeses.popularmovies.adapters.AdapterFactory;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.loaders.MovieLoader;
import us.bridgeses.popularmovies.presenters.callbacks.PosterPresenterCallback;

/**
 * Created by tbrid on 8/29/2016.
 */
public interface PosterPresenter {

    void setMovieLoader(MovieLoader loader);

    void setAdapterFactory(AdapterFactory factory);

    void setCallback(PosterPresenterCallback callback);

    void refresh();

    void getNextPage();

    void changeSort(@Poster.SortMode int sortMode);

    PosterAdapter getCachedAdapter();
}
