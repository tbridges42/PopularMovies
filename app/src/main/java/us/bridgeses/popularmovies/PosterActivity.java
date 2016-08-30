package us.bridgeses.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import us.bridgeses.popularmovies.adapters.EndlessScrollListener;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.fragments.PosterViewFragment;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.PosterPresenter;
import us.bridgeses.popularmovies.presenters.PosterPresenterFragment;

public class PosterActivity extends Activity
        implements PosterPresenterFragment.PosterActivityCallbacks, Spinner.OnItemSelectedListener {

    public static final String VIEW_TAG = "view";

    private PosterViewFragment viewFragment;
    private PosterPresenter presenter;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        viewFragment = (PosterViewFragment) getFragmentManager().findFragmentByTag(VIEW_TAG);

        presenter = PosterPresenterFragment.getInstance(this);
        setAdapter(presenter.getCachedAdapter());
        presenter.setPopularLoader(new TmdbPopularLoader(this));
        presenter.setAdapterFactory(new RecyclerAdapterFactory(this));
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
                presenter.getNextPage();
                return true;
            }
        });
        viewFragment.addSpinnerListener(this);
        viewFragment.setLayoutManager(layoutManager);
    }

    private int getLayoutWidth() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        return size.x / 342;
    }

    public void loadMovieDetails(long id) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void setAdapter(PosterAdapter adapter) {
        viewFragment.setAdapter(adapter);
    }

    @Override
    public void onLocalFailure() {
        // Direct to turn on data radio
    }

    @Override
    public void onRemoteFailure() {
        // Display error
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        presenter.changeSort(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
