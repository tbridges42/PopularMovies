package us.bridgeses.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ListAdapter;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/19/2016.
 */
public class RecyclerAdapterFactory implements AdapterFactory {
    private Context context;

    public RecyclerAdapterFactory(Context context) {
        this.context = context;
    }

    public PosterAdapter getAdapter(List<Poster> posters) {
        return new PosterRecyclerAdapter(context, posters);
    }
}
