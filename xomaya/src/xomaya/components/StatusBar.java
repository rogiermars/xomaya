/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import xomaya.application.Globals;
import xomaya.application.Xomaya;
import xomaya.components.datasource.TransferListener;
import xomaya.logging.Log;


/**
 * This StatusBar class is designed to present the user with some
 * information about when the application is loading. The StatusBar
 * will display a JProgressBar and let the user know via the Status enum
 * 
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class StatusBar extends JPanel implements TransferListener {
    JProgressBar bar = new JProgressBar();
    private final static int MAXIMUM = 100;
    private final static int MINIMUM = 0;
    String statusText = "";
    Status status = Status.LOADING;
    public StatusBar()
    {
        setSize(new Dimension(Globals.appWidth, 15));
        setPreferredSize(new Dimension(Globals.appWidth, 15));
        bar.setMinimum(0);
        bar.setMaximum(100);
        bar.setStringPainted(true);
        setStatus(Status.LOADING);
        setLayout(new GridLayout(0,1));
        add(bar);
    }

    public void increment(int v)
    {
        bar.setValue(bar.getValue()+v);
        if( bar.getValue() >= StatusBar.MAXIMUM) {
            logger.println("Setting bar to 0");
            bar.setValue(StatusBar.MINIMUM);
            bar.repaint();
        }

        bar.setString(statusText);
        bar.repaint();
        this.repaint();
    }

    private void setStatusText(String text)
    {
        statusText = text;
    }

    public void setStatus(Status status)
    {
        this.status = status;
        if( status == Status.LOADING ){
            statusText = "Loading";
        }
        else if( status == Status.RECORDING ){
            statusText = "Recording";
        }
        else if( status == Status.CAPTURING_DEVICE ){
            statusText = "Capturing Device";
        }
        else if( status == Status.CREATING_DATASINK ){
            statusText = "Creating DataSink";
        }
    }

    public void increment()
    {
        increment(1);
    }
   
    public void transferCompleted() {
        //System.out.println("Completed");
        increment();
        setBackground(Color.white);
        //repaint();
    }

    public void transferStarted() {
        //System.out.println("Started");
        increment();
        setBackground(Color.pink);
        //repaint();
    }

    static Log logger = new Log(StatusBar.class);
    
}
