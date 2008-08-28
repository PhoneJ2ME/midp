/*
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
package com.sun.midp.io.j2me.pipe;

import com.sun.midp.io.ConnectionBaseAdapter;
import java.io.IOException;
import javax.microedition.io.Connection;

class PipeClientConnectionImpl extends ConnectionBaseAdapter {

    PipeClientConnectionImpl() {
    }

    PipeClientConnectionImpl(Object suiteId, String serverName, String version) {
    }
    
    void connect(int mode) throws IOException {
        initStreamConnection(mode);
    }

    public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
        throw new IOException("This method should not be called because it shoud not exist. Please refactor");
    }

    protected void disconnect() throws IOException {
        
    }

    protected int readBytes(byte[] b, int off, int len) throws IOException {
        throw new IOException("TODO");
    }

    protected int writeBytes(byte[] b, int off, int len) throws IOException {
        throw new IOException("TODO");
    }

}