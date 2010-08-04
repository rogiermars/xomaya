/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xomaya.components;
///////////////////////////////////////////////
//
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

// Inner classes.
///////////////////////////////////////////////

/**
 * A DataSource to read from a list of JPEG image files and
 * turn that into a stream of JMF buffers.
 * The DataSource is not seekable or positionable.
 */
public class ImageDataSource extends PushBufferDataSource {

    ImageSourceStream streams[];

    public ImageDataSource(int width, int height, int frameRate, Vector images) {
        streams = new ImageSourceStream[1];
        streams[0] = new ImageSourceStream(width, height, frameRate, images);
    }

    public void setLocator(MediaLocator source) {
    }

    public MediaLocator getLocator() {
        return null;
    }

    /**
     * Content type is of RAW since we are sending buffers of video
     * frames without a container format.
     */
    public String getContentType() {
        return ContentDescriptor.RAW;
    }

       boolean connected = false;
    boolean started = false;

   public void connect() throws IOException {
        if (connected) {
            return;
        }
        connected = true;
    }

    public void disconnect() {
        try {
            if (started) {
                stop();
            }
        } catch (IOException e) {
        }
        connected = false;
    }


    public void start() throws IOException {
        // we need to throw error if connect() has not been called
        if (!connected) {
            throw new java.lang.Error("DataSource must be connected before it can be started");
        }
        if (started) {
            return;
        }
        System.out.println("ImageDataSource started");
        started = true;
        streams[0].start(true);
    }
    /**
     * Return the ImageSourceStreams.
     */
    public PushBufferStream[] getStreams() {
        return streams;
    }

    /**
     * We could have derived the duration from the number of
     * frames and frame rate.  But for the purpose of this program,
     * it's not necessary.
     */
    public Time getDuration() {
        return DURATION_UNKNOWN;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String type) {
        return null;
    }

    public void stop() throws IOException {
        if ((!connected) || (!started)) {
            return;
        }
        started = false;
        streams[0].start(false);
    }

}

