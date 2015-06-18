/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.commons.log;

// TODO: Auto-generated Javadoc
/**
 * The Interface Logger.
 */
public interface Logger
{
    
    /**
     * Error.
     *
     * @param format the format
     * @param args the args
     */
    void error(String format, Object... args);

    /**
     * Warn.
     *
     * @param format the format
     * @param args the args
     */
    void warn(String format, Object... args);

    /**
     * Info.
     *
     * @param format the format
     * @param args the args
     */
    void info(String format, Object... args);

    /**
     * Debug.
     *
     * @param format the format
     * @param args the args
     */
    void debug(String format, Object... args);

    /**
     * Error.
     *
     * @param message the message
     * @param t the t
     */
    void error(String message, Throwable t);

    /**
     * Warn.
     *
     * @param message the message
     * @param t the t
     */
    void warn(String message, Throwable t);

    /**
     * Info.
     *
     * @param message the message
     * @param t the t
     */
    void info(String message, Throwable t);

    /**
     * Debug.
     *
     * @param message the message
     * @param t the t
     */
    void debug(String message, Throwable t);
    

    /**
     * Pass.
     *
     * @param format the format
     */
    void pass(String format);
    		
    /**
     * Fail.
     *
     * @param format the format
     */
    void fail(String format);
    
    /**
     * Adds the note.
     *
     * @param format the format
     */
    void addNote(String format);
}
