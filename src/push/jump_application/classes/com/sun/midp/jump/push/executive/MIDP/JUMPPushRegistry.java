/*
 * Copyright  1990-2006 Sun Microsystems, Inc. All Rights Reserved.
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

package com.sun.midp.jump.push.executive.MIDP;

import com.sun.midp.jump.push.executive.JUMPConnectionInfo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.microedition.io.ConnectionNotFoundException;

/**
 * PushRegistry system interface for MIDP system.
 *
 * <p>
 * There would be a way in <code>MIDlet</code> container process to obtain
 * a singleton implementing this interface.  This singleton would
 * hide behind the scene all IPC.
 * </p>
 */
public interface JUMPPushRegistry extends Remote {
    /**
     * Registers new PushRegistry connection.
     *
     * <p>
     * Informs executive on registration of new PushRegistry connection
     * (to be called, e.g., from
     * <code>JUMPPushRegistry.registerConnection</code> method).
     * </p>
     *
     * <p>
     * <strong>Precondition</strong>: <code>connection</code> MUST not have been
     * already registered (it should be easy to check with connection locks).
     * </p>
     *
     * <p>
     * <strong>NB</strong>: locking/unlocking of the connections isn't
     * performed, it's callee who should do it.
     * </p>
     *
     * @param midletSuiteId ID of <code>MIDlet suite</code> to register
     *  connection for
     * @param connection Connection to register
     *
     * @return <code>true</code> if connection has been registered,
     * <code>false</code> otherwise.
     *
     * @throws RemoteException as requested by RMI spec.
     */
    boolean registerConnection(int midletSuiteId,
            JUMPConnectionInfo connection) throws RemoteException;

    /**
     * Unregisters PushRegistry connection.
     *
     * <p>
     * It's way to inform executive on unregistration of PushRegistry connection
     * (to be called, e.g., from
     * <code>JUMPPushRegistry.unregisterConnection</code> method).
     * </p>
     *
     * <p>
     * <strong>Precondition</strong>: <code>connection</code> MUST have been
     * already registered.
     * </p>
     *
     * <p>
     * <strong>NB</strong>: locking/unlocking of the connections isn't
     * performed, it's callee who should do it.
     * </p>
     *
     * @param midletSuiteId ID of <code>MIDlet suite</code> to unregister
     *  connection for
     * @param connection Connection to unregister
     *
     * @return <code>true</code> if connection has been unregistered,
     * <code>false</code> otherwise.
     *
     * @throws RemoteException as requested by RMI spec.
     */
    boolean unregisterConnection(int midletSuiteId,
            JUMPConnectionInfo connection) throws RemoteException;

    /**
     * Registers an alarm.
     *
     * This method is an internal counterpart of <code>PushRegistry.registerAlarm</code>
     * method.
     *
     * <p>
     * NOTE: <code>midletSuiteId</code> and <code>midlet</code> parameters should
     *  refer to valid entities (that is, e.g., <code>midlet</code> should be the name
     *  of the <code>MIDlet</code> from the <code>midletSuiteId</code> suite).
     *  No checks are performed and incorrect values would lead to undefined
     *  behaviour.
     * </p>
     *
     * @param midletSuiteId ID of <code>MIDlet suite</code> to unregister
     *  connection for
     *
     * @param midlet <code>MIDlet</code> class name
     *
     * @param time alarm time
     *
     * @return time of previous registered (but not fired) alarm or 0
     *
     * @throws RemoteException as requested by RMI spec.
     * @throws ConnectionNotFoundException if it's impossible to register alarm
     */
    long registerAlarm(int midletSuiteId, String midlet, long time)
        throws RemoteException, ConnectionNotFoundException;
}
