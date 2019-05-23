package com.example.nhom9.musicplayer.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.nhom9.musicplayer.Activity.Activity_play_nhac;
import com.example.nhom9.musicplayer.Common.PlaybackStatus;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.DatabaseAccess.CaSiService;
import com.example.nhom9.musicplayer.Fragment.list_bai_hat_playlist;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.Model.CaSi;
import com.example.nhom9.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener{

    public static final String ACTION_PLAY = "com.example.nhom9.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.nhom9.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.nhom9.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.nhom9.musicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.nhom9.musicplayer.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;



    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    private MediaPlayer mediaPlayer;
    //path to the audio file
    private String mediaFile;
    //Used to pause/resume MediaPlayer
    private int resumePosition = 0;

    private AudioManager audioManager;

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    //List of available Audio files
    private ArrayList<BaiHat> listBaiHat;
    private int baihatIndex = -1;
    private BaiHat activeBaiHat; //an object on the currently playing audio

    private boolean isRandom;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    private CaSiService caSiService;

    public MediaPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();

        try {
            caSiService = new CaSiService(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
//        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    /**
     *
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeBaiHat.getUrlBaiHat());
//            Activity_play_nhac.baiHat = activeBaiHat;
            Activity_play_nhac.comingBaiHat = activeBaiHat;
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    /**
     *
     */
    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    /**
     *
     */
    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     *
     */
    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     *
     */
    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }



    /**
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
            if(mediaPlayer.isLooping()){
                resetMediaForLooping();
            }else {
                if(isRandom){
                    processShuffled();
                }else{
                    skipToNext();
                }
            }
        updateMetaData();
        buildNotification(PlaybackStatus.PLAYING);
//        //Invoked when playback of a media source has completed.
//        stopMedia();
//
//        removeNotification();
//        //stop the service
//        stopSelf();
    }

    //Handle errors

    /**
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    /**
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    /**
     *
     * @param mp
     */
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    /**
     *
     * @param focusState
     */
    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /**
     *
     * @return
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    /**
     *
     * @return
     */
    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    //Becoming noisy
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }


    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the new media index form SharedPreferences
            if(!isCurrentSong(Activity_play_nhac.comingBaiHat)){
                baihatIndex = getSongIndex(Activity_play_nhac.comingBaiHat.getIdBaiHat());
                if (baihatIndex != -1 && baihatIndex < listBaiHat.size()) {
                    //index is in a valid range
                    activeBaiHat = listBaiHat.get(baihatIndex);
                } else {
                    stopSelf();
                }


                //A PLAY_NEW_AUDIO action received
                //reset mediaPlayer to play the new Audio
                stopMedia();
                mediaPlayer.reset();
                initMediaPlayer();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Activity_play_nhac.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),R.drawable.image5);
        if(activeBaiHat.getHinhAnh()!=null){
            albumArt = BitmapFactory.decodeByteArray(activeBaiHat.getHinhAnh(), 0, activeBaiHat.getHinhAnh().length); //replace with medias albumArt
        }
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, caSiService.layTenCaSi(activeBaiHat.getIdCasi()))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Không có")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeBaiHat.getTenBaiHat())
                .build());
    }


    private void skipToNext() {

        if (baihatIndex == listBaiHat.size() - 1) {
            //if last in playlist
            baihatIndex = 0;
            activeBaiHat = listBaiHat.get(baihatIndex);

        } else {
            //get next in playlist
            activeBaiHat = listBaiHat.get(++baihatIndex);
        }

        //Update stored index
//        new StorageUtil(getApplicationContext()).storeAudioIndex(baihatIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {

        if (baihatIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            baihatIndex = listBaiHat.size() - 1;
            activeBaiHat = listBaiHat.get(baihatIndex);
        } else {
            //get previous in playlist
            activeBaiHat = listBaiHat.get(--baihatIndex);
        }

        //Update stored index
//        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }


    private void buildNotification(PlaybackStatus playbackStatus) {

        /**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.image5);
        if(activeBaiHat.getHinhAnh()!=null){
            largeIcon = BitmapFactory.decodeByteArray(activeBaiHat.getHinhAnh(), 0, activeBaiHat.getHinhAnh().length); //replace with medias albumArt
        }

//        Bitmap largeIcon = BitmapFactory.decodeByteArray(activeBaiHat.getHinhAnh(), 0, activeBaiHat.getHinhAnh().length); //replace with your own image

        Intent intent = new Intent(getApplicationContext(), Activity_play_nhac.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(false)
                // Set the Notification style
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                //.setColor(getResources().getColor(R.color.colorPrimary))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText("Thể hiện : "+caSiService.layTenCaSi(activeBaiHat.getIdCasi())) //activeAudio.getArtist()
                .setContentTitle(activeBaiHat.getTenBaiHat()) //activeAudio.getAlbum()
//                .setContentInfo(activeBaiHat.getTenBaiHat())
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2))
                .setAutoCancel(false);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }



    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    /**
     * The onStartCommand() handles the initialization of the MediaPlayer
     * and the focus request to make sure there are no other apps playing media.
     * In the onStartCommand() code I added an extra try-catch block to make sure the getExtras() method doesn’t throw a NullPointerException.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            try{
                listBaiHat = Activity_play_nhac.currentPlayList;
            }catch (Exception ignore){}
            if(getCurrentBaiHat()==null){
                baihatIndex = getSongIndex(Activity_play_nhac.comingBaiHat.getIdBaiHat());
            }
            if (baihatIndex != -1 && baihatIndex < listBaiHat.size()) {
                //index is in a valid range
                activeBaiHat = listBaiHat.get(baihatIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }
//HERE TODO
        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);


//        try {
//            //An audio file is passed to the service through putExtra();
//            mediaFile = intent.getExtras().getString("media");
//        } catch (NullPointerException e) {
//            stopSelf();
//        }
//
//        //Request audio focus
//        if (requestAudioFocus() == false) {
//            //Could not gain focus
//            stopSelf();
//        }
//
//        if (mediaFile != null && mediaFile != "")
//            initMediaPlayer();
//
//        return super.onStartCommand(intent, flags, startId);
    }

    public boolean btnPlayStopClick(){
        if(mediaPlayer.isPlaying()){
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
            return true;
        }else{
            resumeMedia();
            buildNotification(PlaybackStatus.PLAYING);
            return false;
        }
    }

    public boolean getMediaPlayerState(){
        return mediaPlayer.isPlaying();
    }

    public boolean isMediaPlayerLooping(){
        return mediaPlayer.isLooping();
    }

    public boolean isMediaPlayerRandom(){
        return isRandom;
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getSongIndex(int id){
        for(int i = 0;i<listBaiHat.size();i++) {
            if (id == listBaiHat.get(i).getIdBaiHat()) {
                activeBaiHat = listBaiHat.get(i);
                baihatIndex = i;
            }
        }
        return baihatIndex;
    }

    public boolean isCurrentSong(BaiHat song){
        return song.getIdBaiHat() == activeBaiHat.getIdBaiHat();
    }


    public int setSongIndex(int id){
        for(int i = 0;i<listBaiHat.size();i++) {
            if (id == listBaiHat.get(i).getIdBaiHat()) {
                activeBaiHat = listBaiHat.get(i);
                baihatIndex = i;
            }
        }
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
        updateMetaData();
        buildNotification(PlaybackStatus.PLAYING);
        return baihatIndex;
    }

    public int getCurrentIndex(){
        return baihatIndex;
    }

    public BaiHat getCurrentBaiHat(){
        return activeBaiHat;
    }
    public CaSiService getCaSiService(){
        return caSiService;
    }


    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isMediaPlayerNull(){
        return mediaPlayer == null;
    }

    public void btnNextClick(){
        skipToNext();
        updateMetaData();
        buildNotification(PlaybackStatus.PLAYING);
    }

    public void btnPreviousClick(){
        skipToPrevious();
        updateMetaData();
        buildNotification(PlaybackStatus.PLAYING);
    }


    public void setMediaPlayerLooping(boolean looping){
        mediaPlayer.setLooping(looping);
    }

    private void resetMediaForLooping(){
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    public void setSeekTo(int position){
        mediaPlayer.seekTo(position);
    }

    public void setShuffled(boolean shuffled){
        isRandom = shuffled;
    }

    public void processShuffled(){
        Random random = new Random();
        baihatIndex = random.nextInt(listBaiHat.size());
        activeBaiHat = listBaiHat.get(baihatIndex);
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    public void updateListBaiHat(ArrayList<BaiHat> listBaiHat){
        if(this.listBaiHat != listBaiHat){
            this.listBaiHat = listBaiHat;
        }

//        activeBaiHat = listBaiHat.get(setSongIndex());
//        buildNotification(PlaybackStatus.PLAYING);
    }

}
