package us.bridgeses.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import us.bridgeses.popularmovies.views.MovieViewFragment;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.DetailPresenterFragment;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String PRESENTER_TAG = "presenter";
    @SuppressWarnings("unused")
    private static final String TAG = "MovieDetailActivity";

    private DetailPresenterFragment presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent args = getIntent();
        long id = args.getLongExtra("id", 0);
        presenter = (DetailPresenterFragment) getFragmentManager().findFragmentByTag(PRESENTER_TAG);

        if (presenter == null) {
            presenter = new DetailPresenterFragment();
            presenter.setPopularLoader(new TmdbPopularLoader(this));
            getFragmentManager().beginTransaction().add(presenter, PRESENTER_TAG).commit();
        }
        presenter.loadDetail(id);

        MovieViewFragment movieView = new MovieViewFragment();

        presenter.setCallback(movieView);
        getFragmentManager().beginTransaction().add(R.id.detail_parent, movieView, TAG).commit();
    }
}
