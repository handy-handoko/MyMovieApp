package com.digitalnusantarastudio.mymovieapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.digitalnusantarastudio.mymovieapp.R;
import com.digitalnusantarastudio.mymovieapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;

/**
 * Created by handy on 23/06/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    private Context context;
    private JSONArray movie_list_json_array;
    private final ListItemClickListener listItemClickListener;

    public MovieAdapter(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public interface ListItemClickListener{
        void onListItemClick(int position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        boolean shouldAttachToParentImmediately = false;
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, shouldAttachToParentImmediately);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind_image(position);
    }

    @Override
    public int getItemCount() {
        if(movie_list_json_array == null)
            return 0;
        else
            return movie_list_json_array.length();
    }

    public void setMovieData(JSONArray movie_list_json_array){
        this.movie_list_json_array = movie_list_json_array;
        notifyDataSetChanged();
    }

    public JSONArray getMovie_list_json_array() {
        return movie_list_json_array;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView movie_poster_imageview;

        MovieViewHolder(View itemView) {
            super(itemView);
            movie_poster_imageview = (ImageView)itemView.findViewById(R.id.imv_movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind_image(int position){
            String movie_poster_file_name=null;
            try {
                movie_poster_file_name = movie_list_json_array.getJSONObject(position).getString("poster_path");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            URL url = NetworkUtils.buildMoviePosterUrl(movie_poster_file_name);

            Glide.with(context)
                .load(url)
                .into(movie_poster_imageview);
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
