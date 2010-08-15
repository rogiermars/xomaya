/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import xomaya.application.Globals;
import xomaya.application.Registry;
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
public class StatusBar extends JPanel implements TransferListener, StatusListener {
    JProgressBar bar = new JProgressBar();
    private final static int MAXIMUM = 100;
    private final static int MINIMUM = 0;
    String statusText = "";
    Status status = Status.READY;
    Vector<StatusListener> listeners = new Vector<StatusListener>();

    public StatusBar()
    {
        setSize(new Dimension(Globals.appWidth, 15));
        setPreferredSize(new Dimension(Globals.appWidth, 15));
        bar.setString("");
        bar.setMinimum(0);
        bar.setMaximum(100);
        bar.setStringPainted(true);
        setLayout(new GridLayout(0,1));
        addStatusListener(this);
        setStatus(Status.READY);
        add(bar);
    }

    public void addStatusListener(StatusListener sl)
    {
        listeners.add(sl);
    }

    public void dispatch(Status status)
    {
        Iterator i = listeners.iterator();
        while( i.hasNext() ){
            StatusListener sl = (StatusListener)i.next();
            sl.statusChanged(status);
        }
    }

    public void increment(int v)
    {
        bar.setValue(bar.getValue()+v);
        if( bar.getValue() >= StatusBar.MAXIMUM) {
            logger.println("Setting bar to 0");
            bar.setValue(StatusBar.MINIMUM);
        }
        update();
    }

    public void update()
    {
        bar.setString(statusText);
        bar.repaint();
        this.repaint();
        //System.out.println("update called");
    }

    public void setStatusText(String text)
    {
        statusText = text;
    }

    public void setStatus(Status status)
    {
        this.status = status;
        Registry.register("Status", status);
        
        dispatch(status);
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

    public void statusChanged(Status newStatus) {
        //throw new UnsupportedOperationException("Not supported yet.");
        status = newStatus;
        
        if( status == Status.READY ){
            statusText = "Ready";
        }
        else if( status == Status.INITIALIZING ){
            statusText = "Initializing... (Please wait)";
        }
        else if( status == Status.LOADING ){
            statusText = "Loading...";
        }
        else if( status == Status.RECORDING ){
            statusText = "Recording";
        }
         else if( status == Status.STOPPED ){
            statusText = "Stopped";
        }
        else if( status == Status.CAPTURING_DEVICE ){
            statusText = "Capturing Device";
        }
        else if( status == Status.CREATING_DATASINK ){
            statusText = "Creating DataSink";
        }
    }
    
}
