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
package com.at4wireless.alljoyn.localagent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


// TODO: Auto-generated Javadoc
/**
 * The Class StreamCapturer.
 */
public class StreamCapturer extends OutputStream {

    /** The buffer. */
    private StringBuilder buffer;
    
    /** The prefix. */
    private String prefix;
    
    /** The consumer. */
    private Consumer consumer;
    
    /** The old. */
    private PrintStream old;

    /**
     * Instantiates a new stream capturer.
     *
     * @param prefix the prefix
     * @param consumer the consumer
     * @param old the old
     */
    public StreamCapturer(String prefix, Consumer consumer, PrintStream old) {
        this.prefix = prefix;
        buffer = new StringBuilder(128);
        //buffer.append("[").append(prefix).append("] ");
        this.old = old;
        this.consumer = consumer;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            consumer.appendText(buffer.toString());
            buffer.delete(0, buffer.length());
          //  buffer.append("[").append(prefix).append("] ");
        }
        old.print(c);
    }        



}
