package us.bridgeses.popularmovies.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tony on 8/6/2016.
 */
public class Poster implements Parcelable {
    private Uri imageUri;
    private String contentDescription;
    private long id;

    public Poster(Uri imageUri, String contentDescription, long id) {
        this.imageUri = imageUri;
        this.contentDescription = contentDescription;
        this.id = id;
    }

    public static Poster fromJson(JSONObject json) {
        try {
            return new Poster(
                    Uri.parse("http://image.tmdb.org/t/p/w185/" + json.getString("poster_path")),
                    json.getString("title"),
                    json.getLong("id")
            );
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public long getId() {
        return id;
    }

    protected Poster(Parcel in) {
        this(Uri.parse(in.readString()), in.readString(), in.readLong());
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
    }
}
