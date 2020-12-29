package com.oxymusic.myaudioplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import static com.oxymusic.myaudioplayer.ApplicationClass.ACTION_NEXT;
import static com.oxymusic.myaudioplayer.ApplicationClass.ACTION_PLAY;
import static com.oxymusic.myaudioplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.oxymusic.myaudioplayer.ApplicationClass.CHANNEL_ID_2;
import static com.oxymusic.myaudioplayer.PlayerActivity.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1 ;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext() , "My Audio");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind" ,"Method");
        return mBinder;
    }


//returns instance of the service
    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition" ,-1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition != -1){
            playMedia(myPosition);
        }

        //play songs from notification
        if(actionName != null){
            switch (actionName){
                case "playPause":
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT).show();
                    if(actionPlaying != null){
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "next":
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                    if(actionPlaying != null){
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
                    Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                    if(actionPlaying != null){
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia( int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles != null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start(){
        mediaPlayer.start();
    }

    boolean isPlaying(){
        return  mediaPlayer.isPlaying();
    }

    void stop(){
        mediaPlayer.stop();
    }

    void release(){
        mediaPlayer.release();
    }

    int getDuration(){
        return mediaPlayer.getDuration();
    }

    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    int getCurrentPosition(){
        return  mediaPlayer.getCurrentPosition();
    }

    //create media player
    void createMediaPlayer(int positionInner){
        position = positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext() ,uri);
    }

    void pause(){
        mediaPlayer.pause();
    }
    void onCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }

    //called when song is completed
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(actionPlaying != null){
          actionPlaying.nextBtnClicked();
          if(mediaPlayer != null){
              createMediaPlayer(position);
              mediaPlayer.start();
              onCompleted();
          }
        }
    }

    void showNotification(int playPauseBtn){
        //click notification = go to activity
        Intent intent = new Intent(this ,NotificationReceiver.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this ,0 ,intent ,0);

        Intent prevIntent = new Intent(this ,NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent
                .getBroadcast(this ,0 ,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this ,NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent
                .getBroadcast(this ,0 ,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this ,NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent
                .getBroadcast(this ,0 ,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //get album art to show in notification large icon
        byte[] picture = null;
        picture = getAlbumArt(musicFiles.get(position).getPath());
        Bitmap thumb = null;
        if(picture != null){
            thumb = BitmapFactory.decodeByteArray(picture , 0 ,picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources() ,R.drawable.bewedoc);
        }

        //create notification
        Notification notification = new NotificationCompat.Builder(this , CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous ,"Previous" ,prevPending)
                .addAction(playPauseBtn ,"Pause" ,pausePending)
                .addAction(R.drawable.ic_skip_next ,"Next" ,nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        //startForeground(0 ,notification);
        NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       notificationManager.notify(0 ,notification);


    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    //initialize action playing
    void  setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }
}
