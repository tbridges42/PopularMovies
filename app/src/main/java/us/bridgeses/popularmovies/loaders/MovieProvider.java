package us.bridgeses.popularmovies.loaders;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import us.bridgeses.popularmovies.loaders.implementations.DBHelper;

/**
 * Created by Tony on 8/31/2016.
 */
public class MovieProvider extends ContentProvider implements MoviesContract {

    @SuppressWarnings("unused")
    private static final String TAG = "MovieProvider";

    private ContentResolver resolver;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @IntDef({SINGLE_MOVIE, SINGLE_TRAILER, MULTIPLE_MOVIES, MULTIPLE_TRAILERS})
    public @interface ItemType {}

    public static final int SINGLE_MOVIE = 100;
    public static final int MULTIPLE_MOVIES = 101;
    public static final int SINGLE_TRAILER = 102;
    public static final int MULTIPLE_TRAILERS = 103;

    public static final String INSERT = "insert";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";

    public static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, MOVIES_TABLE, MULTIPLE_MOVIES);
        matcher.addURI(CONTENT_AUTHORITY, MOVIES_TABLE + "/#", SINGLE_MOVIE);
        matcher.addURI(CONTENT_AUTHORITY, TRAILERS_TABLE, MULTIPLE_TRAILERS);
        matcher.addURI(CONTENT_AUTHORITY, TRAILERS_TABLE + "/#", SINGLE_TRAILER);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException();
        }
        if (resolver == null) {
            resolver = context.getContentResolver();
        }
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return false;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri)) {
            case SINGLE_MOVIE:
                return MovieEntry.SINGLE_MOVIE_TYPE;
            case MULTIPLE_MOVIES:
                return MovieEntry.MULTIPLE_MOVIES_TYPE;
            case SINGLE_TRAILER:
                return TrailerEntry.SINGLE_TRAILER_TYPE;
            case MULTIPLE_TRAILERS:
                return TrailerEntry.MULTIPLE_TRAILERS_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private String getTable(@ItemType int match) {
        switch (match) {
            case SINGLE_MOVIE:
            case MULTIPLE_MOVIES:
                return MOVIES_TABLE;
            case SINGLE_TRAILER:
            case MULTIPLE_TRAILERS:
                return TRAILERS_TABLE;
            default:
                throw new UnsupportedOperationException("Unknown match: " + match);
        }
    }

    private boolean isMultiple(@ItemType int match) {
        switch(match) {
            case SINGLE_MOVIE:
            case SINGLE_TRAILER:
                return false;
            case MULTIPLE_MOVIES:
            case MULTIPLE_TRAILERS:
                return true;
            default:
                throw new UnsupportedOperationException("Unknown match: " + match);
        }
    }

    private Uri getBaseUri(@ItemType int match) {
        switch(match) {
            case SINGLE_MOVIE:
            case MULTIPLE_MOVIES:
                return MovieEntry.MOVIE_URI;
            case SINGLE_TRAILER:
            case MULTIPLE_TRAILERS:
                return TrailerEntry.TRAILER_URI;
            default:
                throw new UnsupportedOperationException("Unknown match: " + match);
        }
    }

    private Uri buildCallbackUri(Uri uri, String changeType) {
        @ItemType int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case SINGLE_MOVIE:
            case MULTIPLE_MOVIES:
                returnUri = MovieEntry.MOVIE_URI;
                break;
            case SINGLE_TRAILER:
            case MULTIPLE_TRAILERS:
                returnUri = TrailerEntry.TRAILER_URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown match: " + match);
        }
        Uri.Builder builder = returnUri.buildUpon();
        builder.appendPath(changeType);
        if (!isMultiple(match)) {
            builder.appendPath(uri.getLastPathSegment());
        }
        returnUri = builder.build();
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        initDb();
        Log.d(TAG, "query: " + uri.toString());
        @ItemType int match = uriMatcher.match(uri);
        if (!isMultiple(match)) {
            selection = BaseColumns._ID + " = ?";
            selectionArgs = new String[]{uri.getLastPathSegment()};
        }
        result = query(getTable(match), projection, selection, selectionArgs, sortOrder);
        result.setNotificationUri(resolver, uri);
        return result;
    }

    private Cursor query(String table, String[] projection,
                         String selection, String[] selectionArgs,
                         String sortOrder) {
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri result;
        long id;
        initDb();
        @ItemType int match = uriMatcher.match(uri);
        Uri baseUri = getBaseUri(match);
        id = db.insertWithOnConflict(getTable(match), null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        result = baseUri.buildUpon().appendPath(Long.toString(id)).build();
        resolver.notifyChange(buildCallbackUri(result, INSERT), null);
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        initDb();
        int rowsDeleted;
        @ItemType int match = uriMatcher.match(uri);
        if (!isMultiple(match)) {
            selection = BaseColumns._ID + " = ?";
            selectionArgs = new String[]{uri.getLastPathSegment()};
        }
        rowsDeleted = delete(getTable(match), selection, selectionArgs);

        if (rowsDeleted != 0) {
            resolver.notifyChange(buildCallbackUri(uri, DELETE), null);
        }
        return rowsDeleted;
    }

    private int delete(String tableName, String selection, String[] selectionArgs) {
        return db.delete(tableName, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        initDb();
        int rowsUpdated;
        @ItemType int match = uriMatcher.match(uri);
        if (!isMultiple(match)) {
            selection = BaseColumns._ID + " = ?";
            selectionArgs = new String[]{uri.getLastPathSegment()};
        }
        rowsUpdated = db.update(getTable(match), values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            resolver.notifyChange(buildCallbackUri(uri, UPDATE), null);
        }
        return rowsUpdated;
    }

    private void initDb() {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }
}
