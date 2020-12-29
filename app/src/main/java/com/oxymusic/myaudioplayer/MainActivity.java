package com.oxymusic.myaudioplayer;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener , NavigationView.OnNavigationItemSelectedListener{
    public static final int REQUEST_CODE =1;
    static ArrayList<MusicFiles> musicFiles;
    //for repeat and shuffle buttons
    static boolean shuffleBoolean = false , repeatBoolean = false ;
    static ArrayList<MusicFiles>albums = new ArrayList<>();
    private String MY_SORT_PREF = "SortOrder" ;

    //Splash screen timeout
    //private static int SPLASH_TIME_OUT = 1000;

    //navigation drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    //loads main activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //handle splash screen
       // new Handler().postDelayed(new Runnable() {
         //   @Override
           // public void run() {
              //  Intent homeIntent  = new Intent(MainActivity.this ,HomeActivity.class);
              //  startActivity(homeIntent);
               // finish();
           // }
       // } ,SPLASH_TIME_OUT);

        //call permissions
        permission();

        //navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.dl);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open ,R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView =   findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);


        //Bottom Navigation
       // BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
       // bottomNav.setOnNavigationItemSelectedListener(navListener);
      //  getSupportFragmentManager().beginTransaction().replace(R.id.viewpager ,new SongsFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.website:
                Toast.makeText(this, "Website", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.developer:
                Toast.makeText(this, "Developer", Toast.LENGTH_SHORT).show();
                break;
            case R.id.version:
                Toast.makeText(this, "Version", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    //Bottom Navigation method
   // private BottomNavigationView.OnNavigationItemSelectedListener navListener =
          //  new BottomNavigationView.OnNavigationItemSelectedListener() {
              //  @Override
              //  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 //   Fragment selectedFragement = null;
                   // switch (item.getItemId()){
                      //  case R.id.nav_songs:
                       // selectedFragement = new SongsFragment();
                       // Toast.makeText(MainActivity.this, "Shows all your songs", Toast.LENGTH_SHORT).show();
                      //  break;
                       // case R.id.nav_playlists:
                        //    selectedFragement = new PlaylistsFragment();
                        //    Toast.makeText(MainActivity.this, "Create Playlists", Toast.LENGTH_SHORT).show();
                        //    break;
                        //case R.id.nav_genres:
                         //   selectedFragement = new GenreFragment();
                         //   Toast.makeText(MainActivity.this, "Group your songs according to genres", Toast.LENGTH_SHORT).show();
                         //   break;
                   // }
                   // getSupportFragmentManager().beginTransaction().replace(R.id.viewpager ,selectedFragement).commit();
                    //return  true;
               // }
         //   };

    //requests for permissions
    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this ,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}
            ,REQUEST_CODE);
        }
        else{
            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                musicFiles = getAllAudio(this);
                initViewPager();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this ,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,REQUEST_CODE);
            }
        }
    }

    //initialize viewpager
    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout =findViewById(R.id.tab_layout);
        //ViewPager Adapter that brings in the data into the ViewPager
        ViewPagerAdapter viewPagerAdapter =new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs" );
        viewPagerAdapter.addFragments(new AlbumFragment() , "Albums");


        // Not Added
       // viewPagerAdapter.addFragments(new PlaylistsFragment(),"Playlists");
        //viewPagerAdapter.addFragments(new GenreFragment() ,  "Genres");
        // viewPagerAdapter.addFragments(new My_Music_Fragment(), "My Music");
        //viewPagerAdapter.addFragments(new DiscoverFragment() , "Discover");

        viewPager.setAdapter(viewPagerAdapter);

        //Linking ViewPager with  TabLayout
        tabLayout.setupWithViewPager(viewPager);
    }



    //Create the  Viewpager Adapter
    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments; //For Fragments in the Viewpager
        private ArrayList<String> titles; //For title of the Fragments

        //Creates the View page adapter.The data will come from the fragments themselves
        //viewpager adapter constructor
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        //add fragments and titles to viewpager
        void addFragments ( Fragment fragment ,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        } //gets the fragments position i.e is it the 1st,2nd etc

        @Override
        public int getCount() {
            return fragments.size();
        }//return the number of fragments

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        } //gets the page title
    }

    //gets all the music files
    public  ArrayList<MusicFiles> getAllAudio( Context context){
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF , MODE_PRIVATE);
        String sortOrder  = preferences.getString("sorting" , "sortByName" );

        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();

        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();

        //Check sort order preference
        String order = null;

        //This Uri points to the location of the music files in the media store database
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder){
            case "sortByName" :
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortByDate" :
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "sortBySize" :
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }

        //selects what column to load from media store
        String [] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, //FOR PATH
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID

        };

        Cursor cursor = context.getContentResolver().query(uri,projection, null,null ,order); //order missing

        if( cursor != null){
            while(cursor.moveToNext()){
                String album =cursor.getString(0);
                String title =cursor.getString(1);
                String duration =cursor.getString(2);
                String path =cursor.getString(3);
                String artist =cursor.getString(4);
                String id = cursor.getString(5);

                //load songs with the following parameters and add them to the arraylist of music files  called tempAudioList declared above
                MusicFiles musicFiles = new MusicFiles(path ,title ,artist ,album ,duration ,id );
                tempAudioList.add(musicFiles);

                // if duplicate  albums
                if(!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        //return the music files
        return tempAudioList;
    }


    //responsible for showing menu items like the search bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search , menu );
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        //return modified
        return  super.onCreateOptionsMenu(menu);
    }


    //SearchView Methods
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles song : musicFiles){
            if(song.getTitle().toLowerCase().contains(userInput)){
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor  = getSharedPreferences(MY_SORT_PREF , MODE_PRIVATE).edit();
        switch (item.getItemId()){
            case R.id.by_name:
                editor.putString("sorting" , "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_date:
                editor.putString("sorting" , "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_size:
                editor.putString("sorting" , "sortBySize");
                editor.apply();
                this.recreate();
                break;
        }

        return  actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}
