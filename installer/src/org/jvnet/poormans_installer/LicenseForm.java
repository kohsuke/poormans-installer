/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */

/*
 * LicenseForm.java
 *
 * Created on 2003/11/06, 21:17
 */
package org.jvnet.poormans_installer;

import java.awt.Adjustable;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * License screen.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class LicenseForm extends JDialog {
    
    private boolean accepted = false;
    
    public LicenseForm( Reader text ) throws IOException {
        super((JFrame)null,"License Agreement",true);
        final JScrollPane scrollPane = new JScrollPane();
        JTextArea licenseTextArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        final JButton acceptButton = new JButton();
        final JButton cancelButton = new JButton();
        
        
        {// load the license text
            BufferedReader reader = new BufferedReader(text);
            String line;
            StringBuffer buf = new StringBuffer();
            while((line=reader.readLine())!=null) {
                buf.append(line);
                buf.append('\n');
            }
            licenseTextArea.setText(buf.toString());
            licenseTextArea.setLineWrap(true);
        }
        
        
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().add(scrollPane);
        scrollPane.setViewportView(licenseTextArea);
        
        licenseTextArea.setEditable(false);
        
        getContentPane().add(buttonPanel);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        buttonPanel.add(acceptButton);
        acceptButton.setText("Accept");
        acceptButton.setEnabled(false);

        buttonPanel.add(cancelButton);
        cancelButton.setText("Decline");
        
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitDialog(true);
             }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitDialog(false);
            }
        });
        
        pack();
        
        // don't enable the yes button until the scroll bar has been dragged
        // to the bottom or the window was enlarged enough to make the scroll
        // bar disappear
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            Adjustable a;
            public void adjustmentValueChanged(AdjustmentEvent e) {
                a = e.getAdjustable();
                if( a.getValue() + a.getVisibleAmount() >= a.getMaximum() ) 
                    acceptButton.setEnabled(true);
            }
        });
        
        java.awt.Dimension screenSize = 
            java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(550, 450));
        setLocation((screenSize.width-550)/2,(screenSize.height-450)/2);
    }

    private void exitDialog(boolean accepted) {
        this.accepted = accepted;
        dispose();
    }
    
    /**
     * Returns true if the license is accepted by the user.
     */
    public boolean isAccepted() {
        return accepted;
    }
}
