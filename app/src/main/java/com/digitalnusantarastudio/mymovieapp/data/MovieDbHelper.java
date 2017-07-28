package com.digitalnusantarastudio.mymovieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luqman on 29/06/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "saved_movie.db";
    private static final short DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLLUMN_POSTER_IMAGE_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLLUMN_RATING + " FLOAT NOT NULL, " +
                MovieContract.MovieEntry.COLLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                ");";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
