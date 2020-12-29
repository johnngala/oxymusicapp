package com.oxymusic.myaudioplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.oxymusic.myaudioplayer.AlbumDetailsAdapter.albumFiles;
import static com.oxymusic.myaudioplayer.MainActivity.repeatBoolean;
import static com.oxymusic.myaudioplayer.MainActivity.shuffleBoolean;
import static com.oxymusic.myaudioplayer.MusicAdapter.mFiles;


public class PlayerActivity extends AppCompatActivity  implements ActionPlaying , ServiceConnection {

    //declaring all the views in player activity
    TextView song_name ,artist_name , duration_played ,duration_total;
    ImageView cover_art,nextBtn ,prevBtn ,backBtn ,shuffleBtn ,repeatBtn ;
    FloatingActionButton playPauseBtn ;
    SeekBar seekBar;
    //position -1 because we passed the arraylist of musicfiles which starts at index 0
    int position = -1 ;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri ;

    // public static MediaPlayer mediaPlayer;

    //post delays for runonuothread
    private Handler handler = new Handler();

    private Thread playThread,prevThread,nextThread;
    MusicService musicService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity to  full screen
        setFullScreen();
        setContentView(R.layout.activity_player);
        //hides title bar
        getSupportActionBar().hide();

        initViews();
        getIntentMethod();

        //seekbar methods
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(musicService != null &&  fromUser){
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //thread that plays a song
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService != null){
                    int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        //shuffle button
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBoolean){
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                }
                else{
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        //repeat button
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean){
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                }
                else{
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
    }

    @Override
    protected void onResume() {
        //called  to bind to service
        Intent intent = new Intent(this ,MusicService.class);
        bindService(intent,this ,BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    //called when service disconnects
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    //controls previous button
    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    // previous button callback
    public void prevBtnClicked() {
        if(musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1 );
            }
            else if(!shuffleBoolean && !repeatBoolean){
                position = ( ( position  - 1 ) < 0 ? (listSongs.size() -1 ) : position -1);
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        }
        else{
            musicService.stop();
            musicService.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1 );
            }
            else if(!shuffleBoolean && !repeatBoolean){
                position = ( ( position  - 1 ) < 0 ? (listSongs.size() -1 ) : position -1);
            }

            uri = Uri.parse(listSongs.get(position).getPath());
           musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    //controls next button
    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    //next button callback
    public void nextBtnClicked() {
        if(musicService.isPlaying()){
            musicService.stop();
            musicService.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1 );
            }
            else if(!shuffleBoolean && !repeatBoolean){
                position = ( ( position + 1) % listSongs.size());
            }
            //else position will be position
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        }
        else{
            musicService.stop();
            musicService.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1 );
            }
            else if(!shuffleBoolean && !repeatBoolean){
                position = ( ( position + 1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
           musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
           musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    //generates a random number in the range of no. of songs
    private int getRandom(int i) {
        Random random =new Random();
        return random.nextInt(i + 1);
    }

    //controls play button
    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    //play pause callback
    public void playPauseBtnClicked() {
        if(musicService.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.ic_play);
            musicService.showNotification(R.drawable.ic_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }

        //playing after pause
        else{
            musicService.showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition() /1000 ;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    //delays playing of a song by a second
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    //formats durations
    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition %60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":"  + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1){
            return totalNew;
        }
        else{
            return totalOut;
        }
    }


    //from player activity intent
    private void getIntentMethod() {
        position = getIntent().getIntExtra("position" , -1);
        String sender = getIntent().getStringExtra("sender");

        //get intent from albumDetails
        if(sender != null && sender.equals("albumDetails")){
            listSongs = albumFiles;
        }
        else{
            listSongs = mFiles;
        }
        if(listSongs != null){
          //some code removed
             playPauseBtn.setImageResource(R.drawable.ic_pause);
             uri = Uri.parse(listSongs.get(position).getPath());
         }
         Intent intent = new Intent(this ,MusicService.class);
         intent.putExtra("servicePosition" ,position);
         startService(intent);

    }

    //initializes the views
    private void initViews() {
        song_name =findViewById(R.id.song_name);
        artist_name =findViewById(R.id.artist_name);
        duration_played =findViewById(R.id.durationPlayed);
        duration_total =findViewById(R.id.durationTotal);
        cover_art =findViewById(R.id.cover_art);
        nextBtn =findViewById(R.id.id_next);
        prevBtn =findViewById(R.id.id_prev);
        backBtn =findViewById(R.id.back_btn);
        shuffleBtn =findViewById(R.id.id_shuffle);
        repeatBtn =findViewById(R.id.id_repeat);
        playPauseBtn =findViewById(R.id.play_pause);
       seekBar =findViewById(R.id.seekBar);
    }

    //for getting song metadata like duration,album art...
    public void metaData( Uri uri ){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();


        //player activity color taken from album art using palette api
        Bitmap bitmap ;
        if(art != null){

            bitmap = BitmapFactory.decodeByteArray(art ,0, art.length);
            ImageAnimation(this ,cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch != null){
                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int [] {swatch.getRgb(), 0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }
                    else{
                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int [] {0xff000000, 0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }
        else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.bewedoc)
                    .into(cover_art);
            ImageView gredient = findViewById(R.id.imageViewGredient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gredient.setBackgroundResource(R.drawable.gredient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }

    //fade out and fade in animation when song is changed
        public void ImageAnimation(final Context context, final ImageView imageView , final Bitmap bitmap){
        Animation animOut  = AnimationUtils.loadAnimation(context ,android.R.anim.fade_out);
         final Animation animIn = AnimationUtils.loadAnimation(context ,android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                    imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
            imageView.startAnimation(animOut);
        }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        //toast to show service connected
       // Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.onCompleted();
        //pasted
        musicService.showNotification(R.drawable.ic_pause);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

}
