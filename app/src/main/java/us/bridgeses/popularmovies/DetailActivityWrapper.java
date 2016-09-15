package us.bridgeses.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;

import us.bridgeses.popularmovies.presenters.MovieDetailViewer;

/**
 * Created by tbrid on 8/30/2016.
 */
public class DetailActivityWrapper implements MovieDetailViewer {

    @Override
    public void load(Activity activity, @IdRes int resId, long id) {
        Intent intent = new Intent(activity, MovieDetailActivity.class);
        intent.putExtra("id", id);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    public void loadCached(Activity activity) {
        // Not implemented
    }
}
