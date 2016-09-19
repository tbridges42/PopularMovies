package us.bridgeses.popularmovies.loaders;

import java.util.Set;

import us.bridgeses.popularmovies.loaders.callbacks.DetailsLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.PosterLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.TrailerLoaderCallback;

/**
 * Created by Tony on 9/3/2016.
 */
public class FavoritesManager {

    @SuppressWarnings("unused")
    private static final String TAG = "FavoritesManager";

    private PersistenceHelper persistenceHelper;
    private Set<Long> idCache;

    public FavoritesManager(final PersistenceHelper persistenceHelper) {
        this.persistenceHelper = persistenceHelper;
    }

    private void setIdCache(Set<Long> ids) {
        idCache = ids;
    }

    public void getPosters(final PosterLoaderCallback callback) {
        callback.onReturnPosters(persistenceHelper.getAllPosters());
    }

    public void getDetails(final long id, final DetailsLoaderCallback callback) {
        callback.onReturnDetails(persistenceHelper.getMovieDetail(id));
    }

    public void getTrailers(final long id, final TrailerLoaderCallback callback) {
        callback.onReturnTrailers(persistenceHelper.getTrailers(id));
    }

    public boolean isFavorite(long id) {
        return (idCache != null) && (idCache.contains(id));
    }

    public void refreshIds() {
        setIdCache(persistenceHelper.getIds());
    }

    public Set<Long> getIds() {
        refreshIds();
        return idCache;
    }
}
