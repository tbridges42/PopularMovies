package us.bridgeses.popularmovies.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import us.bridgeses.popularmovies.models.MovieDetail;

/**
 * Created by tbrid on 8/27/2016.
 */
public abstract class AsyncUrlTask<A> extends AsyncTask<String, Void, A> {

    private static final String TAG = "AsyncUrlTask";

    @IntDef({OK, CONNECTIVITY_ERROR, SERVER_ERROR})
    @interface UrlError {}

    public static final int OK = 0;
    public static final int CONNECTIVITY_ERROR = 1;
    public static final int SERVER_ERROR = 2;

    @UrlError int urlError = OK;
    private Context context;

    public AsyncUrlTask(Context context) {
        this.context = context;
    }

    @Override
    protected A doInBackground(String... params) {
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
                    return readJson(is);
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
    protected void onPostExecute(A results) {
        Log.d(TAG, "onPostExecute: Returning with posters");
        switch (urlError) {
            case OK:
                if (results != null) {
                    handleSuccess(results);
                }
                else {
                    Log.d(TAG, "onPostExecute: No results returned");
                }
                break;
            case CONNECTIVITY_ERROR:
                handleConnectionError();
                break;
            case SERVER_ERROR:
                handleRemoteError();
                break;
        }
    }

    public abstract A readJson(InputStream is) throws IOException;

    public abstract void handleSuccess(A result);

    public abstract void handleConnectionError();

    public abstract void handleRemoteError();
}
