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

import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.awt.Desktop;
import xomaya.logging.Log;

/**
 * This class implements some of the OS abstractions for the JNA.
 * See https://jna.dev.java.net/ for more details on JNA
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class JNA {

    public static void initialize()
    {
        try {
            int t = getIdleTime();
            logger.println("Idle time:" + t);
        } catch(Error e){
            e.printStackTrace();
        }       
    }

    public static int getIdleTime()
    {
        if(Platform.isWindows()) {
            return Win32IdleTime.getIdleTimeMillis();
        }
        else if(Platform.isLinux()){
            return (int)LinuxIdleTime.getIdleTimeMillis();
        }
        else if(Platform.isMac()){
            return (int)MacIdleTime.getIdleTimeMillis();
        }
        else {
            logger.println("Platform unknown: Using windows");
            return Win32IdleTime.getIdleTimeMillis();
        }
    }
    static Log logger = new Log(JNA.class);
}