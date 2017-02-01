/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
 *     
 *     SPDX-License-Identifier: Apache-2.0
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *     
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alljoyn.bus.Variant;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class ControllerServiceSignalListener
{
	private static final Logger logger = new WindowsLoggerImpl("SignalListener");

    private enum Signals
    {
        LIGHTING_RESET,
        LAMP_NAME_CHANGED,
        LAMP_STATE_CHANGED,
        LAMPS_FOUND,
        LAMPS_LOST,
        LAMP_GROUPS_NAME_CHANGED,
        LAMP_GROUPS_CREATED,
        LAMP_GROUPS_UPDATED,
        LAMP_GROUPS_DELETED,
        DEFAULT_LAMP_STATE_CHANGED,
        PRESETS_NAME_CHANGED,
        PRESETS_CREATED,
        PRESETS_UPDATED,
        PRESETS_DELETED,
        SCENES_NAME_CHANGED,
        SCENES_CREATED,
        SCENES_UPDATED,
        SCENES_DELETED,
        SCENES_APPLIED,
        MASTER_SCENES_NAME_CHANGED,
        MASTER_SCENES_CREATED,
        MASTER_SCENES_UPDATED,
        MASTER_SCENES_DELETED,
        MASTER_SCENES_APPLIED,
        BLOB_CHANGED,
        TRANSITION_EFFECTS_NAME_CHANGED,
        TRANSITION_EFFECTS_CREATED,
        TRANSITION_EFFECTS_UPDATED,
        TRANSITION_EFFECTS_DELETED,
        PULSE_EFFECTS_NAME_CHANGED,
        PULSE_EFFECTS_CREATED,
        PULSE_EFFECTS_UPDATED,
        PULSE_EFFECTS_DELETED,
        SCENE_ELEMENTS_NAME_CHANGED,
        SCENE_ELEMENTS_CREATED,
        SCENE_ELEMENTS_UPDATED,
        SCENE_ELEMENTS_DELETED,
        SCENE_ELEMENTS_APPLIED
    }

    private String[] payload;
    private Set<Signals> signalsReceived;

    public ControllerServiceSignalListener()
    {
        signalsReceived = new HashSet<Signals>();
        payload = null;
    }

    public void reset()
    {
        signalsReceived.clear();
        payload = null;
    }

    // Return the payload of the LAST signal received
    public String[] getSignalPayload()
    {
        return payload;
    }

    /******
     * Signal: LightingReset
     ******/
    public void handleLightingReset()
    {
        logger.info("LightingReset signal received");
        signalsReceived.add(Signals.LIGHTING_RESET);
    }

    public boolean didLightingReset()
    {
        return signalsReceived.contains(Signals.LIGHTING_RESET);
    }

    /******
    * Signal: LampNameChanged
    ******/
    public void handleLampNameChanged(String lampID, String lampName)
    {
        logger.info("LampsNameChanged signal received");
        signalsReceived.add(Signals.LAMP_NAME_CHANGED);
    }

    public boolean didLampNameChanged()
    {
        return signalsReceived.contains(Signals.LAMP_NAME_CHANGED);
    }

    /******
    * Signal: LampsStateChanged
    ******/
    public void handleLampStateChanged(String lampID, Map<String, Variant> lampState)
    {
        logger.info("LampsStateChanged signal received");
        signalsReceived.add(Signals.LAMP_STATE_CHANGED);
    }

    public boolean didLampStateChanged()
    {
        return signalsReceived.contains(Signals.LAMP_STATE_CHANGED);
    }

    /******
    * Signal: LampsFound
    ******/
    public void handleLampsFound(String[] lampIDs)
    {
        logger.info("LampsFound signal received");
        signalsReceived.add(Signals.LAMPS_FOUND);
    }

    public boolean didLampsFound()
    {
        return signalsReceived.contains(Signals.LAMPS_FOUND);
    }

    /******
    * Signal: LampsLost
    ******/
    public void handleLampsLost(String[] lampIDs)
    {
        logger.info("LampsLost signal received");
        signalsReceived.add(Signals.LAMPS_LOST);
    }

    public boolean didLampsLost()
    {
        return signalsReceived.contains(Signals.LAMPS_LOST);
    }

    /******
    * Signal: LampGroupsNameChanged
    ******/
    public void handleLampGroupsNameChanged(String[] lampGroupsIDs)
    {
        logger.info("LampGroupsNameChanged signal received");
        signalsReceived.add(Signals.LAMP_GROUPS_NAME_CHANGED);
    }

    public boolean didLampGroupsNameChanged()
    {
        return signalsReceived.contains(Signals.LAMP_GROUPS_NAME_CHANGED);
    }

    /******
    * Signal: LampGroupsCreated
    ******/
    public void handleLampGroupsCreated(String[] lampGroupsIDs)
    {
        logger.info("LampGroupsCreated signal received");
        signalsReceived.add(Signals.LAMP_GROUPS_CREATED);
    }

    public boolean didLampGroupsCreated()
    {
        return signalsReceived.contains(Signals.LAMP_GROUPS_CREATED);
    }

    /******
    * Signal: LampGroupsUpdated
    ******/
    public void handleLampGroupsUpdated(String[] lampGroupsIDs)
    {
        logger.info("LampGroupsUpdated signal received");
        signalsReceived.add(Signals.LAMP_GROUPS_UPDATED);
    }

    public boolean didLampGroupsUpdated()
    {
        return signalsReceived.contains(Signals.LAMP_GROUPS_UPDATED);
    }

    /******
    * Signal: LampsGroupdsDeleted
    ******/
    public void handleLampGroupsDeleted(String[] lampGroupsIDs)
    {
        logger.info("LampGroupsDeleted signal received");
        signalsReceived.add(Signals.LAMP_GROUPS_DELETED);
    }

    public boolean didLampGroupsDeleted()
    {
        return signalsReceived.contains(Signals.LAMP_GROUPS_DELETED);
    }

    /******
    * Signal: DefaultLampStateChanged
    ******/
    public void handleDefaultLampStateChanged()
    {
        logger.info("DefaultLampStateChanges signal received");
        signalsReceived.add(Signals.DEFAULT_LAMP_STATE_CHANGED);
    }

    public boolean didDefaultLampStateChanged()
    {
        return signalsReceived.contains(Signals.DEFAULT_LAMP_STATE_CHANGED);
    }

    /******
    * Signal: PresetsNameChanged
    ******/
    public void handlePresetsNameChanged(String[] presetsIDs)
    {
        logger.info("PresetsNameChanged signal received");
        signalsReceived.add(Signals.PRESETS_NAME_CHANGED);
    }

    public boolean didPresetsNameChanged()
    {
        return signalsReceived.contains(Signals.PRESETS_NAME_CHANGED);
    }

    /******
    * Signal: PresetsCreated
    ******/
    public void handlePresetsCreated(String[] presetsIDs)
    {
        logger.info("PresetsCreated signal received");
        signalsReceived.add(Signals.PRESETS_CREATED);
    }

    public boolean didPresetsCreated()
    {
        return signalsReceived.contains(Signals.PRESETS_CREATED);
    }

    /******
    * Signal: PresetsUpdated
    ******/
    public void handlePresetsUpdated(String[] presetsIDs)
    {
        logger.info("PresetsUpdated signal received");
        signalsReceived.add(Signals.PRESETS_UPDATED);
    }

    public boolean didPresetsUpdated()
    {
        return signalsReceived.contains(Signals.PRESETS_UPDATED);
    }

    /******
    * Signal: PresetsDeleted
    ******/
    public void handlePresetsDeleted(String[] presetsIDs)
    {
        logger.info("PresetsDeleted signal received");
        signalsReceived.add(Signals.PRESETS_DELETED);
    }

    public boolean didPresetsDeleted()
    {
        return signalsReceived.contains(Signals.PRESETS_DELETED);
    }

    /******
    * Signal: ScenesNameChanged
    ******/
    public void handleScenesNameChanged(String[] sceneIDs)
    {
        logger.info("ScenesNameChanged signal received");
        signalsReceived.add(Signals.SCENES_NAME_CHANGED);
    }

    public boolean didScenesNameChanged()
    {
        return signalsReceived.contains(Signals.SCENES_NAME_CHANGED);
    }

    /******
    * Signal: ScenesCreated
    ******/
    public void handleScenesCreated(String[] sceneIDs)
    {
        logger.info("ScenesCreated signal received");
        signalsReceived.add(Signals.SCENES_CREATED);
    }

    public boolean didScenesCreated()
    {
        return signalsReceived.contains(Signals.SCENES_CREATED);
    }

    /******
    * Signal: ScenesUpdated
    ******/
    public void handleScenesUpdated(String[] sceneIDs)
    {
        logger.info("ScenesUpdated signal received");
        signalsReceived.add(Signals.SCENES_UPDATED);
    }

    public boolean didScenesUpdated()
    {
        return signalsReceived.contains(Signals.SCENES_UPDATED);
    }

    /******
    * Signal: ScenesDeleted
    ******/
    public void handleScenesDeleted(String[] sceneIDs)
    {
        logger.info("ScenesDeleted signal received");
        signalsReceived.add(Signals.SCENES_DELETED);
    }

    public boolean didScenesDeleted()
    {
        return signalsReceived.contains(Signals.SCENES_DELETED);
    }

    /******
    * Signal: ScenesApplied
    ******/
    public void handleScenesApplied(String[] sceneIDs)
    {
        logger.info("ScenesApplied signal received");
        signalsReceived.add(Signals.SCENES_APPLIED);
    }

    public boolean didScenesApplied()
    {
        return signalsReceived.contains(Signals.SCENES_APPLIED);
    }

    /******
    * Signal: MasterScenesNameChanged
    ******/
    public void handleMasterScenesNameChanged(String[] masterSceneIDs)
    {
        logger.info("MasterScenesNameChanged signal received");
        signalsReceived.add(Signals.MASTER_SCENES_NAME_CHANGED);
    }

    public boolean didMasterScenesNameChanged()
    {
        return signalsReceived.contains(Signals.MASTER_SCENES_NAME_CHANGED);
    }

    /******
    * Signal: MasterScenesCreated
    ******/
    public void handleMasterScenesCreated(String[] masterSceneIDs)
    {
        logger.info("MasterScenesCreated signal received");
        signalsReceived.add(Signals.MASTER_SCENES_CREATED);
    }

    public boolean didMasterScenesCreated()
    {
        return signalsReceived.contains(Signals.MASTER_SCENES_CREATED);
    }

    /******
    * Signal: MasterScenesUpdated
    ******/
    public void handleMasterScenesUpdated(String[] masterSceneIDs)
    {
        logger.info("MasterScenesUpdated signal received");
        signalsReceived.add(Signals.MASTER_SCENES_UPDATED);
    }

    public boolean didMasterScenesUpdated()
    {
        return signalsReceived.contains(Signals.MASTER_SCENES_UPDATED);
    }

    /******
    * Signal: MasterScenesDeleted
    ******/
    public void handleMasterScenesDeleted(String[] masterSceneIDs)
    {
        logger.info("MasterScenesDeleted signal received");
        signalsReceived.add(Signals.MASTER_SCENES_DELETED);
    }

    public boolean didMasterScenesDeleted()
    {
        return signalsReceived.contains(Signals.MASTER_SCENES_DELETED);
    }

    /******
    * Signal: MasterScenesApplied
    ******/
    public void handleMasterScenesApplied(String[] masterSceneIDs)
    {
        logger.info("MasterScenesApplied signal received");
        signalsReceived.add(Signals.MASTER_SCENES_APPLIED);
    }

    public boolean didMasterScenesApplied()
    {
        return signalsReceived.contains(Signals.MASTER_SCENES_APPLIED);
    }

    /******
    * Signal: BlobChanged
    ******/
    public void handleBlobChanged(int blobType, String blob, int checksum, long timestamp)
    {
        logger.info("BlobChanged signal received");
        signalsReceived.add(Signals.BLOB_CHANGED);
    }

    public boolean didBlobChanged()
    {
        return signalsReceived.contains(Signals.BLOB_CHANGED);
    }
    
    /******
     * Signal: TransitionEffectsNameChanged
     ******/
     public void handleTransitionEffectsNameChanged(String[] transitionEffectIDs)
     {
         logger.info("TransitionEffectsNameChanged signal received");
         signalsReceived.add(Signals.TRANSITION_EFFECTS_NAME_CHANGED);
     }

     public boolean didTransitionEffectsNameChanged()
     {
         return signalsReceived.contains(Signals.TRANSITION_EFFECTS_NAME_CHANGED);
     }

     /******
     * Signal: TransitionEffectsCreated
     ******/
     public void handleTransitionEffectsCreated(String[] transitionEffectIDs)
     {
         logger.info("TransitionEffectsCreated signal received");
         signalsReceived.add(Signals.TRANSITION_EFFECTS_CREATED);
     }

     public boolean didTransitionEffectsCreated()
     {
         return signalsReceived.contains(Signals.TRANSITION_EFFECTS_CREATED);
     }

     /******
     * Signal: TransitionEffectsUpdated
     ******/
     public void handleTransitionEffectsUpdated(String[] transitionEffectIDs)
     {
         logger.info("TransitionEffectsUpdated signal received");
         signalsReceived.add(Signals.TRANSITION_EFFECTS_UPDATED);
     }

     public boolean didTransitionEffectsUpdated()
     {
         return signalsReceived.contains(Signals.TRANSITION_EFFECTS_UPDATED);
     }

     /******
     * Signal: TransitionEffectsDeleted
     ******/
     public void handleTransitionEffectsDeleted(String[] transitionEffectIDs)
     {
         logger.info("TransitionEffectsDeleted signal received");
         signalsReceived.add(Signals.TRANSITION_EFFECTS_DELETED);
     }

     public boolean didTransitionEffectsDeleted()
     {
         return signalsReceived.contains(Signals.TRANSITION_EFFECTS_DELETED);
     }

     /******
     * Signal: PulseEffectsNameChanged
     ******/
     public void handlePulseEffectsNameChanged(String[] pulseEffectIDs)
     {
         logger.info("PulseEffectsNameChanged signal received");
         signalsReceived.add(Signals.PULSE_EFFECTS_NAME_CHANGED);
     }

     public boolean didPulseEffectsNameChanged()
     {
         return signalsReceived.contains(Signals.PULSE_EFFECTS_NAME_CHANGED);
     }

     /******
     * Signal: PulseEffectsCreated
     ******/
     public void handlePulseEffectsCreated(String[] pulseEffectIDs)
     {
         logger.info("PulseEffectsCreated signal received");
         signalsReceived.add(Signals.PULSE_EFFECTS_CREATED);
     }

     public boolean didPulseEffectsCreated()
     {
         return signalsReceived.contains(Signals.PULSE_EFFECTS_CREATED);
     }

     /******
     * Signal: PulseEffectsUpdated
     ******/
     public void handlePulseEffectsUpdated(String[] pulseEffectIDs)
     {
         logger.info("PulseEffectsUpdated signal received");
         signalsReceived.add(Signals.PULSE_EFFECTS_UPDATED);
     }

     public boolean didPulseEffectsUpdated()
     {
         return signalsReceived.contains(Signals.PULSE_EFFECTS_UPDATED);
     }

     /******
     * Signal: PulseEffectsDeleted
     ******/
     public void handlePulseEffectsDeleted(String[] pulseEffectIDs)
     {
         logger.info("PulseEffectsDeleted signal received");
         signalsReceived.add(Signals.PULSE_EFFECTS_DELETED);
     }

     public boolean didPulseEffectsDeleted()
     {
         return signalsReceived.contains(Signals.PULSE_EFFECTS_DELETED);
     }

     /******
     * Signal: SceneElementsNameChanged
     ******/
     public void handleSceneElementsNameChanged(String[] sceneElementIDs)
     {
         logger.info("SceneElementsNameChanged signal received");
         signalsReceived.add(Signals.SCENE_ELEMENTS_NAME_CHANGED);
     }

     public boolean didSceneElementsNameChanged()
     {
         return signalsReceived.contains(Signals.SCENE_ELEMENTS_NAME_CHANGED);
     }

     /******
     * Signal: SceneElementsCreated
     ******/
     public void handleSceneElementsCreated(String[] sceneElementIDs)
     {
         logger.info("SceneElementsCreated signal received");
         signalsReceived.add(Signals.SCENE_ELEMENTS_CREATED);
     }

     public boolean didSceneElementsCreated()
     {
         return signalsReceived.contains(Signals.SCENE_ELEMENTS_CREATED);
     }

     /******
     * Signal: SceneElementsUpdated
     ******/
     public void handleSceneElementsUpdated(String[] sceneElementIDs)
     {
         logger.info("SceneElementsUpdated signal received");
         signalsReceived.add(Signals.SCENE_ELEMENTS_UPDATED);
     }

     public boolean didSceneElementsUpdated()
     {
         return signalsReceived.contains(Signals.SCENE_ELEMENTS_UPDATED);
     }

     /******
     * Signal: SceneElementsDeleted
     ******/
     public void handleSceneElementsDeleted(String[] sceneElementIDs)
     {
         logger.info("SceneElementsDeleted signal received");
         signalsReceived.add(Signals.SCENE_ELEMENTS_DELETED);
     }

     public boolean didSceneElementsDeleted()
     {
         return signalsReceived.contains(Signals.SCENE_ELEMENTS_DELETED);
     }

     /******
     * Signal: SceneElementsApplied
     ******/
     public void handleSceneElementsApplied(String[] sceneElementIDs)
     {
         logger.info("SceneElementsApplied signal received");
         signalsReceived.add(Signals.SCENE_ELEMENTS_APPLIED);
     }

     public boolean didSceneElementsApplied()
     {
         return signalsReceived.contains(Signals.SCENE_ELEMENTS_APPLIED);
     }
}