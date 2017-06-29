package com.digitalnusantarastudio.mymovieapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.digitalnusantarastudio.mymovieapp.MovieContract;
import com.digitalnusantarastudio.mymovieapp.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luqman on 29/06/17.
 */

public class DatabaseUtilities {
    private SQLiteDatabase db;

    public DatabaseUtilities(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        db =  dbHelper.getWritableDatabase();
    }

    public long addMovie(JSONObject movieJsonObject){
        ContentValues cv = new ContentValues();
        try {
            cv.put(MovieContract.MovieEntry.COLLUMN_TITLE, movieJsonObject.getString("original_title"));
            cv.put(MovieContract.MovieEntry.COLLUMN_POSTER_IMAGE_NAME, movieJsonObject.getString("poster_path"));
            cv.put(MovieContract.MovieEntry.COLLUMN_SYNOPSIS, movieJsonObject.getString("overview"));
            cv.put(MovieContract.MovieEntry.COLLUMN_RATING, movieJsonObject.getString("vote_average"));
            cv.put(MovieContract.MovieEntry.COLLUMN_RELEASE_DATE, movieJsonObject.getString("release_date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
    }

    public JSONArray getAllMovie(){
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME
                , null // collumn name
                , null // selection
                , null // selection args
                , null // group by
                , null // having
                , null // order by
                , null // limit
        );

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MovieContract.MovieEntry.COLLUMN_TITLE, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLLUMN_TITLE)));
                jsonObject.put(MovieContract.MovieEntry.COLLUMN_POSTER_IMAGE_NAME, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLLUMN_POSTER_IMAGE_NAME)));
                jsonObject.put(MovieContract.MovieEntry.COLLUMN_SYNOPSIS, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLLUMN_SYNOPSIS)));
                jsonObject.put(MovieContract.MovieEntry.COLLUMN_RATING, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLLUMN_RATING)));
                jsonObject.put(MovieContract.MovieEntry.COLLUMN_RELEASE_DATE, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLLUMN_RELEASE_DATE)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    public boolean removeMovie(String id){
        return db.delete(MovieContract.MovieEntry.TABLE_NAME
                , MovieContract.MovieEntry._ID + " = "+ id
                , null
        ) > 0;
    }
}
