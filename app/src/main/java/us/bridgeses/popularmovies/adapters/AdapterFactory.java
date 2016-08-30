package us.bridgeses.popularmovies.adapters;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by tbrid on 8/29/2016.
 */
public interface AdapterFactory {

    PosterAdapter getAdapter(List<Poster> posters);
}
