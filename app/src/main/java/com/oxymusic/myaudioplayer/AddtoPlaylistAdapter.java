package com.oxymusic.myaudioplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AddtoPlaylistAdapter extends RecyclerView.Adapter<AddtoPlaylistAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MusicFiles>addFilesToPlaylist;

    public AddtoPlaylistAdapter(Context context, ArrayList<MusicFiles> addFilesToPlaylist) {
        this.context = context;
        this.addFilesToPlaylist = addFilesToPlaylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_to_playlist , parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.music_file_name.setText(addFilesToPlaylist.get(position).getTitle());
        
        //loads  the image in front of the song  name
        byte[] image = getAlbumArt(addFilesToPlaylist.get(position).getPath());
        if(image != null){
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.music_img);
        }
        else{
            Glide.with(context)
                    .load(R.drawable.bewedoc)
                    .into(holder.music_img);
        }

    }

    @Override
    public int getItemCount() {
        return addFilesToPlaylist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
       ImageView music_img;
       TextView music_file_name;
       public static  CheckBox add_songs_checkbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            music_file_name = itemView.findViewById(R.id.music_file_name);
            music_img = itemView.findViewById(R.id.music_img);
            add_songs_checkbox = itemView.findViewById(R.id.add_songs_checkbox);
        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
