package us.bridgeses.popularmovies.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;

import java.util.Locale;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.loaders.implementations.DiskImageSaver;
import us.bridgeses.popularmovies.loaders.implementations.FavoriteMovieLoader;
import us.bridgeses.popularmovies.loaders.implementations.PersistenceHelperImpl;
import us.bridgeses.popularmovies.loaders.networking.TmdbMovieLoader;

/**
 * Created by Tony on 9/18/2016.
 */
public class MovieLoaderFactory {

    public static MovieLoader getInstance(Context context) {
        FavoritesManager fm = new FavoritesManager(
                new PersistenceHelperImpl(context.getContentResolver(),
                        new DiskImageSaver(Uri.parse(context.getFilesDir().toString()))));

        TmdbMovieLoader movieLoader = new TmdbMovieLoader(
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE),
                context.getResources().getString(R.string.imdb_api_key),
                Locale.getDefault().getLanguage());

        return new FavoriteMovieLoader(
                fm,
                movieLoader,
                new Handler());
    }
}
