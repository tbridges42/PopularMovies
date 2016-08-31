package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.IdRes;
import android.util.Log;

import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.views.MovieView;
import us.bridgeses.popularmovies.views.MovieViewFragment;

/**
 * Created by tbrid on 8/30/2016.
 */
public class DetailFragmentWrapper implements MovieDetailViewer {

    @SuppressWarnings("unused")
    private static final String TAG = "DetailFragmentWrapper";

    private DetailPresenter presenter;
    private MovieView movieView;

    @Override
    public void load(Activity activity, @IdRes int resId, long id) {
        Log.d(TAG, "load: ");
        if (presenter == null) {

            presenter = DetailPresenterFragment.getInstance(activity,
                    (DetailPresenterCallback) movieView, new TmdbPopularLoader(activity));
        }
        if (movieView == null) {
            movieView = MovieViewFragment.getInstance(activity, resId);
        }
        presenter.setCallback((DetailPresenterCallback) movieView);
        presenter.loadDetail(id);
    }

    @Override
    public void loadCached(Activity activity) {
        Log.d(TAG, "loadCached: ");
        if (presenter == null) {
            presenter = DetailPresenterFragment.getInstance(activity,
                    (DetailPresenterCallback) movieView, new TmdbPopularLoader(activity));
        }
        if (movieView == null) {
            movieView = MovieViewFragment.getInstance(activity, 0);
        }
        presenter.setCallback((DetailPresenterCallback) movieView);
        Log.d(TAG, "loadCached: Presenter not null");
        presenter.loadCached();
    }
}
