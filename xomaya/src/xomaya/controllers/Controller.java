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
package xomaya.controllers;

import java.awt.Desktop;
import java.awt.Dimension;
import xomaya.application.Globals;
import xomaya.application.State;
import xomaya.application.Command;
import xomaya.logging.Log;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.*;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.*;
import xomaya.application.Application;
import xomaya.application.Mode;
import xomaya.components.CaptureFormatSelector;
import xomaya.components.SelectableVideoFormat;
import xomaya.application.Xomaya;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Controller implements ActionListener {

    public Controller(JFrame frame) {
        this.frame = null;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {
        logger.println("Action Command:" + e);
        String cmd = e.getActionCommand();
        if (cmd == null) {
            throw new IllegalArgumentException("Cannot have null action");
        }

        if (cmd.equals(Command.START_RECORDING.toString())) {
            doStartRecording();
        } else if (cmd.equals(Command.STOP_RECORDING.toString())) {
            doStopRecording();
        } else if (cmd.equals(Command.SMOOTH_SWITCH.toString())) {
            doSmoothSwitch();
        } else if (cmd.equals(Command.VIDEO_ONLY.toString())) {
            doVideoOnly();
        } else if (cmd.equals(Command.SCREEN_ONLY.toString())) {
            doScreenOnly();
        } else if (cmd.equals(Command.TOGGLE_COMPRESSION.toString())) {
            doToggleCompression();
        } else if (cmd.equals(Command.REGISTER_SUCCESS.toString())) {
            doRegisterSuccess();
        } else if (cmd.equals(Command.CAPTURE_INPUT_FORMAT.toString())) {
            doCaptureInputFormat();
        } else if (cmd.equals(Command.OPEN_OUTPUT_DIRECTORY.toString())) {
            doOpenOutputDirectory();
        } else if (cmd.equals(Command.CLEAR_OUTPUT_DIRECTORY.toString())) {
            doClearOutputDirectory();
        } else if (cmd.equals(Command.EXIT.toString())) {
            doExit();
        } else if (cmd.equals(Command.ABOUT.toString())) {
            doAbout();
        } else if (cmd.equals(Command.HELP.toString())) {
            doHelp();
        }
    }

    public void doRegisterSuccess() {
        try {
            URL url = new URL("http://www.xomaya.com/targets/Success");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String b = "";
            String buffer = "";
            while ((b = reader.readLine()) != null) {
                buffer = buffer + b;
            }
            reader.close();
        } catch (Exception ex) {
            logger.println(ex);
            ex.printStackTrace();
        }
    }

    public void doExit() {
        Application.quit(-1);
    }

    public void doOpenOutputDirectory() {
        if (Desktop.isDesktopSupported()) {
            try {
                String s = System.getProperty("user.dir") + File.separator + "out";
                logger.println("Open:" + s);
                File f = new File(s);
                Desktop.getDesktop().browse(f.toURI());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Could not open output directory because this machine does not support Java Desktop");
                logger.println(ex);
                ex.printStackTrace();
            }
        }
    }

    public void doClearOutputDirectory() {
        try {
            int result = JOptionPane.showConfirmDialog(frame, "Warning: This will remove all of the files in this directory. Are you sure?");
            if( result == JOptionPane.YES_OPTION ){
                String s = System.getProperty("user.dir") + File.separator + "out";
                logger.println("Clear:" + s);
                File f = new File(s);
                File[] list = f.listFiles();
                for( int i = 0; i < list.length; i++ ){
                    if( !list[i].delete() ) {
                        logger.println("Could not delete:" + list[i].getAbsolutePath() );
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Could not open output directory because this machine does not support Java Desktop");
            logger.println(ex);
            ex.printStackTrace();
        }
    }

    public void doCaptureInputFormat() {
        try {
            CaptureFormatSelector sel = new CaptureFormatSelector();
            Globals.selectedVideoFormat = sel.getSelectedVideoFormat();
            if( Globals.selectedVideoFormat == null ) {
                Format format = new RGBFormat(
                        new Dimension(Globals.captureWidth, Globals.captureHeight),
                        Format.NOT_SPECIFIED,
                        Format.byteArray,
                        Format.NOT_SPECIFIED,
                        24,
                        3, 2, 1,
                        3, Format.NOT_SPECIFIED,
                        Format.TRUE,
                        Format.NOT_SPECIFIED);
                Globals.selectedVideoFormat = new SelectableVideoFormat( format, null );
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void doAbout() {
        JOptionPane.showMessageDialog(frame, "This application is Xomaya [version " + Globals.version + "]\nPlease visit http://www.xomaya.com for more details\n" + Globals.copyright);
    }

    public void doHelp() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://www.xomaya.com/FAQ.html"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please visit http://www.xomaya.com/FAQ.html");
                logger.println(ex);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please visit http://www.xomaya.com/FAQ.html");
        }
    }

    public void doSmoothSwitch() {
        Globals.currentMode = Mode.SMOOTH_SWITCH;
    }

    public void doScreenOnly() {
        Globals.currentMode = Mode.SCREEN_ONLY;
    }

    public void doToggleCompression() {
        logger.println("Toggle Compression");
        Globals.compress = !Globals.compress;
        if (Globals.compress) {
            logger.println("Compression ON MOV");
            Globals.videoExt = "mov";
            Globals.fileTypeDescriptor = FileTypeDescriptor.QUICKTIME;
        } else {
            logger.println("Compression OFF AVI");
            Globals.videoExt = "avi";
            Globals.fileTypeDescriptor = FileTypeDescriptor.MSVIDEO;
        }
    }

    public void doVideoOnly() {
        Globals.currentMode = Mode.VIDEO_ONLY;
    }

    public void doStartRecording() {
        logger.println("Started!");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Globals.snap = System.currentTimeMillis();
                    Xomaya jmf = (Xomaya) Globals.registry.get("Xomaya");
                    jmf.start();
                } catch (Exception ex) {
                    logger.println(ex);
                    ex.printStackTrace();
                }
            }
        });
        JButton start = (JButton) Globals.registry.get("StartRecording");
        JCheckBox compression = (JCheckBox) Globals.registry.get("Compression");
        start.setEnabled(false);
        // stop should be enabled later, when the program actually starts recording.
        compression.setEnabled(false);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void doStopRecording() {
        logger.println("Stopped!");
        Thread t = new Thread() {

            public void run() {
                try {
                    Xomaya jmf = (Xomaya) Globals.registry.get("Xomaya");
                    jmf.stop();

                    java.util.Timer timer = (java.util.Timer) Globals.registry.get("Timer");
                    TimerTask task = (TimerTask) Globals.registry.get("STimerTask");
                    JButton start = (JButton) Globals.registry.get("StartRecording");
                    JButton stop = (JButton) Globals.registry.get("StopRecording");
                    timer.cancel();
                    task.cancel();
                    Controller.logger.println("Total time:" + (System.currentTimeMillis() - Globals.snap));
                    Globals.tt = System.currentTimeMillis() - Globals.snap;
                    stop.setEnabled(false);
                    System.out.println("stop");

                    JOptionPane.showMessageDialog(null, "Your presentation is finished compiling.");
                    JOptionPane.showMessageDialog(null, "Saved Successfully.\nThe location is:./out/" + Globals.videoName + "." + Globals.videoExt + "\n Application will now exit.");

                    // Trigger the action
                    doOpenOutputDirectory();
                    // Register that we completed the application.
                    doRegisterSuccess();

                    Application.quit(-1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.println(ex);
                }
            }
        };
        t.start();
    }
    static Log logger = new Log(Controller.class);
    JFrame frame;
}
