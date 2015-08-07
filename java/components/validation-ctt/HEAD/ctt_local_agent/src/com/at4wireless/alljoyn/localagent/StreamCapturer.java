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
package com.at4wireless.alljoyn.localagent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Captures a stream from system output and redirects to a display panel
 */
public class StreamCapturer extends OutputStream
{
    /** Panel where stream is redirected */
    private Consumer consumer;
    
    /** Normal output stream */
    private PrintStream printStream;
    
    private byte[] buffer;
    
    int i = 0;

    /**
     * Instantiates a new stream capturer.
     *
     * @param 	consumer 	panel where stream is redirected
     * @param 	pStream		normal output stream
     */
    public StreamCapturer(Consumer consumer, PrintStream pStream)
    {
        buffer = new byte[1024];
        i = 0;
        this.printStream = pStream;
        this.consumer = consumer;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException
    {    	
    	buffer[i] = (byte) b;
    	i++;
    	
    	if(Character.toString((char)b).equals("\n"))
    	{
    		consumer.appendText(new String(Arrays.copyOfRange(buffer, 0, i)));
    		i = 0;
    		buffer = new byte[1024];
    	}
        printStream.write(b);
    }
}
