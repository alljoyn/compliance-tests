/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

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