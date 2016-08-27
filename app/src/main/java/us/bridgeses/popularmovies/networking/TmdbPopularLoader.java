package us.bridgeses.popularmovies.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.models.Trailer;

/**
 * Created by Tony on 8/7/2016.
 */
public class TmdbPopularLoader implements PopularLoader {

    private static final String TAG = "TmdbPopularLoader";

    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String POPULAR_URL = "popular";
    public static final String TOP_RATED_URL = "top_rated";
    public static final String API_KEY = "?api_key=";
    public static final String PAGE = "&page=";
    public static final int MAX_PAGE = 1000;

    @Override
    public void getPosters(@NonNull PosterLoaderCallback callback, @SortMode int mode, int page) {
        if (page <= 0 || page > MAX_PAGE) {
            return;
        }
        switch (mode) {
            case MOST_POPULAR_MODE: new UrlPosterFetcher(callback, context).execute(
                    BASE_URL + POPULAR_URL + API_KEY +
                    context.getResources().getString(R.string.imdb_api_key)
                    + PAGE + page);
                break;
            case TOP_RATED_MODE: new UrlPosterFetcher(callback, context).execute(
                    BASE_URL + TOP_RATED_URL + API_KEY +
                            context.getResources().getString(R.string.imdb_api_key)
                    + PAGE + page);
                break;
        }
    }

    @Override
    public void getDetails(@NonNull DetailsLoaderCallback callback, long id) {
        new UrlDetailFetcher(callback, context).execute(BASE_URL + Long.toString(id) +
        API_KEY + context.getResources().getString(R.string.imdb_api_key));
    }

    @Override
    public void cancel() {

    }

    @IntDef({OK, CONNECTIVITY_ERROR, SERVER_ERROR})
    @interface UrlError {}

    public static final int OK = 0;
    public static final int CONNECTIVITY_ERROR = 1;
    public static final int SERVER_ERROR = 2;

    private Context context;

    public TmdbPopularLoader(Context context) {
        this.context = context;
    }

    private static class UrlDetailFetcher extends AsyncUrlTask<MovieDetail> {

        private DetailsLoaderCallback callback;

        public UrlDetailFetcher(DetailsLoaderCallback callback, Context context) {
            super(context);
            this.callback = callback;
        }

        public JsonReader readIt(InputStream stream) throws IOException {
            return new JsonReader(new InputStreamReader(stream, "UTF-8"));
        }

        @Override
        public MovieDetail readJson(InputStream is) throws IOException {
            JsonReader reader = readIt(is);
            String title = "";
            Uri posterPath = null;
            String releaseDate = "";
            float rating = 0f;
            String synopsis = "";
            String name;
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                Log.d(TAG, "readDetails: " + name);
                switch (name) {
                    case "original_title":
                        title = reader.nextString();
                        break;
                    case "release_date":
                        releaseDate = reader.nextString();
                        break;
                    case "poster_path":
                        posterPath = Uri.parse("http://image.tmdb.org/t/p/w342/" + reader.nextString());
                        break;
                    case "vote_average":
                        rating = (float)reader.nextDouble();
                        break;
                    case "overview":
                        synopsis = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(MovieDetail.defaultFormat.parse(releaseDate));
            }
            catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format");
            }
            return new MovieDetail(title, cal, new Poster(posterPath, title, 0L), rating, synopsis);
        }

        @Override
        public void handleSuccess(MovieDetail result) {
            callback.onReturnDetails(result);
        }

        @Override
        public void handleConnectionError() {
            callback.onLocalFailure();
        }

        @Override
        public void handleRemoteError() {
            callback.onRemoteFailure();
        }
    }

    private static class UrlPosterFetcher extends AsyncUrlTask<List<Poster>> {

        private PosterLoaderCallback callback;

        public UrlPosterFetcher(PosterLoaderCallback callback, Context context) {
            super(context);
            this.callback = callback;
        }

        public JsonReader readIt(InputStream stream) throws IOException {
            return new JsonReader(new InputStreamReader(stream, "UTF-8"));
        }

        public List<Poster> readJson(InputStream is) throws IOException{
            // Convert the InputStream into a string
            JsonReader reader = readIt(is);
            List<Poster> posters = new ArrayList<>();
            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("results")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        posters.add(readPoster(reader));
                    }
                    reader.endArray();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return posters;
        }

        public Poster readPoster(JsonReader reader) throws IOException {
            long id = -1;
            String text = "";
            Uri path = null;
            String name;
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                switch (name) {
                    case "id":
                        id = reader.nextLong();
                        break;
                    case "title":
                        text = reader.nextString();
                        break;
                    case "poster_path":
                        path = Uri.parse("http://image.tmdb.org/t/p/w342/" + reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
            return new Poster(path, text, id);
        }

        public void handleSuccess(List<Poster> results) {
            callback.onReturnPosters(results);
        }

        public void handleConnectionError() {
            callback.onLocalFailure();
        }

        public void handleRemoteError() {
            callback.onRemoteFailure();
        }
    }

    private static class UrlTrailerFetcher extends AsyncUrlTask<List<Trailer>> {

        public UrlTrailerFetcher(Context context) {
            super(context);
        }

        @Override
        public List<Trailer> readJson(InputStream is) throws IOException {
            return null;
        }

        @Override
        public void handleSuccess(List<Trailer> result) {

        }

        @Override
        public void handleConnectionError() {

        }

        @Override
        public void handleRemoteError() {

        }
    }
}
