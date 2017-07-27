package com.digitalnusantarastudio.mymovieapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalnusantarastudio.mymovieapp.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by luqman on 28/07/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private JSONArray reviews_json_array;

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        boolean shouldAttachToParentImmediately = false;
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, shouldAttachToParentImmediately);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(reviews_json_array == null)
            return 0;
        else
            return reviews_json_array.length();
    }

    public void setData(JSONArray trailers_json_array){
        this.reviews_json_array = trailers_json_array;
        notifyDataSetChanged();
    }

    public JSONArray getReviewJsonArray() {
        return reviews_json_array;
    }

    //Holder class for movie list
    class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView txt_author;
        TextView txt_content;

        ReviewViewHolder(View itemView) {
            super(itemView);
            txt_author = (TextView)itemView.findViewById(R.id.txt_author);
            txt_content = (TextView)itemView.findViewById(R.id.txt_review_content);
        }

        //function for bind data to adapter
        void bind(int position){
            String author=null;
            String content=null;
            try {
                author = reviews_json_array.getJSONObject(position).getString("author");
                content = reviews_json_array.getJSONObject(position).getString("content");

                txt_author.setText(author);
                txt_content.setText(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
