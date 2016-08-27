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

    private static class UrlDetailFetcher extends AsyncTask<String, Void, MovieDetail> {

        @UrlError int urlError = OK;
        private DetailsLoaderCallback callback;
        private Context context;

        public UrlDetailFetcher(DetailsLoaderCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected MovieDetail doInBackground(String... params) {
            Log.d("url", params[0]);
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("response", Integer.toString(response));
                    if (response == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();
                        return readDetails(is);
                    }
                }
                catch (IOException e) {
                    urlError = SERVER_ERROR;
                }
            } else {
                urlError = CONNECTIVITY_ERROR;
            }

            return null;
        }

        @Override
        protected void onPostExecute(MovieDetail results) {
            Log.d(TAG, "onPostExecute: Returning with posters");
            switch (urlError) {
                case OK:
                    if (results != null) {
                        callback.onReturnDetails(results);
                    }
                    else {
                        Log.d(TAG, "onPostExecute: No results returned");
                    }
                    break;
                case CONNECTIVITY_ERROR:
                    callback.onLocalFailure();
                    break;
                case SERVER_ERROR:
                    callback.onRemoteFailure();
                    break;
            }
        }

        public JsonReader readIt(InputStream stream) throws IOException {
            return new JsonReader(new InputStreamReader(stream, "UTF-8"));
        }

        public MovieDetail readDetails(InputStream is) throws IOException {
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
    }

    private static class UrlPosterFetcher extends AsyncTask<String, Void, List<Poster>> {

        @UrlError int urlError = OK;
        private PosterLoaderCallback callback;
        private Context context;

        public UrlPosterFetcher(PosterLoaderCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected List<Poster> doInBackground(String... params) {
            Log.d("url", params[0]);
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("response", Integer.toString(response));
                    if (response == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();
                        return readPosters(is);
                    }
                }
                catch (IOException e) {
                    urlError = SERVER_ERROR;
                }
            } else {
                urlError = CONNECTIVITY_ERROR;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Poster> results) {
            Log.d(TAG, "onPostExecute: Returning with posters");
            switch (urlError) {
                case OK:
                    if (results != null && results.size() >= 0) {
                        callback.onReturnPosters(results);
                    }
                    else {
                        Log.d(TAG, "onPostExecute: No results returned");
                    }
                    break;
                case CONNECTIVITY_ERROR:
                    callback.onLocalFailure();
                    break;
                case SERVER_ERROR:
                    callback.onRemoteFailure();
                    break;
            }
        }

        public JsonReader readIt(InputStream stream) throws IOException {
            return new JsonReader(new InputStreamReader(stream, "UTF-8"));
        }

        public List<Poster> readPosters(InputStream is) throws IOException{
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
    }
}
