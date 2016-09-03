package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.File;
import java.io.IOException;
import java.util.List;

import us.bridgeses.popularmovies.adapters.RecyclerTrailerAdapter;
import us.bridgeses.popularmovies.adapters.TrailerAdapter;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.models.Trailer;
import us.bridgeses.popularmovies.persistence.DetailsLoaderCallback;
import us.bridgeses.popularmovies.persistence.MovieLoader;
import us.bridgeses.popularmovies.persistence.TrailerLoaderCallback;
import us.bridgeses.popularmovies.persistence.PersistenceHelper;

/**
 * Created by Tony on 8/27/2016.
 */
public class DetailPresenterFragment extends Fragment implements DetailsLoaderCallback,
        TrailerLoaderCallback, RecyclerTrailerAdapter.TrailerClickCallback,
        DetailPresenter, CheckBox.OnCheckedChangeListener {

    @SuppressWarnings("unused")
    private static final String TAG = "DetailPresenterFragment";

    private MovieLoader movieLoader;
    private MovieDetail movieDetail;
    private List<Trailer> trailers;
    private Intent shareIntent;
    private DetailPresenterCallback callback;
    private TrailerAdapter adapter;
    private PersistenceHelper persistenceHelper;

    public static DetailPresenterFragment getInstance(Activity activity,
                                                      DetailPresenterCallback callback,
                                                      MovieLoader movieLoader,
                                                      PersistenceHelper persistenceHelper) {
        FragmentManager fm = activity.getFragmentManager();
        DetailPresenterFragment fragment = (DetailPresenterFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new DetailPresenterFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        fragment.setCallback(callback);
        fragment.setMovieLoader(movieLoader);
        fragment.setPersistenceHelper(persistenceHelper);
        return fragment;
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
        if (movieLoader != null) {
            movieLoader.cancel();
        }
        super.onDestroy();
    }

    public void setMovieLoader(MovieLoader movieLoader) {
        this.movieLoader = movieLoader;
    }

    public void loadDetail(long id) {
        Log.d(TAG, "loadDetail: ");
        if (movieLoader != null) {
            movieLoader.getDetails(this, id);
            movieLoader.getTrailers(this, id);
        }
    }

    @Override
    public void onReturnDetails(MovieDetail detail) {
        this.movieDetail = detail;
        if (callback != null) {
            callback.setMovieDetail(detail);
        }
    }

    @Override
    public void onLocalFailure() {

    }

    @Override
    public void onRemoteFailure() {

    }

    @Override
    public void onReturnTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        Log.d(TAG, "onReturnTrailers: setting adapter with size " + trailers.size());
        adapter = new RecyclerTrailerAdapter(getActivity(), trailers);
        adapter.setCallback(this);
        if (callback != null) {
            callback.setAdapter(adapter);
        }
        updateShareIntent();
    }

    @Override
    public void onTrailerClick(Uri trailerUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        startActivity(intent);
    }

    public void updateShareIntent() {
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, createSubject());
        shareIntent.putExtra(Intent.EXTRA_TEXT, createText());
        shareIntent.setType("text/plain");
        if (callback != null) {
            callback.setShareIntent(shareIntent);
        }
    }

    private String createText() {
        return trailers.get(0).getVideo_path().toString() + "\n\n" + "Shared from Popular Movies by Building Bridges";
    }

    private String createSubject() {
        return movieDetail.getTitle() + ": " + trailers.get(0).getTitle();
    }

    public void setCallback(DetailPresenterCallback callback) {
        Log.d(TAG, "setCallback: ");
        this.callback = callback;
    }

    public void loadCached() {
        Log.d(TAG, "loadCached: ");
        if (callback != null) {
            Log.d(TAG, "loadCached: callback not null");
            callback.setMovieDetail(movieDetail);
            callback.setAdapter(adapter);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            persistenceHelper.saveFavorite(movieDetail, trailers);
        }
        else {
            persistenceHelper.deleteFavorite(movieDetail.getId());
        }
    }

    public void setPersistenceHelper(PersistenceHelper persistenceHelper) {
        this.persistenceHelper = persistenceHelper;
    }
}
