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

package com.sun.midp.automation;
import com.sun.midp.events.*;
import javax.microedition.lcdui.Image;

/**
 * Implements Automation class abstract methods.
 */
final class AutomationImpl extends Automation {
    /** The one and only class instance */
    private static AutomationImpl instance = null;

    /** Event queue */
    private EventQueue eventQueue;

    /** Event factory */
    private AutoEventFactoryImpl eventFactory;

    /** index 0: foreground isolate id, index 1: foreground display id */
    private int[] foregroundIsolateAndDisplay;
    
    private AutoScreenshotTaker screenshotTaker;


    /**
     * Gets instance of AutoSuiteStorage class.
     *
     * @return instance of AutoSuiteStorage class
     */
    public AutoSuiteStorage getStorage() {
        return AutoSuiteStorageImpl.getInstance();
    }

    /**
     * Gets instance of AutoEventFactory class.
     *
     * @return instance of AutoEventFactory class
     */
    public AutoEventFactory getEventFactory() {
        return AutoEventFactoryImpl.getInstance();
    }

    /**
     * Simulates single event.
     *
     * @param event event to simulate
     */    
    public void simulateEvents(AutoEvent event) 
        throws IllegalArgumentException {

        if (event == null) {
            throw new IllegalArgumentException("Event is null");
        }

        // Delay event is a special case
        if (event.getType() == AutoEventType.DELAY) {
            AutoDelayEvent delayEvent = (AutoDelayEvent)event;
            try {
                Thread.sleep(delayEvent.getMsec());
            } catch (InterruptedException e) {
            }

            return;
        }

        // obtain native event corresponding to this AutoEvent
        AutoEventImplBase eventBase = (AutoEventImplBase)event;
        NativeEvent nativeEvent = eventBase.toNativeEvent();
        if (nativeEvent == null) {
            throw new IllegalArgumentException(
                    "Can't simulate this type of event: " + 
                    eventBase.getType().getName());
        }

        // obtain ids of foreground isolate and display
        int foregroundIsolateId;
        int foregroundDisplayId;
        synchronized (foregroundIsolateAndDisplay) {
            getForegroundIsolateAndDisplay(foregroundIsolateAndDisplay);
            foregroundIsolateId = foregroundIsolateAndDisplay[0];
            foregroundDisplayId = foregroundIsolateAndDisplay[1];
        }

        // and send this native event to foreground isolate
        nativeEvent.intParam4 = foregroundDisplayId;
        eventQueue.sendNativeEventToIsolate(nativeEvent, foregroundIsolateId);
    }

    /**
     * Simulates (replays) sequence of events.
     *
     * @param events event sequence to simulate
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulateEvents(AutoEventSequence events) 
        throws IllegalArgumentException {

        if (events == null) {
            throw new IllegalArgumentException("Event sequence is null");
        }
        
        AutoEvent[] arr = events.getEvents();
        for (int i = 0; i < arr.length; ++i) {
            simulateEvents(arr[i]);
        }
    }

    /**
     * Simulates (replays) sequence of events.
     *
     * @param events event sequence to simulate
     * @param delayDivisor a double value for adjusting duration 
     * of delays within the sequence: duration of all delays is 
     * divided by this value. It allows to control the speed of
     * sequence replay. For example, to make it replay two times 
     * faster, specify 2.0 as delay divisor. 
     */
    public void simulateEvents(AutoEventSequence events, 
            double delayDivisor) 
        throws IllegalArgumentException {

        if (events == null) {
            throw new IllegalArgumentException("Event sequence is null");
        }

        if (delayDivisor == 0.0) {
            throw new IllegalArgumentException("Delay divisor is zero");
        }

        AutoEvent[] arr = events.getEvents();
        for (int i = 0; i < arr.length; ++i) {
            AutoEvent event = arr[i];

            if (event.getType() == AutoEventType.DELAY) {
                AutoDelayEvent delayEvent = (AutoDelayEvent)event;
                double msec = delayEvent.getMsec();
                simulateDelayEvent((int)(msec/delayDivisor));
            } else {
                simulateEvents(event);
            }
        }        
    }

