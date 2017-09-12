package com.example.vlkukushkin.lastfm;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        holder.bindVal(albums.get(position),position);

    }
    class AlbumHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView album;
        private TextView artist;
        private LinearLayout layout;

        private Context context;

        public AlbumHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();

            layout = (LinearLayout) itemView.findViewById(R.id.itemList_layout);
            image = (ImageView) itemView.findViewById(R.id.itemList_albumImage);
            album = (TextView) itemView.findViewById(R.id.itemList_albumTitle);
            artist =  (TextView) itemView.findViewById(R.id.itemList_groupTitle);
        }

        public void bindVal(Map<String, Object> data, int position){
            album.setText((data.get(MainActivity.ALBUM_NAME).toString()));
            artist.setText((data.get(MainActivity.ARTIST).toString()));
            String imageURL = (data.get(MainActivity.MEDIUM_IMAGE_URL).toString());
            if (position % 2 == 0)
                layout.setBackgroundColor(ContextCompat.getColor(context,R.color.dark_gray));
            else
                layout.setBackgroundColor(ContextCompat.getColor(context,R.color.gray));
            if (!imageURL.isEmpty()) {
                Picasso.with(context)
                        .load(data.get(MainActivity.MEDIUM_IMAGE_URL).toString())
                        .into(image);
            }

        }
    }
}
