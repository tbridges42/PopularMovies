package us.bridgeses.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import us.bridgeses.dateview.DateView;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.MovieDetailPresenter;
import us.bridgeses.popularmovies.presenters.PosterActivityPresenter;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String PRESENTER_TAG = "presenter";

    private MovieDetail movieDetail;
    private MovieDetailPresenter presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent args = getIntent();
        long id = args.getLongExtra("id", 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        presenter = (MovieDetailPresenter) getFragmentManager().findFragmentByTag(PRESENTER_TAG);

        if (presenter == null) {
            presenter = new MovieDetailPresenter();
            presenter.setPopularLoader(new TmdbPopularLoader(this));
            getFragmentManager().beginTransaction().add(presenter, PRESENTER_TAG).commit();
        }
        presenter.loadDetail(id);
    }

    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
        Picasso.with(this).load(movieDetail.getPoster()
                .getImageUri()).into((ImageView)findViewById(R.id.detail_poster));
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(movieDetail.getTitle());
        DateView releaseDate = (DateView)findViewById(R.id.detail_release);
        releaseDate.setDate(movieDetail.getReleaseDate().getTime());
        ((TextView)findViewById(R.id.detail_ratings)).setText(String.format(Locale.getDefault(),
                "%.1f", movieDetail.getRating()) + "/10");
        ((TextView)findViewById(R.id.detail_synopsis)).setText(movieDetail.getSynopsis());
    }
}
