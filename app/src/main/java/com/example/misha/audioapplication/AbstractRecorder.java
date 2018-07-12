package com.example.misha.audioapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract class AbstractRecorder
 */
public abstract class AbstractRecorder {

  protected final PullTransport pullTransport;
  protected final File file;
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private OutputStream outputStream;

  private final Runnable recordingTask = new Runnable() {
    @Override public void run() {
      try {
        pullTransport.start(outputStream);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (IllegalStateException e) {
        throw new RuntimeException("AudioRecord state has uninitialized state", e);
      }
    }
  };

  /**
   * The constructor of class AbstractRecorder
   * @param pullTransport - pull transport
   * @param file - file for recording
   */
  protected AbstractRecorder(PullTransport pullTransport, File file) {
    this.pullTransport = pullTransport;
    this.file = file;
  }

  /**
   * The method sets outputStream and submit recodingTask
   */
  public void startRecording() {
    outputStream = outputStream(file);
    executorService.submit(recordingTask);
  }

  /**
   * The method initializes file for recoding(FileOutputStream)
   * and returns outputStream
   * @param file - file for recoding
   * @return outputStream
   */
  private OutputStream outputStream(File file) {
    if (file == null) {
      throw new RuntimeException("file is null !");
    }
    OutputStream outputStream;

    try {
      outputStream = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(
          "could not build OutputStream from" + " this file " + file.getName(), e);
    }
    return outputStream;
  }

  /**
   * The method stops pullTransport and flushes and closes output stream
   * @throws IOException
   */
  public void stopRecording() throws IOException {
    pullTransport.stop();
    outputStream.flush();
    outputStream.close();
  }
}
