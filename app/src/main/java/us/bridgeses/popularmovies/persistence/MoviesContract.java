package us.bridgeses.popularmovies.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for persistence of movie information.
 * Should be implemented by and classes involved in the persistence of movie information.
 */
public interface MoviesContract {

    // Increment this after every change
    int SCHEMA_VERSION = 2;

    String CONTENT_AUTHORITY = "us.bridgeses.popularmovies.provider";
    Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    String MOVIES_TABLE = "movies_table";

    String TRAILERS_TABLE = "trailers_table";

    interface MovieEntry extends BaseColumns {

        Uri MOVIE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(MOVIES_TABLE).build();

        String SINGLE_MOVIE_TYPE =
                "vnd.android.cursor.item/"
                + CONTENT_AUTHORITY
                + "/"
                + MOVIES_TABLE;

        String MULTIPLE_MOVIES_TYPE =
                "vnd.android.cursor.dir/"
                + CONTENT_AUTHORITY
                + "/"
                + MOVIES_TABLE;

        String COLUMN_TITLE = "title";
        String TYPE_TITLE = " TEXT";
        String COLUMN_RELEASE = "release";
        String TYPE_RELEASE_TYPE = " TEXT";
        String COLUMN_POSTER = "poster";
        String TYPE_POSTER = " TEXT";
        String COLUMN_RATING = "rating";
        String TYPE_RATING = " REAL";
        String COLUMN_SYNOPSIS = "synopsis";
        String TYPE_SYNOPSIS = " TEXT";

        String COLUMN_DECLARATION = _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TITLE + TYPE_TITLE + ", "
                + COLUMN_RELEASE + TYPE_RELEASE_TYPE + ", "
                + COLUMN_POSTER + TYPE_POSTER + ", "
                + COLUMN_RATING + TYPE_RATING + ", "
                + COLUMN_SYNOPSIS + TYPE_SYNOPSIS;

        String[] SUMMARY_PROJECTION = {
                _ID,
                COLUMN_TITLE,
                COLUMN_RELEASE,
                COLUMN_POSTER,
                COLUMN_RATING,
                COLUMN_SYNOPSIS
        };

        String[] POSTER_PROJECTION = {
                _ID,
                COLUMN_TITLE,
                COLUMN_POSTER
        };
    }

    interface TrailerEntry extends BaseColumns {

        Uri TRAILER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TRAILERS_TABLE).build();

        String SINGLE_TRAILER_TYPE =
                "vnd.android.cursor.item/"
                + CONTENT_AUTHORITY
                + "/"
                + TRAILERS_TABLE;

        String MULTIPLE_TRAILERS_TYPE =
                "vnd.android.cursor.dir/"
                + CONTENT_AUTHORITY
                + "/"
                + TRAILERS_TABLE;

        String COLUMN_TITLE = "title";
        String TYPE_TITLE = " TEXT";
        String COLUMN_VIDEO_PATH = "video";
        String TYPE_VIDEO_PATH = " TEXT";
        String COLUMN_THUMB_PATH = "thumb";
        String TYPE_THUMB_PATH = " TEXT";
        String COLUMN_MOVIE_ID = "movie";
        String TYPE_MOVIE_ID = " INTEGER, FOREIGN KEY("
                + COLUMN_MOVIE_ID
                +") REFERENCES "
                + MOVIES_TABLE
                + "(" + _ID + ")"
                + " ON DELETE CASCADE";

        String COLUMN_DECLARATION = _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TITLE + TYPE_TITLE + ", "
                + COLUMN_VIDEO_PATH + TYPE_VIDEO_PATH + ", "
                + COLUMN_THUMB_PATH + TYPE_THUMB_PATH +", "
                + COLUMN_MOVIE_ID + TYPE_MOVIE_ID;

        String[] SUMMARY_PROJECTION = {
                _ID,
                COLUMN_TITLE,
                COLUMN_VIDEO_PATH,
                COLUMN_THUMB_PATH
        };
    }
}
