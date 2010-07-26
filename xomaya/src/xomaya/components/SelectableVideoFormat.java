/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components;

import java.awt.Dimension;
import javax.media.CaptureDeviceInfo;
import javax.media.format.VideoFormat;

public class SelectableVideoFormat {

    VideoFormat vf = null;
    CaptureDeviceInfo cdi = null;
    public SelectableVideoFormat(VideoFormat vf, CaptureDeviceInfo cdi)
    {
        this.vf = vf;
        this.cdi = cdi;
    }

    public String toString()
    {
        Dimension d = vf.getSize();
        String enc = vf.getEncoding();
        enc = enc.toUpperCase();
        return enc + ":" + d.width + "x" + d.height;
    }
    public VideoFormat getFormat()
    {
        return vf;
    }
    public CaptureDeviceInfo getCaptureDeviceInfo()
    {
        return cdi;
    }
}