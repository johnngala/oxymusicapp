package com.oxymusic.myaudioplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.oxymusic.myaudioplayer.AddtoPlaylistAdapter.ViewHolder.add_songs_checkbox;
import static com.oxymusic.myaudioplayer.MainActivity.musicFiles;

public class Add_Songs_to_Playlist extends AppCompatActivity {
        RecyclerView recyclerView;
        AddtoPlaylistAdapter addtoPlaylistAdapter;
        Button add_btn;
        CheckBox checkbox = add_songs_checkbox;
        //Selected Songs Arraylist
       private ArrayList<MusicFiles> selectedSongs;
       ArrayList<MusicFiles> AddToSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__songs_to__playlist);

        recyclerView = findViewById(R.id.recyclerView);
        if(!(musicFiles.size() < 1)){
            addtoPlaylistAdapter = new AddtoPlaylistAdapter(getBaseContext() , musicFiles);
            recyclerView.setAdapter(addtoPlaylistAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext() ,RecyclerView.VERTICAL ,false));

        }

        //add songs button
        add_btn = findViewById(R.id.addsongs_button);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Add_Songs_to_Playlist.this, "Songs Added", Toast.LENGTH_SHORT).show();
                //check if checkbox is checked and add songs to "selectedSongs" Arraylist
                if(checkbox.isChecked()){
                    selectedSongs.addAll(musicFiles);
                }

                //pass selected songs to Playlist
                //AddToSongs = selectedSongs;
            }
        });
    }
}
