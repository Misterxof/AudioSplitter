package com.example.misha.audioapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Build;
import android.util.Log;

/**
 * Interface PullableSource
 */
public interface PullableSource{

    int pullSizeInBytes();

    void isEnableToBePulled(boolean enabledToBePulled);

    boolean isEnableToBePulled();

    AudioRecord preparedToBePulled();

    int channelPositionMask();

    int frequency();

    byte bitsPerSample();

    AudioRecord audioRecord();

    /**
     * Class Default
     */
    class Default implements PullableSource {
        private final int audioSource;
        private final int channelPositionMask;
        private final int frequency;
        private final int audioEncoding;

        private final int pullSizeInBytes;
        private volatile boolean pull;
        public AudioRecord audioRecordd;

        /**
         * The constructor of class Default
         * @param audioSource - audio source
         * @param audioEncoding - audio encoding type
         * @param channelPositionMask - channel position mask
         * @param frequency - frequency
         */
        public Default(int audioSource, int audioEncoding, int channelPositionMask, int frequency) {
            this.audioSource = audioSource;
            this.audioEncoding = audioEncoding;
            this.channelPositionMask = channelPositionMask;
            this.frequency = frequency;
            this.pullSizeInBytes =  AudioRecord.getMinBufferSize(frequency, channelPositionMask, audioEncoding);

            this.audioRecordd = new AudioRecord(audioSource, frequency, channelPositionMask, audioEncoding, pullSizeInBytes);
        }

        /**
         * The method returns channelPositionMask
         * @return channelPositionMask
         */
        @Override public int channelPositionMask() {
            return channelPositionMask;
        }

        /**
         * The method returns frequency
         * @return frequency
         */
        @Override public int frequency() {
            return frequency;
        }

        /**
         * The method returns bits per sample
         * @return byte - bits per sample
         */
        @Override public byte bitsPerSample() {
            if (audioEncoding == AudioFormat.ENCODING_PCM_16BIT) {
                return 16;
            } else if (audioEncoding == AudioFormat.ENCODING_PCM_8BIT) {
                return 8;
            } else {
                return 16;
            }
        }

        /**
         * The method returns the object of type AudioRecord
         * @return audioRecordd - the object of type AudioRecord
         */
        @Override public AudioRecord audioRecord() {
            return audioRecordd;
        }

        /**
         * The method returns pull size in bytes(minimum buffer size)
         * @return pull size in bytes
         */
        @Override
        public int pullSizeInBytes() {
            return pullSizeInBytes;
        }

        /**
         * The method sets enabledToBePulled variable
         * @param enabledToBePulled - flag that shows that source can be pulled
         */
        @Override
        public void isEnableToBePulled(boolean enabledToBePulled) {
            this.pull = enabledToBePulled;
        }

        /**
         * The method returns enabledToBePulled variable
         * @return enabledToBePulled
         */
        @Override
        public boolean isEnableToBePulled() {
            return pull;
        }

        /**
         * The method starts recoding and sets enabledToBePulled in true
         * and returns the object of type AudioRecord
         * @return the object of type AudioRecord
         */
        @Override
        public AudioRecord preparedToBePulled() {
            final AudioRecord audioRecord = audioRecordd;
            audioRecord.startRecording();
            isEnableToBePulled(true);
            return audioRecord;
        }
    }
}
