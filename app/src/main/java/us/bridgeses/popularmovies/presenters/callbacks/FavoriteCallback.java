package us.bridgeses.popularmovies.presenters.callbacks;

import us.bridgeses.popularmovies.loaders.networking.ServiceCallback;

/**
 * Created by Tony on 9/5/2016.
 */
public interface FavoriteCallback extends ServiceCallback {
    void updateFavorite(long id, boolean favorite);
}
