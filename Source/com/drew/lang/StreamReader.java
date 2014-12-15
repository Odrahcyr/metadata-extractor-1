/*
 * Copyright 2002-2014 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.lang;

import com.drew.lang.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class StreamReader extends SequentialReader
{
    @NotNull
    private final InputStream _stream;

    @SuppressWarnings("ConstantConditions")
    public StreamReader(@NotNull InputStream stream)
    {
        if (stream == null)
            throw new NullPointerException();

        _stream = stream;
    }

    @Override
    protected byte getByte() throws IOException
    {
        int value = _stream.read();
        if (value == -1)
            throw new EOFException("End of data reached.");
        return (byte)value;
    }

    @NotNull
    @Override
    public byte[] getBytes(int count) throws IOException
    {
        byte[] bytes = new byte[count];
        int totalBytesRead = 0;

        while (totalBytesRead != count) {
            final int bytesRead = _stream.read(bytes, totalBytesRead, count - totalBytesRead);
            if (bytesRead == -1)
                throw new EOFException("End of data reached.");
            totalBytesRead += bytesRead;
            assert(totalBytesRead <= count);
        }

        return bytes;
    }

    @Override
    public void skip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be zero or greater.");

        long skippedCount = skipInternal(n);

        if (skippedCount != n)
            throw new EOFException(String.format("Unable to skip. Requested %d bytes but skipped %d.", n, skippedCount));
    }

    @Override
    public boolean trySkip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be zero or greater.");

        return skipInternal(n) == n;
    }

    private long skipInternal(long n) throws IOException
    {
        // It seems that for some streams, such as BufferedInputStream, that skip can return
        // some smaller number than was requested. So loop until we either skip enough, or
        // InputStream.skip returns zero.
        //
        // See http://stackoverflow.com/questions/14057720/robust-skipping-of-data-in-a-java-io-inputstream-and-its-subtypes
        //
        long skippedTotal = 0;
        while (skippedTotal != n) {
            long skipped = _stream.skip(n - skippedTotal);
            assert(skipped >= 0);
            skippedTotal += skipped;
            if (skipped == 0)
                break;
        }
        return skippedTotal;
    }
    
    /**
     * Skips the given bytes or throws an exception if the skip failed. This method will try to skip n-1 first and then it will try to read
     * the last byte. This way we can check if we skipped past EOF.
     * 
     * @param n
     *            the number of bytes to be skipped
     * @throws EOFException
     *             , if the end of the Stream is reached
     * 
     * @see FileInputStream#skip(long)
     * @see <a href=" http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4454092"> JDK-4178064 : java.io.FileInputStream.skip returns
     *      incorrect result at EOF</a>
     */
    public void skipFully(final long n) throws IOException 
    {
        if (n <= 0)
            throw new IllegalArgumentException("n must be greater than zero.");
        long toSkip = n - 1;
        while (toSkip > 0) 
        {
            long amt = _stream.skip(toSkip);
            // skip() should never return a negative number. However the according javadoc is a bit vague in this matter and thus not
            // particularly guarantee that.
            if (amt <= 0) 
            {
                if (_stream.read() == -1)
                    throw new EOFException("reached end of stream after skipping " + (n - toSkip) + " bytes; " + n + " bytes expected");
                else
                    amt = 1;
            }

            toSkip -= amt;
        }

        if (_stream.read() == -1) 
            throw new EOFException("reached end of stream after skipping " + (n - 1) + " bytes; " + n + " bytes expected");
    }
}
