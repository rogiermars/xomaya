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
package xomaya.components.datasource;

import xomaya.components.events.EventType;
import xomaya.components.events.Event;
import java.awt.Dimension;
import java.io.IOException;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Vector;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import xomaya.application.Globals;

/**
 * The source stream to go along with ImageDataSource.
 */
public class ImageSourceStream implements PushBufferStream, Runnable {

    Vector images;
    int width, height;
    Format format;
    protected BufferTransferHandler transferHandler;
    Thread thread = null;
    boolean started = false;

    public ImageSourceStream(int width, int height, int frameRate) {
        this.width = width;
        this.height = height;
        thread = new Thread(this);
        format = new RGBFormat(new Dimension(width, height),
                Format.NOT_SPECIFIED,
                Format.byteArray,
                Format.NOT_SPECIFIED,
                24,
                3, 2, 1,
                3, Format.NOT_SPECIFIED,
                Format.TRUE,
                Format.NOT_SPECIFIED);

        // Check the input buffer type & size.

    }

    /**
     * This is called from the Processor to read a frame worth
     * of video data.
     */
    public void read(Buffer buffer) throws IOException {

        synchronized (this) {
            try {
                int len = Globals.captureWidth * Globals.captureHeight * 3; //(int)raf.length();
                byte[] b = new byte[len];
                buffer.setData(b);
                buffer.setFormat(format);
                buffer.setOffset(0);
                buffer.setSequenceNumber(seqNo++);
                buffer.setTimeStamp(Globals.time.getNanoseconds());
                buffer.setFlags(buffer.getFlags() | Buffer.FLAG_KEY_FRAME);
                buffer.setHeader(null);
                //System.out.println(".");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    long seqNo = 0;

    /**
     * Return the format of each video frame.  That will be JPEG.
     */
    public Format getFormat() {
        return format;
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return 0;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String type) {
        return null;
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        synchronized (this) {
            this.transferHandler = transferHandler;
            notifyAll();
        }
    }

    void start(boolean started) {
        synchronized (this) {
            this.started = started;
            if (started && !thread.isAlive()) {
                thread = new Thread(this);
                thread.start();
            }

            notifyAll();
        }
    }

    public void stop() {
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/
    public void run() {
        //boolean running = true;
        while (started) {
            synchronized (this) {
                while (transferHandler == null && started) {
                    try {
                        wait(100);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }

            if (started && transferHandler != null) {
                try {
                    dispatch(new Event(EventType.TRANSFER_STARTED));
                    transferHandler.transferData(this);
                    dispatch(new Event(EventType.TRANSFER_COMPLETE));
                    Thread.sleep(100);
                } catch (InterruptedException ise) {
                    ise.printStackTrace();
                }
            }
        }
        System.out.println("finished");
    }
    Vector<TransferListener> listeners = new Vector<TransferListener>();

    public void addTransferListener(TransferListener el) {
        listeners.add(el);
    }

    public void dispatch(Event ae) {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            TransferListener l = (TransferListener) i.next();
            if (ae.getType() == EventType.TRANSFER_COMPLETE) {
                l.transferCompleted();
            } else if (ae.getType() == EventType.TRANSFER_STARTED) {
                l.transferStarted();
            }
        }
    }
}