/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

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
 * The Interface ControllerServicePresetBusInterface.
 */
@BusInterface(name = "org.allseen.LSF.ControllerService.Preset")
public interface ControllerServicePresetBusInterface
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
     * The Class PresetValues.
     */
    public class PresetValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The preset id. */
        @Position(1)
        public String presetID;
    }

    /*
    "    <method name='GetDefaultLampState'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetDefaultLampStateValues.
     */
    public class GetDefaultLampStateValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The lamp state. */
        @Position(1)
        public Map<String, Variant> lampState;
    }

    /**
     * Gets the default lamp state.
     *
     * @return the gets the default lamp state values
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "ua{sv}")
    public GetDefaultLampStateValues GetDefaultLampState() throws BusException;

    /*
    "    <method name='SetDefaultLampState'>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "    </method>"
    */
    /**
     * Sets the default lamp state.
     *
     * @param lampState the lamp state
     * @return the int
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "a{sv}", replySignature = "u")
    public int SetDefaultLampState(Map<String, Variant> lampState) throws BusException;

    /*
    "    <method name='GetAllPresetIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetIDs' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetAllPresetIDsValues.
     */
    public class GetAllPresetIDsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The preset i ds. */
        @Position(1)
        public String[] presetIDs;
    }

    /**
     * Gets the all preset i ds.
     *
     * @return the gets the all preset i ds values
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "uas")
    public GetAllPresetIDsValues GetAllPresetIDs() throws BusException;

    /*
    "    <method name='GetPresetName'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='presetName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetPresetNameValues.
     */
    public class GetPresetNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The preset id. */
        @Position(1)
        public String presetID;
        
        /** The language. */
        @Position(2)
        public String language;
        
        /** The preset name. */
        @Position(3)
        public String presetName;
    }

    /**
     * Gets the preset name.
     *
     * @param presetID the preset id
     * @param language the language
     * @return the gets the preset name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetPresetNameValues GetPresetName(String presetID, String language) throws BusException;

    /*
    "    <method name='SetPresetName'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='presetName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class SetPresetNameValues.
     */
    public class SetPresetNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The preset id. */
        @Position(1)
        public String presetID;
        
        /** The language. */
        @Position(2)
        public String language;
    }

    /**
     * Sets the preset name.
     *
     * @param presetID the preset id
     * @param presetName the preset name
     * @param language the language
     * @return the sets the preset name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetPresetNameValues SetPresetName(String presetID, String presetName, String language) throws BusException;

    /*
    "    <method name='CreatePreset'>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='presetName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Creates the preset.
     *
     * @param lampState the lamp state
     * @param presetName the preset name
     * @param language the language
     * @return the preset values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "a{sv}ss", replySignature = "us")
    public PresetValues CreatePreset(Map<String, Variant> lampState, String presetName, String language) throws BusException;

    /*
    "    <method name='UpdatePreset'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Update preset.
     *
     * @param presetID the preset id
     * @param lampState the lamp state
     * @return the preset values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sa{sv}", replySignature = "us")
    public PresetValues UpdatePreset(String presetID, Map<String, Variant> lampState) throws BusException;

    /*
    "    <method name='DeletePreset'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Delete preset.
     *
     * @param presetID the preset id
     * @return the preset values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public PresetValues DeletePreset(String presetID) throws BusException;

    /*
    "    <method name='GetPreset'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetPresetValues.
     */
    public class GetPresetValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The preset id. */
        @Position(1)
        public String presetID;
        
        /** The lamp state. */
        @Position(2)
        public Map<String, Variant> lampState;
    }

    /**
     * Gets the preset.
     *
     * @param presetID the preset id
     * @return the gets the preset values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetPresetValues GetPreset(String presetID) throws BusException;

    /*
    "    <signal name='DefaultLampStateChanged'>"
    "    </signal>"
    */
    /**
     * Default lamp state changed.
     *
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void DefaultLampStateChanged() throws BusException;

    /*
    "    <signal name='PresetsNameChanged'>"
    "      <arg name='presetIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Presets name changed.
     *
     * @param presetIDs the preset i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void PresetsNameChanged(String[] presetIDs) throws BusException;

    /*
    "    <signal name='PresetsCreated'>"
    "      <arg name='presetsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Presets created.
     *
     * @param presetsIDs the presets i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void PresetsCreated(String[] presetsIDs) throws BusException;

    /*
    "    <signal name='PresetsUpdated'>"
    "      <arg name='presetsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Presets updated.
     *
     * @param presetsIDs the presets i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void PresetsUpdated(String[] presetsIDs) throws BusException;

    /*
    "    <signal name='PresetsDeleted'>"
    "      <arg name='presetsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Presets deleted.
     *
     * @param presetsIDs the presets i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void PresetsDeleted(String[] presetsIDs) throws BusException;
}