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
import us.bridgeses.popularmovies.fragments.MovieViewFragment;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.networking.TmdbPopularLoader;
import us.bridgeses.popularmovies.presenters.MovieDetailPresenter;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String PRESENTER_TAG = "presenter";
    @SuppressWarnings("unused")
    private static final String TAG = "MovieDetailActivity";

    private MovieDetailPresenter presenter;


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

        getFragmentManager().beginTransaction().add(R.id.detail_parent, new MovieViewFragment(), TAG).commit();
    }
}
