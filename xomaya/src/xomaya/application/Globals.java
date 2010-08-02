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

package xomaya.application;

import java.util.Hashtable;
import javax.media.Buffer;
import javax.media.Manager;
import javax.media.TimeBase;
import javax.media.protocol.FileTypeDescriptor;
import xomaya.components.SelectableVideoFormat;

// Referenced classes of package xomaya.application:
//            Application

/**
 * This class is used for storing global variables in the application.
 * 
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Globals
{

    public Globals()
    {
    }

    public static boolean logo = true;
    public static long ttga = 0;
    public static String copyright = "copyright (c) 2010. All Rights Reserved.";
    public static String name = "xomaya";
    public static double version = 1.21D;
    public static long snap = 0L;
    public static long tt = 0L;
    public static boolean compress = false;
    public static String context = null;
    public static int captureWidth = 640;
    public static int captureHeight = 480;

    public static int appWidth = 240;
    public static int appHeight = 125;

    public static long seqNo = 0;
    public static Application dw = null;
    public static float fps = 8f;
    public static Hashtable registry = new Hashtable();
    public static String expiryDate = "";
    public static String account = "";
    public static String username = "";
    public static String licenseKey = "";
    public static String licenseType = "";
    public static boolean debug = true;
    public static boolean verbose = false;
    public static boolean recording = false;
    public static long ts = 0;

    public static SelectableVideoFormat selectedVideoFormat = null;

    public static String encoding = "RGB";
    public static String videoExt = "avi";
    public static String videoName = "xomaya";
    public static String fileTypeDescriptor = FileTypeDescriptor.MSVIDEO;

    // key frames seemed to work.
    public static int bufferFlags = Buffer.FLAG_SYSTEM_TIME | Buffer.FLAG_KEY_FRAME;
    public static TimeBase time = Manager.getSystemTimeBase();
    public static Mode currentMode = Mode.SCREEN_ONLY;

}
