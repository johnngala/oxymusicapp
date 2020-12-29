package com.oxymusic.myaudioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class My_Music_Fragment extends Fragment {
    View myFragment;
    ViewPager viewPager;
    TabLayout tabLayout;

    public My_Music_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.my_music_viewpager, container, false);

        viewPager = myFragment.findViewById(R.id.my_music_viewpager);
        tabLayout = myFragment.findViewById(R.id.my_music_tablayout);

        return myFragment;
    }

    //Call onActivity Created Method

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        My_Music_ViewPager_Adapter adapter = new My_Music_ViewPager_Adapter(getChildFragmentManager());
        adapter.addFragments(new PlaylistsFragment(),"Playlists");
        adapter.addFragments(new GenreFragment(),"Genres");
        adapter.addFragments(new FavouritesFragment() ,"Favourites");



        viewPager.setAdapter(adapter);
    }
}
