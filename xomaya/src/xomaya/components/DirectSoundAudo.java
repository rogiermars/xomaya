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

import java.util.*;
import javax.media.*;
import javax.media.format.*;
import xomaya.logging.Log;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class DirectSoundAudo {

    private static final String detectClass = "com.sun.media.protocol.dsound.DSound";
    CaptureDeviceInfo[] devices = null;

    private boolean supports(AudioFormat af) {
        try {
            com.sun.media.protocol.dsound.DSound ds;
            ds = new com.sun.media.protocol.dsound.DSound(af, 1024);
            ds.open();
            ds.close();
        } catch (Exception e) {
            //logger.error(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public DirectSoundAudo() {
        boolean supported = false;
        // instance JavaSoundDetector to check is javasound's capture is availabe
        try {
            Class cls = Class.forName(detectClass);
            supported = true;
        } catch (Throwable t) {
            supported = false;
            // t.printStackTrace();
        }

        logger.println("DirectSound Capture Supported = " + supported);

        if (supported) {
            // It's there, start to register JavaSound with CaptureDeviceManager
            Vector devices = (Vector) CaptureDeviceManager.getDeviceList(null).clone();

            // remove the old direct sound capturers
            String name;
            Enumeration enumeration = devices.elements();
            while (enumeration.hasMoreElements()) {
                CaptureDeviceInfo cdi = (CaptureDeviceInfo) enumeration.nextElement();
                name = cdi.getName();
                if (name.startsWith(com.sun.media.protocol.dsound.DataSource.NAME))
                    CaptureDeviceManager.removeDevice(cdi);
            }
            int LE = AudioFormat.LITTLE_ENDIAN;
            int SI = AudioFormat.SIGNED;
            int US = AudioFormat.UNSIGNED;
            int UN = AudioFormat.NOT_SPECIFIED;
            float [] Rates = new float[] {
                    48000, 44100, 32000, 22050, 16000, 11025, 8000
            };
            Vector formats = new Vector(4);
            for (int rateIndex = 0; rateIndex < Rates.length; rateIndex++) {
                float rate = Rates[rateIndex];
                AudioFormat af;
                af = new AudioFormat(AudioFormat.LINEAR, rate, 16, 2, LE, SI);
                if (supports(af)) formats.addElement(af);
                af = new AudioFormat(AudioFormat.LINEAR, rate, 16, 1, LE, SI);
                if (supports(af)) formats.addElement(af);
                af = new AudioFormat(AudioFormat.LINEAR, rate, 8, 2, UN, US);
                if (supports(af)) formats.addElement(af);
                af = new AudioFormat(AudioFormat.LINEAR, rate, 8, 1, UN, US);
                if (supports(af)) formats.addElement(af);
            }

            AudioFormat [] formatArray = new AudioFormat[formats.size()];
            for (int fa = 0; fa < formatArray.length; fa++)
                formatArray[fa] = (AudioFormat) formats.elementAt(fa);

            CaptureDeviceInfo cdi = new CaptureDeviceInfo(
                    com.sun.media.protocol.dsound.DataSource.NAME,
                    new MediaLocator("dsound://"),
                    formatArray);
            CaptureDeviceManager.addDevice(cdi);
            try {
                CaptureDeviceManager.commit();
                logger.println("DirectSoundAuto: Committed ok");
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    static Log logger = new Log(DirectSoundAudo.class);
}