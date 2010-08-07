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
///////////////////////////////////////////////
//
import java.io.IOException;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import xomaya.application.Globals;
import xomaya.components.Status;
import xomaya.logging.Log;

// Inner classes.
///////////////////////////////////////////////
/**
 * A DataSource to read from a list of JPEG image files and
 * turn that into a stream of JMF buffers.
 * The DataSource is not seekable or positionable.
 */
public class ImageDataSource extends PushBufferDataSource {

    ImageSourceStream streams[];
    boolean connected = false;
    boolean started = false;

    public ImageDataSource(int width, int height, int frameRate) {
        streams = new ImageSourceStream[1];
        streams[0] = new ImageSourceStream(width, height, frameRate);
        Status status = (Status)Globals.registry.get("Status");
        if( status != null ){
            streams[0].addTransferListener(status);
        }

        Globals.registry.put("ImageSourceStream", streams[0]);
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
        logger.println("ImageDataSource started");
        started = true;
        if( !streams[0].started ){
           streams[0].start(true);
        }
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
    }

    static Log logger = new Log(ImageDataSource.class);
}

