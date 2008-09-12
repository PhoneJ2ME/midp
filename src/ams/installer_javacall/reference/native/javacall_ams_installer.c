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

#include <kni.h>
#include <sni.h>
#include <midpEvents.h>
#include <midpEventUtil.h>
#include <midp_thread.h>
#include <midpUtilKni.h>
#include <midpServices.h>

#include <pcsl_string.h>
#include <midpNativeAppManager.h>

#include <javacall_defs.h>
#include <javacall_ams_installer.h>

#include <javautil_unicode.h>

/* IMPL_NOTE: must be removed! MIDP API must be used instead. */
#include <javacall_ams_app_manager.h>

/*
 * IMPL_NOTE: move to *.h.
 *            For now, only one running installer at a time is supported.
 */
extern int g_installerIsolateId;
extern jboolean g_fAnswer, g_fAnswerReady;

/**
 * Application manager invokes this function to start a suite installation.
 *
 * NOTE: if it is desired to invoke Graphical Installer to use Java GUI,
 *       the corresponding MIDlet should be launched instead of calling
 *       this function:
 *
 * <pre>
 *   const javacall_utf16 guiInstallerClass[] = {
 *       'c', 'o', 'm', '.', 's', 'u', 'n', '.', 'm', 'i', 'd', 'p', '.',
 *       'i', 'n', 's', 't', 'a', 'l', 'l', 'e', 'r', '.',
 *       'G', 'r', 'a', 'p', 'h', 'i', 'c', 'a', 'l',
 *       'I', 'n', 's', 't', 'a', 'l', 'l', 'e', 'r',
 *       0
 *   };
 *   const javacall_utf16 argInstallStr[] = { 'I', 0 };
 *   javacall_const_utf16_string pArgs[2];
 *
 *   pArgs[0] = argInstallStr;
 *   pArgs[1] = urlToInstallFrom;
 *
 *   res = java_ams_midlet_start_with_args(-1, appId, guiInstallerClass,
 *                                         pArgs, 2, NULL);
 * </pre>
 *
 * NOTE: storageId and folderId parameters are mutually exclusive because any
 *       of them uniquely identifies where the new suite will reside; i. e.,
 *       if one of these parameters is set to a valid ID, the second must be
 *       JAVACALL_INVALID_[STORAGE|FOLDER]_ID
 *
 * @param appId ID that will be used to uniquely identify this operation
 * @param srcType
 *             type of data pointed by installUrl: a JAD file, a JAR file
 *             or any of them
 * @param installUrl
 *             null-terminated http url string of MIDlet's jad or jar file.
 *             The url is of the following form:
 *             http://www.website.com/a/b/c/d.jad
 *             or
 *             file:////a/b/c/d.jad
 *             or
 *             c:\a\b\c\d.jad
 * @param storageId ID of the storage where to install the suite or
 *                  JAVACALL_INVALID_STORAGE_ID to use the default storage
 * @param folderId ID of the folder into which to install the suite or
 *                  JAVACALL_INVALID_FOLDER_ID to use the default folder
 *
 * @return status code: <tt>JAVACALL_OK</tt> if the installation was
 *                                           successfully started,
 *                      an error code otherwise
 *
 * The return of this function only tells if the install process is started
 * successfully. The actual result of if the installation (status and ID of
 * the newly installed suite) will be reported later by
 * java_ams_operation_completed().
 */
javacall_result
java_ams_install_suite(javacall_app_id appId,
                       javacall_ams_install_source_type srcType,
                       javacall_const_utf16_string installUrl,
                       javacall_storage_id storageId,
                       javacall_folder_id folderId) {
    const javacall_utf16 installerClass[] = {
       'c', 'o', 'm', '.', 's', 'u', 'n', '.', 'm', 'i', 'd', 'p', '.',
       'i', 'n', 's', 't', 'a', 'l', 'l', 'e', 'r', '.',
       'I', 'n', 's', 't', 'a', 'l', 'l', 'e', 'r', 'P', 'e', 'e', 'r',
       'M', 'I', 'D', 'l', 'e', 't',
       0
    };
    const javacall_utf16 argAppIdStr[] = { '1', 0 };
    javacall_const_utf16_string pArgs[2];
    javacall_result res = JAVACALL_FAIL;

    if (installUrl == NULL) {
        return JAVACALL_FAIL;
    }

    (void)srcType;
    (void)storageId;
    (void)folderId;

    pArgs[0] = argAppIdStr;
    pArgs[1] = installUrl;

    res = java_ams_midlet_start_with_args(-1, appId, installerClass,
                                          pArgs, 2, NULL);

    return res;
}

