package us.bridgeses.popularmovies.presenters;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import us.bridgeses.popularmovies.MovieDetailActivity;
import us.bridgeses.popularmovies.PosterActivity;
import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.adapters.ListAdapterFactory;
import us.bridgeses.popularmovies.adapters.PosterRecyclerAdapter;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.networking.PopularLoader;
import us.bridgeses.popularmovies.networking.PosterLoaderCallback;
import us.bridgeses.popularmovies.networking.ServiceCallback;

/**
 * Created by Tony on 8/6/2016.
 */
public class PosterActivityPresenter extends Fragment implements PosterLoaderCallback,
        PosterRecyclerAdapter.PosterClickListener, Spinner.OnItemSelectedListener {

    private static final String TAG = "PosterActivityPresenter";

    private PopularLoader popularLoader;
    private RecyclerAdapterFactory listAdapterFactory;
    private PosterActivityCallbacks callbacks;
    private RecyclerView.Adapter listAdapter;
    private int currSort = 0;
    private int page = 1;

    public void setPopularLoader(PopularLoader popularLoader) {
        this.popularLoader = popularLoader;
    }

    public void setListAdapterFactory(RecyclerAdapterFactory listAdapterFactory) {
        this.listAdapterFactory = listAdapterFactory;
    }

    public void setCallbacks(PosterActivityCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public RecyclerView.Adapter getCachedListAdapter() {
        return listAdapter;
    }

    public void refresh() {
        Log.d(TAG, "refresh: ");
        if (popularLoader != null) {
            Log.d(TAG, "refresh: Getting posters");
            switch (currSort) {
                case 0:
                    popularLoader.getPosters(this, PopularLoader.MOST_POPULAR_MODE, page);
                    break;
                case 1:
                    popularLoader.getPosters(this, PopularLoader.TOP_RATED_MODE, page);
                    break;
            }
        }
    }

    public RecyclerView.Adapter getCachedAdapter() {
        return listAdapter;
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
            if (listAdapter == null) {
                Log.d(TAG, "onReturnPosters: Settings posters");
                listAdapter = listAdapterFactory.getAdapter(posters);
                ((PosterRecyclerAdapter)listAdapter).setListener(this);
                callbacks.setAdapter(listAdapter);
            }
            else {
                ((PosterRecyclerAdapter) listAdapter).addPosters(posters);
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
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (currSort != 0) {
                    currSort = 0;
                    if (popularLoader != null) {
                        Log.d(TAG, "refresh: Getting posters");
                        page = 1;
                        popularLoader.getPosters(this, PopularLoader.MOST_POPULAR_MODE, page);
                        listAdapter = null;
                        page = 0;
                    }
                }
                break;
            case 1:
                if (currSort != 1) {
                    currSort = 1;
                    if (popularLoader != null) {
                        Log.d(TAG, "refresh: Getting posters");
                        page = 1;
                        popularLoader.getPosters(this, PopularLoader.TOP_RATED_MODE, page);
                        listAdapter = null;
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface PosterActivityCallbacks extends ServiceCallback {
        void setAdapter(RecyclerView.Adapter adapter);
    }
}
