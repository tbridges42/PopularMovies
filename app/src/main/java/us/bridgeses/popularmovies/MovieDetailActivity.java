package us.bridgeses.popularmovies;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import us.bridgeses.popularmovies.persistence.networking.TmdbMovieLoader;
import us.bridgeses.popularmovies.persistence.ImageSaver;
import us.bridgeses.popularmovies.persistence.PersistenceHelperImpl;
import us.bridgeses.popularmovies.views.MovieViewFragment;
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

        presenter = DetailPresenterFragment.getInstance(this, movieView, new TmdbMovieLoader(this),
                new PersistenceHelperImpl(getContentResolver(), new ImageSaver() {
                    @Override
                    public Uri saveImage(Uri uri) {
                        return uri;
                    }

                    @Override
                    public void deleteImage(Uri uri) {

                    }
                }));
        presenter.loadDetail(id);
    }
}
