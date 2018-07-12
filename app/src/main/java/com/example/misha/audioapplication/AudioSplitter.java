package com.example.misha.audioapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class AudioSplitter
 */
public class AudioSplitter extends AbstractRecorder{

    /**
     * The constructor of class AudioSplitter
     * @param pullTransport - pullTransport
     * @param file - file for recording
     */
   public AudioSplitter(PullTransport pullTransport, File file) {
       super(pullTransport, file);
    }

    /**
     * The method stops the recoding
     */
    @Override public void stopRecording() {
        try {
            super.stopRecording();
            writeWavHeader();
        } catch (IOException e) {
            throw new RuntimeException("Error in applying wav header", e);
        }
    }

    /**
     * The method writes wav header
     * @throws IOException
     */
    private void writeWavHeader() throws IOException {
        final RandomAccessFile wavFile = randomAccessFile(file);
        wavFile.seek(0); // to the beginning
        wavFile.write(new WavHeader(pullTransport.pullableSource(), file.length()).toBytes());
        wavFile.close();
    }

    /**
     * The method returns RandomAccessFile
     * @param file - file for recoding
     * @return RandomAccessFile
     */
    private RandomAccessFile randomAccessFile(File file) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return randomAccessFile;
    }
}