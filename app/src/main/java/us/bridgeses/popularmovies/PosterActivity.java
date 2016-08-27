package us.bridgeses.popularmovies;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Spinner;

import us.bridgeses.popularmovies.adapters.EndlessScrollListener;
import us.bridgeses.popularmovies.adapters.RecyclerAdapterFactory;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.PosterActivityPresenter;

public class PosterActivity extends Activity
        implements PosterActivityPresenter.PosterActivityCallbacks {

    public static final String PRESENTER_TAG = "presenter";

    private RecyclerView posterView;
    private PosterActivityPresenter presenter;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        presenter = null;

        posterView = (RecyclerView) findViewById(R.id.poster_list);
        posterView.setHasFixedSize(true);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x / 342;
        GridLayoutManager layoutManager = new GridLayoutManager(this, width);
        posterView.setLayoutManager(layoutManager);


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
        posterView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getNextPage();
                return true;
            }
        });
        Spinner spinner = (Spinner)findViewById(R.id.sort_mode);
        spinner.setOnItemSelectedListener(presenter);
    }

    public void getNextPage() {
        presenter.getNextPage();
    }

    public void setPosterAdapter(RecyclerView.Adapter adapter) {
        posterView.setAdapter(adapter);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        posterView.addOnScrollListener(scrollListener);
    }

    public void loadMovieDetails(long id) {

    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        setPosterAdapter(adapter);
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
