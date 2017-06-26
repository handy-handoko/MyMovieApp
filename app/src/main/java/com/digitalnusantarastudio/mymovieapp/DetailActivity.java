package com.digitalnusantarastudio.mymovieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.digitalnusantarastudio.mymovieapp.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    private static final String MOVIE_ITEM = "movie";

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

    }
}
