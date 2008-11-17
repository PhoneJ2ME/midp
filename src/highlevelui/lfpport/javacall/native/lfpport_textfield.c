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
 * @brief TextField-specific porting functions and data structures.
 */

#include <midpMalloc.h>
#include <lfpport_displayable.h>
#include <lfpport_item.h>
#include <lfpport_textfield.h>
#include "lfpport_gtk.h"

#include <gtk/gtk.h>
#include <gdk/gdk.h>


#ifdef __cplusplus
extern "C" {
#endif

extern GtkVBox *main_container;
extern GtkWidget *main_window;

extern MidpError gchar_to_pcsl_string(gchar *src, pcsl_string *dst);

typedef struct {
    GtkWidget *container;
    int maxSize;
    int conststaints;
} TextFieldItem;


MidpError lfpport_text_field_show_cb(MidpItem* itemPtr){
    GtkWidget *text_field = ((TextFieldItem *)itemPtr->widgetPtr)->container;
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    gtk_widget_show(text_field);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}



MidpError lfpport_text_field_hide_cb(MidpItem* itemPtr){
    GtkWidget *text_field;

    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    text_field = ((TextFieldItem *)itemPtr->widgetPtr)->container;
    gtk_widget_hide(text_field);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);

    return KNI_OK;
}

MidpError lfpport_text_field_set_label_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}


MidpError lfpport_text_field_destroy_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}


MidpError lfpport_text_field_get_min_height_cb(int *height, MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *height = STUB_MIN_HEIGHT;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}


