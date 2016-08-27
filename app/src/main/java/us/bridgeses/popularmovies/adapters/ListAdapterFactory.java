package us.bridgeses.popularmovies.adapters;

import android.content.Context;
import android.widget.ListAdapter;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/6/2016.
 */
public class ListAdapterFactory {

    private Context context;

    public ListAdapterFactory(Context context) {
        this.context = context;
    }

    public ListAdapter getListAdapter(List<Poster> posters) {
        return new PosterAdapter(context, posters);
    }
}
