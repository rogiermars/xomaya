/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import xomaya.application.Globals;
import xomaya.components.datasource.TransferListener;

/**
 *
 * @author beecrofs
 */
public class Status extends JPanel implements TransferListener {
    JLabel label = new JLabel();
    public Status()
    {
        setSize(new Dimension(Globals.appWidth, 15));
        setPreferredSize(new Dimension(Globals.appWidth, 15));
        add(label);
    }
   
    public void transferCompleted() {
        //System.out.println("Completed");
        setBackground(Color.white);
        //repaint();
    }

    public void transferStarted() {
        //System.out.println("Started");
        setBackground(Color.pink);
        //repaint();
    }

    
}
