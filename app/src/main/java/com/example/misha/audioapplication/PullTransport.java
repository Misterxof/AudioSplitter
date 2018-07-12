package com.example.misha.audioapplication;

import android.media.AudioRecord;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface PullTransport
 */
public interface PullTransport {

    void start(OutputStream outputStream) throws IOException;

    void stop();

    PullableSource pullableSource();

    /**
     * Interface definition for a callback to be invoked when a chunk of audio is pulled from
     * {@link PullableSource}.
     */
    interface OnAudioChunkPulledListener {
        /**
         * The method called when {@link PullableSource} is pulled and returned{@link AudioChunk}.
         */
        void onAudioChunkPulled(AudioChunk audioChunk);
    }

    /**
     * Abstract Class AbstractPullTransport
     */
    abstract class AbstractPullTransport implements PullTransport {
        final PullableSource pullableSource;
        final OnAudioChunkPulledListener onAudioChunkPulledListener;
        private final UiThread uiThread = new UiThread();

        /**
         * The constructor of class AbstractPullTransport
         * @param pullableSource - pullable source
         * @param onAudioChunkPulledListener - onAudioChunkPulledListener
         */
        AbstractPullTransport(PullableSource pullableSource, OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this.pullableSource = pullableSource;
            this.onAudioChunkPulledListener = onAudioChunkPulledListener;
        }

        /**
         * The method starts to pull the {@link PullableSource} and transport it to {@link OutputStream}
         * @param outputStream the OutputStream where we want to transport the pulled audio data.
         * @throws IOException if there is any problem arise in pulling and transporting
         */
        @Override public void start(OutputStream outputStream) throws IOException {
            startPoolingAndWriting(pullableSource.preparedToBePulled(), pullableSource.pullSizeInBytes(), outputStream);
        }

        /**
         * The method starts polling and writing
         * @param audioRecord - audioRecord
         * @param pullSizeInBytes - pull size
         * @param outputStream - outputStream
         * @throws IOException
         */
        void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException {}

        /**
         * The method immediately stops pulling PullableSource
         */
        @Override public void stop() {
            pullableSource.isEnableToBePulled(false);
            pullableSource.audioRecord().stop();
            pullableSource.audioRecord().release();
        }

        /**
         * The method returns the object of type pullableSource which is used for pulling
         * @return the object of type pullableSource
         */
        public PullableSource pullableSource() {
            return pullableSource;
        }

        /**
         * The method sends the chunk for work
         * @param audioChunk - audio chunk
         */
        void postPullEvent(final AudioChunk audioChunk) {
            uiThread.execute(new Runnable() {
                @Override public void run() {
                    onAudioChunkPulledListener.onAudioChunkPulled(audioChunk);
                }
            });
        }
    }

    /**
     * Class WriteAction
     */
    final class WriteAction {
        /**
         * The method writes audioChunk in the outputStream
         * @param audioChunk - audio chunk
         * @param outputStream - output stream
         * @throws IOException
         */
        public void execute(AudioChunk audioChunk, OutputStream outputStream) throws IOException {
            outputStream.write(audioChunk.toBytes());
        }
    }

    /**
     * Class Default
     */
    final class Default extends AbstractPullTransport {

        private final WriteAction writeAction;

        /**
         * The constructor of class Default
         * @param pullableSource - pullableSource
         * @param onAudioChunkPulledListener - onAudioChunkPulledListener
         * @param writeAction - writeAction
         */
        public Default(PullableSource pullableSource,
                       OnAudioChunkPulledListener onAudioChunkPulledListener, WriteAction writeAction) {
            super(pullableSource, onAudioChunkPulledListener);
            this.writeAction = writeAction;
        }

        /**
         * The constructor of class Default
         * @param pullableSource - pullableSource
         * @param onAudioChunkPulledListener - onAudioChunkPulledListener
         */
        public Default(PullableSource pullableSource,
                       OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this(pullableSource, onAudioChunkPulledListener, new WriteAction());
        }

        /**
         * The method starts polling and writing
         * @param audioRecord - audioRecord
         * @param pullSizeInBytes - pull size
         * @param outputStream - outputStream
         * @throws IOException
         */
        @Override void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException {

            AudioChunk audioChunk = new AudioChunk.Bytes(new byte[pullSizeInBytes]);

            while (pullableSource.isEnableToBePulled()) {
                audioChunk.readCount(audioRecord.read(audioChunk.toBytes(), 0, pullSizeInBytes));
                if (AudioRecord.ERROR_INVALID_OPERATION != audioChunk.readCount()
                        && AudioRecord.ERROR_BAD_VALUE != audioChunk.readCount()) {
                    if (onAudioChunkPulledListener != null) {
                        postPullEvent(audioChunk);
                    }
                    writeAction.execute(audioChunk, outputStream);
                }
            }
        }
    }
}
