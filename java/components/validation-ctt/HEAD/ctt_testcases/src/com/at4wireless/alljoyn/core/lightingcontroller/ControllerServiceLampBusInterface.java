/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * The Interface ControllerServiceLampBusInterface.
 */
@BusInterface(name = "org.allseen.LSF.ControllerService.Lamp")
public interface ControllerServiceLampBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */

    /*
    "   <property name="Version" type="u" access="read" />"
    */
    /**
     * Gets the version.
     *
     * @return the version
     * @throws BusException the bus exception
     */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    /*
    "    <method name='GetAllLampIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetAllLampIDsValues.
     */
    public class GetAllLampIDsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;

        /** The lamp i ds. */
        @Position(1)
        public String[] lampIDs;
    }

    /**
     * Gets the all lamp i ds.
     *
     * @return the gets the all lamp i ds values
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "uas")
    public GetAllLampIDsValues GetAllLampIDs() throws BusException;

    /*
    "    <method name='GetLampSupportedLanguages'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='supportedLanguages' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampSupportedLanguagesValues.
     */
    public class GetLampSupportedLanguagesValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The supported languages. */
        @Position(2)
        public String[] supportedLanguages;
    }

    /**
     * Gets the lamp supported languages.
     *
     * @param lampID the lamp id
     * @return the gets the lamp supported languages values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usas")
    public GetLampSupportedLanguagesValues GetLampSupportedLanguages(String lampID) throws BusException;

    /*
    "    <method name='GetLampManufacturer'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='manufacturer' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampManufacturerValues.
     */
    public class GetLampManufacturerValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The language. */
        @Position(2)
        public String language;
        
        /** The manufacturer. */
        @Position(3)
        public String manufacturer;
    }

    /**
     * Gets the lamp manufacturer.
     *
     * @param lampID the lamp id
     * @param language the language
     * @return the gets the lamp manufacturer values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampManufacturerValues GetLampManufacturer(String lampID, String language) throws BusException;

    /*
    "    <method name='GetLampName'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='lampName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampNameValues.
     */
    public class GetLampNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The language. */
        @Position(2)
        public String language;
        
        /** The lamp name. */
        @Position(3)
        public String lampName;
    }

    /**
     * Gets the lamp name.
     *
     * @param lampID the lamp id
     * @param language the language
     * @return the gets the lamp name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampNameValues GetLampName(String lampID, String language) throws BusException;

    /*
    "    <method name='SetLampName'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class SetLampNameValues.
     */
    public class SetLampNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The language. */
        @Position(2)
        public String language;
    }

    /**
     * Sets the lamp name.
     *
     * @param lampID the lamp id
     * @param lampName the lamp name
     * @param language the language
     * @return the sets the lamp name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetLampNameValues SetLampName(String lampID, String lampName, String language) throws BusException;

    /*
    "    <method name='GetLampDetails'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampDetails' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampDetailsValues.
     */
    public class GetLampDetailsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp details. */
        @Position(2)
        public Map<String, Variant> lampDetails;
    }

    /**
     * Gets the lamp details.
     *
     * @param lampID the lamp id
     * @return the gets the lamp details values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampDetailsValues GetLampDetails(String lampID) throws BusException;

    /*
    "    <method name='GetLampParameters'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampParameters' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampParametersValues.
     */
    public class GetLampParametersValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp parameters. */
        @Position(2)
        public Map<String, Variant> lampParameters;
    }

    /**
     * Gets the lamp parameters.
     *
     * @param lampID the lamp id
     * @return the gets the lamp parameters values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampParametersValues GetLampParameters(String lampID) throws BusException;

    /*
    "    <method name='GetLampParametersField'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampParameterFieldName' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampParameterFieldName' type='s' direction='out'/>"
    "      <arg name='lampParameterFieldValue' type='v' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampParametersFieldValues.
     */
    public class GetLampParametersFieldValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp parameter field name. */
        @Position(2)
        public String lampParameterFieldName;
        
        /** The lamp parameter field value. */
        @Position(3)
        public Variant lampParameterFieldValue;
    }

    /**
     * Gets the lamp parameters field.
     *
     * @param lampID the lamp id
     * @param lampParameterFieldName the lamp parameter field name
     * @return the gets the lamp parameters field values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "ussv")
    public GetLampParametersFieldValues GetLampParametersField(String lampID, String lampParameterFieldName) throws BusException;

    /*
    "    <method name='GetLampState'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampStateValues.
     */
    public class GetLampStateValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp state. */
        @Position(2)
        public Map<String, Variant> lampState;
    }

    /**
     * Gets the lamp state.
     *
     * @param lampID the lamp id
     * @return the gets the lamp state values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampStateValues GetLampState(String lampID) throws BusException;

    /*
    "    <method name='GetLampStateField'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampStateFieldName' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampStateFieldName' type='s' direction='out'/>"
    "      <arg name='lampStateFieldValue' type='v' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampStateFieldValues.
     */
    public class GetLampStateFieldValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp state field name. */
        @Position(2)
        public String lampStateFieldName;
        
        /** The lamp state field value. */
        @Position(3)
        public Variant lampStateFieldValue;
    }

    /**
     * Gets the lamp state field.
     *
     * @param lampID the lamp id
     * @param lampStateFieldName the lamp state field name
     * @return the gets the lamp state field values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "ussv")
    public GetLampStateFieldValues GetLampStateField(String lampID, String lampStateFieldName) throws BusException;

    /*
    "    <method name='TransitionLampState'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class TransitionLampStateValues.
     */
    public class TransitionLampStateValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
    }

    /**
     * Transition lamp state.
     *
     * @param lampID the lamp id
     * @param lampState the lamp state
     * @param transitionPeriod the transition period
     * @return the transition lamp state values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sa{sv}u", replySignature = "us")
    public TransitionLampStateValues TransitionLampState(String lampID, Map<String, Variant> lampState, int transitionPeriod) throws BusException;

    /*
    "    <method name='PulseLampWithState'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='fromLampState' type='a{sv}' direction='in'/>"
    "      <arg name='toLampState' type='a{sv}' direction='in'/>"
    "      <arg name='period' type='u' direction='in'/>"
    "      <arg name='duration' type='u' direction='in'/>"
    "      <arg name='numPulses' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class PulseLampWithStateValues.
     */
    public class PulseLampWithStateValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
    }

    /**
     * Pulse lamp with state.
     *
     * @param lampID the lamp id
     * @param fromLampState the from lamp state
     * @param toLampState the to lamp state
     * @param period the period
     * @param duration the duration
     * @param numPulses the num pulses
     * @return the pulse lamp with state values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sa{sv}a{sv}uuu", replySignature = "us")
    public PulseLampWithStateValues PulseLampWithState(String lampID, Map<String, Variant> fromLampState, Map<String, Variant> toLampState, int period, int duration, int numPulses) throws BusException;

    /*
    "    <method name='PulseLampWithPreset'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='fromPresetID' type='s' direction='in'/>"
    "      <arg name='toPresetID' type='s' direction='in'/>"
    "      <arg name='period' type='u' direction='in'/>"
    "      <arg name='duration' type='u' direction='in'/>"
    "      <arg name='numPulses' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class PulseLampWithPresetValues.
     */
    public class PulseLampWithPresetValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
    }

    /**
     * Pulse lamp with preset.
     *
     * @param lampID the lamp id
     * @param fromPresetID the from preset id
     * @param toPresetID the to preset id
     * @param period the period
     * @param duration the duration
     * @param numPulses the num pulses
     * @return the pulse lamp with preset values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sssuuu", replySignature = "us")
    public PulseLampWithPresetValues PulseLampWithPreset(String lampID, String fromPresetID, String toPresetID, int period, int duration, int numPulses) throws BusException;

    /*
    "    <method name='TransitionLampStateToPreset'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class TransitionLampStateToPresetValues.
     */
    public class TransitionLampStateToPresetValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
    }

    /**
     * Transition lamp state to preset.
     *
     * @param lampID the lamp id
     * @param presetID the preset id
     * @param transitionPeriod the transition period
     * @return the transition lamp state to preset values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ssu", replySignature = "us")
    public TransitionLampStateToPresetValues TransitionLampStateToPreset(String lampID, String presetID, int transitionPeriod) throws BusException;

    /*
    "    <method name='TransitionLampStateField'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampStateFieldName' type='s' direction='in'/>"
    "      <arg name='lampStateFieldValue' type='v' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampStateFieldName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class TransitionLampStateFieldValues.
     */
    public class TransitionLampStateFieldValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp state field name. */
        @Position(2)
        public String lampStateFieldName;
    }

    /**
     * Transition lamp state field.
     *
     * @param lampID the lamp id
     * @param lampStateFieldName the lamp state field name
     * @param lampStateFieldValue the lamp state field value
     * @param transitionPeriod the transition period
     * @return the transition lamp state field values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampStateFieldValues TransitionLampStateField(String lampID, String lampStateFieldName, Variant lampStateFieldValue, int transitionPeriod) throws BusException;

    /*
    "    <method name='ResetLampState'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class ResetLampStateValues.
     */
    public class ResetLampStateValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
    }

    /**
     * Reset lamp state.
     *
     * @param lampID the lamp id
     * @return the reset lamp state values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public ResetLampStateValues ResetLampState(String lampID) throws BusException;

    /*
    "    <method name='ResetLampStateField'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampStateFieldName' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampStateFieldName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class ResetLampStateFieldValues.
     */
    public class ResetLampStateFieldValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp state field name. */
        @Position(2)
        public String lampStateFieldName;
    }

    /**
     * Reset lamp state field.
     *
     * @param lampID the lamp id
     * @param lampStateFieldName the lamp state field name
     * @return the reset lamp state field values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "uss")
    public ResetLampStateFieldValues ResetLampStateField(String lampID, String lampStateFieldName) throws BusException;

    /*
    "    <method name='GetLampFaults'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampFaults' type='au' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampFaultsValues.
     */
    public class GetLampFaultsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp faults. */
        @Position(2)
        public int[] lampFaults;
    }

    /**
     * Gets the lamp faults.
     *
     * @param lampID the lamp id
     * @return the gets the lamp faults values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usau")
    public GetLampFaultsValues GetLampFaults(String lampID) throws BusException;

    /*
    "    <method name='ClearLampFault'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='lampFault' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampFault' type='u' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class ClearLampFaultValues.
     */
    public class ClearLampFaultValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp fault. */
        @Position(2)
        public int lampFault;
    }

    /**
     * Clear lamp fault.
     *
     * @param lampID the lamp id
     * @param lampFault the lamp fault
     * @return the clear lamp fault values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "su", replySignature = "usu")
    public ClearLampFaultValues ClearLampFault(String lampID, int lampFault) throws BusException;

    /*
    "    <method name='GetLampServiceVersion'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampServiceVersion' type='u' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampServiceVersionValues.
     */
    public class GetLampServiceVersionValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp id. */
        @Position(1)
        public String lampID;
        
        /** The lamp service version. */
        @Position(2)
        public int lampServiceVersion;
    }

    /**
     * Gets the lamp service version.
     *
     * @param lampID the lamp id
     * @return the gets the lamp service version values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usu")
    public GetLampServiceVersionValues GetLampServiceVersion(String lampID) throws BusException;

    /*
    "    <signal name='LampNameChanged'>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampName' type='s' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamp name changed.
     *
     * @param lampID the lamp id
     * @param lampName the lamp name
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "ss")
    public void LampNameChanged(String lampID, String lampName) throws BusException;

    /*
    "    <signal name='LampStateChanged'>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamp state changed.
     *
     * @param lampID the lamp id
     * @param lampState the lamp state
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "sa{sv}")
    public void LampStateChanged(String lampID, Map<String, Variant> lampState) throws BusException;

    /*
    "    <signal name='LampsFound'>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamps found.
     *
     * @param lampIDs the lamp i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void LampsFound(String[] lampIDs) throws BusException;

    /*
    "    <signal name='LampsLost'>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamps lost.
     *
     * @param lampIDs the lamp i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void LampsLost(String[] lampIDs) throws BusException;
}