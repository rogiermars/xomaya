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
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * This documentation is part of the Xomaya Express  <A HREF="http://www.xomaya.com">screen capture software</A> suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Application extends JFrame {

    ImageIcon icon = new ImageIcon("./media/picon-small.png");
    static Log logger = new Log(Application.class);

    public Application() {
        super(Globals.name + " " + Globals.version);
        showFrame();
    }

    private static void registerUserID() {
        String file = "./user.properties";
        File u = new File(file);
        if (!u.exists()) {
            boolean done = false;
            do {
                String email = JOptionPane.showInputDialog(null, "Please enter your email address.");
                if (email != null && !email.equals("") && email.indexOf("@") != -1 && email.indexOf(".") != -1) {
                    JOptionPane.showMessageDialog(null, "Thank you for registering.\nAn email has been sent to confirm your address.\n");
                    email = email.toLowerCase();
                    Globals.userid = email;
                    try {
                        URL url = new URL("http://www.xomaya.com/register.jsp?spamtest=4&email=" + email);
                        BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
                        while( r.readLine() != null ){}
                        FileWriter writer = new FileWriter(file);
                        writer.write("userid=" + email);
                        writer.flush();
                        writer.close();
                    } catch (IOException ex) {
                        logger.println(ex);
                    }
                    done = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Format");
                }
            } while ( !done );
        }

    }

    private static void register() {
        try {
            registerUserID();

            Properties props = new Properties();
            props.load(new FileInputStream("./application.properties"));
            String licenseKey = props.getProperty("licenseKey");
            String expiryDate = props.getProperty("expiryDate");
            String licenseType = props.getProperty("licenseType");

            licenseKey = licenseKey.toUpperCase();
            licenseKey = licenseKey.trim();
            logger.println("License Key:" + licenseKey);

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
        } catch (Exception ex) {
            String message = "Invalid License Key: ERROR CODE 5";
            logger.println(message);
        }
    }

    public static void quit(ExitReason er) {

        try {
            URL url = new URL("http://www.xomaya.com/targets/" + er.toString());
            logger.println("Quit reason:" + url);
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
        System.exit(-1);
    }

    public static void initialize() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        long t = System.currentTimeMillis();

        JNA.initialize();
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
        Controller controller = new Controller(this);
        controller.doCaptureInputFormat();
        StatusBar status = new StatusBar();
        xomaya.logging.Console console = new xomaya.logging.Console();
        Xomaya fa = new Xomaya(controller);

        Registry.register("Controller", controller);
        Registry.register("Console", console);
        Registry.register("StatusBar", status);
        Registry.register("Application", this);
        Registry.register("Xomaya", fa);
        ModeSelector ms = new ModeSelector();
        setIconImage(icon.getImage());
        add(fa, BorderLayout.WEST);
        add(ms, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());
        setSize(w, h);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - w / 2, d.height / 2 - h / 2);
        setVisible(true);
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
        Controller controller = (Controller) Registry.get("Controller");
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
        Controller controller = (Controller) Registry.get("Controller");

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
        Controller controller = (Controller) Registry.get("Controller");
        JMenuItem about = new JMenuItem("About Xomaya");
        about.setMnemonic('A');
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.SHIFT_DOWN_MASK, true));
        about.setToolTipText("About this application");
        about.setIcon(icon);
        about.setActionCommand(Command.ABOUT.toString());
        about.addActionListener(controller);
        helpMenu.add(about);

        JMenuItem buyLicenseKey = new JMenuItem("Buy License Key");
        buyLicenseKey.setMnemonic('L');
        buyLicenseKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.SHIFT_DOWN_MASK, true));
        buyLicenseKey.setToolTipText("Register License Key");
        buyLicenseKey.setActionCommand(Command.BUY_LICENSE_KEY.toString());
        buyLicenseKey.addActionListener(controller);
        helpMenu.add(buyLicenseKey);

        JMenuItem licenseKey = new JMenuItem("License Key");
        licenseKey.setMnemonic('L');
        licenseKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.SHIFT_DOWN_MASK, true));
        licenseKey.setToolTipText("Register License Key");
        licenseKey.setActionCommand(Command.LICENSE_KEY.toString());
        licenseKey.addActionListener(controller);
        helpMenu.add(licenseKey);

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

            logger.println("System starting...");
            logger.println(Globals.name + " " + Globals.version + " " + Globals.copyright);
            logger.println("--------------------------");
            Application.register();
            Application.initialize();
            logger.println("Application initialized");
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
}
