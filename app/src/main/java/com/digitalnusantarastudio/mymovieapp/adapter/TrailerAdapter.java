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
 * adapter for show list of movie poster
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{
    private Context context;
    private JSONArray trailers_json_array;
    private final ListItemClickListener listItemClickListener;

    //initialize adapter with listener
    public TrailerAdapter(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    //listener for click event. used when user click on movie poster to see about movie detail
    public interface ListItemClickListener{
        void onListItemClick(int position);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        boolean shouldAttachToParentImmediately = false;
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, shouldAttachToParentImmediately);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(trailers_json_array == null)
            return 0;
        else
            return trailers_json_array.length();
    }

    public void setData(JSONArray trailers_json_array){
        this.trailers_json_array = trailers_json_array;
        notifyDataSetChanged();
    }

    public JSONArray getTrailerJsonArray() {
        return trailers_json_array;
    }

    //Holder class for movie list
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txt_trailer_name;

        TrailerViewHolder(View itemView) {
            super(itemView);
            txt_trailer_name = (TextView)itemView.findViewById(R.id.txt_trailer_name);
            itemView.setOnClickListener(this);
        }

        //function for bind data to adapter
        void bind(int position){
            String trailer_name=null;
            try {
                trailer_name = trailers_json_array.getJSONObject(position).getString("name");

                txt_trailer_name.setText(trailer_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
