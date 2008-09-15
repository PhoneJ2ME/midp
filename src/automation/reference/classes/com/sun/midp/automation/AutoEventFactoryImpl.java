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
import java.util.*;

final class AutoEventFactoryImpl implements AutoEventFactory {
    /** The one and only instance */
    private static AutoEventFactoryImpl instance = null;

    /** All EventFromStringFactory instances indexed by prefix */
    private Hashtable eventFromStringFactories;

    /** Event string parser */
    private AutoEventStringParser eventStringParser;

    /**
     * Gets instance of AutoEventFactoryImpl class.
     *
     * @return instance of AutoEventFactoryImpl class
     */
    static final synchronized AutoEventFactoryImpl getInstance() {
        if (instance == null) {
            instance = new AutoEventFactoryImpl();
        }            

        return instance;
    }

    /**
     * Creates event sequence from string representation.
     *
     * @param sequenceString string representation of event sequence
     * @param offset offset into sequenceString
     * @return AutoEventSequence corresponding to the specified string 
     * representation
     * @throws IllegalArgumentException if specified string isn't valid 
     * string representation of events sequence
     */
    public AutoEventSequence createFromString(String eventString, int offset) 
        throws IllegalArgumentException {

        int curOffset = offset;
        AutoEventSequence seq = new AutoEventSequenceImpl();
        AutoEvent[] events = null;

        eventStringParser.parse(eventString, curOffset);
        String eventPrefix = eventStringParser.getEventPrefix();
        while (eventPrefix != null) {
            Object o = eventFromStringFactories.get(eventPrefix);
            AutoEventFromStringFactory f = (AutoEventFromStringFactory)o;
            if (f == null) {
                throw new IllegalArgumentException(
                        "Illegal event prefix: " + eventPrefix);
            }

            Hashtable eventArgs = eventStringParser.getEventArgs();
            events = f.create(eventArgs);
            seq.addEvents(events);            

            curOffset = eventStringParser.getEndOffset(); 
            eventStringParser.parse(eventString, curOffset);
            eventPrefix = eventStringParser.getEventPrefix();
        }
        
        return seq;
    }

    /**
     * Creates event sequence from string representation.
     *
     * @param sequenceString string representation of event sequence
     * @return AutoEventSequence corresponding to the specified string 
     * representation
     * @throws IllegalArgumentException if specified string isn't valid 
     * string representation of events sequence
     */
    public AutoEventSequence createFromString(String eventString)
        throws IllegalArgumentException {

        return createFromString(eventString, 0);
    }

    /**
     * Creates key event.
     *
     * @param keyCode key code not representable as character 
     * (soft key, for example) 
     * @param keyState key state
     * @return AutoKeyEvent instance representing key event
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public AutoKeyEvent createKeyEvent(AutoKeyCode keyCode, 
            AutoKeyState keyState) 
        throws IllegalArgumentException {

        return new AutoKeyEventImpl(keyCode, keyState);
    }

    /**
     * Creates key event.
     *
     * @param keyChar key char (letter, digit)
     * @param keyState key state
     * @return AutoKeyEvent representing key event
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public AutoKeyEvent createKeyEvent(char keyChar, AutoKeyState keyState) 
        throws IllegalArgumentException {

        return new AutoKeyEventImpl(keyChar, keyState);
    }

    /**
     * Creates pen event.
     *
     * @param x x coord of pen tip
     * @param y y coord of pen tip
     * @param penState pen state
     * @return AutoPenEvent representing pen event
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public AutoPenEvent createPenEvent(int x, int y, AutoPenState penState) 
        throws IllegalArgumentException {

        return new AutoPenEventImpl(x, y, penState);
    }

    /**
     * Creates delay event.
     *
     * @param msec delay value in milliseconds
     * @return AutoDelayEvent representing delay event
     * @throws IllegalArgumentException if some of the specified 
     * parameters has illegal value
     */
    public AutoDelayEvent createDelayEvent(int msec) 
        throws IllegalArgumentException {

        return new AutoDelayEventImpl(msec);
    }

    /**
     * Registers all AutoEventFromStringFactory factories.
     */ 
    private void registerEventFromStringFactories() {
        AutoEventFromStringFactory f;
    
        f = new AutoKeyEventFromStringFactory();
        eventFromStringFactories.put(f.getPrefix(), f);

        f = new AutoPenEventFromStringFactory();
        eventFromStringFactories.put(f.getPrefix(), f);

        f = new AutoDelayEventFromStringFactory();
        eventFromStringFactories.put(f.getPrefix(), f);
    }


    /**
     * Private constructor to prevent creating class instances.
     */
    private AutoEventFactoryImpl() {
        eventFromStringFactories = new Hashtable();
        eventStringParser = new AutoEventStringParser();

        registerEventFromStringFactories();
    }
}
