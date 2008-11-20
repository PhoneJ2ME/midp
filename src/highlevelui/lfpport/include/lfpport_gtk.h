/*
 *
 *
 * Copyright  1990-2008 Sun Microsystems, Inc. All Rights Reserved.
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

/**
 * @file
 * @ingroup lfpport_gtk.h
 *
 * @brief general header file
 */
#ifndef _LFPPORT_GTK_H_
#define _LFPPORT_GTK_H_


#define     STUB_MIN_HEIGHT     20
#define     STUB_MIN_WIDTH      20
#define     STUB_PREF_HEIGHT    20
#define     STUB_PREF_WIDTH     20

#define MAX_TEXT_LENGTH 256

#include <stdio.h>  //TODO@gd212247:  remove at release
#include <fcntl.h>
#include <syslog.h>
#include <gtk/gtk.h>

extern char *DEBUG_FNAME;
extern char debug_buff[1024];
extern int  debug_fid;


#define LIMO_TRACE(...) do { \
                   if (debug_fid < 0 ) \
                       debug_fid = open(DEBUG_FNAME, O_CREAT|O_WRONLY, (int)0x0666); \
                   if (debug_fid >= 0 ) { \
                       sprintf(debug_buff,  __VA_ARGS__); \
                       write(debug_fid, debug_buff, strlen(debug_buff)); \
                   } \
                   } while (0)


#endif //_LFPPORT_GTK_H_

