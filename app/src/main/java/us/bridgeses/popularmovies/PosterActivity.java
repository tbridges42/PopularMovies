package us.bridgeses.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.persistence.networking.TmdbMovieLoader;
import us.bridgeses.popularmovies.presenters.DetailViewerFactory;
import us.bridgeses.popularmovies.presenters.MovieDetailViewer;
import us.bridgeses.popularmovies.presenters.PosterPresenter;
import us.bridgeses.popularmovies.presenters.PosterPresenterCallback;
import us.bridgeses.popularmovies.presenters.PosterPresenterFragment;
import us.bridgeses.popularmovies.views.PosterViewCallback;
import us.bridgeses.popularmovies.views.PosterViewFragment;

public class PosterActivity extends Activity
        implements PosterPresenterCallback,
        PosterViewCallback {

    @SuppressWarnings("unused")
    private static final String TAG = "PosterActivity";

    private PosterViewFragment posterView;
    private PosterPresenter presenter;
    private MovieDetailViewer detailViewer;
    private boolean isDualPane = false;
    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        Log.d(TAG, "onCreate: ");
        isDualPane = findViewById(R.id.detail_frame) != null;
        if (savedInstanceState != null) {
            firstRun = savedInstanceState.getBoolean("firstRun");
        }

        detailViewer = DetailViewerFactory.getViewer(isDualPane);

        setupView();

        setupPresenter();


    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putBoolean("firstRun", firstRun);
    }

    private void setupView() {
        posterView = PosterViewFragment.getInstance(this, R.id.poster_fragment_frame, this);

    }

    private void setupPresenter() {
        Log.d(TAG, "setupPresenter: ");
        presenter = PosterPresenterFragment.getInstance(this,
                new TmdbMovieLoader(this),
                new RecyclerAdapterFactory(this),
                this);
        loadAdapter();
    }

    private void loadAdapter() {
        Log.d(TAG, "loadAdapter: ");
        PosterAdapter posterAdapter = presenter.getCachedAdapter();
        if (posterAdapter != null) {
            setAdapter(posterAdapter);
        }
        else {
            presenter.refresh();
        }
    }

    @Override
    public void loadMovieDetails(long id) {
        Log.d(TAG, "loadMovieDetails: ");
        if (detailViewer != null) {
            detailViewer.load(this, R.id.detail_frame, id);
        }
    }

    @Override
    public void setAdapter(PosterAdapter adapter) {
        Log.d(TAG, "setAdapter: ");
        if (isDualPane) {
            if (firstRun) {
                loadMovieDetails(adapter.getPoster(0).getId());
                firstRun = false;
            }
            else {
                Log.d(TAG, "setAdapter: loading cached");
                detailViewer.loadCached(this);
            }
        }
        posterView.setAdapter((RecyclerView.Adapter)adapter);
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
    public void loadMore() {
        presenter.getNextPage();
    }

    @Override
    public void onSortSelected(@Poster.SortMode int position) {
        presenter.changeSort(position);
    }
}
