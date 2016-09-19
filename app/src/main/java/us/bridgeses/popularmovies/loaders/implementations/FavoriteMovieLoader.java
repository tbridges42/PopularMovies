package us.bridgeses.popularmovies.loaders.implementations;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Set;

import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.loaders.FavoritesManager;
import us.bridgeses.popularmovies.loaders.MovieLoader;
import us.bridgeses.popularmovies.loaders.callbacks.DetailsLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.PosterLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.TrailerLoaderCallback;
import us.bridgeses.popularmovies.loaders.networking.TmdbMovieLoader;

/**
 * Created by Tony on 9/5/2016.
 */
public class FavoriteMovieLoader implements MovieLoader {

    @SuppressWarnings("unused")
    private static final String TAG = "FavoriteMovieLoader";

    private final FavoritesManager favoritesManager;
    private final TmdbMovieLoader remoteLoader;
    private Handler handler;

    public FavoriteMovieLoader(FavoritesManager favoritesManager, TmdbMovieLoader remoteLoader,
                               Handler handler) {
        this.favoritesManager = favoritesManager;
        this.remoteLoader = remoteLoader;
        this.handler = handler;
    }

    @Override
    public void getPosters(@NonNull final PosterLoaderCallback callback,
                           @Poster.SortMode final int mode, final int page) {
        Log.d(TAG, "getPosters: getting posters");
        final PosterLoaderCallback favoriteCallback = new FavoritePosterLoaderCallback(callback);
        switch (mode) {
            case Poster.FAVORITED_MODE:
                Log.d(TAG, "getPosters: getting favorite posters");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        favoritesManager.getPosters(favoriteCallback);
                    }
                });
                break;
            case Poster.MOST_POPULAR_MODE:
            case Poster.TOP_RATED_MODE:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteLoader.getPosters(favoriteCallback, mode, page);
                    }
                });
                break;
        }
    }

    @Override
    public void getDetails(@NonNull final DetailsLoaderCallback callback, final long id) {
        Log.d(TAG, "getDetails: Getting details");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Set<Long> ids = favoritesManager.getIds();
                if (ids != null && ids.contains(id)) {
                    Log.d(TAG, "run: Getting favorites details");
                    favoritesManager.getDetails(id, callback);
                }
                else {
                    remoteLoader.getDetails(callback, id);
                }
            }
        });

    }

    @Override
    public void getTrailers(@NonNull final TrailerLoaderCallback callback, final long id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Set<Long> ids = favoritesManager.getIds();
                if (ids != null && ids.contains(id)) {
                    favoritesManager.getTrailers(id, callback);
                } else {
                    remoteLoader.getTrailers(callback, id);
                }
            }
        });
    }

    @Override
    public void cancel() {
        remoteLoader.cancel();
    }

    private class FavoritePosterLoaderCallback implements PosterLoaderCallback {

        private PosterLoaderCallback callback;

        private FavoritePosterLoaderCallback(PosterLoaderCallback callback) {
            this.callback = callback;
        }


        @Override
        public void onReturnPosters(List<Poster> posters) {
            Set<Long> ids = favoritesManager.getIds();
            for (Poster poster : posters) {
                if (ids.contains(poster.getId())) {
                    poster.setFavorite(true);
                }
            }
            callback.onReturnPosters(posters);
        }

        @Override
        public void onLocalFailure() {
            callback.onLocalFailure();
        }

        @Override
        public void onRemoteFailure() {
            callback.onRemoteFailure();
        }
    }
}
