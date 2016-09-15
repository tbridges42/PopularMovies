package us.bridgeses.popularmovies.presenters.implementations;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.util.Log;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.persistence.implementations.DiskImageSaver;
import us.bridgeses.popularmovies.persistence.implementations.FavoriteMovieLoader;
import us.bridgeses.popularmovies.persistence.FavoritesManager;
import us.bridgeses.popularmovies.persistence.PersistenceHelper;
import us.bridgeses.popularmovies.persistence.networking.TmdbMovieLoader;
import us.bridgeses.popularmovies.persistence.ImageSaver;
import us.bridgeses.popularmovies.persistence.implementations.PersistenceHelperImpl;
import us.bridgeses.popularmovies.presenters.DetailPresenter;
import us.bridgeses.popularmovies.presenters.MovieDetailViewer;
import us.bridgeses.popularmovies.presenters.callbacks.DetailPresenterCallback;
import us.bridgeses.popularmovies.presenters.callbacks.FavoriteCallback;
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
            presenter = getPresenter(activity);
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
            presenter = getPresenter(activity);
        }
        if (movieView == null) {
            movieView = MovieViewFragment.getInstance(activity, 0);
        }
        presenter.setCallback((DetailPresenterCallback) movieView);
        Log.d(TAG, "loadCached: Presenter not null");
        presenter.loadCached();
    }

    private DetailPresenterFragment getPresenter(Activity activity) {
        PersistenceHelper ph = new PersistenceHelperImpl(activity.getContentResolver(),
                new DiskImageSaver(Uri.parse(activity.getFilesDir().toString())));
        FavoriteMovieLoader fml = new FavoriteMovieLoader(
                new FavoritesManager(ph),
                new TmdbMovieLoader(
                        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE),
                        activity.getResources().getString(R.string.imdb_api_key)),
                new Handler()
                );
        return DetailPresenterFragment.getInstance(activity, (DetailPresenterCallback) movieView,
                fml, ph, (FavoriteCallback) activity);
    }
}
