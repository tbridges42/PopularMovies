package us.bridgeses.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/6/2016.
 */
public interface PosterAdapter {

    void setListener(PosterClickListener listener);
    void addPosters(List<Poster> posters);
    Poster getPoster(int position);
}
