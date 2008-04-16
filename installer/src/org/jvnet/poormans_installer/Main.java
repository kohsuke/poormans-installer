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
package org.jvnet.poormans_installer;

import javax.swing.UIManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Reader in =
            new InputStreamReader(
                Main.class.getResourceAsStream("/license.txt"));
        
        boolean accepted;
        
        if (args.length>0 && args[0].equals("-console")) {
            accepted = doConsole(in);
        } else {
            try {
                setUILookAndFeel();
                LicenseForm form = new LicenseForm(in);
                form.show();
                accepted = form.isAccepted();
            } catch( InternalError e ) {
                // if there's no GUI, I get InternalError or HeadlessException
                accepted = doConsole(in);
            } catch( UnsupportedOperationException e ) {
                // really we want to catch HeadlessException, but that seems
                // to cause NoClassDefError on JDK 1.3
                accepted = doConsole(in);
            } catch( LinkageError e ) {
                /* and on some systems, I got a report that you get the following error.

                Exception in thread "main" java.lang.NoClassDefFoundError: Could not initialize class sun.awt.X11GraphicsEnvironment
                    at java.lang.Class.forName0(Native Method)
                    at java.lang.Class.forName(Class.java:169)
                    at java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment(GraphicsEnvironment.java:68)
                    at sun.awt.X11.XToolkit.<clinit>(XToolkit.java:89)
                    at java.lang.Class.forName0(Native Method)
                    at java.lang.Class.forName(Class.java:169)
                    at java.awt.Toolkit$2.run(Toolkit.java:836)
                    at java.security.AccessController.doPrivileged(Native Method)
                    at java.awt.Toolkit.getDefaultToolkit(Toolkit.java:828)
                    at sun.swing.SwingUtilities2$AATextInfo.getAATextInfo(SwingUtilities2.java:120)
                    at javax.swing.plaf.metal.MetalLookAndFeel.initComponentDefaults(MetalLookAndFeel.java:1556)
                    at javax.swing.plaf.basic.BasicLookAndFeel.getDefaults(BasicLookAndFeel.java:130)
                    at javax.swing.plaf.metal.MetalLookAndFeel.getDefaults(MetalLookAndFeel.java:1591)
                    at javax.swing.UIManager.setLookAndFeel(UIManager.java:537)
                    at javax.swing.UIManager.setLookAndFeel(UIManager.java:577)
                    at org.jvnet.poormans_installer.Main.setUILookAndFeel(Main.java:95)
                    at org.jvnet.poormans_installer.Main.main(Main.java:50)

                 */
                if(e.getMessage().contains("GraphicsEnvironment")) {
                    accepted = doConsole(in);
                } else {
                    throw e;
                }
            }
        }
        
        if(accepted)
            install();
        
        System.exit(accepted?0:1);
    }

    private static boolean doConsole(Reader in) throws IOException {
        boolean accepted;
        ConsoleForm form = new ConsoleForm(in);
        form.show();
        accepted = form.isAccepted();
        return accepted;
    }

    /**
     * Sets to the platform native look and feel.
     *
     * see http://javaalmanac.com/egs/javax.swing/LookFeelNative.html
     */
    private static void setUILookAndFeel() {
        // Get the native look and feel class name
        String nativeLF = UIManager.getSystemLookAndFeelClassName();

        // Install the look and feel.
        // some reports on the web shows that this causes 6389282 and 6585553
        // so catch any errors and ignore it. This is not a fatal issue
        // for the installer.
        try {
            UIManager.setLookAndFeel(nativeLF);
        } catch (Throwable e) {
        }
    }

    /**
     * Does the actual installation.
     */
    private static void install() throws IOException {
        ZipInputStream zip =
            new ZipInputStream(Main.class.getResourceAsStream("/package.zip"));
        ZipEntry e;
        while ((e = zip.getNextEntry()) != null) {
            File name = new File(e.getName());
            System.out.println(name);
            if (e.isDirectory()) {
                name.mkdirs();
            } else {
                File parent = name.getParentFile();
                if(parent!=null && !parent.exists())
                    parent.mkdirs();
                if (!name.exists())
                    copyStream(zip, new FileOutputStream(name));
            }
        }
        zip.close();
        System.out.println("installation complete");
    }

    public static void copyStream(InputStream in, OutputStream out)
        throws IOException {
        byte[] buf = new byte[256];
        int len;
        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }
        out.close();
    }
}
