package com.example.misha.audioapplication;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends Activity {

    AudioRecord audioRecord;
    boolean isReading = false;

    TextView textView;

    AudioSplitter audioSplitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);

        audioSplitter = new AudioSplitter(new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
            @Override
            public void onAudioChunkPulled(AudioChunk audioChunk) {
                mAudioTrack.write(audioChunk.toBytes(), 0, audioChunk.toBytes().length); //for playing audio
                //Add here what need to be done before saving on sd
            }
        }), file());

        setupRecorder();
    }

    private AudioTrack mAudioTrack;
    private int[] rate = {8000, 11025, 16000, 22050, 44100};
    private int sampleRate = rate[4];

    private void setupRecorder() {

        int minBuffer = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBuffer,
                AudioTrack.MODE_STREAM);

        mAudioTrack.setPlaybackRate(sampleRate);

        mAudioTrack.play();
    }

    /**
     * The method returns file for recoding
     * @returnfile for recoding
     */
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "demo.wav");
    }

    /**
     * The method returns info for recoding
     * @return info for recoding
     */
    private PullableSource mic() {
        return new PullableSource.Default(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100);
    }

    /**
     * The method starts recoding
     * @param v
     */
    public void recordStart(View v) {
        audioSplitter.startRecording();
        textView.setText("record start");
    }

    /**
     * The method stops the recoding
     * @param v
     */
    public void recordStop(View v) {
        audioSplitter.stopRecording();
        textView.setText("record stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isReading = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }
}