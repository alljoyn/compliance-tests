/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.config.server.ConfigChangeListener;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.simulator.DUTSimulator;

public class DUTSimulatorConfigChangeListener implements ConfigChangeListener
{
    private DUTSimulator dutSimulator;

    public DUTSimulatorConfigChangeListener(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
    }

    @Override
    public void onConfigChanged(Map<String, Variant> newConfiguration, String languageTag)
    {
        try
        {
            updateVariantMapAndAnnounce(newConfiguration);
        }
        catch (BusException e)
        {
            throw new RuntimeException(e);
        }

    }

    private void updateVariantMapAndAnnounce(Map<String, Variant> newConfiguration) throws BusException
    {
        dutSimulator.getVariantAboutMap().putAll(newConfiguration);
        dutSimulator.getAnnouncementEmmitter().Announce((short) dutSimulator.getAboutInterface().getVersion(), dutSimulator.CONTACT_PORT,
                dutSimulator.getAboutInterface().GetObjectDescription(), dutSimulator.getVariantAboutMap());
    }

    @Override
    public void onResetConfiguration(String language, String[] fieldNames)
    {
        Map<String, Object> objectMap = new HashMap<String, Object>();
        for (String fieldName : fieldNames)
        {
            if (fieldName.equals(AboutKeys.ABOUT_DEVICE_NAME))
            {
                objectMap.put(fieldName, dutSimulator.getDeviceDetails().getDeviceName());
            }
        }
        Map<String, Variant> newConfiguration = TransportUtil.toVariantMap(objectMap);
        try
        {
            updateVariantMapAndAnnounce(newConfiguration);
        }
        catch (BusException e)
        {
            throw new RuntimeException(e);
        }
    }

}
