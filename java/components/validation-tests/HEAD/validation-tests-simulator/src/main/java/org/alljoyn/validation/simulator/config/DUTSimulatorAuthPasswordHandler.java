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
package org.alljoyn.validation.simulator.config;

import java.util.Arrays;

import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.utils.GenericLogger;
import org.alljoyn.validation.simulator.DUTSimulator;

public class DUTSimulatorAuthPasswordHandler implements AuthPasswordHandler
{
    private static String TAG = "DUTSimulatorAuthPasswordHandler";

    private GenericLogger logger = new AndroidLogger();
    private DUTSimulator dutSimulator;

    public DUTSimulatorAuthPasswordHandler(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
    }

    @Override
    public void completed(String mechanism, String authPeer, boolean authenticated)
    {

        if (!authenticated)
            logger.info(TAG, " ** " + authPeer + " failed to authenticate");
        else
            logger.info(TAG, " ** " + authPeer + " successfully authenticated");

    }

    @Override
    public char[] getPassword(String peerName)
    {
        char[] securedSessionPassword = dutSimulator.getSecuredSessionPassword();
        logger.info(TAG, String.format("Providing passcode %s for %s", Arrays.toString(securedSessionPassword), peerName));
        return securedSessionPassword != null ? securedSessionPassword : SrpAnonymousKeyListener.DEFAULT_PINCODE;
    }

}
