package us.bridgeses.popularmovies;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import us.bridgeses.popularmovies.views.MovieViewFragment;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.DetailPresenterFragment;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = "MovieDetailActivity";

    private DetailPresenterFragment presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent args = getIntent();
        long id = args.getLongExtra("id", 0);


        MovieViewFragment movieView = MovieViewFragment.getInstance(this, R.id.detail_parent);

        presenter = DetailPresenterFragment.getInstance(this, movieView, new TmdbPopularLoader(this));
        presenter.loadDetail(id);
    }
}
