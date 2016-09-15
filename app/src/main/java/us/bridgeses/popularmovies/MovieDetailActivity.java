package us.bridgeses.popularmovies;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import us.bridgeses.popularmovies.persistence.implementations.FavoriteMovieLoader;
import us.bridgeses.popularmovies.persistence.FavoritesManager;
import us.bridgeses.popularmovies.persistence.PersistenceHelper;
import us.bridgeses.popularmovies.persistence.networking.TmdbMovieLoader;
import us.bridgeses.popularmovies.persistence.ImageSaver;
import us.bridgeses.popularmovies.persistence.implementations.PersistenceHelperImpl;
import us.bridgeses.popularmovies.presenters.callbacks.FavoriteCallback;
import us.bridgeses.popularmovies.presenters.implementations.DetailFragmentWrapper;
import us.bridgeses.popularmovies.views.MovieViewFragment;
import us.bridgeses.popularmovies.presenters.implementations.DetailPresenterFragment;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends Activity implements FavoriteCallback {

    @SuppressWarnings("unused")
    private static final String TAG = "MovieDetailActivity";

    private DetailFragmentWrapper presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent args = getIntent();
        long id = args.getLongExtra("id", 0);

        presenter = new DetailFragmentWrapper();
        presenter.load(this, R.id.detail_parent, id);
    }

    @Override
    public void updateFavorite(long id, boolean favorite) {
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("favorite", favorite);
        setResult(RESULT_OK, intent);
    }
}
