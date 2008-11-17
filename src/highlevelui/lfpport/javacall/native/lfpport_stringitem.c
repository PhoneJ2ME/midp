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
 * @ingroup highui_lfpport
 *
 * @brief StringItem-specific porting functions and data structures.
 */

#include <lfpport_displayable.h>
#include <lfpport_item.h>
#include <lfpport_font.h>
#include <lfpport_stringitem.h>
#include "lfpport_gtk.h"

#include <gtk/gtk.h>
#include <gdk/gdk.h>

#ifdef __cplusplus
extern "C" {
#endif

extern GtkVBox *main_container;
extern GtkWidget *main_window;

MidpError lfpport_string_item_show_cb(MidpItem* itemPtr){
    GtkWidget *string_item = (GtkWidget*)itemPtr->widgetPtr;
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    gtk_widget_show(string_item);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}
MidpError lfpport_string_item_hide_cb(MidpItem* itemPtr){
    GtkWidget *string_item = (GtkWidget*)itemPtr->widgetPtr;
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);

    gtk_widget_hide((GtkWidget *)string_item);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_string_item_set_label_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}
MidpError lfpport_string_item_destroy_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

MidpError lfpport_string_item_get_min_height_cb(int *height, MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *height = STUB_MIN_HEIGHT;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_string_item_get_min_width_cb(int *width, MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *width = STUB_MIN_WIDTH;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_string_item_get_pref_height_cb(int* height,
                                                 MidpItem* itemPtr,
                                                 int lockedWidth){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *height = STUB_PREF_HEIGHT;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_string_item_get_pref_width_cb(int* width,
                                                MidpItem* itemPtr,
                                                int lockedHeight){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *width = STUB_PREF_WIDTH;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_string_item_handle_event_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

MidpError lfpport_string_item_relocate_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

MidpError lfpport_string_item_resize_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}



/**
 * Creates a string item's native peer, but does not display it.  When
 * this function returns successfully, the fields in *itemPtr should be
 * set.
 *
 * @param itemPtr pointer to the string item's MidpItem structure.
 * @param ownerPtr pointer to the item's owner(form)'s MidpDisplayable
 *                 structure.
 * @param label the item label.
 * @param layout the item layout directive.
 * @param text the text to be used to create this string item.
 * @param fontPtr pointer to the font to be used.
 * @param appearanceMode appearance mode for the string
 *                       item (eg. PLAIN, HYPERLINK and so on).
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_stringitem_create(MidpItem* itemPtr,
				    MidpDisplayable* ownerPtr,
				    const pcsl_string* label, int layout,
				    const pcsl_string* text,
				    PlatformFontPtr fontPtr,
				    int appearanceMode){
    GtkWidget *box;
    GtkWidget *string_item_label;
    GtkWidget *string_item_text;
    GtkWidget *form;
    GtkWidget *vbox;
    int label_len, text_len;

    gchar label_buf[MAX_TEXT_LENGTH];
    gchar text_buf[MAX_TEXT_LENGTH];

    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);

    pcsl_string_convert_to_utf8(label, label_buf, MAX_TEXT_LENGTH, &label_len);
    pcsl_string_convert_to_utf8(text, text_buf,  MAX_TEXT_LENGTH, &text_len);


    box = gtk_hbox_new(FALSE, 0);
    string_item_label = gtk_label_new(label_buf);
    string_item_text = gtk_label_new(text_buf);
    gtk_widget_show(string_item_label);
    gtk_widget_show(string_item_text);

    gtk_box_pack_start(GTK_BOX (box), string_item_label, FALSE, FALSE, 0);
    gtk_box_pack_start(GTK_BOX (box), string_item_text, FALSE, FALSE, 0);

    form = (GtkWidget*)ownerPtr->frame.widgetPtr;
    vbox = gtk_object_get_user_data(form);
    gtk_box_pack_start(GTK_BOX(vbox),
                       box,
                       FALSE, FALSE, 0);

    /* set font */
    itemPtr->widgetPtr = box;
    itemPtr->ownerPtr = ownerPtr;
    itemPtr->layout = layout;

    itemPtr->show = lfpport_string_item_show_cb;
    itemPtr->hide = lfpport_string_item_hide_cb;
    itemPtr->setLabel = lfpport_string_item_set_label_cb;
    itemPtr->destroy = lfpport_string_item_destroy_cb;

    //itemPtr->component
    itemPtr->getMinimumHeight = lfpport_string_item_get_min_height_cb;
    itemPtr->getMinimumWidth = lfpport_string_item_get_min_width_cb;
    itemPtr->getPreferredHeight = lfpport_string_item_get_pref_height_cb;
    itemPtr->getPreferredWidth = lfpport_string_item_get_pref_width_cb;
    itemPtr->handleEvent = lfpport_string_item_handle_event_cb;
    itemPtr->relocate = lfpport_string_item_relocate_cb;
    itemPtr->resize = lfpport_string_item_resize_cb;

    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}


/**
 * Notifies the native peer of a change in the item's content.
 *
 * @param itemPtr pointer to the string item's MidpItem structure.
 * @param text the new string.
 * @param appearanceMode the appearance mode of the passed in text.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_stringitem_set_content(MidpItem* itemPtr,
					 const pcsl_string* text,
					 int appearanceMode){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

/**
 * Notifies the native peer of a change in the item's font.
 *
 * @param itemPtr pointer to the string item's MidpItem structure.
 * @param fontPtr the new font.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_stringitem_set_font(MidpItem* itemPtr,
				      PlatformFontPtr fontPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}


#ifdef __cplusplus
} /* extern "C" */
#endif

