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
 * The Interface ControllerServiceSceneBusInterface.
 */
@BusInterface(name = "org.allseen.LSF.ControllerService.Scene")
public interface ControllerServiceSceneBusInterface
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
     * The Class SceneValues.
     */
    public class SceneValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The scene id. */
        @Position(1)
        public String sceneID;
    }

    /*
    "    <method name='GetAllSceneIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetAllSceneIDsValues.
     */
    public class GetAllSceneIDsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The scene i ds. */
        @Position(1)
        public String[] sceneIDs;
    }

    /**
     * Gets the all scene i ds.
     *
     * @return the gets the all scene i ds values
     * @throws BusException the bus exception
     */
    @BusMethod(replySignature = "uas")
    public GetAllSceneIDsValues GetAllSceneIDs() throws BusException;

    /*
    "    <method name='GetSceneName'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='sceneName' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetSceneNameValues.
     */
    public class GetSceneNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The scene id. */
        @Position(1)
        public String sceneID;
        
        /** The language. */
        @Position(2)
        public String language;
        
        /** The scene name. */
        @Position(3)
        public String sceneName;
    }

    /**
     * Gets the scene name.
     *
     * @param sceneID the scene id
     * @param language the language
     * @return the gets the scene name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetSceneNameValues GetSceneName(String sceneID, String language) throws BusException;

    /*
    "    <method name='SetSceneName'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='SceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class SetSceneNameValues.
     */
    public class SetSceneNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The scene id. */
        @Position(1)
        public String sceneID;
        
        /** The language. */
        @Position(2)
        public String language;
    }

    /**
     * Sets the scene name.
     *
     * @param sceneID the scene id
     * @param sceneName the scene name
     * @param language the language
     * @return the sets the scene name values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetSceneNameValues SetSceneName(String sceneID, String sceneName, String language) throws BusException;

    /*
    "    <method name='CreateScene'>"
    "      <arg name='transitionlampsLampGroupsToState' type='a(asasa{sv}u)' direction='in'/>"
    "      <arg name='transitionlampsLampGroupsToPreset' type='a(asassu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithState' type='a(asasa{sv}a{sv}uuu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithPreset' type='a(asasssuuu)' direction='in'/>"
    "      <arg name='sceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"

    */

    /**
     * The Class CreateSceneTransitionlampsLampGroupsToState.
     */
    public class CreateSceneTransitionlampsLampGroupsToState
    {
        
        /** The lamp i ds. */
        @Position(0)
        public String[] lampIDs;
        
        /** The lamp group i ds. */
        @Position(1)
        public String[] lampGroupIDs;
        
        /** The lamp state. */
        @Position(2)
        public Map<String, Variant> lampState;
        
        /** The transition period. */
        @Position(3)
        public int transitionPeriod;
    }

    /**
     * The Class CreateSceneTransitionlampsLampGroupsToPreset.
     */
    public class CreateSceneTransitionlampsLampGroupsToPreset
    {
        
        /** The lamp i ds. */
        @Position(0)
        public String[] lampIDs;
        
        /** The lamp group i ds. */
        @Position(1)
        public String[] lampGroupIDs;
        
        /** The preset id. */
        @Position(2)
        public String presetID;
        
        /** The transition period. */
        @Position(3)
        public int transitionPeriod;
    }

    /**
     * The Class CreateScenePulselampsLampGroupsWithState.
     */
    public class CreateScenePulselampsLampGroupsWithState
    {
        
        /** The lamp i ds. */
        @Position(0)
        public String[] lampIDs;
        
        /** The lamp group i ds. */
        @Position(1)
        public String[] lampGroupIDs;
        
        /** The to state. */
        @Position(2)
        public Map<String, Variant> toState;
        
        /** The from state. */
        @Position(3)
        public Map<String, Variant> fromState;
        
        /** The period. */
        @Position(4)
        public int period;
        
        /** The duration. */
        @Position(5)
        public int duration;
        
        /** The num pulses. */
        @Position(6)
        public int numPulses;
    }

    /**
     * The Class CreateScenePulselampsLampGroupsWithPreset.
     */
    public class CreateScenePulselampsLampGroupsWithPreset
    {
        
        /** The lamp i ds. */
        @Position(0)
        public String[] lampIDs;
        
        /** The lamp group i ds. */
        @Position(1)
        public String[] lampGroupIDs;
        
        /** The to preset id. */
        @Position(2)
        public String toPresetID;
        
        /** The from preset id. */
        @Position(3)
        public String fromPresetID;
        
        /** The period. */
        @Position(4)
        public int period;
        
        /** The duration. */
        @Position(5)
        public int duration;
        
        /** The num pulses. */
        @Position(6)
        public int numPulses;
    }

    /**
     * Creates the scene.
     *
     * @param transitionlampsLampGroupsToState the transitionlamps lamp groups to state
     * @param transitionlampsLampGroupsToPreset the transitionlamps lamp groups to preset
     * @param pulselampsLampGroupsWithState the pulselamps lamp groups with state
     * @param pulselampsLampGroupsWithPreset the pulselamps lamp groups with preset
     * @param sceneName the scene name
     * @param language the language
     * @return the scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "a(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)ss", replySignature = "us")
    public SceneValues CreateScene(CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset, String sceneName, String language) throws BusException;

    /*"    <method name='UpdateScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='transitionlampsLampGroupsToState' type='a(asasa{sv}u)' direction='in'/>"
    "      <arg name='transitionlampsLampGroupsToPreset' type='a(asassu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithState' type='a(asasa{sv}a{sv}uuu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithPreset' type='a(asasssuuu)' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
    */

    /**
     * Update scene.
     *
     * @param sceneID the scene id
     * @param transitionlampsLampGroupsToState the transitionlamps lamp groups to state
     * @param transitionlampsLampGroupsToPreset the transitionlamps lamp groups to preset
     * @param pulselampsLampGroupsWithState the pulselamps lamp groups with state
     * @param pulselampsLampGroupsWithPreset the pulselamps lamp groups with preset
     * @return the scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "sa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)", replySignature = "us")
    public SceneValues UpdateScene(String sceneID, CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset) throws BusException;

    /*
    "    <method name='DeleteScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
    */

    /**
     * Delete scene.
     *
     * @param sceneID the scene id
     * @return the scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues DeleteScene(String sceneID) throws BusException;

    /*
    "    <method name='GetScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='transitionlampsLampGroupsToState' type='a(asasa{sv}u)' direction='out'/>"
    "      <arg name='transitionlampsLampGroupsToPreset' type='a(asassu)' direction='out'/>"
    "      <arg name='pulselampsLampGroupsWithState' type='a(asasa{sv}a{sv}uuu)' direction='out'/>"
    "      <arg name='pulselampsLampGroupsWithPreset' type='a(asasssuuu)' direction='out'/>"
    "    </method>"
    */

    /**
     * The Class GetSceneValues.
     */
    public class GetSceneValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The scene id. */
        @Position(1)
        public String sceneID;
        
        /** The transitionlamps lamp groups to state. */
        @Position(2)
        public CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState;
        
        /** The transitionlamps lamp groups to preset. */
        @Position(3)
        public CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset;
        
        /** The pulselamps lamp groups with state. */
        @Position(4)
        public CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState;
        
        /** The pulselamps lamp groups with preset. */
        @Position(5)
        public CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset;
    }

    /**
     * Gets the scene.
     *
     * @param sceneID the scene id
     * @return the gets the scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)")
    public GetSceneValues GetScene(String sceneID) throws BusException;

    /*
    "    <method name='ApplyScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
    */

    /**
     * Apply scene.
     *
     * @param sceneID the scene id
     * @return the scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues ApplyScene(String sceneID) throws BusException;

    /*
    "    <signal name='ScenesNameChanged'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Scenes name changed.
     *
     * @param sceneIDs the scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void ScenesNameChanged(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesCreated'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Scenes created.
     *
     * @param sceneIDs the scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void ScenesCreated(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesUpdated'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Scenes updated.
     *
     * @param sceneIDs the scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void ScenesUpdated(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesDeleted'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Scenes deleted.
     *
     * @param sceneIDs the scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void ScenesDeleted(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesApplied'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Scenes applied.
     *
     * @param sceneIDs the scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal(signature = "as")
    public void ScenesApplied(String[] sceneIDs) throws BusException;
}
