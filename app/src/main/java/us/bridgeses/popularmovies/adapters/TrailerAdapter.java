package us.bridgeses.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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

    public static class TrailerHolder extends RecyclerView.ViewHolder {

        public TrailerHolder(View itemView) {
            super(itemView);
        }
    }
}
