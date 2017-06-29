package com.digitalnusantarastudio.mymovieapp;

import android.provider.BaseColumns;

/**
 * Created by luqman on 27/06/17.
 */

public class MovieContract {
    private MovieContract() {}

    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movie";
        public static final String COLLUMN_TITLE = "title";
        public static final String COLLUMN_POSTER_IMAGE_NAME = "poster_image_name";
        public static final String COLLUMN_SYNOPSIS = "synopsis";
        public static final String COLLUMN_RATING = "rating";
        public static final String COLLUMN_RELEASE_DATE = "release_date";

    }

}
