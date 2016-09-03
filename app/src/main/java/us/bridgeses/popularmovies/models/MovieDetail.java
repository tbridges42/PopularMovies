package us.bridgeses.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Tony on 8/6/2016.
 */
public class MovieDetail implements Parcelable {

    public static final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
    // Add id
    private final String title;
    private final Calendar releaseDate;
    private final Poster poster;
    private final float rating;
    private final String synopsis;

    public boolean isFavorite() {
        return favorite;
    }

    public void setId(long id) {
        this.id = id;
    }

    private final boolean favorite;
    private long id;

    public String getTitle() {
        return title;
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public Poster getPoster() {
        return poster;
    }

    public float getRating() {
        return rating;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public MovieDetail(long id, String title, Calendar releaseDate, Poster poster, float rating,
                       String synopsis) {
        this(id, title, releaseDate, poster, rating, synopsis, false);
    }

    public MovieDetail(long id, String title, Calendar releaseDate, Poster poster, float rating,
                       String synopsis, boolean favorite) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.rating = rating;
        this.synopsis = synopsis;
        this.favorite = favorite;
    }

    protected MovieDetail(Parcel in) {
        this.title = in.readString();
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(defaultFormat.parse(in.readString()));
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format");
        }
        this.releaseDate = cal;
        this.poster = in.readParcelable(Poster.class.getClassLoader());
        this.rating = in.readFloat();
        this.synopsis = in.readString();
        this.favorite = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(defaultFormat.format(releaseDate.getTime()));
        dest.writeParcelable(poster, flags);
        dest.writeFloat(rating);
        dest.writeString(synopsis);
        dest.writeInt(favorite ? 1 : 0);
    }

    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

    public long getId() {
        return id;
    }
}
