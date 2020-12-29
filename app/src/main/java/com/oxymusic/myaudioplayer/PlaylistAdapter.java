package com.oxymusic.myaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private Context context;
    private List<Playlist> playlists;
    View view;

    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.playlist_item ,parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
    // holder.genre_name.setText(genresList.get(position).getTitle());
        holder.playlist_name.setText(playlists.get(position).getTitle());
        holder.playlist_img.setImageResource(playlists.get(position).getThumbnail());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Playlist Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context , Tap_Playlist.class);

                intent.putExtra("Playlist" ,playlists.get(position).getTitle());
                intent.putExtra("Playlist Image" ,playlists.get(position).getThumbnail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView playlist_img;
        TextView playlist_name;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //genre_image = itemView.findViewById(R.id.genre_image);
           playlist_img  = itemView.findViewById(R.id.playlist_image);
           playlist_name =itemView.findViewById(R.id.playlist_name);
           cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
