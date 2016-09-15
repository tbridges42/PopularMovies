package us.bridgeses.popularmovies.adapters;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;

/**
 * An interface for a factory that returns a Poster Adapter
 */
public interface AdapterFactory {

    /**
     * Return an initialized PosterAdapter
     */
    PosterAdapter getAdapter(List<Poster> posters);
}
