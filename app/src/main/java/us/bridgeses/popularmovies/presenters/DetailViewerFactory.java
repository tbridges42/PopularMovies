package us.bridgeses.popularmovies.presenters;

import us.bridgeses.popularmovies.DetailActivityWrapper;
import us.bridgeses.popularmovies.presenters.implementations.DetailFragmentWrapper;

/**
 * Created by Tony on 8/30/2016.
 */
public class DetailViewerFactory {

    public static MovieDetailViewer getViewer(boolean hasDualPane) {
        if (hasDualPane) {
            return new DetailFragmentWrapper();
        }
        else {
            return new DetailActivityWrapper();
        }
    }
}
