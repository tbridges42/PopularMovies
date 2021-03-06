package us.bridgeses.popularmovies.loaders.callbacks;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.loaders.networking.ServiceCallback;

/**
 * Created by Tony on 8/6/2016.
 */
public interface PosterLoaderCallback extends ServiceCallback {
    void onReturnPosters(List<Poster> posters);
}