/**
 * Helper for java_ams_install_enable_ocsp() and
 * java_ams_install_is_ocsp_enabled().
 *
 * Sends an event of the given type to all running installers.
 *
 * @param eventType type of the event to send
 * @param param     parameter for the event
 *
 * @return status code: <tt>JAVACALL_OK</tt> if the operation was
 *                                           successfully started,
 *                      <tt>JAVACALL_FAIL</tt> otherwise
 */
static javacall_result send_request_impl(int eventType, int param) {
    MidpEvent evt;

    if (g_installerIsolateId == -1) {
        return JAVACALL_FAIL;
    }

    MIDP_EVENT_INITIALIZE(evt);

    evt.type = eventType;
    evt.intParam1 = -1; /* appId is -1: broadcast */
    evt.intParam2 = param;

    StoreMIDPEventInVmThread(evt, g_installerIsolateId);

    return JAVACALL_OK;
}

/**
 * Application manager invokes this function to enable or disable
 * certificate revocation check using OCSP.
 *
 * An installation must be started before using this method.
 * It affects all running installers.
 *
 * This call is asynchronous, the new OCSP chack state (JAVACALL_TRUE if
 * OCSP is enabled, JAVACALL_FALSE - if disabled) will be reported later
 * via java_ams_operation_completed() with the argument
 * operation == JAVACALL_OPCODE_ENABLE_OCSP.
 *
 * @param enable JAVACALL_TRUE to enable OCSP check,
 *               JAVACALL_FALSE - to disable it
 *
 * @return status code: <tt>JAVACALL_OK</tt> if the operation was
 *                                           successfully started,
 *                      <tt>JAVACALL_FAIL</tt> otherwise
 */
javacall_result
java_ams_install_enable_ocsp(javacall_bool enable) {
    return send_request_impl(NATIVE_ENABLE_OCSP_REQUEST,
                             (enable == JAVACALL_TRUE ? 1 : 0));
}

/**
 * Application manager invokes this function to find out if OCSP
 * certificate revocation check is enabled.
 *
 * An installation must be started before using this method.
 *
 * This call is asynchronous, the current OCSP check state (JAVACALL_TRUE if
 * OCSP is enabled, JAVACALL_FALSE - if disabled) will be reported later
 * via java_ams_operation_completed() with the argument
 * operation == JAVACALL_OPCODE_IS_OCSP_ENABLED.
 *
 * @return status code: <tt>JAVACALL_OK</tt> if the operation was
 *                                           successfully started,
 *                      <tt>JAVACALL_FAIL</tt> otherwise
 */
javacall_result
java_ams_install_is_ocsp_enabled() {
    return send_request_impl(NATIVE_CHECK_OCSP_ENABLED_REQUEST, 0);
}

/**
 * This function is called by the application manager to report the results
 * of handling of the request previously sent by java_ams_install_ask().
 *
 * It must be implemented at that side (SJWC or Platform) where the installer
 * is located.
 *
 * After processing the request, java_ams_install_answer() must
 * be called to report the result to the installer.
 *
 * @param requestCode   in pair with pInstallState->appId uniquely
 *                      identifies the request for which the results
 *                      are reported by this call
 * @param pInstallState pointer to a structure containing all information
 *                      about the current installation state
 * @param pResultData   pointer to request-specific results (may NOT be NULL)
 *
 * @return <tt>JAVACALL_OK</tt> if the answer was understood,
 *         <tt>JAVACALL_FAIL</tt> otherwise
 */
javacall_result
java_ams_install_answer(javacall_ams_install_request_code requestCode,
                        const javacall_ams_install_state* pInstallState,
                        const javacall_ams_install_data* pResultData) {
    JVMSPI_ThreadID thread;
    javacall_result res;

    g_fAnswerReady = JAVACALL_TRUE;

    if (pResultData != NULL) {
        g_fAnswer = pResultData->fAnswer;
        res = JAVACALL_OK;
    } else {
        g_fAnswer = JAVACALL_FALSE;
        res = JAVACALL_FAIL;
    }

    if (pInstallState == NULL) {
        res = JAVACALL_FAIL;
    }

    if (g_installerIsolateId != -1) {
        thread = SNI_GetSpecialThread(g_installerIsolateId);
        if (thread != NULL) {
            midp_thread_unblock(thread);
        } else {
            res = JAVACALL_FAIL;
        }
    }

    return res;
}