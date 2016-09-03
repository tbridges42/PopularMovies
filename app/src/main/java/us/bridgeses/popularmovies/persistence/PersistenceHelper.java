package us.bridgeses.popularmovies.persistence;

import java.util.List;
import java.util.Set;

import us.bridgeses.popularmovies.models.MovieDetail;
import us.bridgeses.popularmovies.models.Poster;
import us.bridgeses.popularmovies.models.Trailer;

/**
 * Created by Tony on 8/31/2016.
 */
public interface PersistenceHelper {

    void saveFavorite(MovieDetail movie, List<Trailer> trailers);

    Set<Long> getIds();

    List<Poster> getAllPosters();

    MovieDetail getMovieDetail(long id);

    List<Trailer> getTrailers(long id);

    void deleteFavorite(long id);
}
