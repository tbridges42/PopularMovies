package us.bridgeses.popularmovies.loaders.networking;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An implementation of AsyncTask for retrieving JSON information from a URL
 */
public abstract class AsyncUrlTask<A> extends AsyncTask<String, Void, A> {

    @SuppressWarnings("unused")
    private static final String TAG = "AsyncUrlTask";

    @IntDef({OK, CONNECTIVITY_ERROR, SERVER_ERROR})
    @interface UrlError {}
    public static final int OK = 0;
    public static final int CONNECTIVITY_ERROR = 1;
    public static final int SERVER_ERROR = 2;

    protected interface DoneListener {
        void done();
    }

    @UrlError int urlError = OK;
    private ConnectivityManager connMgr;
    private DoneListener doneListener;

    public AsyncUrlTask(ConnectivityManager connMgr, DoneListener doneListener) {
        this.connMgr = connMgr;
        this.doneListener = doneListener;
    }

    @Override
    protected A doInBackground(String... params) {
        Log.d("url", params[0]);
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
        switch (urlError) {
            case OK:
                if (results != null) {
                    handleSuccess(results);
                }
                break;
            case CONNECTIVITY_ERROR:
                handleConnectionError();
                break;
            case SERVER_ERROR:
                handleRemoteError();
                break;
        }
        if (doneListener != null) {
            doneListener.done();
        }
    }

    @Override
    protected void onCancelled(A results) {
        if (results != null) {
            handleSuccess(results);
        }
    }

    public abstract A readJson(InputStream is) throws IOException;

    public abstract void handleSuccess(A result);

    public abstract void handleConnectionError();

    public abstract void handleRemoteError();
}
