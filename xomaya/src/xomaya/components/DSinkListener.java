/**
 * Copyright (c) 2010 Sean Beecroft, http://xomaya.com/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package xomaya.components;

import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;

/**
 * Control the ending of the program prior to closing the data sink
 */
public class DSinkListener implements DataSinkListener {

    boolean endOfStream = false;

    // Flag the ending of the data stream
    public void dataSinkUpdate(DataSinkEvent event) {
        if (event instanceof javax.media.datasink.EndOfStreamEvent) {
            endOfStream = true;
            System.out.println("Reached end of stream************");
        }
    }

    /*Cause the current thread to sleep if the data stream is still available.
     * This makes certain that JMF threads are done prior to closing the data sink and finalizing the output file
     */
    public void waitEndOfStream(long checkTimeMs) {
        while (!endOfStream) {
            try {
                //Thread.currentThread().sleep(checkTimeMs);
                System.out.println("Waiting for end of stream************");
                Thread.sleep(checkTimeMs);
            } catch (InterruptedException ie) {
                // your exception handling here
                ie.printStackTrace();
            }
        }
    }
}
