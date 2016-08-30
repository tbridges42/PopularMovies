package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import us.bridgeses.popularmovies.MovieDetailActivity;
import us.bridgeses.popularmovies.PosterActivity;
import us.bridgeses.popularmovies.adapters.AdapterFactory;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.PosterClickListener;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.networking.PopularLoader;
import us.bridgeses.popularmovies.networking.PosterLoaderCallback;
import us.bridgeses.popularmovies.networking.ServiceCallback;

import static us.bridgeses.popularmovies.models.Poster.MOST_POPULAR_MODE;
import static us.bridgeses.popularmovies.models.Poster.TOP_RATED_MODE;

/**
 * Created by Tony on 8/6/2016.
 */
public class PosterPresenterFragment extends Fragment implements PosterLoaderCallback, PosterPresenter,
        PosterClickListener {

    private static final String TAG = "PosterPresenterFragment";

    private PopularLoader popularLoader;
    private AdapterFactory adapterFactory;
    private PosterActivityCallbacks callbacks;
    private PosterAdapter posterAdapter;
    private int currSort = 0;
    private int page = 1;

    public static PosterPresenter getInstance(Activity context) {
        FragmentManager fm = context.getFragmentManager();
        PosterPresenterFragment presenter = (PosterPresenterFragment)fm.findFragmentByTag(TAG);
        if (presenter == null) {
            presenter = new PosterPresenterFragment();
            fm.beginTransaction().add(presenter, TAG).commit();
        }
        return presenter;
    }

    public void setPopularLoader(PopularLoader popularLoader) {
        this.popularLoader = popularLoader;
    }

    public void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public void setCallbacks(PosterActivityCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void refresh() {
        Log.d(TAG, "refresh: ");
        if (popularLoader != null) {
            Log.d(TAG, "refresh: Getting posters");
            switch (currSort) {
                case 0:
                    popularLoader.getPosters(this, MOST_POPULAR_MODE, page);
                    break;
                case 1:
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
        Log.d(TAG, "onReturnPosters: Got posters");
        if (callbacks != null) {
            if (posterAdapter == null) {
                Log.d(TAG, "onReturnPosters: Settings posters");
                posterAdapter = adapterFactory.getAdapter(posters);
                posterAdapter.setListener(this);
                callbacks.setAdapter(posterAdapter);
            }
            else {
                posterAdapter.addPosters(posters);
            }
        }
    }

    @Override
    public void onLocalFailure() {
        if (callbacks != null) {
            callbacks.onLocalFailure();
        }
    }

    @Override
    public void onRemoteFailure() {
        if (callbacks != null) {
            callbacks.onRemoteFailure();
        }
    }

    @Override
    public void onItemClick(long id) {
        ((PosterActivity)getActivity()).loadMovieDetails(id);
    }

    public void changeSort(@Poster.SortMode int newSort) {
        switch (newSort) {
            case MOST_POPULAR_MODE:
                if (currSort != 0) {
                    currSort = 0;
                    if (popularLoader != null) {
                        Log.d(TAG, "refresh: Getting posters");
                        page = 1;
                        popularLoader.getPosters(this, MOST_POPULAR_MODE, page);
                        posterAdapter = null;
                        page = 0;
                    }
                }
                break;
            case TOP_RATED_MODE:
                if (currSort != 1) {
                    currSort = 1;
                    if (popularLoader != null) {
                        Log.d(TAG, "refresh: Getting posters");
                        page = 1;
                        popularLoader.getPosters(this, TOP_RATED_MODE, page);
                        posterAdapter = null;
                    }
                }
                break;
        }
    }

    public interface PosterActivityCallbacks extends ServiceCallback {
        void setAdapter(PosterAdapter adapter);
    }
}
