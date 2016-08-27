package us.bridgeses.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import us.bridgeses.popularmovies.R;

/**
 * Created by Tony on 8/27/2016.
 */
public class TrailerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class TrailerHolder extends RecyclerView.ViewHolder {

        TextView title;

        public TrailerHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.trailer_title);
        }
    }
}
