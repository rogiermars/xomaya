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

    public ModeSelector()
    {
        super();
        JRadioButton radioVideoOnly = new JRadioButton("Video Capture");
        JRadioButton radioScreenOnly = new JRadioButton("Screen Capture");
        JRadioButton radioSmoothSwitch = new JRadioButton("Smooth Switch");
        JCheckBox checkboxCompression = new JCheckBox("Compress to MOV");

        radioVideoOnly.setActionCommand(Command.VIDEO_ONLY.toString());
        radioScreenOnly.setActionCommand(Command.SCREEN_ONLY.toString());
        radioSmoothSwitch.setActionCommand(Command.SMOOTH_SWITCH.toString());
        checkboxCompression.setActionCommand(Command.TOGGLE_COMPRESSION.toString());
        
        radioScreenOnly.setSelected(true);
        Globals.registry.get("Controller");
        Globals.registry.put("Compression", checkboxCompression);
        Controller controller = (Controller)Globals.registry.get("Controller");

        radioSmoothSwitch.addActionListener(controller);
        radioVideoOnly.addActionListener(controller);
        radioScreenOnly.addActionListener(controller);
        checkboxCompression.addActionListener(controller);

        ButtonGroup gp = new ButtonGroup();
        gp.add(radioVideoOnly);
        gp.add(radioScreenOnly);
        gp.add(radioSmoothSwitch);
        
        setLayout(new GridLayout(0,1));
        
        add(radioVideoOnly);
        add(radioScreenOnly);
        add(radioSmoothSwitch);
        add(checkboxCompression);
        //setBackground(Color.pink);


    }
}
