package us.bridgeses.popularmovies.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.loaders.MovieLoaderFactory;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.presenters.DetailViewerFactory;
import us.bridgeses.popularmovies.presenters.MovieDetailViewer;
import us.bridgeses.popularmovies.presenters.PosterPresenter;
import us.bridgeses.popularmovies.presenters.PosterPresenterFactory;
import us.bridgeses.popularmovies.presenters.callbacks.FavoriteCallback;
import us.bridgeses.popularmovies.presenters.callbacks.PosterPresenterCallback;
import us.bridgeses.popularmovies.views.PosterViewCallback;
import us.bridgeses.popularmovies.views.PosterViewFragment;

public class PopularMoviesActivity extends Activity
        implements PosterPresenterCallback,
        PosterViewCallback, FavoriteCallback {

    @SuppressWarnings("unused")
    private static final String TAG = "PopularMoviesActivity";

    private PosterViewFragment posterView;
    private PosterPresenter presenter;
    private MovieDetailViewer detailViewer;
    private boolean isDualPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        isDualPane = findViewById(R.id.detail_frame) != null;
        detailViewer = DetailViewerFactory.getViewer(isDualPane);

        setupView();
        setupPresenter();
        loadCachedAdapter();

        presenter.refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            handleResult(resultCode, data);
        }
    }

    private void handleResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            updateFavorite(data.getLongExtra("id", -1),
                    data.getBooleanExtra("favorite", false));
        }
        else {
            checkForErrorResult(data);
        }
    }

    private void checkForErrorResult(Intent data) {
        if (data != null) {
            String error = data.getStringExtra("error");
            handleErrorResult(error);
        }
    }

    private void handleErrorResult(String error) {
        switch (error) {
            case "local":
                onLocalFailure();
                break;
            case "remote":
                onRemoteFailure();
                break;
        }
    }

    private void setupView() {
        posterView = PosterViewFragment.getInstance(this, R.id.poster_fragment_frame, this);
    }

    private void setupPresenter() {
        presenter = PosterPresenterFactory.getInstance(
                this,
                MovieLoaderFactory.getInstance(this),
                new RecyclerAdapterFactory(this),
                this);
    }

    private void loadCachedAdapter() {
        PosterAdapter posterAdapter = presenter.getCachedAdapter();
        if (posterAdapter != null) {
            setAdapter(posterAdapter);
        }
    }

    private void handleNoNetwork() {
        Toast.makeText(this, R.string.no_network,
                Toast.LENGTH_SHORT).show();
        posterView.setSpinnerItem(2);
    }

    private void handleServerError() {
        Toast.makeText(this, R.string.server_error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFavorite(long id, boolean favorite) {
        posterView.updateFavorite(id, favorite);
    }

    @Override
    public void loadMovieDetails(long id) {
        if (detailViewer != null) {
            detailViewer.load(this, R.id.detail_frame, id);
        }
    }

    @Override
    public void setAdapter(PosterAdapter adapter) {
        if (isDualPane && adapter.getPoster(0) != null) {
            loadMovieDetails(adapter.getPoster(0).getId());
            adapter.setSelected(0);
        }
        posterView.setAdapter((RecyclerView.Adapter)adapter);
    }

    @Override
    public void onLocalFailure() {
        handleNoNetwork();
    }

    @Override
    public void onRemoteFailure() {
        handleServerError();
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
