package us.bridgeses.popularmovies.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import us.bridgeses.dateview.DateView;
import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.adapters.TrailerAdapter;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.presenters.DetailPresenterCallback;

/**
 * Created by tbrid on 8/30/2016.
 */
public class MovieViewFragment extends Fragment implements MovieView, DetailPresenterCallback {

    @SuppressWarnings("unused")
    private static final String TAG = "MovieViewFragment";
    private static final String MOVIE_DETAIL = "MOVIE_DETAIL";

    private RecyclerView trailerView;
    private CheckBox favorite;
    private ImageView poster;
    private DateView releaseDate;
    private TextView ratings;
    private TextView synopsis;
    private MovieDetail movieDetail;
    private ShareActionProvider shareActionProvider;
    private TrailerAdapter cachedAdapter;
    private Intent cachedIntent;

    public static MovieViewFragment getInstance(Activity activity, @IdRes int res) {
        FragmentManager fm = activity.getFragmentManager();
        MovieViewFragment fragment = (MovieViewFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new MovieViewFragment();
            activity.getFragmentManager().beginTransaction().add(res, fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, parent, false);
        trailerView = (RecyclerView) view.findViewById(R.id.trailers);
        if (trailerView != null) {
            trailerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            if (cachedAdapter != null) {
                trailerView.setAdapter((RecyclerView.Adapter) cachedAdapter);
            }
        }
        favorite = (CheckBox)view.findViewById(R.id.detail_favorite);
        poster = (ImageView)view.findViewById(R.id.detail_poster);
        releaseDate = (DateView)view.findViewById(R.id.detail_release);
        ratings = (TextView)view.findViewById(R.id.detail_ratings);
        synopsis = (TextView)view.findViewById(R.id.detail_synopsis);
        if (movieDetail != null) {
            setMovieDetail(movieDetail);
        }
        else {
            if (savedInstanceState != null) {
                setMovieDetail((MovieDetail) savedInstanceState.getParcelable(MOVIE_DETAIL));
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(MOVIE_DETAIL, movieDetail);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_share);

        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        if (cachedIntent == null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Test");
            shareActionProvider.setShareIntent(shareIntent);
        }
        else {
            shareActionProvider.setShareIntent(cachedIntent);
        }
    }

    @Override
    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
        if (poster != null) {
            Picasso.with(getActivity()).load(movieDetail.getPoster()
                    .getImageUri()).into(poster);
            getActivity().setTitle(movieDetail.getTitle());
            releaseDate.setDate(movieDetail.getReleaseDate().getTime());
            ratings.setText(String.format(getResources().getString(R.string.rating),
                    movieDetail.getRating()));
            synopsis.setText(movieDetail.getSynopsis());
            favorite.setChecked(movieDetail.isFavorite());
        }
    }

    @Override
    public void setAdapter(TrailerAdapter trailerAdapter) {
        if (trailerView != null) {
            trailerView.setAdapter((RecyclerView.Adapter) trailerAdapter);
        }
        else {
            cachedAdapter = trailerAdapter;
        }
    }

    @Override
    public void setShareIntent(Intent intent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intent);
        }
        else {
            cachedIntent = intent;
        }
    }

    @Override
    public void setFavoriteListener(CheckBox.OnCheckedChangeListener listener) {
        if (favorite != null) {
            favorite.setOnCheckedChangeListener(listener);
        }
    }
}
