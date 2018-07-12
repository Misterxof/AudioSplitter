package com.example.misha.audioapplication;

import android.os.Handler;
import android.os.Looper;

/**
 * Class UiThread
 */
final class UiThread{
  private static final Handler handler = new Handler(Looper.getMainLooper());

  /**
   * The method executes the {@code Runnable} on UI Thread
   * @param runnable
   */
  public void execute(Runnable runnable) {
    handler.post(runnable);
  }
}