/*
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

#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#include <kni.h>

#include <midpMalloc.h>
#include <midp_properties_port.h>
#include <midpStorage.h>
#include <gcf_export.h>
#include <midp_logging.h>
#include <javacall_properties.h>

/* generated by Configurator */
#include <midp_property_callouts.h>

extern char* midp_internalKey[];
extern char* midp_internalValue[];
extern char* midp_systemKey[];
extern char* midp_systemValue[];

/**
 * Calls out to the system to obtain a property.
 *
 * @param key The key to search for
 *
 * @return The value returned by the system if the key has a
 *         callout method defined, otherwise <tt>NULL<tt>
 */
static char* doCallout(const char* key) {
    int i;
    for (i = 0; calloutKey[i] != NULL; ++i) {
        if (strcmp(key, calloutKey[i]) == 0) {
            return (calloutFunction[i])();
        }
    }

    return NULL;
}

/**
 * Initializes the configuration sub-system.
 *
 * @return <tt>0</tt> for success, otherwise a non-zero value
 */
int
initializeConfig(void) {
    int i;
    if (JAVACALL_OK != javacall_initialize_configurations()) {
        return -1;
    }

    /* Add the full set of properties to JavaCall property database */
    for (i = 0; NULL != midp_internalKey[i]; i++) {
        javacall_set_property(midp_internalKey[i], midp_internalValue[i], 1,
            JAVACALL_INTERNAL_PROPERTY);
    }
    for (i = 0; NULL != midp_systemKey[i]; i++) {
        javacall_set_property(midp_systemKey[i], midp_systemValue[i], 1,
            JAVACALL_APPLICATION_PROPERTY);
    }

    return 0;
}

/**
 * Finalize the configuration subsystem by releasing all the
 * allocating memory buffers. This method should only be called by
 * midpFinalize or internally by initializeConfig.
 */
void
finalizeConfig(void) {
    javacall_finalize_configurations();
}

/**
 * Sets a property key to the specified value in the internal
 * property set.
 *
 * @param key The key to set
 * @param value The value to set <tt>key</tt> to
 */
void
setInternalProperty(const char* key , const char* value) {
    javacall_set_property(key, value, KNI_TRUE, JAVACALL_INTERNAL_PROPERTY);
}

/**
 * Gets the value of the specified property key in the internal
 * property set. If the key is not found in the internal property
 * set, the application  property set is searched.
 *
 * @param key The key to search for
 *
 * @return The value associated with <tt>key</tt> if found, otherwise
 *         <tt>NULL</tt>
 */
const char*
getInternalProperty(const char* key) {
    char *str;

    if (JAVACALL_OK == javacall_get_property(key, JAVACALL_INTERNAL_PROPERTY, &str)) {
        return str;
    } else if (JAVACALL_OK == javacall_get_property(key, JAVACALL_APPLICATION_PROPERTY, &str)) {
        return str;
    }

    return NULL;
}

/**
 * Gets the integer value of the specified property key in the internal
 * property set.  
 *
 * @param key The key to search for
 *
 * @return The value associated with <tt>key</tt> if found, otherwise
 *         <tt>0</tt>
 */
int getInternalPropertyInt(const char* key) {
    const char *tmp;    

    tmp = getInternalProperty(key); 

    return(NULL == tmp) ? 0 : atoi(tmp);
}



/**
 * Sets a property key to the specified value in the application
 * property set.
 *
 * @param key The key to set
 * @param value The value to set <tt>key</tt> to
 */
void
setSystemProperty(const char* key , const char* value) {
    javacall_set_property(key, value, KNI_TRUE, JAVACALL_APPLICATION_PROPERTY); 
}

/**
 * Gets the value of the specified property key in the application
 * property set.
 *
 * @param key The key to search for
 *
 * @return The value associated with <tt>key</tt> if found, otherwise
 *         <tt>NULL</tt>
 */
const char*
getSystemProperty(const char* key) {
    char *str;

    if (JAVACALL_OK == javacall_get_property(key, JAVACALL_APPLICATION_PROPERTY, &str)) {
        return str;
    }

    /* Attempt to obtain the property from callout function */
    return doCallout(key);
}

