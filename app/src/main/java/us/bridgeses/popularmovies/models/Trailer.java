package us.bridgeses.popularmovies.models;

import android.net.Uri;

/**
 * A model class representing a trailer
 */
public class Trailer {

    private long id;
    private String title;
    private Uri video_path;
    private Uri thumbnail_path;

    public Trailer() {

    }

    public Trailer(long id, String title, Uri video_path, Uri thumbnail_path) {
        this.id = id;
        this.title = title;
        this.video_path = video_path;
        this.thumbnail_path = thumbnail_path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getVideo_path() {
        return video_path;
    }

    public void setVideo_path(Uri video_path) {
        this.video_path = video_path;
    }

    public Uri getThumbnail_path() {
        return thumbnail_path;
    }

    public void setThumbnail_path(Uri thumbnail_path) {
        this.thumbnail_path = thumbnail_path;
    }
}
