/*
 *
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package com.sun.midp.appmanager;

import com.sun.midp.configurator.Constants;
import com.sun.midp.installer.GraphicalInstaller;
import com.sun.midp.log.LogChannels;
import com.sun.midp.log.Logging;
import com.sun.midp.main.MIDletProxy;
import com.sun.midp.midletsuite.MIDletInfo;
import com.sun.midp.midletsuite.MIDletSuiteImpl;
import com.sun.midp.midletsuite.MIDletSuiteInfo;
import com.sun.midp.midletsuite.MIDletSuiteStorage;

import javax.microedition.lcdui.Image;
import java.util.Vector;


/** Simple attribute storage for MIDlet suites */
public class RunningMIDletSuiteInfo extends MIDletSuiteInfo {
    // IMPL_NOTE: currently this value is 4, no need to specify a different initial size.
    /** The list of MIDlet proxies, one for each running MIDlet. It is set from AppManagerUI.java. */
    private Vector proxies = new Vector(Constants.MAX_ISOLATES);
    /** Icon for this suite. */
    public Image icon = null;
    /** Whether suite is under debug */
    public boolean isDebugMode = false;

    /**
     * Constructs a RunningMIDletSuiteInfo object for a suite.
     *
     * @param theID ID the system has for this suite
     */
    public RunningMIDletSuiteInfo(int theID) {
        super(theID);
    }

    /**
     * Constructs a RunningMIDletSuiteInfo object for a suite.
     *
     * @param theID ID the system has for this suite
     * @param theMidletToRun Class name of the only midlet in the suite
     * @param theDisplayName Name to display to the user
     * @param isEnabled true if the suite is enabled
     */
    public RunningMIDletSuiteInfo(int theID, String theMidletToRun,
            String theDisplayName, boolean isEnabled) {
        super(theID, theMidletToRun, theDisplayName, isEnabled);
        icon = getDefaultSingleSuiteIcon();
    }

    /**
     * Constructs a RunningMIDletSuiteInfo object for a suite.
     *
     * @param theID ID the system has for this suite
     * @param theMidletSuite MIDletSuite information
     * @param mss the midletSuite storage
     */
    public RunningMIDletSuiteInfo(int theID, MIDletSuiteImpl theMidletSuite,
                           MIDletSuiteStorage mss) {
        super(theID, theMidletSuite);

        icon = getIcon(theID, theMidletSuite.getProperty("MIDlet-Icon"), mss);
        if (icon == null && numberOfMidlets == 1) {
            MIDletInfo midlet =
                new MIDletInfo(theMidletSuite.getProperty("MIDlet-1"));

            // MIDlet icons are optional, so it the icon may be null
            icon = getIcon(theID, midlet.icon, mss);
        }

        if (icon == null) {
            icon = getDefaultSingleSuiteIcon();
        }
    }

    /**
     * Constructs a RunningMIDletSuiteInfo from MIDletSuiteInfo.
     *
     * @param info MIDletSuiteInfo reference
     * @param mss the midletSuite storage
     */
    public RunningMIDletSuiteInfo(MIDletSuiteInfo info,
                                  MIDletSuiteStorage mss) {
        super(info.suiteId, info.midletToRun, info.displayName,
              info.enabled);

        storageId = info.storageId;
        folderId = info.folderId;
        numberOfMidlets = info.numberOfMidlets;
        trusted = info.trusted;
        preinstalled = info.preinstalled;
        iconName = info.iconName;

        loadIcon(mss);
    }

    /**
     * Loads an icon for this suite.
     *
     * @param mss the midletSuite storage
     */
    public void loadIcon(MIDletSuiteStorage mss) {
        if (iconName != null) {
            icon = getIcon(suiteId, iconName, mss);
        }

        if (icon == null) {
            if (numberOfMidlets == 1) {
                icon = getDefaultSingleSuiteIcon();
            } else {
                icon = getDefaultMultiSuiteIcon();
            }
        }
    }

    /**
     * Gets suite icon either from image cache, or from the suite jar.
     *
     * @param theID the suite id that system has for this suite
     * @param iconName the name of the file where the icon is
     *     stored in the JAR
     * @param mss The midletSuite storage
     * @return Image provided by the application with
     *     the passed in iconName
     */
    public static Image getIcon(int theID, String iconName,
            MIDletSuiteStorage mss) {
        byte[] iconBytes;

        try {
            iconBytes = mss.getMIDletSuiteIcon(theID, iconName);

            if (iconBytes == null) {
                if (Logging.REPORT_LEVEL <= Logging.WARNING) {
                    Logging.report(Logging.WARNING, LogChannels.LC_AMS,
                        "getIcon: iconBytes == null");
                }
                return null;
            }

            return Image.createImage(iconBytes, 0, iconBytes.length);
        } catch (Throwable t) {
            if (Logging.REPORT_LEVEL <= Logging.WARNING) {
                Logging.report(Logging.WARNING, LogChannels.LC_AMS,
                    "getIcon threw an " + t.getClass());
            }
            return null;
        }
    }

