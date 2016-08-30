package us.bridgeses.popularmovies;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import us.bridgeses.popularmovies.adapters.EndlessScrollListener;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.fragments.PosterViewFragment;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.PosterActivityPresenter;

public class PosterActivity extends Activity
        implements PosterActivityPresenter.PosterActivityCallbacks {

    public static final String PRESENTER_TAG = "presenter";
    public static final String VIEW_TAG = "view";

    private PosterViewFragment viewFragment;
    private PosterActivityPresenter presenter;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        presenter = null;

        viewFragment = (PosterViewFragment) getFragmentManager().findFragmentByTag(VIEW_TAG);

        presenter = (PosterActivityPresenter) getFragmentManager().findFragmentByTag(PRESENTER_TAG);

        if (presenter == null) {
            presenter = new PosterActivityPresenter();
            presenter.setRetainInstance(true);
            getFragmentManager().beginTransaction().add(presenter, PRESENTER_TAG).commit();
        }
        if (presenter.getCachedAdapter() != null) {
            setAdapter(presenter.getCachedAdapter());
        }
        presenter.setPopularLoader(new TmdbPopularLoader(this));
        presenter.setListAdapterFactory(new RecyclerAdapterFactory(this));
        presenter.setCallbacks(this);
        presenter.refresh();

        if (viewFragment == null) {
            createViewFragment();
            getFragmentManager().beginTransaction().add(viewFragment, VIEW_TAG).commit();
        }
    }

    private void createViewFragment() {
        viewFragment = new PosterViewFragment();
        GridLayoutManager layoutManager = new GridLayoutManager(this, getLayoutWidth());
        viewFragment.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getNextPage();
                return true;
            }
        });
        viewFragment.addSpinnerListener(presenter);
        viewFragment.setLayoutManager(layoutManager);
    }

    private int getLayoutWidth() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        return size.x / 342;
    }

    private void getNextPage() {
        presenter.getNextPage();
    }

    public void setPosterAdapter(RecyclerView.Adapter adapter) {
        viewFragment.setAdapter(adapter);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        viewFragment.addOnScrollListener(scrollListener);
    }

    public void loadMovieDetails(long id) {

    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        setPosterAdapter(adapter);
    }

    @Override
    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {

    }

    @Override
    public void onLocalFailure() {
        // Direct to turn on data radio
    }

    @Override
    public void onRemoteFailure() {
        // Display error
    }
}
