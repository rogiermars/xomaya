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

import xomaya.application.Globals;
import xomaya.application.State;
import xomaya.util.JNA;
import xomaya.components.effects.GraphicEffect;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import java.util.Vector;
import xomaya.application.Mode;
import xomaya.application.Registry;
import xomaya.logging.Log;

/**
 * This class is responsible for taking snapshots of the computer screen
 * and translating them into a format which the class @see GraphicEffect
 * can translate into video.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class STimerTask extends TimerTask {

    Vector scListeners = new Vector();
    int c = 0;
    long ts = System.currentTimeMillis();
    long snap = 0;
    Toolkit tk = null;
    Robot robot = null;
    Dimension dim = null;
    Rectangle rect = null; 
    BufferedImage scaledImage = new BufferedImage(Globals.captureWidth, Globals.captureHeight, 1);
    GraphicEffect target = null;

    public STimerTask(GraphicEffect target) {

        this.target = target;
        tk = Toolkit.getDefaultToolkit();
        try {
            robot = new Robot();
        } catch (AWTException ex) {

        }
        dim = tk.getScreenSize();
        rect = new Rectangle(0, 0, dim.width, dim.height);

    }

    long totalTime = 0;
    public void run() {
        //Run our screen capture
        //long l = System.currentTimeMillis();

        Thread.yield();
        long tss = System.currentTimeMillis();
        int idleSec = JNA.getIdleTime() / 1000;
        long afterJNACall = System.currentTimeMillis() - tss;
        if( afterJNACall > 10 ){
            logger.println("Delay:" + afterJNACall + "|" + totalTime);
        }
        totalTime += afterJNACall;
        Thread.yield();
        
        int t = 5;
        State newState = idleSec >= t ? idleSec <= 300 ? State.IDLE : State.AWAY : State.ONLINE;
        Registry.register("State", newState);

        // If the mode is set to screen only, then state must always be ONLINE.
        if( Globals.currentMode == Mode.SCREEN_ONLY ){
            newState = State.ONLINE;
        }
        // If the mode is video only, make sure no screen capture will occur and return.
        else if( Globals.currentMode == Mode.VIDEO_ONLY ){
            target.screenCaptureEnabled = false;
            return;
        }

        if (newState == State.IDLE) {
            target.screenCaptureEnabled = false;
        } else if (newState == State.ONLINE) {
            //BEFORE YOU OPTOMIZE ANYMORE - MAKE A DAMN COPY!
            if( target.store == null ){
                return;
            }
            //System.out.println("Online");
            //System.out.println("Taking a pic");
            BufferedImage img = robot.createScreenCapture(rect);
            int mx = MouseInfo.getPointerInfo().getLocation().x;
            int my = MouseInfo.getPointerInfo().getLocation().y;
            Graphics2D g2D = (Graphics2D) img.createGraphics();
            g2D.setColor(Color.magenta);
            g2D.fillRect(mx, my, 5, 5);
            
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(img, 0, 0, Globals.captureWidth, Globals.captureHeight, null);
            //graphics2D.drawImage(img, 0, 0, 640, 480, null);
            //graphics2D.drawImage(img, mx, my, mx, my, mx, my, mx, my, null);
            int w = Globals.captureWidth;
            int h = Globals.captureHeight;

            int _w = w; /// 3;
            //int _h = ((vfIn.getMaxDataLength()) / 3) / _w;

            int[] pixels = new int[w * h];
            scaledImage.getRGB(0, 0, w, h, pixels, 0, w);
            Thread.yield();
            for (int _y = 0; _y < h; _y++) {
                for (int _x = 0; _x < w; _x++) {
                    int pixel = pixels[(w * _y) + _x];
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = (pixel) & 0xFF;
                    int dest = 0;
                    dest = target.store.length;
                    dest -= (_w * _y * 3) - (_x * 3);
                    if (dest < 0) {
                        continue;
                    }
                    if (dest + 3 > target.store.length) {
                        continue;
                    }
                    target.store[dest + 2] = (byte) (r);
                    target.store[dest + 1] = (byte) (g);
                    target.store[dest + 0] = (byte) (b);
                }
            }

            target.screenCaptureEnabled = true;
            //System.out.println("Complete");
        }
    }

    public STimerTask() {
    }
    
    static Log logger = new Log(STimerTask.class);
}
