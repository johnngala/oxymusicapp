package com.oxymusic.myaudioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistsFragment extends Fragment {

    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;
    List<Playlist> playlists;

    public PlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

      playlists = new ArrayList<>();
        playlists.add(new Playlist( "add Playlist" ,R.drawable.bewedoc));
        playlists.add(new Playlist( "Party Playlist" ,R.drawable.bewedoc));
        playlists.add(new Playlist( "Happy Moments" ,R.drawable.bewedoc));
        playlists.add(new Playlist( "Love Songs" ,R.drawable.bewedoc));
        playlists.add(new Playlist( "Slow Music" ,R.drawable.bewedoc));
        playlists.add(new Playlist( "Memories" ,R.drawable.bewedoc));
        playlists.add(new Playlist( "Party Playlist" ,R.drawable.bewedoc));        playlists.add(new Playlist( "Party Playlist" ,R.drawable.bewedoc));


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //set the adapter and layout manager of the recycler view
       playlistAdapter= new PlaylistAdapter(getContext(), playlists);
        recyclerView.setAdapter(playlistAdapter);

       // recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 2 ));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }
}
