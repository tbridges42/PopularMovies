package us.bridgeses.popularmovies.adapters.implementations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.internal.util.Predicate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.adapters.PosterAdapter;
import us.bridgeses.popularmovies.adapters.PosterClickListener;
import us.bridgeses.popularmovies.models.Poster;

/**
 * A {@link PosterAdapter} for RecyclerViews
 */
public class PosterRecyclerAdapter extends RecyclerView.Adapter<PosterRecyclerAdapter.PosterHolder>
        implements PosterAdapter {

    private List<Poster> posters;
    private Picasso picasso;
    private PosterClickListener listener;
    private Predicate<Poster> filter;
    private int selected = -1;

    /**
     * @param picasso: This adapter requires a Picasso instance to load images
     * @param posters: The {@link Poster}s to be displayed
     */
    public PosterRecyclerAdapter(Picasso picasso, List<Poster> posters) {
        this(picasso, posters, null);
    }

    public PosterRecyclerAdapter(Picasso picasso, List<Poster> posters, Predicate<Poster> filter) {
        this.picasso = picasso;
        this.filter = filter;
        applyFilter(posters);
        this.posters = posters;
    }

    public void setListener(PosterClickListener listener) {
        this.listener = listener;
    }

    //<editor-fold desc="RecyclerView.Adapter methods">
    @Override
    public PosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poster, parent, false);
        return new PosterHolder(v);
}

    @Override
    public void onBindViewHolder(PosterHolder holder, int position) {
        Poster poster = posters.get(position);
        if (poster.isFavorite()) {
            holder.starView.setVisibility(View.VISIBLE);
        }
        else {
            holder.starView.setVisibility(View.GONE);
        }
        holder.imageView.setContentDescription(poster.getContentDescription());
        picasso.load(poster.getImageUri())
                .error(R.drawable.error)
                .placeholder(R.drawable.loading).into(holder.imageView);
        holder.frame.setSelected(position == selected);
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }
    //</editor-fold>

    //<editor-fold desc="PosterAdapter methods">
    @Override
    public void addPosters(List<Poster> newPosters) {
        int oldSize = posters.size();
        applyFilter(newPosters);
        posters.addAll(newPosters);
        notifyItemRangeInserted(oldSize, posters.size());
    }

    @Override
    public void updateFavorite(long id, boolean favorite) {
        int pos = getPosition(id);
        posters.get(pos).setFavorite(favorite);
        notifyItemChanged(getPosition(id));
    }

    @Override
    public Poster getPoster(int position) {
        if (posters.size() > position) {
            return posters.get(position);
        }
        return null;
    }

    @Override
    public void setFilter(Predicate<Poster> filter) {
        this.filter = filter;
        applyFilter(posters);
    }
    //</editor-fold>

    private void applyFilter(List<Poster> posters) {
        if (filter != null) {
            for (Poster poster : posters) {
                if (filter.apply(poster)) {
                    posters.remove(poster);
                }
            }
        }
    }

    private int getPosition(long id) {
        for (int i=0; i < posters.size(); i++) {
            if (posters.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void setSelected(int selected) {
        int old = this.selected;
        this.selected = selected;
        notifyItemChanged(old);
        notifyItemChanged(selected);
    }

    class PosterHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView starView;
        View frame;

        public PosterHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.poster_image);
            frame = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onPosterClick(posters.get(position).getId());
                        setSelected(position);
                    }
                }
            });
            starView = (ImageView) itemView.findViewById(R.id.favorite_star);
        }
    }
}
