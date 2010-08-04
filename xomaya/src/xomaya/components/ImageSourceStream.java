/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xomaya.components;

import com.sun.media.format.AviVideoFormat;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import javax.media.util.ImageToBuffer;
import xomaya.application.Globals;
import xomaya.components.effects.GraphicEffect;

/**
 * The source stream to go along with ImageDataSource.
 */
public class ImageSourceStream implements PushBufferStream, Runnable {

    Vector images;
    int width, height;
    VideoFormat format;
    int nextImage = 0;	// index of the next image to be read.
    boolean ended = false;

    protected BufferTransferHandler transferHandler;

    Thread thread = null;

    boolean started = false;
    
    
    public ImageSourceStream(int width, int height, int frameRate, Vector images) {
        this.width = width;
        this.height = height;
        thread = new Thread(this);
        format = new VideoFormat(VideoFormat.JPEG,
                new Dimension(width, height),
                Format.NOT_SPECIFIED,
                Format.byteArray,
                (float) frameRate);

    
        
        // Check the input buffer type & size.

    }


    /**
     * This is called from the Processor to read a frame worth
     * of video data.
     */
    public void read(Buffer buffer) throws IOException {
 
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
        return ended;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String type) {
        return null;
    }

    public void setTransferHandler(BufferTransferHandler bth) {
       synchronized (this) {
            this.transferHandler = bth;
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
