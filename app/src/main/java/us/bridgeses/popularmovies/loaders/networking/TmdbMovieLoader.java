package us.bridgeses.popularmovies.loaders.networking;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.models.Trailer;
import us.bridgeses.popularmovies.loaders.callbacks.DetailsLoaderCallback;
import us.bridgeses.popularmovies.loaders.MovieLoader;
import us.bridgeses.popularmovies.loaders.callbacks.PosterLoaderCallback;
import us.bridgeses.popularmovies.loaders.callbacks.TrailerLoaderCallback;

import static us.bridgeses.popularmovies.models.Poster.FAVORITED_MODE;
import static us.bridgeses.popularmovies.models.Poster.MOST_POPULAR_MODE;
import static us.bridgeses.popularmovies.models.Poster.TOP_RATED_MODE;

/**
 * An implementation of {@link MovieLoader} that retrieves data from TMDB
 */
public class TmdbMovieLoader implements MovieLoader {

    private static final String TAG = "TmdbMovieLoader";

    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String POPULAR_URL = "popular";
    public static final String TOP_RATED_URL = "top_rated";
    public static final String API_KEY = "?api_key=";
    public static final String PAGE = "&page=";
    public static final String VIDEOS = "/videos";
    public static final String LANGUAGE = "&language=";
    public static final int MAX_PAGE = 1000;

    private String apiKey;
    private final String lang;
    private ConnectivityManager connMgr;
    private UrlDetailFetcher activeDetailFetcher;
    private UrlPosterFetcher activePosterFetcher;
    private UrlTrailerFetcher activeTrailerFetcher;

    public TmdbMovieLoader(ConnectivityManager connMgr, String apiKey) {
        this(connMgr, apiKey, "en");
    }

    public TmdbMovieLoader(ConnectivityManager connMgr, String apiKey, String lang) {
        this.connMgr = connMgr;
        this.apiKey = apiKey;
        this.lang = lang;
    }

    @Override
    public void getPosters(@NonNull PosterLoaderCallback callback,
                           @Poster.SortMode int mode, int page) {
        if (page <= 0 || page > MAX_PAGE) {
            return;
        }
        if (activePosterFetcher != null) {
            activePosterFetcher.cancel(true);
        }
        AsyncUrlTask.DoneListener doneListener = new AsyncUrlTask.DoneListener() {
            @Override
            public void done() {
                activePosterFetcher = null;
            }
        };
        switch (mode) {
            case MOST_POPULAR_MODE: 
                activePosterFetcher = new UrlPosterFetcher(callback, connMgr, doneListener);
                activePosterFetcher.execute(BASE_URL + POPULAR_URL + API_KEY + apiKey + PAGE + page
                + LANGUAGE + lang);
                break;
            case TOP_RATED_MODE: 
                activePosterFetcher = new UrlPosterFetcher(callback, connMgr, doneListener);
                activePosterFetcher.execute(BASE_URL + TOP_RATED_URL + API_KEY +apiKey + PAGE + page
                        + LANGUAGE + lang);
                break;
            case FAVORITED_MODE:
                // Shouldn't be here
                break;
        }
    }

    @Override
    public void getDetails(@NonNull DetailsLoaderCallback callback, long id) {
        AsyncUrlTask.DoneListener doneListener = new AsyncUrlTask.DoneListener() {
            @Override
            public void done() {
                activeDetailFetcher = null;
            }
        };
        activeDetailFetcher = new UrlDetailFetcher(callback, connMgr, doneListener);
        activeDetailFetcher.execute(BASE_URL + Long.toString(id) + API_KEY + apiKey
                + LANGUAGE + lang);
    }

    @Override
    public void getTrailers(@NonNull TrailerLoaderCallback callback, long id) {
        AsyncUrlTask.DoneListener doneListener = new AsyncUrlTask.DoneListener() {
            @Override
            public void done() {
                activeTrailerFetcher = null;
            }
        };
        activeTrailerFetcher = new UrlTrailerFetcher(callback, connMgr, doneListener);
        activeTrailerFetcher.execute(BASE_URL + Long.toString(id) + VIDEOS + API_KEY + apiKey
                + LANGUAGE + lang);
    }

    @Override
    public void cancel() {
        if (activeTrailerFetcher != null) {
            activeTrailerFetcher.cancel(true);
        }
        if (activePosterFetcher != null) {
            activePosterFetcher.cancel(true);
        }
        if (activeDetailFetcher != null) {
            activeDetailFetcher.cancel(true);
        }
    }



    private static class UrlDetailFetcher extends AsyncUrlTask<MovieDetail> {

        private DetailsLoaderCallback callback;

        public UrlDetailFetcher(DetailsLoaderCallback callback, ConnectivityManager connMgr,
                                DoneListener doneListener) {
            super(connMgr, doneListener);
            this.callback = callback;
        }

        public JsonReader readIt(InputStream stream) throws IOException {
            return new JsonReader(new InputStreamReader(stream, "UTF-8"));
        }

        @Override
        public MovieDetail readJson(InputStream is) throws IOException {
            JsonReader reader = readIt(is);
            long id = 0L;
            String title = "";
            Uri posterPath = null;
            String releaseDate = "";
            float rating = 0f;
            String synopsis = "";
            String name;
            reader.beginObject();
            while (reader.hasNext() && !isCancelled()) {
                name = reader.nextName();
                Log.d(TAG, "readDetails: " + name);
                switch (name) {
                    case "id":
                        id = reader.nextLong();
                        break;
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
            return new MovieDetail(id, title, cal, new Poster(posterPath, title, 0L), rating, synopsis);
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

        public UrlPosterFetcher(PosterLoaderCallback callback, ConnectivityManager connMgr,
                                DoneListener doneListener) {
            super(connMgr, doneListener);
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
            while (reader.hasNext() && !isCancelled()) {
                name = reader.nextName();
                if (name.equals("results")) {
                    reader.beginArray();
                    while (reader.hasNext() && !isCancelled()) {
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

        TrailerLoaderCallback callback;

        public UrlTrailerFetcher(TrailerLoaderCallback callback, ConnectivityManager connMgr,
                                 DoneListener doneListener) {
            super(connMgr, doneListener);
            this.callback = callback;
        }

        @Override
        public List<Trailer> readJson(InputStream is) throws IOException {
            List<Trailer> trailers = new ArrayList<>();
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("results")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        trailers.add(readTrailer(reader));
                    }
                    reader.endArray();
                }
                else {
                    reader.skipValue();
                }
            }
            return trailers;
        }

        public Trailer readTrailer(JsonReader reader) throws IOException {
            Trailer trailer = new Trailer();
            reader.beginObject();
            String name;
            while (reader.hasNext()) {
                name = reader.nextName();
                switch(name) {
                    case "key":
                        String key = reader.nextString();
                        trailer.setVideo_path(Uri.parse("https://www.youtube.com/watch?v=" + key));
                        trailer.setThumbnail_path(Uri.parse("https://img.youtube.com/vi/" + key
                                + "/hqdefault.jpg"));
                        Log.d(TAG, "readTrailer: " + trailer.getVideo_path());
                        Log.d(TAG, "readTrailer: " + trailer.getThumbnail_path());
                        break;
                    case "name":
                        trailer.setTitle(reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            return trailer;
        }

        @Override
        public void handleSuccess(List<Trailer> result) {
            callback.onReturnTrailers(result);
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
}
