package us.bridgeses.popularmovies.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A model representing a poster and containing relevant information
 */
public class Poster implements Parcelable {

    // TODO: Does this really make sense here?
    // A psuedo-Enum of sort modes
    @IntDef({MOST_POPULAR_MODE, TOP_RATED_MODE, FAVORITED_MODE})
    public @interface SortMode {}
    public static final int MOST_POPULAR_MODE = 0;
    public static final int TOP_RATED_MODE = 1;
    public static final int FAVORITED_MODE = 2;

    public static final int THUMBNAIL_WIDTH = 342;

    private Uri imageUri;
    private String contentDescription;
    private long id;
    private boolean favorite;

    //<editor-fold desc="xtors">
    public Poster(Uri imageUri, String contentDescription, long id) {
        this(imageUri, contentDescription, id, false);
    }

    public Poster(Uri imageUri, String contentDescription, long id, boolean favorite) {
        this.imageUri = imageUri;
        this.contentDescription = contentDescription;
        this.id = id;
        this.favorite = favorite;
    }
    //</editor-fold>

    public Uri getImageUri() {
        return imageUri;
    }

    public long getId() {
        return id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    //<editor-fold desc="Parcelable">
    protected Poster(Parcel in) {
        this(Uri.parse(in.readString()), in.readString(), in.readLong(), in.readInt() == 1);
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUri.toString());
        dest.writeString(contentDescription);
        dest.writeLong(id);
        dest.writeInt(favorite ? 1 : 0);
    }
    //</editor-fold>
}
