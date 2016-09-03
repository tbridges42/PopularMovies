package us.bridgeses.popularmovies.persistence.networking;

/**
 * Created by Tony on 8/6/2016.
 */
public interface ServiceCallback {
    void onLocalFailure();
    void onRemoteFailure();
}
