package us.bridgeses.popularmovies.presenters;

/**
 * Created by Tony on 8/30/2016.
 */
public interface DetailPresenter {
    void setCallback(DetailPresenterCallback callback);

    void loadDetail(long id);

    void updateShareIntent();
}
