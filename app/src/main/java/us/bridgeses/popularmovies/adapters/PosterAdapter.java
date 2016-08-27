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
public class PosterAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private final List<Poster> posters;

    public PosterAdapter(Context context, List<Poster> posters) {
        this.context = context;
        this.posters = posters;
    }

    @Override
    public int getCount() {
        return posters.size();
    }

    @Override
    public Poster getItem(int position) {
        return posters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return posters.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_poster, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.poster_image);
            viewHolder.errorText = (TextView) convertView.findViewById(R.id.poster_error_text);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load(posters.get(position).getImageUri()).into(viewHolder.image);
        viewHolder.image.setVisibility(View.VISIBLE);
        viewHolder.image.setContentDescription(
                posters.get(position).getContentDescription());
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView errorText;
    }
}
