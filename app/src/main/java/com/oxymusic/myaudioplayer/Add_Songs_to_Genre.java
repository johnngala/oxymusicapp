package com.oxymusic.myaudioplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.oxymusic.myaudioplayer.MainActivity.musicFiles;

public class Add_Songs_to_Genre extends AppCompatActivity {
    RecyclerView recyclerView;
    AddtoGenre addtoGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__songs_to__genre);

        recyclerView = findViewById(R.id.recyclerView);
        if (!(musicFiles.size() < 1)) {
            addtoGenre = new AddtoGenre(getBaseContext(), musicFiles);
            recyclerView.setAdapter(addtoGenre);
            recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
        }
    }
}
