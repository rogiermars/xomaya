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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.*;
import xomaya.controllers.Controller;
import xomaya.components.ModeSelector;
import xomaya.components.StatusBar;
import xomaya.logging.Log;
import xomaya.util.JNA;

/**
 * Application is the main class of xomaya.
 * This class is responsible for setting up the application main frame,
 * displaying buttons and initial loading procedures.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Application extends JFrame {

    ImageIcon icon = new ImageIcon("./media/picon-small.png");

    public Application() {
        super(Globals.name + " " + Globals.version + " " + Globals.copyright);
        frame = this;

        showFrame();
    }

    private static void register() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("./application.properties"));
            String account = props.getProperty("account");
            String username = props.getProperty("username");
            String licenseKey = props.getProperty("licenseKey");
            String expiryDate = props.getProperty("expiryDate");
            String licenseType = props.getProperty("licenseType");

            licenseKey = licenseKey.toUpperCase();
            licenseKey = licenseKey.trim();
            Globals.account = account;
            Globals.username = username;
            Globals.licenseKey = licenseKey;
            logger.println("License Key:" + licenseKey);
            Globals.expiryDate = expiryDate;
            Globals.licenseType = licenseType;

            URL url = new URL("http://www.xomaya.com/keys/license." + licenseKey);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String b = "";
            String buffer = "";
            while ((b = reader.readLine()) != null) {
                buffer = buffer + b;
            }

            reader.close();

            if (!licenseKey.equals("EXPRESS")) {
                if (buffer.indexOf(licenseKey) != -1) {
                    logger.println("Application validated: Thank you for your business");
                    Globals.logo = false;
                } else {
                    String message = "Invalid License Key: ERROR CODE 2";
                    logger.println(message);
                }
            }
        } catch (IOException ex) {
            String message = "Invalid License Key: ERROR CODE 5";
            logger.println(message);
        }
    }

    public static void quit(int v) {
        System.exit(v);
    }

    public static void initialize() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        //DirectSoundAudo audo = new DirectSoundAudo();
        long t = System.currentTimeMillis();
        //Runtime.getRuntime().traceMethodCalls(true);

        JNA.initialize();
        //getIdleTimeMillisWin32();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        Globals.videoName = "xomaya-" + c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH);
        long tl = System.currentTimeMillis() - t;
        logger.println("Loaded DLL:" + tl);

        try {
            File out = new File("./out");
            if (!out.mkdir()) {
                logger.println("Could not create directory ./out");
            }
            File logs = new File("./logs");
            if (!logs.mkdir()) {
                logger.println("Could not create directory ./logs");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showFrame() {
        int w = Globals.appWidth;
        int h = Globals.appHeight;
        Controller controller = new Controller(frame);
        controller.doCaptureInputFormat();
        StatusBar status = new StatusBar();
        xomaya.logging.Console console = new xomaya.logging.Console();
        Xomaya fa = new Xomaya(controller);

        Globals.registry.put("Controller", controller);
        Globals.registry.put("Console", console);
        Globals.registry.put("StatusBar", status);
        Globals.registry.put("Application", frame);
        Globals.registry.put("Xomaya", fa);
        ModeSelector ms = new ModeSelector();
        frame.setIconImage(icon.getImage());
        frame.add(fa, BorderLayout.WEST);
        frame.add(ms, BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        frame.setJMenuBar(createMenuBar());
        frame.setSize(w, h);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(3);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width / 2 - w / 2, d.height / 2 - h / 2);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menu = new JMenuBar();
        menu.add(createFileMenu());
        menu.add(createToolsMenu());
        menu.add(createHelpMenu());
        return menu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        Controller controller = (Controller) Globals.registry.get("Controller");
        JMenuItem exit = new JMenuItem("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.SHIFT_DOWN_MASK, true));
        exit.setMnemonic('X');
        exit.setToolTipText("Exit this application");
        exit.setActionCommand(Command.EXIT.toString());
        exit.addActionListener(controller);
        fileMenu.add(exit);
        return fileMenu;
    }

    private JMenu createToolsMenu() {
        JMenu toolsMenu = new JMenu("Tools");
        Controller controller = (Controller) Globals.registry.get("Controller");

        JMenuItem openOutputDirectory = new JMenuItem("Open Output Directory");
        openOutputDirectory.setMnemonic('O');
        openOutputDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.SHIFT_DOWN_MASK, true));
        openOutputDirectory.setToolTipText("Get the output directory");
        openOutputDirectory.setActionCommand(Command.OPEN_OUTPUT_DIRECTORY.toString());
        openOutputDirectory.addActionListener(controller);
        toolsMenu.add(openOutputDirectory);

        JMenuItem clearOutputDirectory = new JMenuItem("Clear Output Directory");
        clearOutputDirectory.setMnemonic('C');
        clearOutputDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.SHIFT_DOWN_MASK, true));
        clearOutputDirectory.setToolTipText("Clear the output directory");
        clearOutputDirectory.setActionCommand(Command.CLEAR_OUTPUT_DIRECTORY.toString());
        clearOutputDirectory.addActionListener(controller);
        toolsMenu.add(clearOutputDirectory);

        toolsMenu.add(new JSeparator());
        JMenuItem captureInputFormat = new JMenuItem("Select Input Format");
        captureInputFormat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.SHIFT_DOWN_MASK, true));
        captureInputFormat.setMnemonic('F');
        captureInputFormat.setToolTipText("Select Capture Input Format");
        captureInputFormat.setActionCommand(Command.CAPTURE_INPUT_FORMAT.toString());
        captureInputFormat.addActionListener(controller);
        toolsMenu.add(captureInputFormat);

        return toolsMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        Controller controller = (Controller) Globals.registry.get("Controller");
        JMenuItem about = new JMenuItem("About Xomaya");
        about.setMnemonic('A');
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.SHIFT_DOWN_MASK, true));
        about.setToolTipText("About this application");
        about.setIcon(icon);
        about.setActionCommand(Command.ABOUT.toString());
        about.addActionListener(controller);
        helpMenu.add(about);



        JMenuItem help = new JMenuItem("Help");
        help.setMnemonic('H');
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK, true));
        help.setToolTipText("Get help online with this application");
        help.setActionCommand(Command.HELP.toString());
        help.addActionListener(controller);
        helpMenu.add(help);

        return helpMenu;
    }

    public static void main(String args[]) {
        try {
            String key = "EXPRESS";
            if (args.length > 0) {
                key = args[0];
            }

            logger.println("System starting...");
            logger.println(Globals.name + " " + Globals.version + " " + Globals.copyright);
            logger.println("--------------------------");
            Application.register();
            Application.initialize();
            logger.println("Application validated:" + key);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    Application app = new Application();
                }
            });

        } catch (Exception ex) {
            logger.println(ex);
        } catch (Error e) {
            logger.println(e);
        } finally {
            logger.println("-finally-");
        }
    }
    static Log logger = new Log(Application.class);
    JFrame frame;
}
