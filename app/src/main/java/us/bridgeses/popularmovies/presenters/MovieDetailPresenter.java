package us.bridgeses.popularmovies.presenters;

import android.app.Fragment;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import us.bridgeses.popularmovies.MovieDetailActivity;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.networking.DetailsLoaderCallback;
import us.bridgeses.popularmovies.networking.PopularLoader;

/**
 * Created by Tony on 8/27/2016.
 */
public class MovieDetailPresenter extends Fragment implements DetailsLoaderCallback {

    private static final String TAG = "MovieDetailPresenter";

    private PopularLoader popularLoader;

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
        }
    }

    @Override
    public void onReturnDetails(MovieDetail detail) {
        ((MovieDetailActivity)getActivity()).setMovieDetail(detail);
    }

    @Override
    public void onLocalFailure() {

    }

    @Override
    public void onRemoteFailure() {

    }
}
