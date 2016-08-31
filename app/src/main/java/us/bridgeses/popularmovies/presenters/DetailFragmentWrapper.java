package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.IdRes;

import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.views.MovieView;
import us.bridgeses.popularmovies.views.MovieViewFragment;

/**
 * Created by tbrid on 8/30/2016.
 */
public class DetailFragmentWrapper implements MovieDetailViewer {

    private DetailPresenter presenter;
    private MovieView movieView;

    @Override
    public void load(Activity activity, @IdRes int resId, long id) {
        if (presenter == null) {
            presenter = DetailPresenterFragment.getInstance(activity,
                    (DetailPresenterCallback) movieView, new TmdbPopularLoader(activity));
        }
        if (movieView == null) {
            movieView = new MovieViewFragment();
            presenter.setCallback((DetailPresenterCallback) movieView);
            activity.getFragmentManager().beginTransaction().add(resId, (Fragment) movieView).commit();
        }
        presenter.loadDetail(id);
    }
}
