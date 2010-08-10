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
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.X11;

/**
 * This class implements Linux abstractions for the JNA.
 * See https://jna.dev.java.net/ for more details on JNA
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class LinuxIdleTime {

	/** Definition (incomplete) of the Xext library. */
	interface Xss extends Library {

		Xss INSTANCE = (Xss) Native.loadLibrary("Xss", Xss.class);

		public class XScreenSaverInfo extends Structure {
			public X11.Window window; /* screen saver window */

			public int state; /* ScreenSaver{Off,On,Disabled} */

			public int kind; /* ScreenSaver{Blanked,Internal,External} */

			public NativeLong til_or_since; /* milliseconds */

			public NativeLong idle; /* milliseconds */

			public NativeLong event_mask; /* events */
		}

		XScreenSaverInfo XScreenSaverAllocInfo();

		int XScreenSaverQueryInfo(X11.Display dpy, X11.Drawable drawable,
				XScreenSaverInfo saver_info);
	}

	public static long getIdleTimeMillis() {
		X11.Window win = null;
		Xss.XScreenSaverInfo info = null;
		X11.Display dpy = null;
		final X11 x11 = X11.INSTANCE;
		final Xss xss = Xss.INSTANCE;

		long idlemillis = 0L;
		try {
			dpy = x11.XOpenDisplay(null);
			win = x11.XDefaultRootWindow(dpy);
			info = xss.XScreenSaverAllocInfo();
			xss.XScreenSaverQueryInfo(dpy, win, info);

			idlemillis = info.idle.longValue();
		} finally {
			if (info != null)
				x11.XFree(info.getPointer());
			info = null;

			if (dpy != null)
				x11.XCloseDisplay(dpy);
			dpy = null;
		}
		return idlemillis;
	}
}
