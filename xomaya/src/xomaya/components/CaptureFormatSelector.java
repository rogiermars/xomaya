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
 * This class is a JDialog class which is used for capturing technical
 * preferences for capture mode. The dialog will display the various
 * modes that are available. If no modes are available the application
 * will provide a default selection.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class CaptureFormatSelector extends JDialog implements ActionListener {

    JComboBox combo = new JComboBox();
    ImageIcon icon = new ImageIcon("./media/picon-small.png");

    public CaptureFormatSelector() {
        // REFACTOR
        populate();
        // REFACTOR

        int w = Globals.appWidth;
        int h = Globals.appHeight;

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setIconImage(icon.getImage());
        this.setTitle("Capture Format Dialog");
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
        setSize(Globals.appWidth, Globals.appHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - w / 2, d.height / 2 - h / 2);
        setModal(true);
        setVisible(true);

    }

    public void populate() {
        Vector v = Utility.getVideoFormats();
        if (v != null) {
            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                try {
                    javax.media.CaptureDeviceInfo o = (javax.media.CaptureDeviceInfo) e.nextElement();
                    Format[] f = (Format[]) o.getFormats();
                    for (int i = 0; i < f.length; i++) {
                        if (f[i] instanceof VideoFormat) {
                            VideoFormat vf = (VideoFormat) f[i];
                            combo.addItem(new SelectableVideoFormat(vf, o));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (combo.getItemCount() == 0) {
            // No options available - add a default option.
            combo.addItem(new SelectableVideoFormat("Screen Capture"));
        }

    }

    public SelectableVideoFormat getSelectedVideoFormat() {
        return (SelectableVideoFormat) combo.getSelectedItem();
    }

    public static void main(String[] args) {
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
