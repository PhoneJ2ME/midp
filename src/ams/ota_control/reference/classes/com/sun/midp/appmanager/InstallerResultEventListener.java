/*
 * Copyright 2009 Sun Microsystems, Inc. All Rights Reserved.
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

import com.sun.midp.events.EventQueue;
import com.sun.midp.events.EventTypes;
import com.sun.midp.events.Event;
import com.sun.midp.events.NativeEvent;
import com.sun.midp.events.EventListener;

import com.sun.midp.log.Logging;
import com.sun.midp.log.LogChannels;

/**
 * Listener for installation results.
 */
public class InstallerResultEventListener implements EventListener {

    /** Consumer that will process events */
    private InstallerResultEventConsumer installerResultEventConsumer;

    /**
     * Default public constructor.
     *
     * @param  eventQueue reference to the event queue
     * @param  odtControllerEventConsumerParam comsumer that will process
     *         events received by this listener
     */
    public InstallerResultEventListener(
        EventQueue eventQueue,
        InstallerResultEventConsumer installerResultEventConsumerParam) {

        installerResultEventConsumer = installerResultEventConsumerParam;

        eventQueue.registerEventListener(
            EventTypes.MIDP_INSTALLATION_DONE_EVENT, this);
    }

    /**
     * Preprocess an event that is being posted to the event queue.
     *
     * @param event event being posted
     *
     * @param waitingEvent previous event of this type waiting in the
     *     queue to be processed
     *
     * @return true to allow the post to continue, false to not post the
     *     event to the queue
     */
    public boolean preprocess(Event event, Event waitingEvent) {
        return true;
    }

    /**
     * Processes events.
     *
     * @param event event to process
     */
    public void process(Event event) {
        try {
            NativeEvent nativeEvent = (NativeEvent)event;

            switch (nativeEvent.getType()) {

                case EventTypes.MIDP_INSTALLATION_DONE_EVENT: {
                    int result = nativeEvent.intParam1;
                    int suiteId = nativeEvent.intParam2;
                    String exception = nativeEvent.stringParam1;
                    String message = nativeEvent.stringParam2;
                    int reason = nativeEvent.intParam3;
                    String extraData = nativeEvent.stringParam3;

                    installerResultEventConsumer.handleODDInstallationDoneEvent(
                            result, suiteId, exception, message, reason,
                            extraData
                    );
                    return;
                }

                default: {
                    if (Logging.REPORT_LEVEL <= Logging.WARNING) {
                        Logging.report(Logging.WARNING, LogChannels.LC_CORE,
                           "unknown event (" + event.getType() + ")");
                    }
                    return;
                }
            }
        } catch (Throwable t) {
            if (Logging.TRACE_ENABLED) {
                Logging.trace(t, "Error occurred processing installer " +
                        "result event " + event.getType());
            }
        }
    }
}
