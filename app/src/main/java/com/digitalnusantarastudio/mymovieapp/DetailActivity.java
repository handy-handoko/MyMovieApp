package com.digitalnusantarastudio.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    private TrailerAdapter trailer_adapter;
    private String movie_id;

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
            JSONObject movie_json_object = new JSONObject(intent.getStringExtra(MOVIE_ITEM));
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

        trailer_adapter = new TrailerAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trailer_recycler_view.setLayoutManager(layoutManager);
        trailer_recycler_view.setAdapter(trailer_adapter);

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
    private class NetworkConnectionTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url = NetworkUtils.buildMovieVideoUrl(params[0]);
            String response;
            JSONArray movie_list_json_array = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
                JSONObject response_json_object = new JSONObject(response);
                movie_list_json_array = response_json_object.getJSONArray("results");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movie_list_json_array;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            trailer_adapter.setData(jsonArray);
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
}
