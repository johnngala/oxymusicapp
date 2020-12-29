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

public class GenreAdapter extends  RecyclerView.Adapter<GenreAdapter.MyHolder> {
    private Context context;
    private List<Genres> genresList;

    View view;

    public GenreAdapter(Context context, List<Genres> genresList) {
        this.context = context;
        this.genresList = genresList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.genre_items ,parent , false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.genre_name.setText(genresList.get(position).getTitle());
        holder.genre_image.setImageResource(genresList.get(position).getThumbnail());

        holder.genre_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , Tap_Genre.class);
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                intent.putExtra("Genres" ,genresList.get(position).getTitle());
                intent.putExtra("Image" ,genresList.get(position).getThumbnail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genresList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView genre_image ;
        TextView genre_name;
        CardView genre_cardview;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            genre_image = itemView.findViewById(R.id.genre_image);
            genre_name = itemView.findViewById(R.id.genre_name);
            genre_cardview =itemView.findViewById(R.id.genre_cardview);
        }
    }

}
