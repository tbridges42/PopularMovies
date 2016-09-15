package us.bridgeses.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.bridgeses.popularmovies.adapters.implementations.PosterRecyclerAdapter;
import us.bridgeses.popularmovies.models.Poster;

/**
 * A simple factory class that returns an initialized PosterAdapter that is compatible with
 * {@link RecyclerView}s
 */
public class RecyclerAdapterFactory implements AdapterFactory {
    private Context context;

    public RecyclerAdapterFactory(Context context) {
        this.context = context;
    }

    public PosterAdapter getAdapter(List<Poster> posters) {
        return new PosterRecyclerAdapter(Picasso.with(context), posters);
    }
}
