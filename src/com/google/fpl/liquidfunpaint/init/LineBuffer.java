package com.google.fpl.liquidfunpaint.init;

import com.google.fpl.liquidfunpaint.util.Vector2f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class LineBuffer {
    private static final String TAG = "LineBuffer";

    private int mBufferBlockStart = -1;
    private int mBufferBlockCurrent = -1;
    private int mNumPoints = 0;

    /**
     * This is a static buffer for storing generated points per line.
     * We allow LineBuffer to use this buffer in blocks.
     * Could be implemented as a ring buffer but currently we are going to throw
     * errors on memory overflow.
     */
    private static class LineGeneratedBuffer {
        private static final int FLUSH_LIMIT = 40;
        // 2 times flush limit for enough padding
        private static final int BLOCK_SIZE = FLUSH_LIMIT * 2;
        // Assume maximum of 40 unique pointers on a screen
        private static final int TOTAL_BUFFER_SIZE = BLOCK_SIZE * 40;
        private ByteBuffer mByteBuffer;
        private int mBufferEnd = 0;

        private LineGeneratedBuffer() {
            mByteBuffer = ByteBuffer
                    .allocateDirect(TOTAL_BUFFER_SIZE)
                    .order(ByteOrder.nativeOrder());
        }

        private int getNewBlock() {
            int newBlock = mBufferEnd;
            mBufferEnd += BLOCK_SIZE;
            if (mBufferEnd >= TOTAL_BUFFER_SIZE) {
                Log.e(TAG, "Buffer overflow in sGeneratedPoints! " +
                           "Increase buffer size or decrease flush size.");
            }
            return newBlock;
        }

        private int putPoint(int index, Vector2f point) {
            int currIndex = index;
            mByteBuffer.putFloat(currIndex, point.x);
            currIndex += 4;
            mByteBuffer.putFloat(currIndex, point.y);
            currIndex += 4;
            return currIndex;
        }

        private void reset() {
            mByteBuffer.clear();
            mBufferEnd = 0;
        }

        private ByteBuffer getRawBuffer() {
            return mByteBuffer;
        }
    }

    // The actual buffer for storing pointer inputs
    private static LineGeneratedBuffer sLineGeneratedBuffer =
            new LineGeneratedBuffer();

    public LineBuffer() {
        mBufferBlockStart = sLineGeneratedBuffer.getNewBlock();
        mBufferBlockCurrent = mBufferBlockStart;
    }

    public void putPoint(Vector2f point) {
        if (mBufferBlockCurrent != -1) {
             // Error checking
             if ((mBufferBlockCurrent - mBufferBlockStart) >=
                     LineGeneratedBuffer.BLOCK_SIZE) {
                 Log.e(TAG, "Overflow in a LineGeneratedBuffer block." +
                            "Increase block size or decrease flush limit.");
             } else {
                 mBufferBlockCurrent =
                     sLineGeneratedBuffer.putPoint(mBufferBlockCurrent, point);
                 ++mNumPoints;
             }
         }
    }

    public boolean needsFlush() {
        return ((mBufferBlockCurrent != -1) &&
                ((mBufferBlockCurrent - mBufferBlockStart) >=
                    LineGeneratedBuffer.FLUSH_LIMIT));
    }

    public void reset() {
        mBufferBlockCurrent = mBufferBlockStart;
        mNumPoints = 0;
    }

    public ByteBuffer getRawPointsBuffer() {
        return sLineGeneratedBuffer.getRawBuffer();
    }

    public int getBufferStart() {
        return mBufferBlockStart;
    }

    public int getNumPoints() {
        return mNumPoints;
    }


}
