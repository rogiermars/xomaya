/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xomaya.components;

import java.awt.Dimension;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

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
        this.images = images;
        thread = new Thread(this);
        format = new VideoFormat(VideoFormat.JPEG,
                new Dimension(width, height),
                Format.NOT_SPECIFIED,
                Format.byteArray,
                (float) frameRate);
    }

    /**
     * We should never need to block assuming data are read from files.
     */
    public boolean willReadBlock() {
        return false;
    }

    /**
     * This is called from the Processor to read a frame worth
     * of video data.
     */
    public void read(Buffer buf) throws IOException {
        // Check if we've finished all the frames.
        
        String imageFile = (String) "./media/plain.jpg";

        //System.out.println("  - ready to read image file: " + imageFile);
        
        // Open a random access file for the next image.
        RandomAccessFile raFile;
        //System.out.println("    - reading image file: " + imageFile);
        raFile = new RandomAccessFile(imageFile, "r");

        byte data[] = null;

        // Check the input buffer type & size.

        if (buf.getData() instanceof byte[]) {
            data = (byte[]) buf.getData();
        }

        // Check to see the given buffer is big enough for the frame.
        if (data == null || data.length < raFile.length()) {
            data = new byte[(int) raFile.length()];
            buf.setData(data);
        }

        // Read the entire JPEG image from the file.
        raFile.readFully(data, 0, (int) raFile.length());

        //System.out.println("    read " + raFile.length() + " bytes.");

        buf.setOffset(0);
        buf.setLength((int) raFile.length());
        buf.setFormat(format);
        buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);

        // Close the random access file.
        raFile.close();
    }

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
