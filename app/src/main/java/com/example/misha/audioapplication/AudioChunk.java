package com.example.misha.audioapplication;

/**
 * Interface AudioChunk
 */
public interface AudioChunk {

    byte[] toBytes();

    int readCount();

    void readCount(int numberOfUnitThatWereRead);

    /**
     * Class Bytes
     */
    final class Bytes implements AudioChunk {
        private final byte[] bytes;
        private int numberOfBytesRead;

        /**
         * The constructor of class Bytes
         * @param bytes - an array of type bytes
         */
        Bytes(byte[] bytes) {
            this.bytes = bytes;
        }

        /**
         * The method returns an array of type bytes
         * @return bytes - an array of type bytes
         */
        @Override public byte[] toBytes() {
            return bytes;
        }

        /**
         * The method returns number of read bytes
         * @return numberOfBytesRead
         */
        @Override public int readCount() {
            return numberOfBytesRead;
        }

        /**
         * The method sets the number of read bytes
         * @param numberOfUnitThatWereRead - number of read bytes
         */
        @Override public void readCount(int numberOfUnitThatWereRead) {
            this.numberOfBytesRead = numberOfUnitThatWereRead;
        }
    }
}