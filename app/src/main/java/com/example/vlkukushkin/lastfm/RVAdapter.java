package com.example.vlkukushkin.lastfm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * Created by WG on 10.09.2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AlbumHolder>{
        private List<Map<String,Object>> albums;


    public RVAdapter(List<Map<String, Object>> data) {
        albums = data;
    }


    @Override
    public int getItemCount() {
        return albums.size();
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate((R.layout.album_list_item), parent, false);
        return new AlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumHolder holder, int position) {
        holder.bindVal(albums.get(position));

    }
    class AlbumHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView album;
        private TextView artist;

        private Context context;

        public AlbumHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();

            image = (ImageView) itemView.findViewById(R.id.itemList_albumImage);
            album = (TextView) itemView.findViewById(R.id.itemList_albumTitle);
            artist =  (TextView) itemView.findViewById(R.id.itemList_groupTitle);
        }

        public void bindVal(Map<String, Object> data){
            album.setText((data.get(MainActivity.ALBUM_NAME).toString()));
            artist.setText((data.get(MainActivity.ARTIST).toString()));
            String imageURL = (data.get(MainActivity.MEDIUM_IMAGE_URL).toString());
            if (!imageURL.isEmpty()) {
                Picasso.with(context)
                        .load(data.get(MainActivity.MEDIUM_IMAGE_URL).toString())
                        .into(image);
            }
        }
    }
}
