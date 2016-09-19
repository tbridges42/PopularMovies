package us.bridgeses.popularmovies.loaders.implementations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import us.bridgeses.popularmovies.loaders.MoviesContract;

/**
 * An implementatio of {@link SQLiteOpenHelper} that implements {@link MoviesContract}
 */
public class DBHelper extends SQLiteOpenHelper implements MoviesContract {

    @SuppressWarnings("unused")
    private static final String TAG = "DBHelper";

    public static final String DATABASE_NAME = "movie_database";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, packVersions(SCHEMA_VERSION, DATABASE_VERSION));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MOVIES_TABLE + " (" + MovieEntry.COLUMN_DECLARATION + ");");
        db.execSQL("CREATE TABLE " + TRAILERS_TABLE + " (" + TrailerEntry.COLUMN_DECLARATION + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Change this behavior before release
        // STOPSHIP: 9/5/2016
        db.execSQL("DROP TABLE " + MOVIES_TABLE);
        db.execSQL("DROP TABLE " + TRAILERS_TABLE);
        onCreate(db);
    }

    // By combining the schema version and database version bitwise, we ensure that if either
    // version increments, the database will be upgraded. What has been upgraded can be determined
    // by using the unpacking methods below
    private static int packVersions(int schemaVersion, int databaseVersion) {
        return schemaVersion << 16 | databaseVersion;
    }

    @SuppressWarnings("unused")
    private static int getSchemaVersion(int packedVersion) {
        return packedVersion >> 16;
    }

    @SuppressWarnings("unused")
    private static int getDatabaseVersion(int packedVersion) {
        return packedVersion & (1<<16)-1;
    }
}
