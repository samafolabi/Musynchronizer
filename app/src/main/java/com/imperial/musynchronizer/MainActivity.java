package com.imperial.musynchronizer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String audioPath;
    String audioName;
    Uri audioURI;
    Uri audioURI1G;
    Uri audioURI2G;
    int oneORtwo;
    MediaPlayer mediaPlayer1;
    MediaPlayer mediaPlayer2;
    TextView emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailView = (TextView) findViewById(R.id.userEmail);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            emailView.setText(extras.getString("email"));
        }
    }

    public void isIntentSafe (Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivityForResult(intent, 1);
        }
    }

    public MediaPlayer playAudio (Uri uri){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    protected void audioSelect1 (View view) {
        Intent audio_intent = new Intent();
        audio_intent.setType("audio/*");
        audio_intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(audio_intent, "Select Audio File"), INTENT_CHOICE_REQUEST);
        Intent.createChooser(audio_intent, "Select Audio File");
        isIntentSafe(audio_intent);
        oneORtwo = 1;
    }

    protected void playMusic1 (View view){
        mediaPlayer1.start();
    }

    protected void pauseMusic1 (View view){
        mediaPlayer1.pause();
    }

    protected void audioSelect2 (View view) {
        Intent audio_intent = new Intent();
        audio_intent.setType("audio/*");
        audio_intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(audio_intent, "Select Audio File"), INTENT_CHOICE_REQUEST);
        Intent.createChooser(audio_intent, "Select Audio File");
        isIntentSafe(audio_intent);
        oneORtwo = 2;
    }

    protected void playMusic2 (View view){
        mediaPlayer2.start();
    }

    protected void pauseMusic2 (View view){
        mediaPlayer2.pause();
    }

    protected void playMusicAll (View view) {
        playMusic1(view);
        playMusic2(view);
    }

    protected void pauseMusicAll (View view) {
        pauseMusic1(view);
        pauseMusic2(view);
    }

    public void activateFuncs (){
        TextView textView1 = (TextView) findViewById(R.id.textview1);
        Button playButton1 = (Button) findViewById(R.id.play1);
        Button pauseButton1 = (Button) findViewById(R.id.pause1);
        TextView textView2 = (TextView) findViewById(R.id.textview2);
        Button playButton2 = (Button) findViewById(R.id.play2);
        Button pauseButton2 = (Button) findViewById(R.id.pause2);
        Button playAllButton = (Button) findViewById(R.id.playAll);
        Button pauseAllButton = (Button) findViewById(R.id.pauseAll);
        if (oneORtwo == 1) {
            Uri audioUri1 = audioURI;
            String audioName1 = audioName;
            audioURI1G = audioUri1;
            textView1.setText(audioName1);
            playButton1.setEnabled(true);
            pauseButton1.setEnabled(true);
            mediaPlayer1 = playAudio(audioURI1G);
        } else {
            Uri audioUri2 = audioURI;
            String audioName2 = audioName;
            audioURI2G = audioUri2;
            textView2.setText(audioName2);
            playButton2.setEnabled(true);
            pauseButton2.setEnabled(true);
            mediaPlayer2 = playAudio(audioURI2G);
        }
        if (playButton1.isEnabled() && playButton2.isEnabled()
                && pauseButton1.isEnabled() && pauseButton2.isEnabled()) {
            playAllButton.setEnabled(true);
            pauseAllButton.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }

                    audioPath = path;
                    audioName = displayName;
                    audioURI = uri;
                    activateFuncs();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
