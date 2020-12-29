package com.oxymusic.myaudioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenreFragment extends Fragment {
    RecyclerView recyclerView;
    GenreAdapter genreAdapter;
    List<Genres> genresList;
    public GenreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);


      genresList= new ArrayList<>();
        genresList.add(new Genres("Pop" ,R.drawable.bewedoc));
        genresList.add(new Genres("House" ,R.drawable.bewedoc));
        genresList.add(new Genres("Rock" ,R.drawable.bewedoc));
        genresList.add(new Genres("Hip Hop" ,R.drawable.bewedoc));
        genresList.add(new Genres("Blues" ,R.drawable.bewedoc));
        genresList.add(new Genres("Blues" ,R.drawable.bewedoc));
        genresList.add(new Genres("Jazz" ,R.drawable.bewedoc));
        genresList.add(new Genres("Blues" ,R.drawable.bewedoc));


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //set the adapter and layout manager of the recycler view
        genreAdapter= new GenreAdapter(getContext(), genresList);
        recyclerView.setAdapter(genreAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 2 ));

        return view;
    }
}
