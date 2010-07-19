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
package xomaya.util;

import java.awt.Dimension;
import java.io.*;
import xomaya.logging.Log;
import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.*;
import javax.media.control.*;
import java.util.Vector;
import xomaya.application.Globals;
import xomaya.components.JavaSoundDataSource;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Utility {

    public Utility() {
    }

    private static void nuke() {
        logger.println("Can't initiate application.");
        System.exit(1);

    }

    /**
     * Create a media locator from the given string.
     */
    public static MediaLocator createMediaLocator(String url) {

        MediaLocator ml;

        if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null) {
            return ml;
        }

        if (url.startsWith(File.separator)) {
            if ((ml = new MediaLocator("file:" + url)) != null) {
                return ml;
            }
        } else {
            String file = "file:" + System.getProperty("user.dir") + File.separator + url;
            if ((ml = new MediaLocator(file)) != null) {
                return ml;
            }
        }

        return null;
    }

    public static DataSource getCaptureDS() {
        logger.println("TRYING:" + Globals.encoding);
        Dimension size = new Dimension(Globals.captureWidth, Globals.captureHeight);
        VideoFormat vf = new VideoFormat("YUV", size, Format.NOT_SPECIFIED, null, Globals.fps);

        //AudioFormat af = null;
        //new AudioFormat(AudioFormat.LINEAR, 44100, 8, 2);
        // new AudioFormat(AudioFormat.LINEAR, 44100, 4, 4);
        // new AudioFormat(AudioFormat.LINEAR, 44100, 4, 4);
        AudioFormat af = new AudioFormat(AudioFormat.LINEAR);

        //DirectSoundAudo audo = new DirectSoundAudo();
        // Use CaptureUtil to create a monitored capture datasource
        DataSource original = Utility.getCaptureDS(vf, af);

        /*
        if (original == null) {
        vf = new VideoFormat("YUV", new Dimension(720, 480), Format.NOT_SPECIFIED, null, Globals.fps);
        original = Utility.getCaptureDS(vf, af);
        Globals.captureHeight = 480;
        Globals.captureWidth = 720;
        }

        Globals.encoding = "YUV";
        logger.println("TRYING:" + Globals.encoding);
        if (original == null) {
        // was null - lets try a different encoding.
        vf = new VideoFormat("YUV", null, Format.NOT_SPECIFIED, null, Globals.fps);
        original = Utility.getCaptureDS(vf, af);
        }

        if (original == null) {
        vf = new VideoFormat("YUV", new Dimension(720, 480), Format.NOT_SPECIFIED, null, Globals.fps);
        original = Utility.getCaptureDS(vf, af);
        Globals.captureHeight = 480;
        Globals.captureWidth = 720;
        Globals.encoding = "YUV";
        }

        if( original == null ){
        vf = new VideoFormat(VideoFormat.YUV);
        original = Utility.getCaptureDS(vf, af);
        System.out.println(vf);
        Globals.encoding = "YUV";
        }
         */

        return original;
    }

    public static DataSource getCaptureDS(VideoFormat vf, AudioFormat af) {
        DataSource dsVideo = null;
        DataSource dsAudio = null;
        DataSource ds = null;

        // Create a capture DataSource for the video
        // If there is no video capture device, then exit with null
        if (vf != null) {
            dsVideo = createDataSource(vf);
            if (dsVideo == null) {
                logger.println("Unable to create dsVideo from VideoFormat");
                return null;
            }
        }
        try {
            dsAudio = new JavaSoundDataSource();
            Globals.registry.put("dsAudio", dsAudio);
            dsAudio.connect();
        } catch (Exception ex) {
            logger.println("Busted here:" + ex);
            ex.printStackTrace();
        }

        // Create the monitoring datasource wrapper
        if (dsVideo != null) {
            if (dsAudio == null) {
                return dsVideo;
            }
            ds = dsVideo;
        } else if (dsAudio != null) {
            return dsAudio;
        } else {
            logger.println("Unable to create dsVideo");
            return null;
        }

        // Merge the data sources, if both audio and video are available
        try {
            ds = Manager.createMergingDataSource(new DataSource[]{
                        dsVideo, dsAudio
                    });
        } catch (IncompatibleSourceException ise) {
            ise.printStackTrace();
            return null;
        }

        return ds;
    }

    static DataSource createDataSource(Format format) {
        DataSource ds;
        Vector devices;
        CaptureDeviceInfo cdi;
        MediaLocator ml;

        // Find devices for format

        devices = CaptureDeviceManager.getDeviceList(format);
        for (int i = 0; i < devices.size(); i++) {
            logger.println(devices.get(i));
        }
        //devices = CaptureDeviceManager.getDeviceList(null);
        if (devices.size() < 1) {
            logger.println("! No Devices for " + format);
            return null;
        }
        // Pick the first device
        cdi = (CaptureDeviceInfo) devices.elementAt(0);
        ml = cdi.getLocator();
        try {
            ds = Manager.createDataSource(ml);
            ds.connect();
            if (ds instanceof CaptureDevice) {
                setCaptureFormat((CaptureDevice) ds, format);
            }
        } catch (Exception e) {
            logger.println(e);
            return null;
        }
        return ds;
    }

    static void setCaptureFormat(CaptureDevice cdev, Format format) {
        FormatControl[] fcs = cdev.getFormatControls();
        if (fcs.length < 1) {
            return;
        }
        FormatControl fc = fcs[0];
        Format[] formats = fc.getSupportedFormats();

        for (int i = 0; i < formats.length; i++) {
            if (formats[i].matches(format)) {
                format = formats[i].intersects(format);
                logger.println("Setting format " + format);
                fc.setFormat(format);
                break;
            }
        }
    }

    public static void copy(File src, File dst)
            throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte buf[] = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void clear() {
    }
    static Log logger = new Log(Utility.class);
}
