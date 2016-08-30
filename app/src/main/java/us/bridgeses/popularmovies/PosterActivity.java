package us.bridgeses.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.views.PosterViewCallback;
import us.bridgeses.popularmovies.views.PosterViewFragment;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.PosterPresenter;
import us.bridgeses.popularmovies.presenters.PosterPresenterCallback;
import us.bridgeses.popularmovies.presenters.PosterPresenterFragment;

public class PosterActivity extends Activity
        implements PosterPresenterCallback,
        PosterViewCallback {

    @SuppressWarnings("unused")
    private static final String TAG = "PosterActivity";

    private PosterViewFragment posterView;
    private PosterPresenter presenter;
    private boolean isDualPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        isDualPane = findViewById(R.id.detail_frame) != null;

        setupView();

        setupPresenter();
    }

    private void setupView() {
        posterView = PosterViewFragment.getInstance(this, R.id.poster_fragment_frame, this);

    }

    private void setupPresenter() {
        presenter = PosterPresenterFragment.getInstance(this,
                new TmdbPopularLoader(this),
                new RecyclerAdapterFactory(this),
                this);
        loadAdapter();
    }

    private void loadAdapter() {
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
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void setAdapter(PosterAdapter adapter) {
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
