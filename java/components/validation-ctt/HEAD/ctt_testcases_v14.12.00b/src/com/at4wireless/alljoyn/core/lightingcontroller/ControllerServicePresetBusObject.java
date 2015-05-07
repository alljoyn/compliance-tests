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
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServicePresetBusObject.
 */
public class ControllerServicePresetBusObject implements BusObject, ControllerServicePresetBusInterface
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#getVersion()
     */
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#GetDefaultLampState()
     */
    @Override
    @BusMethod(replySignature = "ua{sv}")
    public GetDefaultLampStateValues GetDefaultLampState() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#SetDefaultLampState(java.util.Map)
     */
    @Override
    @BusMethod(signature = "a{sv}", replySignature = "u")
    public int SetDefaultLampState(Map<String, Variant> lampState) throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#GetAllPresetIDs()
     */
    @Override
    @BusMethod(replySignature = "uas")
    public GetAllPresetIDsValues GetAllPresetIDs() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#GetPresetName(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetPresetNameValues GetPresetName(String presetID, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#SetPresetName(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetPresetNameValues SetPresetName(String presetID, String presetName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#CreatePreset(java.util.Map, java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "a{sv}ss", replySignature = "us")
    public PresetValues CreatePreset(Map<String, Variant> lampState, String presetName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#UpdatePreset(java.lang.String, java.util.Map)
     */
    @Override
    @BusMethod(signature = "sa{sv}", replySignature = "us")
    public PresetValues UpdatePreset(String presetID, Map<String, Variant> lampState) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#DeletePreset(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public PresetValues DeletePreset(String presetID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#GetPreset(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetPresetValues GetPreset(String presetID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#DefaultLampStateChanged()
     */
    @Override
    @BusSignal()
    public void DefaultLampStateChanged() throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#PresetsNameChanged(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void PresetsNameChanged(String[] presetIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#PresetsCreated(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void PresetsCreated(String[] presetsIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#PresetsUpdated(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void PresetsUpdated(String[] presetsIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface#PresetsDeleted(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void PresetsDeleted(String[] presetsIDs) throws BusException
    {
    }
}
