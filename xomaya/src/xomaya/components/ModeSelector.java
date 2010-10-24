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

import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import xomaya.application.Command;
import xomaya.application.Globals;
import xomaya.application.Registry;
import xomaya.controllers.Controller;

/**
 * This JPanel is the View component which enables the user to make
 * their selections.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class ModeSelector extends JPanel {

    private JRadioButton radioWebcamOnly = new JRadioButton("Webcam Capture");
    private JRadioButton radioScreenOnly = new JRadioButton("Screen Capture");
    private JRadioButton radioSmoothSwitch = new JRadioButton("Smooth Switch");
    private JCheckBox checkboxCompression = new JCheckBox("Compress to MOV");
    public ModeSelector() {
        super();

        radioWebcamOnly.setActionCommand(Command.WEBCAM_ONLY.toString());
        radioScreenOnly.setActionCommand(Command.SCREEN_ONLY.toString());
        radioSmoothSwitch.setActionCommand(Command.SMOOTH_SWITCH.toString());
        checkboxCompression.setActionCommand(Command.TOGGLE_COMPRESSION.toString());

        radioScreenOnly.setSelected(true);
        Registry.get("Controller");
        Registry.register("Compression", checkboxCompression);
        Controller controller = (Controller) Registry.get("Controller");

        radioSmoothSwitch.addActionListener(controller);
        radioWebcamOnly.addActionListener(controller);
        radioScreenOnly.addActionListener(controller);
        checkboxCompression.addActionListener(controller);

        ButtonGroup gp = new ButtonGroup();
        gp.add(radioWebcamOnly);
        gp.add(radioScreenOnly);
        gp.add(radioSmoothSwitch);

        setLayout(new GridLayout(0, 1));

        add(radioWebcamOnly);
        add(radioScreenOnly);
        add(radioSmoothSwitch);
        add(checkboxCompression);
        //setBackground(Color.pink);

        // Setup with default setup, therefor disable the other modes.
        if( Globals.defaultSetup ){
            radioWebcamOnly.setEnabled(false);
            radioSmoothSwitch.setEnabled(false);
        }

    }

    /**
     * @return the radioWebcamOnly
     */
    public JRadioButton getRadioWebcamOnly() {
        return radioWebcamOnly;
    }

    /**
     * @param radioWebcamOnly the radioWebcamOnly to set
     */
    public void setRadioWebcamOnly(JRadioButton radioWebcamOnly) {
        this.radioWebcamOnly = radioWebcamOnly;
    }

    /**
     * @return the radioScreenOnly
     */
    public JRadioButton getRadioScreenOnly() {
        return radioScreenOnly;
    }

    /**
     * @param radioScreenOnly the radioScreenOnly to set
     */
    public void setRadioScreenOnly(JRadioButton radioScreenOnly) {
        this.radioScreenOnly = radioScreenOnly;
    }

    /**
     * @return the radioSmoothSwitch
     */
    public JRadioButton getRadioSmoothSwitch() {
        return radioSmoothSwitch;
    }

    /**
     * @param radioSmoothSwitch the radioSmoothSwitch to set
     */
    public void setRadioSmoothSwitch(JRadioButton radioSmoothSwitch) {
        this.radioSmoothSwitch = radioSmoothSwitch;
    }

    /**
     * @return the checkboxCompression
     */
    public JCheckBox getCheckboxCompression() {
        return checkboxCompression;
    }

    /**
     * @param checkboxCompression the checkboxCompression to set
     */
    public void setCheckboxCompression(JCheckBox checkboxCompression) {
        this.checkboxCompression = checkboxCompression;
    }
}
