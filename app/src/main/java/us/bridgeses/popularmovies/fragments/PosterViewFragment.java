package us.bridgeses.popularmovies.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Spinner;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.adapters.EndlessScrollListener;
import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/29/2016.
 */
public class PosterViewFragment extends Fragment implements Spinner.OnItemSelectedListener {

    @SuppressWarnings("unused")
    private static final String TAG = "PosterViewFragment";

    private RecyclerView posterView;
    private PosterViewCallback callback;

    public static PosterViewFragment getInstance(Activity activity, @IdRes int res,
                                                 PosterViewCallback callback) {
        PosterViewFragment fragment = new PosterViewFragment();
        fragment.setCallback(callback);
        activity.getFragmentManager().beginTransaction().add(res, fragment, TAG).commit();
        return fragment;
    }

    public void setCallback(PosterViewCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_poster, parent, false);
        posterView = (RecyclerView) view.findViewById(R.id.poster_list);
        posterView.setHasFixedSize(true);
        Spinner spinner = (Spinner) view.findViewById(R.id.sort_mode);
        spinner.setOnItemSelectedListener(this);
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setUpPosterView(view);
            }
        });
        return view;
    }

    private void setUpPosterView(View view) {
        int width = view.getWidth() / Poster.THUMBNAIL_WIDTH;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), width);
        posterView.setLayoutManager(layoutManager);
        posterView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadMore();
                return false;
            }
        });
    }

    private void loadMore() {
        if (callback != null) {
            callback.loadMore();
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        posterView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (callback != null) {
            callback.onSortSelected(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
