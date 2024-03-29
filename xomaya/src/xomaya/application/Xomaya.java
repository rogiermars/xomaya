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

import xomaya.controllers.Controller;
import xomaya.components.effects.GraphicEffect;
import java.awt.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.Time;
import javax.media.format.*;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import xomaya.components.DSinkListener;
import xomaya.components.STimerTask;
import xomaya.components.Status;
import xomaya.components.StatusBar;
import xomaya.logging.Log;
import xomaya.util.Utility;

/**
 * The Xomaya class is the main displayed panel. This class is responsible
 * for displaying the buttons, opening the media and running most of the
 * important aspects of the application. This is the main class of this
 * application.
 * 
* This documentation is part of the Xomaya Express  <A HREF="http://www.xomaya.com">screen capture software</A> suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Xomaya extends JPanel implements ControllerListener, Runnable {

    GraphicEffect effect = new GraphicEffect();
    Processor p;
    final Object waitSync = new Object();
    boolean stateTransitionOK = true;
    DataSink ddsink = null;
    DSinkListener dataSinkListener = null;
    DataSource ads = null;
    Icon recordIcon = new ImageIcon("./media/media-record.gif");
    Icon stopIcon = new ImageIcon("./media/media-playback-stop.gif");

    public Xomaya(Controller controller) {
        JButton stop = new JButton("Stop", stopIcon);
        JButton start = new JButton("Start", recordIcon);
        setLayout(new GridLayout(0, 1));
        start.setActionCommand(Command.START_RECORDING.toString());
        stop.setActionCommand(Command.STOP_RECORDING.toString());
        Registry.register("StartRecording", start);
        Registry.register("StopRecording", stop);
        start.addActionListener(controller);
        stop.addActionListener(controller);
        stop.setEnabled(false);
        add(start);
        add(stop);

        Thread lt = new Thread(this);
        lt.setPriority(Thread.MIN_PRIORITY);
        lt.start();
    }

    public boolean open(MediaLocator ml) {
        try {
            StatusBar status = (StatusBar)Registry.get("StatusBar");
            Registry.register("GraphicEffect", effect);
            // increase our status
            status.increment(10);
            status.setStatus(Status.CAPTURING_DEVICE);
            DataSource original = Utility.getCaptureDS();
            if (original == null) {
                logger.println("Could not create DS");
                JOptionPane.showMessageDialog(this, "Xomaya requires a Webcam.\nPlease connect your Webcam and run Xomaya again.\nThis program recommends a capture mode of at least 640x480.\nIf you see this error again, please make sure\nyou have valid drivers for your webcam.\nProgram will now exit.", "No Webcam Detected!", JOptionPane.ERROR_MESSAGE);
                Application.quit(ExitReason.NO_WEBCAM);
            }
            // increase our status
            status.increment(10);
            try {
                p = Manager.createProcessor(original);
            } catch (Exception e) {
                logger.println("Failed to create a processor from the given url: " + e);
                e.printStackTrace();
                return false;
            }
            // increase our status
            status.increment(10);
            p.addControllerListener(this);
            // Put the Processor into configured state.
            p.configure();
            if (!waitForState(p.Configured)) {
                logger.println("Failed to configure the processor.");
                return false;
            }
            // increase our status
            status.increment(10);
            //Thread.yield();
            status.setStatus(Status.CREATING_DATASINK);
            p.setContentDescriptor(new ContentDescriptor(Globals.fileTypeDescriptor));
            TrackControl tc[] = p.getTrackControls();
            // increase our status
            status.increment(10);
            if (tc == null) {
                logger.println("Failed to obtain track controls from the processor.");
                return false;
            }
            // Search for the track control for the video track.
            TrackControl videoTrack = null;
            for (int i = 0; i < tc.length; i++) {
                if (tc[i].getFormat() instanceof VideoFormat) {
                    videoTrack = tc[i];
                    break;
                }
            }
            status.setStatus(Status.LOADING);
            // increase our status
            status.increment();
            if (videoTrack == null) {
                logger.println("The input media does not contain a video track.");
                return false;
            }
            // increase our status
            status.increment();
            // Instantiate and set the frame access codec to the data flow path.
            try {
                Effect codec[] = {effect};
                boolean enableEffects = true;
                if (enableEffects) {
                    videoTrack.setCodecChain(codec);
                }
            } catch (UnsupportedPlugInException e) {
                System.err.println("The process does not support effects.");
            }
            // increase our status
            status.increment();
            // Realize the processor.
            p.prefetch();
            logger.println("Middle-");
            if (!waitForState(p.Prefetched)) {
                logger.println("Failed to realize the processor.");
                return false;
            }
            STimerTask task = new STimerTask(effect);
            Timer timer = new java.util.Timer();
            timer.scheduleAtFixedRate(task, 0L, 300L);
            Registry.register("STimerTask", task);
            Registry.register("Timer", timer);
            logger.println("Timers initialized");
            Thread.sleep(1200);
            Registry.register("Processor", p);
            long tt = System.currentTimeMillis();
            p.setMediaTime(new Time(Time.ONE_SECOND));
            p.syncStart(new Time(System.currentTimeMillis() / 1000));
            logger.println("TT GA:" + (tt - Globals.ttga));
            // increase our status
            status.increment();
            long tu = System.currentTimeMillis() - tt;
            JButton stop = (JButton) Registry.get("StopRecording");
            stop.setEnabled(true);
            logger.println("Stop enabled");
            logger.println("Waiting to start...");
            logger.println("State code:" + p.getState());
            // 600
            // increase our status
            status.increment();
            Controller c = (Controller) Registry.get("Controller");
            JFrame f = c.getFrame();
            f.setState(JFrame.ICONIFIED);
            logger.println("Returned true");
            // increase our status
            status.setStatus(Status.RECORDING);
            status.increment();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void addNotify() {
        super.addNotify();
    }

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(int state) {
        synchronized (waitSync) {
            try {
                while (p.getState() != state && stateTransitionOK) {
                    waitSync.wait();
                }
            } catch (Exception e) {
            }
        }
        return stateTransitionOK;
    }

    DataSink createDataSink(Processor p, MediaLocator outML) {

        DataSource ds;
        if ((ds = p.getDataOutput()) == null) {
            logger.println("Error: the processor does not have an output DataSource");
            return null;
        }
        DataSink dsink;
        try {
            //System.err.println("- create DataSink for: " + outML);
            dsink = Manager.createDataSink(ds, outML);
            dsink.addDataSinkListener(dataSinkListener);
            dsink.open();
            ddsink = dsink;
        } catch (Exception e) {
            logger.println("Cannot create the DataSink: " + e);
            e.printStackTrace();
            return null;
        }
        return dsink;
    }

    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {
        if (evt instanceof ConfigureCompleteEvent) {
            synchronized (waitSync) {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else if (evt instanceof RealizeCompleteEvent) {
            synchronized (waitSync) {
                logger.println("Realized");
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(System.currentTimeMillis()));
                Globals.videoName = "xomaya-" + c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH);
                DataSink sink = createDataSink(p, new MediaLocator("file:./out/" + Globals.videoName + "." + Globals.videoExt));
                sink.open();
                sink.start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DataSink could not be created", "Could not write to disk", JOptionPane.ERROR_MESSAGE);
                Application.quit(ExitReason.NO_DATASINK);
                ex.printStackTrace();
            }
        } else if (evt instanceof PrefetchCompleteEvent) {
            synchronized (waitSync) {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else if (evt instanceof ResourceUnavailableEvent) {
            synchronized (waitSync) {
                stateTransitionOK = false;
                waitSync.notifyAll();
            }
        } else if (evt instanceof EndOfMediaEvent) {
            p.close();
        }
    }

    /**
     * Main program
     */
    public void start() {
        String[] args = new String[]{"vfw:/0"};
        String url = args[0];
        MediaLocator ml;
        if ((ml = new MediaLocator(url)) == null) {
            JOptionPane.showMessageDialog(this, "Could not build media locator from: " + url, "Could not open", JOptionPane.ERROR_MESSAGE);
            logger.println("Cannot build media locator from: " + url);
            Application.quit(ExitReason.NO_MEDIA_LOCATOR);
        }
        if (!open(ml)) {
            ////////////////////////////////////////////////////////////////////
            //JOptionPane.showMessageDialog(this, "Could not open device(s) for capture", "Could not open", JOptionPane.ERROR_MESSAGE);
            //Application.quit(ExitReason.NO_DEVICE);
        }
    }

    static void prUsage() {
        logger.println("Usage: java Xomaya <url>");
    }

    public void stop() {
        try {
            //This thing needs to be updated / refactored.
            Status status = (Status)Registry.get("Status");
            status = status.STOPPED;
            Registry.register("Status", status);
            p.stop();
            p.close();
            // Closing the processor will end the data stream to the data sink.
            // Wait for the end of stream to occur before closing the datasink
            try {
                if( dataSinkListener != null ){
                    dataSinkListener.waitEndOfStream(10);
                }
            } catch (Exception ex) {
                logger.println(ex);
                ex.printStackTrace();
            }
            ddsink.stop();
            ddsink.close();
            logger.println("Stopped and closed");
        } catch (IOException ex) {
            logger.println(ex);
            ex.printStackTrace();
        }
    }

    public void run()
    {
        boolean running = true;
        while( running ){
            try {
                Thread.sleep(500);
                Status status = (Status)Registry.get("Status");
                final StatusBar sb = (StatusBar)Registry.get("StatusBar");
                if( status == Status.RECORDING ){
                    if( sb != null ){
                        // show so the application can show that it is doing something.
                        sb.increment();
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        sb.update();
                    }
                });
            } catch(Exception ex){
                logger.println(ex);
                ex.printStackTrace();
            }
        }
    }
    static Log logger = new Log(Xomaya.class);
}
