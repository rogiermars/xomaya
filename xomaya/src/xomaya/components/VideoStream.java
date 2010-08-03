/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xomaya.components;

import java.io.IOException;
import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author beecrofs
 */
public class VideoStream implements PushBufferStream, Runnable {

    //Thread thread = null;
    Format format = null;
    protected boolean started;
    protected Thread thread;
    protected BufferTransferHandler transferHandler;
    protected Control[] controls = new Control[0];
    boolean b = false;

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

    public VideoStream() {
        thread = new Thread(this);
    }

    public Format getFormat() {
        return format;
    }

    public void read(Buffer buffer) throws IOException {
        synchronized (this) {
            Object outdata = buffer.getData();
            int w = 640;
            int h = 480;
            outdata = new byte[w * h * 3];
            buffer.setData(outdata);
            buffer.setLength(w*h*3);
            Format fmt = new VideoFormat(VideoFormat.YUV);
            buffer.setFormat(fmt);
        }
    }

    public void setTransferHandler(BufferTransferHandler bth) {
        synchronized (this) {
            this.transferHandler = bth;
            notifyAll();
        }
    }

    public ContentDescriptor getContentDescriptor() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return false;
    }

    // Controls
    public Object[] getControls() {
        return controls;
    }

    public Object getControl(String controlType) {
        try {
            Class cls = Class.forName(controlType);
            Object cs[] = getControls();
            for (int i = 0; i < cs.length; i++) {
                if (cls.isInstance(cs[i])) {
                    return cs[i];
                }
            }
            return null;

        } catch (Exception e) {   // no such controlType or such control
            return null;
        }
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/
    public void run() {
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
            //Thread.yield();
            if (started && transferHandler != null) {
                transferHandler.transferData(this);
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException ise) {
                    ise.printStackTrace();
                }
            }
        }
    }
}
