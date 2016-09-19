package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.FragmentManager;

import us.bridgeses.popularmovies.adapters.AdapterFactory;
import us.bridgeses.popularmovies.loaders.MovieLoader;
import us.bridgeses.popularmovies.presenters.callbacks.PosterPresenterCallback;
import us.bridgeses.popularmovies.presenters.implementations.PosterPresenterFragment;

/**
 * Created by Tony on 9/18/2016.
 */
public class PosterPresenterFactory {

    @SuppressWarnings("unused")
    private static final String TAG = "PosterPresenterFactory";

    public static PosterPresenter getInstance(Activity activity,
                                              MovieLoader movieLoader,
                                              AdapterFactory adapterFactory,
                                              PosterPresenterCallback callback) {
        FragmentManager fm = activity.getFragmentManager();
        PosterPresenterFragment presenter = (PosterPresenterFragment)fm.findFragmentByTag(TAG);
        if (presenter == null) {
            presenter = new PosterPresenterFragment();
            presenter.setMovieLoader(movieLoader);
            presenter.setAdapterFactory(adapterFactory);
            fm.beginTransaction().add(presenter, TAG).commit();
        }
        presenter.setCallback(callback);
        return presenter;
    }
}