    /**
     * Simulates key event. 
     *
     * @param keyCode key code not representable as character 
     * (soft key, for example)
     * @param keyState key state 
     * @param delayMsec delay in milliseconds before simulating the event
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulateKeyEvent(AutoKeyCode keyCode, AutoKeyState keyState, 
            int delayMsec) 
        throws IllegalArgumentException {

        if (delayMsec != 0) {
            simulateDelayEvent(delayMsec);
        }

        AutoEvent e = eventFactory.createKeyEvent(keyCode, keyState);
        simulateEvents(e);
    }
    
    /**
     * Simulates key event. 
     *
     * @param keyChar key character (letter, digit)
     * @param keyState key state 
     * @param delayMsec delay in milliseconds before simulating the event
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulateKeyEvent(char keyChar, AutoKeyState keyState, 
            int delayMsec) 
        throws IllegalArgumentException {

        if (delayMsec != 0) {
            simulateDelayEvent(delayMsec);
        }

        AutoEvent e = eventFactory.createKeyEvent(keyChar, keyState);
        simulateEvents(e);
    }

    /**
     * Simulates key click (key pressed and then released). 
     *
     * @param keyCode key code not representable as character 
     * (soft key, for example)
     * @param delayMsec delay in milliseconds before simulating the click
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulateKeyClick(AutoKeyCode keyCode, int delayMsec) 
        throws IllegalArgumentException {

        AutoEvent e;

        if (delayMsec != 0) {
            simulateDelayEvent(delayMsec);
        }        

        e = eventFactory.createKeyEvent(keyCode, AutoKeyState.PRESSED);
        simulateEvents(e);

        e = eventFactory.createKeyEvent(keyCode, AutoKeyState.RELEASED);
        simulateEvents(e);
    }
    
    /**
     * Simulates key click (key pressed and then released). 
     *
     * @param keyChar key character (letter, digit)
     * @param delayMsec delay in milliseconds before simulating the click
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulateKeyClick(char keyChar, int delayMsec) 
        throws IllegalArgumentException {

        AutoEvent e;

        if (delayMsec != 0) {
            simulateDelayEvent(delayMsec);
        }        

        e = eventFactory.createKeyEvent(keyChar, AutoKeyState.PRESSED);
        simulateEvents(e);

        e = eventFactory.createKeyEvent(keyChar, AutoKeyState.RELEASED);
        simulateEvents(e);
    }

    /**
     * Simulates pen event.
     *
     * @param x x coord of pen tip
     * @param y y coord of pen tip
     * @param penState pen state
     * @param delayMsec delay in milliseconds before simulating the event 
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulatePenEvent(int x, int y, AutoPenState penState, 
            int delayMsec) 
        throws IllegalArgumentException {

        if (delayMsec != 0) {
            simulateDelayEvent(delayMsec);
        }        

        AutoEvent e = eventFactory.createPenEvent(x, y, penState);
        simulateEvents(e);
    }

    /**
     * Simulates pen click (pen tip pressed and then released).
     *
     * @param x x coord of pen tip
     * @param y y coord of pen tip
     * @param delayMsec delay in milliseconds before simulating the click
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulatePenClick(int x, int y, int delayMsec) 
        throws IllegalArgumentException {

        if (delayMsec != 0) {
            simulateDelayEvent(delayMsec);
        }
        
        AutoEvent e;

        e = eventFactory.createPenEvent(x, y, AutoPenState.PRESSED);
        simulateEvents(e);

        e = eventFactory.createPenEvent(x, y, AutoPenState.RELEASED);
        simulateEvents(e);
    }

    /**
     * Simulates delay event.
     *
     * @param msec delay value in milliseconds 
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public void simulateDelayEvent(int msec) 
        throws IllegalArgumentException {

        AutoEvent e =  eventFactory.createDelayEvent(msec);
        simulateEvents(e);        
    }

    public Image getScreenshot() {
        Image screen = null;

        screenshotTaker.takeScreenshot();

        int w = screenshotTaker.getScreenshotWidth();
        int h = screenshotTaker.getScreenshotHeight();
        int[] rgb = screenshotTaker.getScreenshotRGB();

        if (rgb != null)  {
            screen = Image.createRGBImage(rgb, w, h, false);
        }

        return screen;
    }
    
    /**
     * Gets instance of Automation class.
     *
     * @return instance of Automation class
     * @throws IllegalStateException if Automation API hasn't been
     * initialized or is not permitted to use
     */
    static final synchronized Automation getInstanceImpl() 
        throws IllegalStateException {
        
        AutomationInitializer.guaranteeAutomationInitialized();
        if (instance == null) {
            instance = new AutomationImpl(
                    AutomationInitializer.getEventQueue());
        }
        
        return instance;
    }

    /**
     * Gets ids of foreground isolate and display
     *
     * @param foregroundIsolateAndDisplay array to store ids in
     */
    private static native void getForegroundIsolateAndDisplay(
            int[] foregroundIsolateAndDisplay);

    /**
     * Private constructor to prevent creating class instances.
     *
     * @param eventQueue event queue
     */
    private AutomationImpl(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
        this.eventFactory = AutoEventFactoryImpl.getInstance();
        this.foregroundIsolateAndDisplay = new int[2];
        this.screenshotTaker = new AutoScreenshotTaker();
    } 
}
