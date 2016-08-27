package us.bridgeses.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import us.bridgeses.dateview.DateView;
import us.bridgeses.popularmovies.models.MovieDetail;

/**
 * Created by Tony on 8/25/2016.
 */
public class MovieDetailActivity extends Activity {

    private MovieDetail movieDetail;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm-dd-yyyy", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent incoming = getIntent();
        Bundle args = incoming.getBundleExtra("movie");
        long id = args.getLong("id");
    }

    public void setMovieDetail(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
        Picasso.with(this).load(movieDetail.getPoster()
                .getImageUri()).into((ImageView)findViewById(R.id.detail_poster));
        ((TextView)findViewById(R.id.detail_title)).setText(movieDetail.getTitle());
        DateView releaseDate = (DateView)findViewById(R.id.detail_release);
        releaseDate.setFormat(dateFormat);
        releaseDate.setDate(movieDetail.getReleaseDate().getTime());
        ((TextView)findViewById(R.id.detail_ratings)).setText(String.format(Locale.getDefault(),
                "%.2f", movieDetail.getRating()));
        ((TextView)findViewById(R.id.detail_synopsis)).setText(movieDetail.getSynopsis());
    }
}
