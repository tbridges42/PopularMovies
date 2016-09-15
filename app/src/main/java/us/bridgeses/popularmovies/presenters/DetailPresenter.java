package us.bridgeses.popularmovies.presenters;

import us.bridgeses.popularmovies.presenters.callbacks.DetailPresenterCallback;

/**
 * Created by Tony on 8/30/2016.
 */
public interface DetailPresenter {
    void setCallback(DetailPresenterCallback callback);

    void loadDetail(long id);

    void updateShareIntent();

    void loadCached();
}
