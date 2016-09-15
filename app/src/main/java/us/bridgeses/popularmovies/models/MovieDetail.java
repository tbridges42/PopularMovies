package us.bridgeses.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A model class representing a movie and containing related information
 */
public class MovieDetail implements Parcelable {

    // A format for storage and retrieval, based off of the date format in TMDB.
    // This format is never translated, and should not be used for display.
    public static final SimpleDateFormat defaultFormat =
            new SimpleDateFormat("yyyy-mm-dd", Locale.US);

    private final String title;
    private final Calendar releaseDate;
    private final Poster poster;
    private final float rating;
    private final String synopsis;
    private final boolean favorite;
    private long id;
    private boolean adult;

    public boolean isFavorite() {
        return favorite;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getId() {
        return id;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    //<editor-fold desc="xtors">
    public MovieDetail(long id, String title, Calendar releaseDate, Poster poster, float rating,
                       String synopsis) {
        this(id, title, releaseDate, poster, rating, synopsis, false);
    }

    public MovieDetail(long id, String title, Calendar releaseDate, Poster poster, float rating,
                       String synopsis, boolean favorite) {
        this(id, title, releaseDate, poster, rating, synopsis, favorite, false);
    }

    public MovieDetail(long id, String title, Calendar releaseDate, Poster poster, float rating,
                       String synopsis, boolean favorite, boolean adult) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.rating = rating;
        this.synopsis = synopsis;
        this.favorite = favorite;
        this.adult = adult;
    }
    //</editor-fold>

    //<editor-fold desc="Parcelable">
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
        this.adult = in.readInt() == 1;
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
        dest.writeInt(adult ? 1 : 0);
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
    //</editor-fold>
}
