package com.oxymusic.myaudioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tap_Genre extends AppCompatActivity {

    private TextView genre_name ,number_of_songs;
   // private Button genre_shuffle_button ;
    private ImageView genre_img;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_genre);

        // number_of_songs = (TextView) findViewById(R.id.number_of_songs);
        genre_name = (TextView) findViewById(R.id.genre_name);
        genre_img= (ImageView) findViewById(R.id.genre_image);

        // Receive data
        Intent intent = getIntent();
        String genre = intent.getExtras().getString("Genres");
        int image = intent.getExtras().getInt("Image") ;

        // Setting values
        genre_name.setText(genre);
        genre_img.setImageResource(image);

        //set onclick to fab
        floatingActionButton= (FloatingActionButton) findViewById(R.id.add_songs_to_genre);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Tap_Genre.this, "fab Clicked", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(Tap_Genre.this , Add_Songs_to_Genre.class);
                startActivity(intent);
            }
        });
    }
}
