package com.digitalnusantarastudio.mymovieapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by luqman on 27/06/17.
 */

public class MovieContract {
    private MovieContract() {}

    // The authority, which is how your code knows which Content Provider to access. just copypaste from manifest
    public static final String AUTHORITY = "com.digitalnusantarastudio.mymovieapp";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Movies directory
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns{
        // MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";
        public static final String COLLUMN_TITLE = "title";
        public static final String COLLUMN_POSTER_IMAGE_NAME = "poster_path";
        public static final String COLLUMN_SYNOPSIS = "synopsis";
        public static final String COLLUMN_RATING = "rating";
        public static final String COLLUMN_RELEASE_DATE = "release_date";

    }

}
