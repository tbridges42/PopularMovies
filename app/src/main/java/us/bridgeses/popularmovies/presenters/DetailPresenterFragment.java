package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import us.bridgeses.popularmovies.adapters.RecyclerTrailerAdapter;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.models.Trailer;
import us.bridgeses.popularmovies.networking.DetailsLoaderCallback;
import us.bridgeses.popularmovies.networking.PopularLoader;
import us.bridgeses.popularmovies.networking.TrailerLoaderCallback;

/**
 * Created by Tony on 8/27/2016.
 */
public class DetailPresenterFragment extends Fragment implements DetailsLoaderCallback,
        TrailerLoaderCallback, RecyclerTrailerAdapter.TrailerClickCallback, MovieDetailViewer,
        DetailPresenter {

    @SuppressWarnings("unused")
    private static final String TAG = "DetailPresenterFragment";

    private PopularLoader popularLoader;
    private MovieDetail movieDetail;
    private Trailer firstTrailer;
    private Intent shareIntent;
    private DetailPresenterCallback callback;

    public static DetailPresenterFragment getInstance(Activity activity,
                                                      DetailPresenterCallback callback,
                                                      PopularLoader popularLoader) {
        FragmentManager fm = activity.getFragmentManager();
        DetailPresenterFragment fragment = (DetailPresenterFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new DetailPresenterFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        fragment.setCallback(callback);
        fragment.popularLoader = popularLoader;
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
        if (popularLoader != null) {
            popularLoader.cancel();
        }
        super.onDestroy();
    }

    public void setPopularLoader(PopularLoader popularLoader) {
        this.popularLoader = popularLoader;
    }

    public void loadDetail(long id) {
        if (popularLoader != null) {
            popularLoader.getDetails(this, id);
            popularLoader.getTrailers(this, id);
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
        firstTrailer = trailers.get(0);
        Log.d(TAG, "onReturnTrailers: setting adapter with size " + trailers.size());
        RecyclerTrailerAdapter adapter = new RecyclerTrailerAdapter(getActivity(), trailers);
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
        return firstTrailer.getVideo_path().toString() + "\n\n" + "Shared from Popular Movies by Building Bridges";
    }

    private String createSubject() {
        return movieDetail.getTitle() + ": " + firstTrailer.getTitle();
    }

    @Override
    public void load(Activity activity, @IdRes int resId, long id) {
        activity.getFragmentManager().beginTransaction().add(resId, this, TAG).commit();
    }

    public void setCallback(DetailPresenterCallback callback) {
        this.callback = callback;
    }
}
