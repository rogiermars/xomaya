/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components;

import java.io.IOException;
import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author beecrofs
 */
public class VideoDataSource extends PushBufferDataSource {

    protected String contentType = "raw";
    VideoStream[] streams = null;
    public void createStreams()
    {
        if (streams == null) {
            streams = new VideoStream[1];
            streams[0] = new VideoStream();
        }
    }

    @Override
    public PushBufferStream[] getStreams() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return streams;
    }

    @Override
    public String getContentType() {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (!connected) {
            System.out.println("Error: DataSource not connected");
            return null;
        }
        return contentType;
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
        System.out.println("VideoDataSource started");
        started = true;
        streams[0].start(true);
    }

    public void stop() throws IOException {
        if ((!connected) || (!started)) {
            return;
        }
        started = false;
        streams[0].start(false);
    }

    @Override
    public Object getControl(String string) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public Object[] getControls() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public Time getDuration() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

}
