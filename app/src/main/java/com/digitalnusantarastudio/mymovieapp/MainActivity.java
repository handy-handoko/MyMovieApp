package com.digitalnusantarastudio.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.digitalnusantarastudio.mymovieapp.adapter.MovieAdapter;
import com.digitalnusantarastudio.mymovieapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private RecyclerView movie_recycler_view;
    private MovieAdapter adapter;
    private static final String MOVIE_ITEM = "movie";
    private static final String LIFECYCLE_CALLBACKS_KEY = "sort_by";
    private String sort_by;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize recycler view and adapter
        movie_recycler_view = (RecyclerView)findViewById(R.id.movie_recycler_view);

        adapter = new MovieAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        movie_recycler_view.setLayoutManager(layoutManager);
        movie_recycler_view.setAdapter(adapter);

        //by default will load data and sort by popular
        if((savedInstanceState != null) && savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_KEY))
            sort_by = savedInstanceState.getString(LIFECYCLE_CALLBACKS_KEY);
        else
            sort_by = "popular";
        refresh_data();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(LIFECYCLE_CALLBACKS_KEY, sort_by);
    }

    /**
     * check if connect to internet. based on StackOverflow post below
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * load data by running NetworkConnectionTask. Data will be sorted using sortby variable
     */
    public void refresh_data() {
        if(isOnline()){
            new NetworkConnectionTask().execute(sort_by);
        } else {
            Toast.makeText(this, "Connection Error. Ensure your phone is connect to internet.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * handling movie poster click event. will be put data to intent and open new activity
     * @param position position item clicked
     */
    @Override
    public void onListItemClick(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        JSONArray movie_list_json_array = adapter.getMovie_list_json_array();
        try {
            JSONObject movie_json_object = movie_list_json_array.getJSONObject(position);
            intent.putExtra(MOVIE_ITEM, movie_json_object.toString());
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Task to connect to internet
     * 1st param is url in String
     * 3rd param is JSON array which respond from internet
     */
    private class NetworkConnectionTask extends AsyncTask<String, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url = NetworkUtils.buildUrl(params[0]);
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
            adapter.setMovieData(jsonArray);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort, menu);
        return true;
    }

    /**
     * refresh data on menu selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_popular){
            sort_by = "popular";
            refresh_data();

            return true;
        }else if(item.getItemId() == R.id.action_top_rated){
            sort_by = "top_rated";
            refresh_data();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
