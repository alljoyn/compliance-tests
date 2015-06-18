/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.audio;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioSinkPlayStateChangedSignal.
 */
public class AudioSinkPlayStateChangedSignal
{
    
    /** The old state. */
    private byte oldState;
    
    /** The new state. */
    private byte newState;

    /**
     * Instantiates a new audio sink play state changed signal.
     *
     * @param oldState the old state
     * @param newState the new state
     */
    public AudioSinkPlayStateChangedSignal(byte oldState, byte newState)
    {
        this.oldState = oldState;
        this.newState = newState;
    }

    /**
     * Gets the old state.
     *
     * @return the old state
     */
    public byte getOldState()
    {
        return oldState;
    }

    /**
     * Gets the new state.
     *
     * @return the new state
     */
    public byte getNewState()
    {
        return newState;
    }
}