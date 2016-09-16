package us.bridgeses.popularmovies.adapters;

import com.android.internal.util.Predicate;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;

/**
 * An interface that describes a view adapter that displays {@link Poster}s
 */
public interface PosterAdapter {

    /**
     * The adapter should use the listener set here as a callback when posters are clicked.
     */
    void setListener(PosterClickListener listener);

    /**
     * The adapter should be able to dynamically grow as new data is added
     * @param posters: additional data to be added to the adapter
     */
    void addPosters(List<Poster> posters);

    /**
     * Notify the adapter that a specific poster has changed favorite status
     */
    void updateFavorite(long id, boolean favorite);

    /**
     * Get the poster at position
     */
    Poster getPoster(int position);

    void setSelected(int selected);

    void setFilter(Predicate<Poster> filter);
}
