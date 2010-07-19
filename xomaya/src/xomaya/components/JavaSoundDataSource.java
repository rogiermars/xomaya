/**
 * Copyright (c) 2010 
 * 
 * Jason Foss, Sean Beecroft, http://www.iamfoss.com, http://www.xomaya.com
 *
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


import javax.media.Time;
import javax.media.protocol.*;
import java.io.IOException;
import xomaya.logging.Log;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class JavaSoundDataSource extends PushBufferDataSource {

    protected Object[] controls = new Object[0];
    protected boolean started = false;
    protected String contentType = "raw";
    protected boolean connected = false;
    protected Time duration = DURATION_UNKNOWN;
    protected JavaSoundStream[] streams = null;
    protected JavaSoundStream   stream = null;

    static Log logger = new Log(JavaSoundDataSource.class);
    public JavaSoundDataSource() {
        createStreams();
    }

    public void createStreams()
    {
        if (streams == null) {
            streams = new JavaSoundStream[1];
            stream = streams[0] = new JavaSoundStream();
        }
    }

    public String getContentType() {
        if (!connected) {
            logger.println("Error: DataSource not connected");
            return null;
        }
        return contentType;
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

    //public void realStart()
    //{
    //    started = true;
    //    stream.start(true);
    //}

    public void start() throws IOException {
        // we need to throw error if connect() has not been called
        if (!connected) {
            throw new java.lang.Error("DataSource must be connected before it can be started");
        }
        if (started) {
            return;
        }
        started = true;
        stream.start(true);
    }

    public void stop() throws IOException {
        if ((!connected) || (!started)) {
            return;
        }
        started = false;
        stream.start(false);
    }

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

    public Time getDuration() {
        return duration;
    }

    public PushBufferStream[] getStreams() {
        return streams;
    }
}
