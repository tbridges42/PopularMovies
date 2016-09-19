package us.bridgeses.popularmovies.loaders.networking;

/**
 * Created by Tony on 8/6/2016.
 */
public interface ServiceCallback {
    void onLocalFailure();
    void onRemoteFailure();
}
