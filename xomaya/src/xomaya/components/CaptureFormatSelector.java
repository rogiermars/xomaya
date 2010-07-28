/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import xomaya.application.Globals;
import xomaya.util.Utility;


/**
 *
 * @author beecrofs
 */
public class CaptureFormatSelector extends JDialog implements ActionListener {
    JComboBox combo = new JComboBox();

    ImageIcon icon = new ImageIcon("./media/picon-small.png");
    public CaptureFormatSelector()
    {
        Vector v = Utility.getVideoFormats();
        Enumeration e = v.elements();
        while( e.hasMoreElements() ) {
            javax.media.CaptureDeviceInfo o = (javax.media.CaptureDeviceInfo)e.nextElement();
            Format[] f = (Format[])o.getFormats();
            for( int i = 0; i < f.length; i++ ) {
                if( f[i] instanceof VideoFormat ){
                    VideoFormat vf = (VideoFormat)f[i];
                    combo.addItem(new SelectableVideoFormat(vf,o));
                }
            }
        }

        int w = Globals.appWidth;
        int h = Globals.appHeight;

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setIconImage(icon.getImage());
        this.setTitle("Select an input capture format");
        JPanel center = new JPanel();
        JPanel south = new JPanel();
        JButton button = new JButton("Select");
        button.addActionListener(this);
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.add(button);
        setLayout(new BorderLayout());
        center.setBorder(new TitledBorder("Available Input Formats:"));
        center.add(combo);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        setSize(Globals.appWidth,Globals.appHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - w / 2, d.height / 2 - h / 2);
        setModal(true);
        setVisible(true);
        
    }

    public SelectableVideoFormat getSelectedVideoFormat()
    {
        //this.setModal(false);
        return (SelectableVideoFormat)combo.getSelectedItem();
    }

    public static void main(String[] args)
    {
        CaptureFormatSelector sel = new CaptureFormatSelector();
        SelectableVideoFormat svf = sel.getSelectedVideoFormat();
        System.out.println("The selected format was:" + svf);
        System.exit(-1);
    }

    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        setModal(false);
        setVisible(false);
    }
}
