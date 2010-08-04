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

import java.awt.Dimension;
import javax.media.CaptureDeviceInfo;
import javax.media.Format;
import javax.media.format.VideoFormat;

public class SelectableVideoFormat {

    Format vf = null;
    CaptureDeviceInfo cdi = null;

    public SelectableVideoFormat(Format vf, CaptureDeviceInfo cdi) {
        this.vf = vf;
        this.cdi = cdi;
    }

    public String toString() {
        if (vf instanceof VideoFormat) {
            VideoFormat vvf = (VideoFormat)vf;
            Dimension d = vvf.getSize();
            String enc = vvf.getEncoding();
            enc = enc.toUpperCase();
            if (d != null) {
                enc = enc + ":" + d.width + "x" + d.height;
            }
            return enc;
        }

        return "empty";
    }

    public Format getFormat() {
        return vf;
    }

    public CaptureDeviceInfo getCaptureDeviceInfo() {
        return cdi;
    }
}
