package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import us.bridgeses.popularmovies.adapters.AdapterFactory;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.PosterClickListener;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.networking.PopularLoader;
import us.bridgeses.popularmovies.networking.PosterLoaderCallback;

import static us.bridgeses.popularmovies.models.Poster.MOST_POPULAR_MODE;
import static us.bridgeses.popularmovies.models.Poster.TOP_RATED_MODE;

/**
 * Created by Tony on 8/6/2016.
 */
public class PosterPresenterFragment extends Fragment implements PosterLoaderCallback,
        PosterPresenter,
        PosterClickListener {

    private static final String TAG = "PosterPresenterFragment";

    private PopularLoader popularLoader;
    private AdapterFactory adapterFactory;
    private PosterPresenterCallback callback;
    private PosterAdapter posterAdapter;
    private @Poster.SortMode int currSort = MOST_POPULAR_MODE;
    private int page = 1;

    public static PosterPresenter getInstance(Activity context, PopularLoader popularLoader,
                                              AdapterFactory adapterFactory,
                                              PosterPresenterCallback callback) {
        FragmentManager fm = context.getFragmentManager();
        PosterPresenterFragment presenter = (PosterPresenterFragment)fm.findFragmentByTag(TAG);
        if (presenter == null) {
            presenter = new PosterPresenterFragment();
            presenter.setPopularLoader(popularLoader);
            presenter.setAdapterFactory(adapterFactory);
            fm.beginTransaction().add(presenter, TAG).commit();
        }
        presenter.setCallback(callback);
        return presenter;
    }

    public void setPopularLoader(PopularLoader popularLoader) {
        this.popularLoader = popularLoader;
    }

    public void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public void setCallback(PosterPresenterCallback callback) {
        this.callback = callback;
    }

    public void refresh() {
        if (popularLoader != null) {
            switch (currSort) {
                case MOST_POPULAR_MODE:
                    popularLoader.getPosters(this, MOST_POPULAR_MODE, page);
                    break;
                case TOP_RATED_MODE:
                    popularLoader.getPosters(this, TOP_RATED_MODE, page);
                    break;
            }
        }
    }

    public PosterAdapter getCachedAdapter() {
        return posterAdapter;
    }

    public void getNextPage() {
        page++;
        refresh();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // This fragment has no UI and should not be displayed
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            File httpCacheDir = new File(context.getCacheDir(), "https");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTPS response cache installation failed:" + e);
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        if (popularLoader != null) {
            popularLoader.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onReturnPosters(List<Poster> posters) {
        if (callback != null) {
            if (posterAdapter == null) {
                posterAdapter = adapterFactory.getAdapter(posters);
                posterAdapter.setListener(this);
                callback.setAdapter(posterAdapter);
            }
            else {
                posterAdapter.addPosters(posters);
            }
        }
    }

    @Override
    public void onLocalFailure() {
        if (callback != null) {
            callback.onLocalFailure();
        }
    }

    @Override
    public void onRemoteFailure() {
        if (callback != null) {
            callback.onRemoteFailure();
        }
    }

    @Override
    public void onPosterClick(long id) {
        if (callback != null) {
            callback.loadMovieDetails(id);
        }
    }

    public void changeSort(@Poster.SortMode int newSort) {
        if (currSort != newSort) {
            currSort = newSort;
            if (popularLoader != null) {
                page = 1;
                popularLoader.getPosters(this, newSort, page);
                posterAdapter = null;
                page = 0;
            }
            else {
                throw new IllegalStateException("No PopularLoader set");
            }
        }
    }
}