MidpError lfpport_text_field_get_min_width_cb(int *width, MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *width = STUB_MIN_WIDTH;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_text_field_get_pref_height_cb(int* height,
                                                 MidpItem* itemPtr,
                                                 int lockedWidth){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *height = STUB_PREF_HEIGHT;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_text_field_get_pref_width_cb(int* width,
                                                MidpItem* itemPtr,
                                                int lockedHeight){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    *width = STUB_PREF_WIDTH;
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_text_field_handle_event_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

MidpError lfpport_text_field_relocate_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

MidpError lfpport_text_field_resize_cb(MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}



/**
 * Creates a text field's native peer, but does not display it.
 * When this function returns successfully, the fields in *itemPtr should
 * be set.
 *
 * @param itemPtr pointer to the text field's MidpItem structure.
 * @param ownerPtr pointer to the item's owner(form)'s MidpDisplayable
 *                 structure.
 * @param label the item label.
 * @param layout the item layout directive.
 * @param text the initial text for the text field.
 * @param maxSize maximum size of the text field.
 * @param constraints constraints to be validated against, during
 *                    text input.
 * @param initialInputMode suggested input mode on creation.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_textfield_create(MidpItem* itemPtr,
				   MidpDisplayable* ownerPtr,
				   const pcsl_string* label, int layout,
				   const pcsl_string* text, int maxSize,
				   int constraints,
				   const pcsl_string* initialInputMode){

    GtkWidget *box;
    GtkWidget *vbox;
    GtkWidget *form;
    GtkWidget *text_field_label;
    GtkWidget *text_field_text;
    int label_len, text_len;

    TextFieldItem *text_field_item;

    gchar label_buf[MAX_TEXT_LENGTH];
    gchar text_buf[MAX_TEXT_LENGTH];

    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);

    text_field_item = (TextFieldItem *)midpMalloc(sizeof(TextFieldItem));

    pcsl_string_convert_to_utf8(label, label_buf, MAX_TEXT_LENGTH, &label_len);
    pcsl_string_convert_to_utf8(text, text_buf,  MAX_TEXT_LENGTH, &text_len);


    box = gtk_hbox_new(FALSE, 0);
    text_field_label = gtk_label_new(label_buf);
    text_field_text = gtk_entry_new();
    gtk_entry_set_text(text_field_text, text_buf);
    gtk_entry_set_editable(text_field_text, TRUE);
    gtk_widget_show(text_field_label);
    gtk_widget_show(text_field_text);
    gtk_box_pack_start(GTK_BOX (box), text_field_label, FALSE, FALSE, 0);
    gtk_box_pack_start(GTK_BOX (box), text_field_text, FALSE, FALSE, 0);

    text_field_item->container = box;
    text_field_item->conststaints = constraints;
    text_field_item->maxSize = maxSize;

    form = (GtkWidget*)ownerPtr->frame.widgetPtr;
    vbox = gtk_object_get_user_data(form);

    syslog(LOG_INFO, "%s user_data is %d\n", __FUNCTION__, vbox);
    gtk_box_pack_start(GTK_BOX(vbox),
                       box,
                       FALSE, FALSE, 0);

    /* set font */
    syslog(LOG_INFO, "%s setting textfield container to  %d\n", __FUNCTION__, box);
    itemPtr->widgetPtr = text_field_item;
    itemPtr->ownerPtr = ownerPtr;
    itemPtr->layout = layout;

    itemPtr->show = lfpport_text_field_show_cb;
    itemPtr->hide = lfpport_text_field_hide_cb;
    itemPtr->setLabel = lfpport_text_field_set_label_cb;
    itemPtr->destroy = lfpport_text_field_destroy_cb;

    //itemPtr->component
    itemPtr->getMinimumHeight = lfpport_text_field_get_min_height_cb;
    itemPtr->getMinimumWidth = lfpport_text_field_get_min_width_cb;
    itemPtr->getPreferredHeight = lfpport_text_field_get_pref_height_cb;
    itemPtr->getPreferredWidth = lfpport_text_field_get_pref_width_cb;
    itemPtr->handleEvent = lfpport_text_field_handle_event_cb;
    itemPtr->relocate = lfpport_text_field_relocate_cb;
    itemPtr->resize = lfpport_text_field_resize_cb;

    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

/**
 * Notifies the native peer of a change in the text field's content.
 *
 * @param itemPtr pointer to the text field's MidpItem structure.
 * @param text the new string.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_textfield_set_string(MidpItem* itemPtr, const pcsl_string* text){
    GtkWidget *text_field;
    MidpError status;
    GList *list;
    GList *textWidget;
    gchar buf[MAX_TEXT_LENGTH];
    int length;

    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);

    text_field = ((TextFieldItem *)itemPtr->widgetPtr)->container;
    list = gtk_container_get_children(text_field);
    textWidget = g_list_nth(list, 1);
    pcsl_string_convert_to_utf8(text, buf, MAX_TEXT_LENGTH, &length);
    gtk_entry_set_text(textWidget->data, buf);

    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return KNI_OK;
}

/**
 * Gets the current contents of the text field.
 *
 * @param text pointer to the text field's current content. This
 *             function sets text's value.
 * @param newChange pointer to a flag that is true when the text field's
 *        content has changed since the last call to this function, and is
 *        false otherwise. This function sets newChange's value.
 * @param itemPtr pointer to the text field's MidpItem structure.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_textfield_get_string(pcsl_string* text, jboolean* newChange,
				       MidpItem* itemPtr){
    GtkWidget *text_field;
    MidpError status;
    GList *list;
    GList *textWidget;

    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);

    text_field = ((TextFieldItem *)itemPtr->widgetPtr)->container;
    list = gtk_container_get_children(text_field);
    textWidget = g_list_nth(list, 1);

    status = gchar_to_pcsl_string(gtk_entry_get_text((GtkEntry* )textWidget->data),
                                   text);

    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return status;
}

/**
 * Notifies the native peer of a change in the maximum size of its text field.
 *
 * @param itemPtr pointer to the text field's MidpItem structure.
 * @param maxSize the new maximum size.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_textfield_set_max_size(MidpItem* itemPtr, int maxSize){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

/**
 * Gets the current input position.
 *
 * @param position pointer to current position of the text field's
 *        caret. This function set's position's value.
 * @param itemPtr pointer to the text field's MidpItem structure.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_textfield_get_caret_position(int *position,
					       MidpItem* itemPtr){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

/**
 * Notifies the native peer of a change in the text field's constraints.
 *
 * @param constraints the new input constraints.
 * @param itemPtr pointer to the text field's MidpItem structure.
 *
 * @return an indication of success or the reason for failure
 */
MidpError lfpport_textfield_set_constraints(MidpItem* itemPtr,
					    int constraints){
    syslog(LOG_INFO, ">>>%s\n", __FUNCTION__);
    syslog(LOG_INFO, "<<<%s\n", __FUNCTION__);
    return -1;
}

#ifdef __cplusplus
} /* extern "C" */
#endif

