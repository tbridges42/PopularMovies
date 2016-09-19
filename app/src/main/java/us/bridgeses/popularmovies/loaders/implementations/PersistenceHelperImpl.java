package us.bridgeses.popularmovies.loaders.implementations;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.models.Trailer;
import us.bridgeses.popularmovies.loaders.ImageSaver;
import us.bridgeses.popularmovies.loaders.MoviesContract;
import us.bridgeses.popularmovies.loaders.PersistenceHelper;

/**
 * Created by Tony on 8/31/2016.
 */
public class PersistenceHelperImpl implements PersistenceHelper, MoviesContract {

    @SuppressWarnings("unused")
    private static final String TAG = "PersistenceHelperImpl";

    private ContentResolver resolver;
    private ImageSaver imageSaver;

    public PersistenceHelperImpl(ContentResolver resolver, ImageSaver imageSaver) {
        this.resolver = resolver;
        this.imageSaver = imageSaver;
    }

    //<editor-fold desc="PersistenceHelper methods">
    @Override
    public void saveFavorite(MovieDetail movie, List<Trailer> trailers) {
        Log.d(TAG, "saveFavorite: Adding favorite" + movie.getTitle());
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(MovieEntry.MOVIE_URI)
                .withValues(movieToValues(movie)).build());
        if (trailers != null) {
            for (Trailer trailer : trailers) {
                ops.add(ContentProviderOperation.newInsert(TrailerEntry.TRAILER_URI)
                        .withValues(trailerToValues(trailer, movie.getId())).build());
            }
        }
        try {
            resolver.applyBatch(CONTENT_AUTHORITY, ops);
        }
        catch (OperationApplicationException|RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Long> getIds() {
        Set<Long> ids = new HashSet<>();
        Cursor cursor = resolver.query(MovieEntry.MOVIE_URI, new String[] { MovieEntry._ID},
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ids.add(cursor.getLong(cursor.getColumnIndex(MovieEntry._ID)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "getIds: Found " + ids.size() + " ids");
        return ids;
    }

    @Override
    public List<Poster> getAllPosters() {
        List<Poster> posters = new ArrayList<>();
        Cursor cursor = resolver.query(MovieEntry.MOVIE_URI, MovieEntry.POSTER_PROJECTION, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                posters.add(cursorToPoster(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return posters;
    }

    @Override
    public MovieDetail getMovieDetail(long id) {
        Log.d(TAG, "getMovieDetail: " + Long.toString(id));
        Log.d(TAG, "getMovieDetail: " + MovieEntry.MOVIE_URI.buildUpon().appendPath(
                Long.toString(id)).toString());
        Cursor cursor = resolver.query(MovieEntry.MOVIE_URI.buildUpon().appendPath(
                Long.toString(id)).build(),
                MovieEntry.SUMMARY_PROJECTION, null, null, null);
        MovieDetail movie = null;
        if (cursor != null) {
            cursor.moveToFirst();
            movie = cursorToMovie(cursor);
            cursor.close();
        }
        return movie;
    }

    @Override
    public List<Trailer> getTrailers(long id) {
        List<Trailer> trailers = new ArrayList<>();
        Cursor cursor = resolver.query(TrailerEntry.TRAILER_URI, TrailerEntry.SUMMARY_PROJECTION,
                TrailerEntry.COLUMN_MOVIE_ID + " = ?", new String[] { Long.toString(id)}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trailers.add(cursorToTrailer(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return trailers;
    }

    @Override
    public void deleteFavorite(long id) {
        Cursor cursor = resolver.query(MovieEntry.MOVIE_URI,
                new String[] { MovieEntry.COLUMN_POSTER }, MovieEntry._ID + "= ?",
                new String[] {Long.toString(id)}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Uri imagePath =
                    Uri.parse(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));
            imageSaver.deleteImage(imagePath);
            cursor.close();
        }
        resolver.delete(MovieEntry.MOVIE_URI, MovieEntry._ID + "= ?",
                new String[] {Long.toString(id)});
    }
    //</editor-fold>

    private ContentValues movieToValues(MovieDetail movie) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, movie.getId());
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_RATING, movie.getRating());
        values.put(MovieEntry.COLUMN_RELEASE,
                MovieDetail.defaultFormat.format(movie.getReleaseDate().getTime()));
        values.put(MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
        values.put(MovieEntry.COLUMN_POSTER, getImageFilePath(movie.getPoster().getImageUri()));
        return values;
    }

    private ContentValues trailerToValues(Trailer trailer, long movieId) {
        ContentValues values = new ContentValues();
        values.put(TrailerEntry._ID, trailer.getId());
        values.put(TrailerEntry.COLUMN_MOVIE_ID, movieId);
        values.put(TrailerEntry.COLUMN_TITLE, trailer.getTitle());
        values.put(TrailerEntry.COLUMN_VIDEO_PATH, trailer.getThumbnail_path().toString());
        values.put(TrailerEntry.COLUMN_THUMB_PATH, getImageFilePath(trailer.getThumbnail_path()));
        return values;
    }

    private String getImageFilePath(Uri imageUri) {
        String returnUri = imageSaver.saveImage(imageUri).toString();
        Log.d(TAG, "getImageFilePath: " + returnUri);
        return returnUri;
    }

    private Poster cursorToPoster(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MovieEntry._ID));
        String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
        Log.d(TAG, "cursorToPoster: " + cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));
        Uri posterPath = Uri.parse(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));
        return new Poster(posterPath, title, id, true);
    }

    private Trailer cursorToTrailer(Cursor cursor) {
        return new Trailer(
                cursor.getLong(cursor.getColumnIndex(TrailerEntry._ID)),
                cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_TITLE)),
                Uri.parse(cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_VIDEO_PATH))),
                Uri.parse(cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_THUMB_PATH)))
        );
    }

    private MovieDetail cursorToMovie(Cursor cursor) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(MovieDetail.defaultFormat.parse(cursor.getString(
                    cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE)
            )));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        long id = cursor.getLong(cursor.getColumnIndex(MovieEntry._ID));
        String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
        float rating = cursor.getFloat(cursor.getColumnIndex(MovieEntry.COLUMN_RATING));
        String synopsis = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_SYNOPSIS));
        Uri posterPath = Uri.parse(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));
        Poster poster = new Poster(posterPath, title, id, true);
        return new MovieDetail(id, title, calendar, poster, rating, synopsis, true);
    }
}
