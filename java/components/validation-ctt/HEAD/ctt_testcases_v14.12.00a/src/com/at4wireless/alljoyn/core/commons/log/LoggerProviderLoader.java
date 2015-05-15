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
 * The Class LoggerProviderLoader.
 */
public class LoggerProviderLoader
{
    
    /**
     * Load logger provider.
     *
     * @return the windows logger provider
     */
    public WindowsLoggerProvider loadLoggerProvider()
    {
        try
        {
            return loadWindowsLoggerProvider();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load LoggerProvider", e);
        }
    }

  


/**
 * Load windows logger provider.
 *
 * @return the windows logger provider
 * @throws InstantiationException the instantiation exception
 * @throws IllegalAccessException the illegal access exception
 * @throws ClassNotFoundException the class not found exception
 */
public WindowsLoggerProvider loadWindowsLoggerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return  new WindowsLoggerProvider();
        								
    }

}