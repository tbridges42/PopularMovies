package us.bridgeses.popularmovies.networking;

import java.util.List;

import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/6/2016.
 */
public interface PosterLoaderCallback extends ServiceCallback {
    void onReturnPosters(List<Poster> posters);
}