    /**
     * Returns a string representation of the MIDletSuiteInfo object.
     * For debug only.
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("id = " + suiteId);
        b.append(", midletToRun = " + midletToRun);
        b.append(", proxies = [" + getProxies()+"]");
        return b.toString();
    }

    /** Cache of the suite icon. */
    private static Image multiSuiteIcon;

    /** Cache of the single suite icon. */
    private static Image singleSuiteIcon;

    /**
     * Gets the single MIDlet suite icon from storage.
     *
     * @return icon image
     */
    private static Image getDefaultSingleSuiteIcon() {
        if (singleSuiteIcon == null) {
            singleSuiteIcon = GraphicalInstaller.
                getImageFromInternalStorage("_ch_single");
        }
        return singleSuiteIcon;
    }

    /**
     * Gets the MIDlet suite icon from storage.
     *
     * @return icon image
     */
    private static Image getDefaultMultiSuiteIcon() {
        if (multiSuiteIcon == null) {
            multiSuiteIcon = GraphicalInstaller.
                getImageFromInternalStorage("_ch_suite");
        }
        return multiSuiteIcon;
    }

    /**
     * Check if midlet belongs to the same suite.
     * @param midlet the MIDlet proxy
     * @return true if the MIDletProxy and this RunningMIDletSuiteInfo have the same suite id.
     */
    public boolean sameSuite(MIDletProxy midlet) {
         return suiteId == midlet.getSuiteId();
    }

    /**
     * Check if the suite runs the specified MIDlet.
     * @param midlet specifies the MIDlet
     * @return true if the MIDlet proxy is found in the running MIDlet proxy list.
     */
    public boolean hasRunning(MIDletProxy midlet) {
        if (!sameSuite(midlet)) {
            return false;
        } else {
            synchronized (this) {
                return proxies.contains(midlet);
            }
        }
    }

    /**
     * Return the first item from the running MIDlet proxy list.
     * @return Proxy of a running MIDlet, or null
     */
    synchronized public MIDletProxy getFirstProxy() {
            return (proxies.size() != 0) ? (MIDletProxy) proxies.firstElement() : null;
    }

    /**
     * If the MIDlet identified by className is running, return its proxy.
     * @param className MIDlet class name
     * @return the running MIDlet proxy or null
     */
    public MIDletProxy getProxyFor(String className) {
        if (className == null) {
            return getFirstProxy();
        }
        synchronized (this) {
            for (int i=0; i<proxies.size(); i++) {
                if (className.equals(getProxyAt(i).getClassName())) {
                    return getProxyAt(i);
                }
            }
            return null;
        }
    }

    /**
     * Get the array of proxies for all running MIDlets.
     * @return the proxy array.
     */
    synchronized public MIDletProxy[] getProxies() {
        MIDletProxy[] proxyArray = new MIDletProxy[proxies.size()];
        proxies.copyInto(proxyArray);
        return proxyArray;
    }

    /**
     * Add all specified proxies to the list of MIDlet proxies.
     * @param newList specifies the MIDlet proxies to add
     */
    synchronized void addProxies(MIDletProxy[] newList) {
        for (int i=0; i<newList.length; i++) {
            if (!proxies.contains(newList[i])) {
                proxies.addElement(newList[i]);
            }
        }
    }

    /**
     * Get the i-th MIDlet proxy. An exception happens if i is not a valid proxy index.
     * @param i The number of MIDlet proxy in the midlet proxy list
     * @return the MIDlet proxy
     */
    synchronized public MIDletProxy getProxyAt(int i) {
        return (MIDletProxy) proxies.elementAt(i);
    }

    /**
     * Find proxy among the list of running MIDlet proxies.
     * @param proxy proxy for some MIDlet
     * @return true if this proxy is in the list of running midlets
     */
    synchronized public boolean hasProxy(MIDletProxy proxy) {
        return proxies.contains(proxy);
    }

    /**
     * Check the list of running MIDlet proxies for emptiness.
     * @return true if at least one MIDlet is running
     */
    public boolean hasRunningMidlet() {
        return !proxies.isEmpty();
    }

    /**
     * Get the number of entries in the MIDlet proxy list.
     * @return the number of running MIDlets from this suite
     */
    public int numberOfRunningMidlets() {
        return proxies.size();
    }

    /**
     * True if  alert is waiting for the foreground in at
     * least of one of the MIDlets from this suite.
     * @return true if there is a waiting alert
     */
    public boolean isAnyAlertWaiting() {
        boolean res = false;
        synchronized (this) {
            for (int i=0, n=numberOfRunningMidlets(); i<n && !res; i++) {
                res = getProxyAt(i).isAlertWaiting();
            }
        }
        return res;
    }

    /**
     * Add an item to the list of running MIDlet proxies.
     * @param proxy the MIDlet proxy to add. Nothing is done when it's null.
     */
    public void addProxy(MIDletProxy proxy) {
        if (proxy != null) {
            synchronized (this) {
                proxies.addElement(proxy);
            }
        }
    }

    /**
     * Remove an item from the running MIDlet proxy list.
     * @param proxy a MIDlet proxy to remove from the list
     */
    synchronized public void removeProxy(MIDletProxy proxy) {
        proxies.removeElement(proxy);
    }
}