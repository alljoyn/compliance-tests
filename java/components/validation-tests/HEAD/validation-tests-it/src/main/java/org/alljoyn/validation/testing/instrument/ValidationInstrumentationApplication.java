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
package org.alljoyn.validation.testing.instrument;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class ValidationInstrumentationApplication extends Application
{
    private Map<String, String> instrumentParamMap = new HashMap<String, String>();

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    public String getInstrumentParameter(String parameterName)
    {
        return instrumentParamMap.get(parameterName);
    }

    public void setInstrumentParameter(String parameterName, String parameterValue)
    {
        instrumentParamMap.put(parameterName, parameterValue);
    }

}