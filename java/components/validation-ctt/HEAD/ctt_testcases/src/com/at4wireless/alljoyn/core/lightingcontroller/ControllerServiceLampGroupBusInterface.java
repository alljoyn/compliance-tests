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
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
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
 * The Interface ControllerServiceLampGroupBusInterface.
 */
@BusInterface(name = "org.allseen.LSF.ControllerService.LampGroup")
public interface ControllerServiceLampGroupBusInterface
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

    /**
     * The Class Values.
     */
    public class Values
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
    }

    /*
    "    <method name='GetAllLampGroupIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupIDs' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetAllLampGroupIDsValues.
     */
    public class GetAllLampGroupIDsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group i ds. */
        @Position(1)
        public String[] lampGroupIDs;
    }

    /**
     * Gets the all lamp group i ds.
     *
     * @return the gets the all lamp group i ds values
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "uas")
    public GetAllLampGroupIDsValues GetAllLampGroupIDs() throws BusException;

    /*
    "    <method name='GetLampGroupName'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='lampGroupName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampGroupNameValues.
     */
    public class GetLampGroupNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
        
        /** The language. */
        @Position(2)
        public String language;
        
        /** The lamp group name. */
        @Position(3)
        public String lampGroupName;
    }

    /**
     * Gets the lamp group name.
     *
     * @param lampGroupID the lamp group id
     * @param language the language
     * @return the gets the lamp group name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampGroupNameValues GetLampGroupName(String lampGroupID, String language) throws BusException;

    /*
    "    <method name='SetLampGroupName'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampGroupName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class SetLampGroupNameValues.
     */
    public class SetLampGroupNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
        
        /** The language. */
        @Position(2)
        public String language;
    }

    /**
     * Sets the lamp group name.
     *
     * @param lampGroupID the lamp group id
     * @param lampGroupName the lamp group name
     * @param language the language
     * @return the sets the lamp group name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetLampGroupNameValues SetLampGroupName(String lampGroupID, String lampGroupName, String language) throws BusException;

    /*
    "    <method name='CreateLampGroup'>"
    "      <arg name='lampIDs' type='as' direction='in'/>"
    "      <arg name='lampGroupIDs' type='as' direction='in'/>"
    "      <arg name='lampGroupName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class CreateLampGroupValues.
     */
    public class CreateLampGroupValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
    }

    /**
     * Creates the lamp group.
     *
     * @param lampIDs the lamp i ds
     * @param lampGroupIDs the lamp group i ds
     * @param lampGroupName the lamp group name
     * @param language the language
     * @return the creates the lamp group values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "asasss", replySignature = "us")
    public CreateLampGroupValues CreateLampGroup(String[] lampIDs, String[] lampGroupIDs, String lampGroupName, String language) throws BusException;

    /*
    "    <method name='UpdateLampGroup'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampIDs' type='as' direction='in'/>"
    "      <arg name='lampGroupIDs' type='as' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class UpdateLampGroupValues.
     */
    public class UpdateLampGroupValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
    }

    /**
     * Update lamp group.
     *
     * @param lampGroupID the lamp group id
     * @param lampIDs the lamp i ds
     * @param lampGroupIDs the lamp group i ds
     * @return the update lamp group values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sasas", replySignature = "us")
    public UpdateLampGroupValues UpdateLampGroup(String lampGroupID, String[] lampIDs, String[] lampGroupIDs) throws BusException;

    /*
    "    <method name='DeleteLampGroup'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Delete lamp group.
     *
     * @param lampGroupID the lamp group id
     * @return the values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public Values DeleteLampGroup(String lampGroupID) throws BusException;

    /*
    "    <method name='GetLampGroup'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "      <arg name='lampGroupIDs' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetLampGroupValues.
     */
    public class GetLampGroupValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
        
        /** The lamp i ds. */
        @Position(2)
        public String[] lampIDs;
        
        /** The lamp group i ds. */
        @Position(3)
        public String[] lampGroupIDs;
    }

    /**
     * Gets the lamp group.
     *
     * @param lampGroupID the lamp group id
     * @return the gets the lamp group values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usasas")
    public GetLampGroupValues GetLampGroup(String lampGroupID) throws BusException;

    /*
    "    <method name='TransitionLampGroupState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "	   <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class TransitionLampGroupStateValues.
     */
    public class TransitionLampGroupStateValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
    }

    /**
     * Transition lamp group state.
     *
     * @param lampGroupID the lamp group id
     * @param lampState the lamp state
     * @param transitionPeriod the transition period
     * @return the transition lamp group state values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sa{sv}u", replySignature = "us")
    public TransitionLampGroupStateValues TransitionLampGroupState(String lampGroupID, Map<String, Variant> lampState, int transitionPeriod) throws BusException;

    /*
    "    <method name='PulseLampGroupWithState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='fromLampGroupState' type='a{sv}' direction='in'/>"
    "      <arg name='toLampGroupState' type='a{sv}' direction='in'/>"
    "      <arg name='period' type='u' direction='in'/>"
    "      <arg name='duration' type='u' direction='in'/>"
    "      <arg name='numPulses' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Pulse lamp group with state.
     *
     * @param lampGroupID the lamp group id
     * @param fromLampGroupState the from lamp group state
     * @param toLampGroupState the to lamp group state
     * @param period the period
     * @param duration the duration
     * @param numPulses the num pulses
     * @return the values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sa{sv}a{sv}uuu", replySignature = "us")
    public Values PulseLampGroupWithState(String lampGroupID, Map<String, Variant> fromLampGroupState, Map<String, Variant> toLampGroupState, int period, int duration, int numPulses) throws BusException;

    /*
    "    <method name='PulseLampGroupWithPreset'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='fromPresetID' type='s' direction='in'/>"
    "      <arg name='toPresetID' type='s' direction='in'/>"
    "      <arg name='period' type='u' direction='in'/>"
    "      <arg name='duration' type='u' direction='in'/>"
    "      <arg name='numPulses' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Pulse lamp group with preset.
     *
     * @param lampGroupID the lamp group id
     * @param fromPresetID the from preset id
     * @param toPresetID the to preset id
     * @param period the period
     * @param duration the duration
     * @param numPulses the num pulses
     * @return the values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sssuuu", replySignature = "us")
    public Values PulseLampGroupWithPreset(String lampGroupID, String fromPresetID, String toPresetID, int period, int duration, int numPulses) throws BusException;

    /*
    "    <method name='TransitionLampGroupStateToPreset'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Transition lamp group state to preset.
     *
     * @param lampGroupID the lamp group id
     * @param presetID the preset id
     * @param transitionPeriod the transition period
     * @return the values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ssu", replySignature = "us")
    public Values TransitionLampGroupStateToPreset(String lampGroupID, String presetID, int transitionPeriod) throws BusException;

    /*
    "    <method name='TransitionLampGroupStateField'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='in'/>"
    "      <arg name='lampGroupStateFieldValue' type='v' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class TransitionLampGroupStateFieldValues.
     */
    public class TransitionLampGroupStateFieldValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
        
        /** The lamp group state field name. */
        @Position(2)
        public String lampGroupStateFieldName;
    }

    /**
     * Transition lamp group state field.
     *
     * @param lampGroupID the lamp group id
     * @param lampGroupStateFieldName the lamp group state field name
     * @param lampGroupStateFieldValue the lamp group state field value
     * @param transitionPeriod the transition period
     * @return the transition lamp group state field values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampGroupStateFieldValues TransitionLampGroupStateField(String lampGroupID, String lampGroupStateFieldName, Variant lampGroupStateFieldValue, int transitionPeriod) throws BusException;

    /*
    "    <method name='ResetLampGroupState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Reset lamp group state.
     *
     * @param lampGroupID the lamp group id
     * @return the values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public Values ResetLampGroupState(String lampGroupID) throws BusException;

    /*
    "    <method name='ResetLampGroupFieldState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class ResetLampGroupStateFieldValues.
     */
    public class ResetLampGroupStateFieldValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp group id. */
        @Position(1)
        public String lampGroupID;
        
        /** The lamp group state field name. */
        @Position(2)
        public String lampGroupStateFieldName;
    }

    /**
     * Reset lamp group state field.
     *
     * @param lampGroupID the lamp group id
     * @param lampGroupStateFieldName the lamp group state field name
     * @return the reset lamp group state field values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "uss")
    public ResetLampGroupStateFieldValues ResetLampGroupStateField(String lampGroupID, String lampGroupStateFieldName) throws BusException;

    /*
    "    <signal name='LampGroupsNameChanged'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamp groups name changed.
     *
     * @param lampGroupsIDs the lamp groups i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void LampGroupsNameChanged(String[] lampGroupsIDs) throws BusException;

    /*
    "    <signal name='LampGroupsCreated'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamp groups created.
     *
     * @param lampGroupsIDs the lamp groups i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void LampGroupsCreated(String[] lampGroupsIDs) throws BusException;

    /*
    "    <signal name='LampGroupsUpdated'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamp groups updated.
     *
     * @param lampGroupsIDs the lamp groups i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void LampGroupsUpdated(String[] lampGroupsIDs) throws BusException;

    /*
    "    <signal name='LampGroupsDeleted'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Lamp groups deleted.
     *
     * @param lampGroupsIDs the lamp groups i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void LampGroupsDeleted(String[] lampGroupsIDs) throws BusException;
}