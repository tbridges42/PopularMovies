package us.bridgeses.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import us.bridgeses.dateview.DateView;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.MovieDetailPresenter;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String PRESENTER_TAG = "presenter";
    private static final String TAG = "MovieDetailActivity";

    private MovieDetail movieDetail;
    private MovieDetailPresenter presenter;
    private ShareActionProvider shareActionProvider;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent args = getIntent();
        long id = args.getLongExtra("id", 0);
        presenter = (MovieDetailPresenter) getFragmentManager().findFragmentByTag(PRESENTER_TAG);

        if (presenter == null) {
            presenter = new MovieDetailPresenter();
            presenter.setPopularLoader(new TmdbPopularLoader(this));
            getFragmentManager().beginTransaction().add(presenter, PRESENTER_TAG).commit();
        }
        presenter.loadDetail(id);

        RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.trailers));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_share);

            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Test");
            shareActionProvider.setShareIntent(shareIntent);

        return true;
    }

    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
        Picasso.with(this).load(movieDetail.getPoster()
                .getImageUri()).into((ImageView)findViewById(R.id.detail_poster));
        setTitle(movieDetail.getTitle());
        DateView releaseDate = (DateView)findViewById(R.id.detail_release);
        releaseDate.setDate(movieDetail.getReleaseDate().getTime());
        ((TextView)findViewById(R.id.detail_ratings)).setText(String.format(Locale.getDefault(),
                "%.1f", movieDetail.getRating()) + "/10");
        ((TextView)findViewById(R.id.detail_synopsis)).setText(movieDetail.getSynopsis());
    }

    public void setAdapter(RecyclerView.Adapter trailerAdapter) {
        Log.d(TAG, "setAdapter: setting adapter");
        ((RecyclerView)findViewById(R.id.trailers)).setAdapter(trailerAdapter);
    }

    public void setShareIntent(Intent intent) {
        shareActionProvider.setShareIntent(intent);
    }
}
