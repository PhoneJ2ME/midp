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

package com.sun.midp.jump.isolate;

import com.sun.midp.midletsuite.MIDletSuiteStorage;
import com.sun.midp.midlet.MIDletStateHandler;

class MidletSuiteContainer implements com.sun.midp.rms.SuiteContainer {
    MIDletSuiteStorage suiteStorage;

    MidletSuiteContainer(MIDletSuiteStorage storage) {
        suiteStorage = storage;
    }

    /**
     * Get the suite ID of applicaiton on the current call stack.
     */
    public int getCallersSuiteId() {
        return MIDletStateHandler.getMidletStateHandler().
            getMIDletSuite().getID();
    }
    
    /**
     * Get the suite of identified by vendor and suite name.
     */
    public int getSuiteID(String vendorName, String suiteName) {
        return MIDletSuiteStorage.getSuiteID(vendorName, suiteName);
    }

    /**
     * Get secure filename base to build a an RMS file name that will be
     * deleted with the suite.
     */
    public String getSecureFilenameBase(int suiteId) {
        return suiteStorage.getSecureFilenameBase(suiteId);
    }
    
    /**
     * Get the storage area ID for a suite.
     * This is only CLDC RescordStore implementations, any other implementations can just
     * return 0.
     */
    public int getStorageAreaId(int suiteId) {
        return suiteStorage.getStorageAreaId(suiteId);
    }
}