/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.config.server.ConfigChangeListener;
import org.alljoyn.config.server.SetPasswordHandler;
import org.alljoyn.config.transport.ConfigTransport;
import org.alljoyn.services.common.LanguageNotSupportedException;
import org.alljoyn.services.common.PropertyStoreException;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.simulator.DUTSimulator;
import org.alljoyn.validation.simulator.DUTSimulatorErrorCodes;
import org.alljoyn.validation.simulator.DUTSimulatorErrorCodes.ErrorMessage;
import org.alljoyn.validation.simulator.DUTSimulatorErrorCodes.ErrorName;

import android.util.Log;

public class DUTSimulatorConfigTransport implements ConfigTransport
{
    private static String TAG = "DUTSimulatorConfigTransport";
    private DUTSimulator dutSimulator;
    private DUTSimulatorRestartHandler restartHandler;
    private DUTSimulatorFactoryResetHandler factoryResetHandler;
    private SetPasswordHandler setPasswordHandler;
    private ConfigChangeListener configChangeListener;

    public DUTSimulatorConfigTransport(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
        configChangeListener = new DUTSimulatorConfigChangeListener(dutSimulator);
        restartHandler = new DUTSimulatorRestartHandler(dutSimulator);
        setPasswordHandler = new DUTSimulatorPasswordHandler(dutSimulator);
        factoryResetHandler = new DUTSimulatorFactoryResetHandler(dutSimulator);
    }

    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

    @Override
    public void UpdateConfigurations(String languageTag, Map<String, Variant> configuration) throws BusException
    {
        Map<String, Object> toObjectMap = TransportUtil.fromVariantMap(configuration);

        for (Entry<String, Object> entry : toObjectMap.entrySet())
        {
            try
            {
                dutSimulator.getDeviceDetails().updateConfig(languageTag, entry);
            }
            catch (PropertyStoreException e)
            {

                if (e.getReason() == PropertyStoreException.UNSUPPORTED_LANGUAGE)
                {
                    throw new LanguageNotSupportedException();
                }
                else if (e.getReason() == PropertyStoreException.INVALID_VALUE)
                {
                    throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.INVALID_VALUE.getErrorName(), DUTSimulatorErrorCodes.ErrorMessage.INVALID_VALUE.getErrorMessage());
                }
                else if (e.getReason() == PropertyStoreException.ILLEGAL_ACCESS)
                {
                    throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.UPDATE_NOT_ALLOWED.getErrorName(),
                            DUTSimulatorErrorCodes.ErrorMessage.UPDATE_NOT_ALLOWED.getErrorMessage());
                }
                else if (e.getReason() == PropertyStoreExtendedException.MAX_SIZE_EXCEEDED)
                {
                    throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.MAX_SIZE_EXCEEDED.getErrorName(),
                            DUTSimulatorErrorCodes.ErrorMessage.MAX_SIZE_EXCEEDED.getErrorMessage());
                }
                else
                {
                    Log.d(TAG, "Exception thrown on updating config", e);
                }
            }
        }

        if (configChangeListener != null)
        {
            configChangeListener.onConfigChanged(configuration, languageTag);
        }
    }

    @Override
    public void SetPasscode(String daemonRealm, byte[] newPasscode) throws BusException
    {
        if (newPasscode.length > 0)
        {
            if (setPasswordHandler != null)
            {
                setPasswordHandler.setPassword(daemonRealm, TransportUtil.toCharArray(newPasscode));
            }
        }
        else
        {
            throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.FEATURE_NOT_AVAILABLE.getErrorName(),
                    DUTSimulatorErrorCodes.ErrorMessage.FEATURE_NOT_AVAILABLE.getErrorMessage());
        }
    }

    @Override
    public void Restart() throws BusException
    {
        if (restartHandler != null)
        {
            Thread backgroundRestart = new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    Log.d(TAG, "Inside Runnable");
                    restartHandler.restart();
                }
            });

            backgroundRestart.start();
        }
        else
        {
            throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.FEATURE_NOT_AVAILABLE.getErrorName(),
                    DUTSimulatorErrorCodes.ErrorMessage.FEATURE_NOT_AVAILABLE.getErrorMessage());
        }
    }

    @Override
    public void ResetConfigurations(String language, String[] fieldsToRemove) throws BusException
    {
        for (String fieldToReset : fieldsToRemove)
        {
            try
            {
                dutSimulator.getDeviceDetails().resetConfiguration(language, fieldToReset);
            }
            catch (PropertyStoreException e)
            {
                if (e.getReason() == PropertyStoreException.UNSUPPORTED_LANGUAGE)
                {
                    throw new LanguageNotSupportedException();
                }
                else if (e.getReason() == PropertyStoreException.ILLEGAL_ACCESS || e.getReason() == PropertyStoreException.INVALID_VALUE)
                {
                    throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.INVALID_VALUE.getErrorName(), DUTSimulatorErrorCodes.ErrorMessage.INVALID_VALUE.getErrorMessage());
                }

                else
                {
                    Log.d(TAG, "Exception thrown on updating config", e);
                }
            }
        }
        if (configChangeListener != null)
        {
            configChangeListener.onResetConfiguration(language, fieldsToRemove);
        }
    }

    @Override
    public Map<String, Variant> GetConfigurations(String languageTag) throws ErrorReplyBusException
    {
        Map<String, Variant> configVariantMap = null;
        Map<String, Object> configMapObject = new HashMap<String, Object>();

        try
        {

            configMapObject = dutSimulator.getDeviceDetails().getConfigMapObject(languageTag);
        }
        catch (PropertyStoreException e)
        {
            if (e.getReason() == PropertyStoreException.UNSUPPORTED_LANGUAGE)
            {
                throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.LANGUAGE_NOT_SUPPORTED.getErrorName(),
                        DUTSimulatorErrorCodes.ErrorMessage.LANGUAGE_NOT_SUPPORTED.getErrorMessage());
            }
            else
            {
                Log.d(TAG, "Property Store threw exception other than unsupported Language : ", e);
            }
        }

        configVariantMap = TransportUtil.toVariantMap(configMapObject);
        return configVariantMap;
    }

    @Override
    public void FactoryReset() throws BusException
    {
        if (factoryResetHandler != null)
        {
            Thread backgroundFactoryReset = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(TAG, "Inside Runnable");
                    factoryResetHandler.doFactoryReset();
                    setPasswordHandler.setPassword("", null);
                    Log.d(TAG,
                            "After factory reset and after resetting the secured password on DUTSimulator is : "
                                    + String.valueOf(dutSimulator.getAuthPasswordHandler().getPassword("")));
                }
            });

            backgroundFactoryReset.start();
        }
        else
        {
            throwErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.FEATURE_NOT_AVAILABLE.getErrorName(),
                    DUTSimulatorErrorCodes.ErrorMessage.FEATURE_NOT_AVAILABLE.getErrorMessage());
        }
    }

    private void throwErrorReplyBusException(String errorName, String errorMessage) throws ErrorReplyBusException
    {
        throw new ErrorReplyBusException(errorName, errorMessage);
    }
}