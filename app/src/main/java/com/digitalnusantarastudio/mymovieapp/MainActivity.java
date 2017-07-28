package com.digitalnusantarastudio.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.digitalnusantarastudio.mymovieapp.adapter.MovieAdapter;
import com.digitalnusantarastudio.mymovieapp.data.MovieContract;
import com.digitalnusantarastudio.mymovieapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<JSONArray>{

    private RecyclerView movie_recycler_view;
    private MovieAdapter adapter;
    private static final String MOVIE_ITEM = "movie";
    private static final String LIFECYCLE_CALLBACKS_KEY = "sort_by";
    private static final String SORT_BY_BUNDLE_KEY = "sort";
    private String sort_by;
    private static final int MOVIES_LOADER = 123;
    private static final String FAVORITED_TAG = "favorited";
    private static final String POPULAR_TAG = "popular";
    private static final String TOP_RATED_TAG = "top_rated";

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
     * load data by running Loaders. Data will be sorted using sortby variable
     */
    public void refresh_data() {
        if(sort_by == FAVORITED_TAG){
            run_loader();
        } else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if(NetworkUtils.isOnline(cm)){
                run_loader();
            } else {
                Toast.makeText(this, "Connection Error. Ensure your phone is connect to internet.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void run_loader(){
        Bundle bundle = new Bundle();
        bundle.putString(SORT_BY_BUNDLE_KEY, sort_by);

        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<JSONArray> githubSearchLoader = loaderManager.getLoader(MOVIES_LOADER);

        if (githubSearchLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER, bundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, bundle, this);
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

    @Override
    public Loader<JSONArray> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<JSONArray>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public JSONArray loadInBackground() {
                String sortByParam = args.getString(SORT_BY_BUNDLE_KEY);
                JSONArray movie_list_json_array = new JSONArray();
                if(sortByParam != FAVORITED_TAG){
                    URL url = NetworkUtils.buildUrl(sortByParam);
                    movie_list_json_array = getDatafromInternet(url);
                } else {
                    movie_list_json_array = getDatafromContentProvider();
                }

                return movie_list_json_array;
            }

            private JSONArray getDatafromInternet(URL url){
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

            private JSONArray getDatafromContentProvider(){
                JSONArray movie_list_json_array = new JSONArray();
                Cursor cursor;
                try {
                    cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                    //adapted from adonias's answer here. https://stackoverflow.com/questions/13070791/android-cursor-to-jsonarray
                    cursor.moveToFirst();
                    JSONObject rowObject;
                    while (cursor.isAfterLast() == false) {
                        int totalColumn = cursor.getColumnCount();
                        rowObject = new JSONObject(); //i think reuse object is better. so i reuse rowObject variable rather than make another row object var like adonias's answer
                        for (int i = 0; i < totalColumn; i++) {
                            if (cursor.getColumnName(i) != null) {
                                try {
                                    rowObject.put(cursor.getColumnName(i),cursor.getString(i));
                                } catch (Exception e) {
                                   e.printStackTrace();
                                }
                            }
                        }
                        movie_list_json_array.put(rowObject);
                        cursor.moveToNext();
                    }
                    cursor.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return movie_list_json_array;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {
        adapter.setMovieData(data);
    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {

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
            sort_by = POPULAR_TAG;
            refresh_data();
            return true;
        }else if(item.getItemId() == R.id.action_top_rated){
            sort_by = TOP_RATED_TAG;
            refresh_data();
            return true;
        } else if(item.getItemId() == R.id.action_saved){
            sort_by = FAVORITED_TAG;
            refresh_data();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
