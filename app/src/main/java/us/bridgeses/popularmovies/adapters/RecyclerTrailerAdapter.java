package us.bridgeses.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.bridgeses.popularmovies.R;
import us.bridgeses.popularmovies.models.Trailer;

/**
 * Created by Tony on 8/27/2016.
 */
public class RecyclerTrailerAdapter extends RecyclerView.Adapter<RecyclerTrailerAdapter.TrailerHolder>
            implements TrailerAdapter {

    private List<Trailer> trailers;
    private Context context;
    private TrailerClickCallback callback;

    public interface TrailerClickCallback {
        void onTrailerClick(Uri trailerUri);
    }

    public RecyclerTrailerAdapter(Context context, List<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    public void setCallback(TrailerClickCallback callback){
        this.callback = callback;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);
        return new TrailerHolder(v);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        holder.thumbnail.setContentDescription(trailers.get(position).getTitle());
        Picasso picasso = Picasso.with(context);
        picasso.load(trailers.get(position).getThumbnail_path())
                .placeholder(R.drawable.loading).into(holder.thumbnail);
        holder.title.setText(trailers.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    class TrailerHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView thumbnail;

        public TrailerHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.trailer_title);
            thumbnail = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        int position = getAdapterPosition();
                        callback.onTrailerClick(trailers.get(position).getVideo_path());
                    }
                }
            });
        }
    }
}
