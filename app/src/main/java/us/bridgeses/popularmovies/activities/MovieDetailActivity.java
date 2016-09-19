package us.bridgeses.popularmovies.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.presenters.callbacks.FavoriteCallback;
import us.bridgeses.popularmovies.presenters.implementations.DetailFragmentWrapper;

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

    @Override
    public void onLocalFailure() {
        Intent intent = new Intent();
        intent.putExtra("error", "local");
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onRemoteFailure() {
        Intent intent = new Intent();
        intent.putExtra("error", "remote");
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
