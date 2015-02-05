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
package org.alljoyn.validation.testing.suites.controller.alljoyn;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.Lamp")
public interface ControllerServiceLampBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */

    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    /*
    "    <method name='GetAllLampIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetAllLampIDsValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String[] lampIDs;
    }

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
    public class GetLampSupportedLanguagesValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String[] supportedLanguages;
    }

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
    public class GetLampManufacturerValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String language;
        @Position(3)
        public String manufacturer;
    }

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
    public class GetLampNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String language;
        @Position(3)
        public String lampName;
    }

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
    public class SetLampNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String language;
    }

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
    public class GetLampDetailsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public Map<String, Variant> lampDetails;
    }

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
    public class GetLampParametersValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public Map<String, Variant> lampParameters;
    }

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
    public class GetLampParametersFieldValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String lampParameterFieldName;
        @Position(3)
        public Variant lampParameterFieldValue;
    }

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
    public class GetLampStateValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public Map<String, Variant> lampState;
    }

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
    public class GetLampStateFieldValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String lampStateFieldName;
        @Position(3)
        public Variant lampStateFieldValue;
    }

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
    public class TransitionLampStateValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
    }

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
    public class PulseLampWithStateValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
    }

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
    public class PulseLampWithPresetValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
    }

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
    public class TransitionLampStateToPresetValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
    }

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
    public class TransitionLampStateFieldValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String lampStateFieldName;
    }

    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampStateFieldValues TransitionLampStateField(String lampID, String lampStateFieldName, Variant lampStateFieldValue, int transitionPeriod) throws BusException;

    /*
    "    <method name='ResetLampState'>"
    "      <arg name='lampID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "    </method>"
    */
    public class ResetLampStateValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
    }

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
    public class ResetLampStateFieldValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public String lampStateFieldName;
    }

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
    public class GetLampFaultsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public int[] lampFaults;
    }

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
    public class ClearLampFaultValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public int lampFault;
    }

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
    public class GetLampServiceVersionValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String lampID;
        @Position(2)
        public int lampServiceVersion;
    }

    @BusMethod(signature = "s", replySignature = "usu")
    public GetLampServiceVersionValues GetLampServiceVersion(String lampID) throws BusException;

    /*
    "    <signal name='LampNameChanged'>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampName' type='s' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "ss")
    public void LampNameChanged(String lampID, String lampName) throws BusException;

    /*
    "    <signal name='LampStateChanged'>"
    "      <arg name='lampID' type='s' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "sa{sv}")
    public void LampStateChanged(String lampID, Map<String, Variant> lampState) throws BusException;

    /*
    "    <signal name='LampsFound'>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void LampsFound(String[] lampIDs) throws BusException;

    /*
    "    <signal name='LampsLost'>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void LampsLost(String[] lampIDs) throws BusException;
}
