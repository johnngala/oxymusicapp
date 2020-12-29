package com.oxymusic.myaudioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Tap_Playlist extends AppCompatActivity {
    private TextView playlist_name ,number_of_songs;
    private Button  add_songs_to_playlist ;
    private FloatingActionButton fab;
    private ImageView playlist_image;
    static ArrayList<MusicFiles> mFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap__playlist);

        // number_of_songs = (TextView) findViewById(R.id.number_of_songs);
        playlist_name = (TextView) findViewById(R.id.playlist_name);
        playlist_image= (ImageView) findViewById(R.id.playlist_image);

        // Receive data
        Intent intent = getIntent();
        String playlist = intent.getExtras().getString("Playlist");
        int image = intent.getExtras().getInt("Playlist Image") ;

        // Setting values
        playlist_name.setText(playlist);
        playlist_image.setImageResource(image);

        //setting onclick to fab
        fab= (FloatingActionButton) findViewById(R.id.add_songs_to_playlist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Tap_Playlist.this, "fab clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Tap_Playlist.this , Add_Songs_to_Playlist.class);
                startActivity(intent);

            }
        });
    }
}
