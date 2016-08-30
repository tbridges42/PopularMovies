package us.bridgeses.popularmovies.presenters;

import us.bridgeses.popularmovies.adapters.AdapterFactory;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.networking.PopularLoader;

/**
 * Created by tbrid on 8/29/2016.
 */
public interface PosterPresenter {

    void setPopularLoader(PopularLoader loader);

    void setAdapterFactory(AdapterFactory factory);

    void setCallbacks(PosterPresenterFragment.PosterActivityCallbacks callbacks);

    void refresh();

    void getNextPage();

    void changeSort(@Poster.SortMode int sortMode);

    PosterAdapter getCachedAdapter();
}
