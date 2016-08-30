package us.bridgeses.popularmovies.presenters;

/**
 * Created by Tony on 8/30/2016.
 */
public class DetailViewerFactory {

    public MovieDetailViewer getViewer(boolean hasDualPane) {
        if (hasDualPane) {
            // Set up and return fragment
            return null;
        }
        else {
            // Set up and launch activity
            return null;
        }
    }
}
