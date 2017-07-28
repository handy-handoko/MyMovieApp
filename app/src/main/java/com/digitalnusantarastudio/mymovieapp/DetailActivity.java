package com.digitalnusantarastudio.mymovieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.digitalnusantarastudio.mymovieapp.adapter.ReviewAdapter;
import com.digitalnusantarastudio.mymovieapp.adapter.TrailerAdapter;
import com.digitalnusantarastudio.mymovieapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener{
    private static final String MOVIE_ITEM = "movie";
    private RecyclerView trailer_recycler_view;
    private RecyclerView review_recycler_view;
    private TrailerAdapter trailer_adapter;
    private ReviewAdapter review_adapter;
    private String movie_id;
    private final static String TRAILER_TAG = "trailer";
    private final static String REVIEW_TAG = "review";
    private JSONObject movie_json_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //obtain view from xml
        ImageView movie_poster_imageview = (ImageView) findViewById(R.id.imv_movie_poster);
        TextView title_textview =(TextView) findViewById(R.id.txt_movie_title);
        TextView synopsis = (TextView) findViewById(R.id.txt_synopsis);
        TextView user_rating_textview = (TextView) findViewById(R.id.txt_user_rating);
        TextView release_date_textview = (TextView)findViewById(R.id.txt_release_date);

        //fill view with data passed from intent to view
        Intent intent = getIntent();
        try {
            movie_json_object = new JSONObject(intent.getStringExtra(MOVIE_ITEM));
            movie_id = movie_json_object.getString("id");
            title_textview.setText(movie_json_object.getString("original_title"));
            synopsis.setText(movie_json_object.getString("overview"));
            user_rating_textview.setText(movie_json_object.getString("vote_average"));
            release_date_textview.setText(movie_json_object.getString("release_date"));
            URL poster_url = NetworkUtils.buildMoviePosterUrl(movie_json_object.getString("poster_path"));
            Glide.with(this)
                    .load(poster_url)
                    .into(movie_poster_imageview);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //initialize recycler view and adapter
        trailer_recycler_view = (RecyclerView)findViewById(R.id.trailer_recycler_view);
        review_recycler_view = (RecyclerView)findViewById(R.id.review_recycler_view);
        trailer_adapter = new TrailerAdapter(this);
        review_adapter = new ReviewAdapter();

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);

        trailer_recycler_view.setLayoutManager(trailerLayoutManager);
        review_recycler_view.setLayoutManager(reviewLayoutManager);

        trailer_recycler_view.setAdapter(trailer_adapter);
        review_recycler_view.setAdapter(review_adapter);

        refresh_data();
    }

    public void refresh_data() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(NetworkUtils.isOnline(cm)){
            new DetailActivity.NetworkConnectionTask().execute(movie_id);
        } else {
            Toast.makeText(this, "Connection Error. Ensure your phone is connect to internet.", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Task to connect to internet
     * 1st param is movie id in JSON
     * 3rd param is JSON array which respond from internet
     */
    private class NetworkConnectionTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            URL trailer_url = NetworkUtils.buildMovieVideoUrl(params[0]);
            URL review_url = NetworkUtils.buildMovieReviewUrl(params[0]);
            String response;
            JSONArray movie_trailer_json_array = null;
            JSONArray movie_review_json_array = null;
            JSONObject returnJsonObject = new JSONObject();
            try {
                response = NetworkUtils.getResponseFromHttpUrl(trailer_url);
                JSONObject trailer_response_json_object = new JSONObject(response);
                movie_trailer_json_array = trailer_response_json_object.getJSONArray("results");

                response = NetworkUtils.getResponseFromHttpUrl(review_url);
                JSONObject review_response_json_object = new JSONObject(response);
                movie_review_json_array = review_response_json_object.getJSONArray("results");

                returnJsonObject.put(TRAILER_TAG, movie_trailer_json_array);
                returnJsonObject.put(REVIEW_TAG, movie_review_json_array);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnJsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                trailer_adapter.setData(jsonObject.getJSONArray(TRAILER_TAG));
                review_adapter.setData(jsonObject.getJSONArray(REVIEW_TAG));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListItemClick(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        JSONArray trailer_list_json_array = trailer_adapter.getTrailerJsonArray();

        try {
            JSONObject trailer_json_object = trailer_list_json_array.getJSONObject(position);
            intent.setData(Uri.parse("http://www.youtube.com/watch?v="+trailer_json_object.getString("key")));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();
        }
    }

    public void save_movie(View v){
        ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(MovieContract.MovieEntry.COLLUMN_TITLE, movie_json_object.getString("original_title"));
            contentValues.put(MovieContract.MovieEntry.COLLUMN_POSTER_IMAGE_NAME, movie_json_object.getString("poster_path"));
            contentValues.put(MovieContract.MovieEntry.COLLUMN_SYNOPSIS, movie_json_object.getString("overview"));
            contentValues.put(MovieContract.MovieEntry.COLLUMN_RATING, movie_json_object.getString("vote_average"));
            contentValues.put(MovieContract.MovieEntry.COLLUMN_RELEASE_DATE, movie_json_object.getString("release_date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
