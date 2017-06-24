package com.digitalnusantarastudio.mymovieapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.digitalnusantarastudio.mymovieapp.adapter.MovieAdapter;
import com.digitalnusantarastudio.mymovieapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private RecyclerView movie_recycler_view;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movie_recycler_view = (RecyclerView)findViewById(R.id.movie_recycler_view);

        adapter = new MovieAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        movie_recycler_view.setLayoutManager(layoutManager);
        movie_recycler_view.setAdapter(adapter);

        new NetworkConnectionTask().execute("popular");
    }

    class NetworkConnectionTask extends AsyncTask<String, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url = NetworkUtils.buildUrl(params[0]);
            String response = null;
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
            adapter.setMovieData(jsonArray);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_popular){
            new NetworkConnectionTask().execute("popular");

            return true;
        }else if(item.getItemId() == R.id.action_top_rated){
            new NetworkConnectionTask().execute("top_rated");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
