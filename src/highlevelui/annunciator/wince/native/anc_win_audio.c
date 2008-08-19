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

#include <anc_audio.h>
#include <midp_logging.h>
#include <windows.h>

/**
 * @file
 *
 * Common file to hold simple audio implementation.
 */

/**
 * Simple sound playing implementation for Alert.
 * On most of the ports, play a beeping sound for types:
 * ANC_SOUND_WARNING, ANC_SOUND_ERROR and ANC_SOUND_ALARM.
 */
jboolean anc_play_sound(AncSoundType soundType)
{
    switch (soundType) {
    case ANC_SOUND_INFO:
        MessageBeep(MB_OK);
        break;
    case ANC_SOUND_WARNING:
        MessageBeep(MB_ICONHAND);
        break;
    case ANC_SOUND_ERROR:
        MessageBeep(MB_ICONQUESTION);
        break;
    case ANC_SOUND_ALARM:
        MessageBeep(MB_ICONEXCLAMATION);
        break;
    case ANC_SOUND_CONFIRMATION:
        MessageBeep(MB_ICONASTERISK);
        break;
    default:
        MessageBeep(-1);
    };
    return KNI_TRUE;
}

#define WAVE_FORMAT_MIDI 0x3000
#define MM_MOM_MIDIMESSAGE  (WM_USER+0x100)
#define MM_MOM_SETMIDITICKS (WM_USER+0x101)

#define MIDI_MESSAGE_UPDATETEMPO 0x10000000
#define MIDI_MESSAGE_FREQGENON   0x20000000
#define MIDI_MESSAGE_FREQGENOFF  0x30000000

typedef struct _WAVEFORMAT_MIDI
{
    WAVEFORMATEX wfx;
    UINT32 USecPerQuarterNote;
    UINT32 TicksPerQuarterNote;
} WAVEFORMAT_MIDI, *LPWAVEFORMAT_MIDI;

#define WAVEFORMAT_MIDI_EXTRASIZE (sizeof(WAVEFORMAT_MIDI)-sizeof(WAVEFORMATEX))

typedef struct _WAVEFORMAT_MIDI_MESSAGE
{
    UINT32 DeltaTicks;
    DWORD  MidiMsg;
} WAVEFORMAT_MIDI_MESSAGE;

typedef struct _TONE_PARAMETERS
{
    int note;
    int duration;
    int volume;
} TONE_PARAMETERS;


jboolean anc_play_tone(int note, int duration, int volume) {    
    // Build a MIDI buffer with 2 MIDI messages.
    WAVEFORMAT_MIDI_MESSAGE midiMsg[2];
    // Build a MIDI waveformat header
    WAVEFORMAT_MIDI wfm;
    HANDLE hEvent;
    MMRESULT mmResult;
    HWAVEOUT hWaveOut;
    WAVEHDR waveHdr;

    memset(&wfm, 0, sizeof(wfm));
    wfm.wfx.wFormatTag = WAVE_FORMAT_MIDI;
    wfm.wfx.nChannels = 1;
    wfm.wfx.nBlockAlign = sizeof(WAVEFORMAT_MIDI_MESSAGE);
    wfm.wfx.cbSize = WAVEFORMAT_MIDI_EXTRASIZE;

    // These fields adjust the interpretation of DeltaTicks, and thus the rate of playback
    wfm.USecPerQuarterNote=1000000;   // Set to 1 second. Note driver will default to 500000 if we set this to 0
    wfm.TicksPerQuarterNote=100;      // Set to 100. Note driver will default to 96 if we set this to 0

    hEvent = CreateEvent(NULL,TRUE,FALSE,NULL);

    // Open the waveout device
    mmResult = waveOutOpen(&hWaveOut, 0, (LPWAVEFORMATEX)&wfm, (DWORD)hEvent, 0, CALLBACK_EVENT);

    if (mmResult != MMSYSERR_NOERROR) {
        return KNI_FALSE;
    }

    midiMsg[0].DeltaTicks=0;      // Wait 1 second : (DeltaTicks * (UsecPerQuarterNote/TicksPerQuarterNote))
    midiMsg[0].MidiMsg=0x7F0090 | (note << 8);   // Note on
    midiMsg[1].DeltaTicks=duration / 10;      // Wait 1 second : (DeltaTicks * (UsecPerQuarterNote/TicksPerQuarterNote))
    midiMsg[1].MidiMsg=0x7F0080 | (note << 8); // Note off

    waveHdr.lpData = (LPSTR)midiMsg;
    waveHdr.dwBufferLength = sizeof(midiMsg);
    waveHdr.dwFlags = 0;
    mmResult = waveOutPrepareHeader(hWaveOut, &waveHdr, sizeof(waveHdr));

    // Play the data
    mmResult = waveOutWrite(hWaveOut, &waveHdr, sizeof(waveHdr));

    // Wait for playback to complete
    WaitForSingleObject(hEvent,INFINITE);

    // Cleanup
    mmResult = waveOutUnprepareHeader(hWaveOut, &waveHdr, sizeof(waveHdr));
    mmResult = waveOutClose(hWaveOut);

    return KNI_TRUE;
}

int WINAPI ToneThread(LPVOID pvParam) {
    TONE_PARAMETERS *tone = (TONE_PARAMETERS *)pvParam;
    anc_play_tone(tone->note, tone->duration, tone->volume);
    free(tone);
    
    return 0;
}

jboolean anc_play_tone_thread(int note, int duration, int volume) {    
    HANDLE hToneThread;

    TONE_PARAMETERS *tone = (TONE_PARAMETERS *)malloc(sizeof(TONE_PARAMETERS));
    if (tone != NULL) {
        tone->note = note;
        tone->duration = duration;
        tone->volume = volume;

	hToneThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)ToneThread, tone, 0, NULL);    
    }

    return KNI_TRUE;
}

#if !ENABLE_CDC
KNIEXPORT KNI_RETURNTYPE_BOOLEAN
KNIDECL(javax_microedition_lcdui_Display_playAlertSound0) {
    int displayId = KNI_GetParameterAsInt(1);
    int alertType = KNI_GetParameterAsInt(2);

    KNI_ReturnBoolean(anc_play_sound(alertType));
}
#endif

KNIEXPORT KNI_RETURNTYPE_BOOLEAN
KNIDECL(javax_microedition_media_Manager_playTone0) {
    KNI_ReturnBoolean(anc_play_tone_thread(KNI_GetParameterAsInt(1), KNI_GetParameterAsInt(2), KNI_GetParameterAsInt(3)));
}

KNIEXPORT KNI_RETURNTYPE_BOOLEAN
KNIDECL(javax_microedition_lcdui_Display_playTone0) {
    KNI_ReturnBoolean(anc_play_tone_thread(KNI_GetParameterAsInt(1), KNI_GetParameterAsInt(2), KNI_GetParameterAsInt(3)));
}
