package us.bridgeses.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.persistence.FavoritesManager;
import us.bridgeses.popularmovies.persistence.implementations.DiskImageSaver;
import us.bridgeses.popularmovies.persistence.implementations.FavoriteMovieLoader;
import us.bridgeses.popularmovies.persistence.implementations.PersistenceHelperImpl;
import us.bridgeses.popularmovies.persistence.networking.TmdbMovieLoader;
import us.bridgeses.popularmovies.presenters.DetailViewerFactory;
import us.bridgeses.popularmovies.presenters.MovieDetailViewer;
import us.bridgeses.popularmovies.presenters.PosterPresenter;
import us.bridgeses.popularmovies.presenters.callbacks.FavoriteCallback;
import us.bridgeses.popularmovies.presenters.callbacks.PosterPresenterCallback;
import us.bridgeses.popularmovies.presenters.implementations.PosterPresenterFragment;
import us.bridgeses.popularmovies.views.PosterViewCallback;
import us.bridgeses.popularmovies.views.PosterViewFragment;

public class PosterActivity extends Activity
        implements PosterPresenterCallback,
        PosterViewCallback, FavoriteCallback {

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
        presenter.refresh();
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putBoolean("firstRun", firstRun);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                updateFavorite(data.getLongExtra("id", -1),
                        data.getBooleanExtra("favorite", false));
            }
            else {
                if (data != null) {
                    String error = data.getStringExtra("error");
                    switch (error) {
                        case "local":
                            onLocalFailure();
                            break;
                        case "remote":
                            onRemoteFailure();
                            break;
                    }
                }
            }
        }
    }

    private void setupView() {
        posterView = PosterViewFragment.getInstance(this, R.id.poster_fragment_frame, this);

    }

    private void setupPresenter() {
        FavoritesManager fm = new FavoritesManager(new PersistenceHelperImpl(getContentResolver(),
                new DiskImageSaver(Uri.parse(getFilesDir().toString()))));
        presenter = PosterPresenterFragment.getInstance(this,
                new FavoriteMovieLoader(fm, new TmdbMovieLoader(
                        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE),
                        getResources().getString(R.string.imdb_api_key), Locale.getDefault().getLanguage()
                ), new Handler()),
                new RecyclerAdapterFactory(this),
                this);
        loadAdapter();
        presenter.refresh();
    }

    private void loadAdapter() {
        Log.d(TAG, "loadAdapter: ");
        PosterAdapter posterAdapter = presenter.getCachedAdapter();
        if (posterAdapter != null) {
            setAdapter(posterAdapter);
        }
    }

    private void haltNetwork() {
        Toast.makeText(this, "Unable to connect to network, switching to Favorites",
                Toast.LENGTH_SHORT).show();
        posterView.setSpinnerItem(2);
    }

    @Override
    public void updateFavorite(long id, boolean favorite) {
        posterView.updateFavorite(id, favorite);
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
        if (isDualPane && adapter.getPoster(0) != null) {
            loadMovieDetails(adapter.getPoster(0).getId());
            adapter.setSelected(0);
            firstRun = false;
        }
        posterView.setAdapter((RecyclerView.Adapter)adapter);
    }

    @Override
    public void onLocalFailure() {
        haltNetwork();
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
