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
import java.io.*;
import java.net.URL;
import java.util.Properties;
import javax.swing.*;
import xomaya.components.DirectSoundAudo;
import xomaya.controllers.Controller;
import xomaya.components.Xomaya;
import xomaya.components.ModeSelector;
import xomaya.logging.Log;
import xomaya.util.JNA;
import xomaya.util.Utility;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Application extends JFrame {

    public Application() {
        super(Globals.name + " " + Globals.version + " " + Globals.copyright);
        frame = this;
        showFrame();
    }

    public static void registerSuccess() {
        try {
            URL url = new URL("http://www.xomaya.com/targets/Success");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String b = "";
            String buffer = "";
            while ((b = reader.readLine()) != null) {
                buffer = buffer + b;
            }
        } catch (Exception ex) {
            //ex.printStackTrace();//
        }
    }

    private static void validateApplication(String key) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("./application.properties"));
            String account = props.getProperty("account");
            String username = props.getProperty("username");
            String licenseKey = props.getProperty("licenseKey");
            String expiryDate = props.getProperty("expiryDate");
            String licenseType = props.getProperty("licenseType");

            Globals.account = account;
            Globals.username = username;
            Globals.licenseKey = licenseKey;
            licenseKey = licenseKey.toUpperCase();
            licenseKey = licenseKey.trim();
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

            if( !licenseKey.equals("EXPRESS")){
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
            //Globals.logo = true;
        }
    }

    private static void validateEnvironment() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (d.width != 800 && d.height != 600) {
            JOptionPane.showMessageDialog(null, "You are running the application in " + d.width + "x" + d.height + ".\nThe recommended resolution is 800x600\nApplication will try to run anyways.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


    public static void initialize()
    {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        DirectSoundAudo audo = new DirectSoundAudo();
        long t = System.currentTimeMillis();
        JNA.getIdleTimeMillisWin32();
        long tl = System.currentTimeMillis() - t;
        logger.println("Loaded DLL:" + tl);
        try {
            File out = new File("./out");
            out.mkdir();
            File logs = new File("./logs");
            logs.mkdir();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void showFrame() {
        int w = Globals.appWidth;
        int h = Globals.appHeight;
        Controller controller = new Controller(frame);
        xomaya.logging.Console console = new xomaya.logging.Console();
        Xomaya fa = new Xomaya(controller);

        Globals.registry.put("Controller", controller);
        Globals.registry.put("Console", console);
        Globals.registry.put("Application", frame);
        Globals.registry.put("Xomaya", fa);
        ModeSelector ms = new ModeSelector();
        frame.add(fa, BorderLayout.WEST);
        frame.add(ms, BorderLayout.CENTER);
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
        menu.add(createHelpMenu());
        return menu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        Controller controller = (Controller)Globals.registry.get("Controller");
        JMenuItem exit = new JMenuItem("Exit");
        exit.setActionCommand(Command.EXIT.toString());
        exit.addActionListener(controller);
        fileMenu.add(exit);
        return fileMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        Controller controller = (Controller)Globals.registry.get("Controller");
        JMenuItem about = new JMenuItem("About");
        about.setActionCommand(Command.ABOUT.toString());
        about.addActionListener(controller);
        helpMenu.add(about);

        JMenuItem help = new JMenuItem("Help");
        help.setActionCommand(Command.HELP.toString());
        help.addActionListener(controller);
        helpMenu.add(help);

        return helpMenu;
    }

    

    public static void main(String args[])
            throws Exception {
        String key = "EXPRESS";
        if( args.length > 0 ){
            key = args[0];
        }

        logger.println("System starting...");
        logger.println(Globals.name + " " + Globals.version + " " + Globals.copyright);
        logger.println("--------------------------");
        Application.validateApplication(key);
        Application.validateEnvironment();
        initialize();
        logger.println("Application validated:" + key );
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application();
            }
        });
    }
    static Log logger = new Log(Application.class);        
    JFrame frame;
}
