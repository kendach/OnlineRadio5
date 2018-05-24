package com.domagojkenda.onlineradio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;
import saschpe.exoplayer2.ext.icy.IcyHttpDataSourceFactory;

public class MainActivity extends AppCompatActivity {

    Button b_play;
    TextView textView;

    ExoPlayer player;

    boolean prepared = false;
    boolean started = false;

    String stream = "http://161.53.122.184:8000/aacPlus48.aac";
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private IcyHttpDataSourceFactory icyHttpDataSourceFactory;
    // "http://144.217.153.67/live?icy=http";
    // http://161.53.122.184:8000/aacPlus48.aac


    /*
    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }
    */

    private void initializePlayer() {
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        /*
        SimpleExoPlayerView simpleExoPlayerView = findViewById(R.id.exoplayer);
        simpleExoPlayerView.setPlayer(player);
        */

        // Produces DataSource instances through which media data is loaded.
        String userAgent = Util.getUserAgent(
                this, "RadioStudentExoplayer");
        dataSourceFactory = new DefaultDataSourceFactory(this, userAgent);

        // Dodano za: ExoPlayer2 Shoutcast Metadata Protocol (ICY) extension

        // Custom HTTP data source factory which requests Icy metadata and parses it if
        // the stream server supports it
        // Log.d("XXX", "onIcyHeaders: %s".format(icyHeaders.toString()))
        icyHttpDataSourceFactory = new IcyHttpDataSourceFactory.Builder(userAgent)
                .setIcyHeadersListener(this::iceHeader)
                .setIcyMetadataChangeListener(this::icyMetadata)
                .build();

        // Produces Extractor instances for parsing the media data.
        extractorsFactory = new DefaultExtractorsFactory();
    }

    private void iceHeader(IcyHttpDataSource.IcyHeaders icyHeaders)
    {
        // Log.d("XXX", "onIcyHeaders: %s".format(icyHeaders.toString()))
        System.out.println("onIcyHeaders: %s".format(icyHeaders.toString()));
        textView.setText(icyHeaders.toString());
    }

    private void icyMetadata(IcyHttpDataSource.IcyMetadata icyMetadata)
    {
        System.out.println("onIcyMetaData: %s".format(icyMetadata.toString()));
        textView.setText(icyMetadata.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_play = (Button) findViewById(R.id.b_play);
        b_play.setEnabled(false);
        b_play.setText("LOADING");

        textView = (TextView) findViewById(R.id.textView);
        textView.setEnabled(true);
        textView.setText(" ... loading ...");

        initializePlayer();

        // mediaPlayer = new MediaPlayer();
        // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new PlayerTask().execute(stream);

        b_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started){
                    started = false;
                    // mediaPlayer.pause();
                    player.setPlayWhenReady(false);
                    b_play.setText("PLAY");
                } else {
                    started = true;
                    // mediaPlayer.start();
                    player.setPlayWhenReady(true);
                    b_play.setText("PAUSE");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(started){
            // mediaPlayer.pause();
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started){
            // mediaPlayer.start();
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared){
            // mediaPlayer.release();
            player.setPlayWhenReady(false);
        }
    }

    // * * * * * * *

    class PlayerTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            //mediaPlayer.setDataSource(strings[0]);
            // mediaPlayer.prepare();
            // This is the MediaSource representing the media to be played.
            Uri videoUri = Uri.parse(strings[0]);
            MediaSource videoSource = new ExtractorMediaSource(videoUri,
                    // dataSourceFactory,
                    icyHttpDataSourceFactory,
                    extractorsFactory,
                    null, null);

            // Prepare the player with the source.
            player.prepare(videoSource);
            prepared = true;
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            b_play.setEnabled(true);
            b_play.setText("PLAY");
        }
    }

}

