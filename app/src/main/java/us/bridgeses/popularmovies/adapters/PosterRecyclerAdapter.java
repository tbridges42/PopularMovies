package us.bridgeses.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.models.Poster;

/**
 * Created by Tony on 8/19/2016.
 */
public class PosterRecyclerAdapter extends RecyclerView.Adapter<PosterRecyclerAdapter.PosterHolder> {

    private List<Poster> posters;
    private Context context;
    private PosterClickListener listener;

    public interface PosterClickListener {
        void onItemClick(long id);
    }

    public PosterRecyclerAdapter(Context context, List<Poster> posters) {
        this.context = context;
        this.posters = posters;
    }

    public void setListener(PosterClickListener listener) {
        this.listener = listener;
    }

    @Override
    public PosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poster, parent, false);
        return new PosterHolder(v);
}

    @Override
    public void onBindViewHolder(PosterHolder holder, int position) {
        holder.imageView.setContentDescription(posters.get(position).getContentDescription());
        Picasso picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(true);
        picasso.load(posters.get(position).getImageUri()).placeholder(R.drawable.loading).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    public void addPosters(List<Poster> newPosters) {
        int oldSize = posters.size();
        posters.addAll(newPosters);
        notifyItemRangeInserted(oldSize, posters.size());
    }

    class PosterHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public PosterHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.poster_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(posters.get(position).getId());
                    }
                }
            });
        }
    }
}
