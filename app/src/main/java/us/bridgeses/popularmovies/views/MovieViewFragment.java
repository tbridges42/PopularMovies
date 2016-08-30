package us.bridgeses.popularmovies.views;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private static final String TAG = "MovieViewFragment";
    private static final String MOVIE_DETAIL = "MOVIE_DETAIL";

    private RecyclerView trailerView;
    private ImageView poster;
    private DateView releaseDate;
    private TextView ratings;
    private TextView synopsis;
    private MovieDetail movieDetail;
    private ShareActionProvider shareActionProvider;

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
        }
        poster = (ImageView)view.findViewById(R.id.detail_poster);
        releaseDate = (DateView)view.findViewById(R.id.detail_release);
        ratings = (TextView)view.findViewById(R.id.detail_ratings);
        synopsis = (TextView)view.findViewById(R.id.detail_synopsis);
        if (savedInstanceState != null) {
            setMovieDetail((MovieDetail) savedInstanceState.getParcelable(MOVIE_DETAIL));
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Test");
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
        Picasso.with(getActivity()).load(movieDetail.getPoster()
                .getImageUri()).into(poster);
        getActivity().setTitle(movieDetail.getTitle());
        releaseDate.setDate(movieDetail.getReleaseDate().getTime());
        ratings.setText(String.format(getResources().getString(R.string.rating),
                movieDetail.getRating()));
        synopsis.setText(movieDetail.getSynopsis());
    }

    @Override
    public void setAdapter(TrailerAdapter trailerAdapter) {
        trailerView.setAdapter((RecyclerView.Adapter) trailerAdapter);
    }

    @Override
    public void setShareIntent(Intent intent) {
        shareActionProvider.setShareIntent(intent);
    }
}
