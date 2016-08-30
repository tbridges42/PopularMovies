package us.bridgeses.popularmovies.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Spinner;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.presenters.PosterActivityPresenter;

/**
 * Created by Tony on 8/29/2016.
 */
public class PosterViewFragment extends Fragment
        implements PosterActivityPresenter.PosterActivityCallbacks{

    private RecyclerView posterView;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_poster, parent);
        posterView = (RecyclerView) view.findViewById(R.id.poster_list);
        posterView.setHasFixedSize(true);
        spinner = (Spinner) view.findViewById(R.id.sort_mode);
        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setPosterWidth(view);
                vto.removeOnGlobalLayoutListener(this);
            }
        });
        return view;
    }

    private void setPosterWidth(View view) {
        int width = view.getWidth() / 342;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), width);
        posterView.setLayoutManager(layoutManager);
    }

    public void addSpinnerListener(AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }

    @Override
    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        posterView.addOnScrollListener(listener);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        posterView.setAdapter(adapter);
    }

    @Override
    public void onLocalFailure() {
        // TODO: Handle a local connection issue
    }

    @Override
    public void onRemoteFailure() {
        // TODO: Handle a remote connection issue
    }

    public void setLayoutManager(GridLayoutManager layoutManager) {
        posterView.setLayoutManager(layoutManager);
    }
}
