package edu.uic.swethag.cs478.funclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView titleTv, artistTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, stop, musicIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        setTitle("Oscar Tunes");

        titleTv = findViewById(R.id.song_title);
        artistTv = findViewById(R.id.song_artist);
        pausePlay = findViewById(R.id.pause_play);
        stop = findViewById(R.id.stop);
        musicIcon = findViewById(R.id.music_icon_big);

        Intent i = getIntent();
        String individualSongData = i.getStringExtra("song_data");
        String[] splitData = individualSongData.split("&", -1);
        byte[] imageBytes = Base64.decode(splitData[2], Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        titleTv.setText(splitData[0]);
        artistTv.setText(splitData[1]);
        musicIcon.setImageBitmap(decodedImage);

        pausePlay.setOnClickListener(v -> {
            try {
                boolean isPlaying = MainActivity.pausePlay();
                if (isPlaying) {
                    pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline);
                } else {
                    pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        stop.setOnClickListener(v -> {
            try {
                MainActivity.stop();
                pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}