package us.bridgeses.popularmovies.presenters;

import android.app.Activity;
import android.support.annotation.IdRes;

/**
 * Created by Tony on 8/30/2016.
 */
public interface MovieDetailViewer {

    void load(Activity activity, @IdRes int resId, long id);

    void loadCached(Activity activity);
}
