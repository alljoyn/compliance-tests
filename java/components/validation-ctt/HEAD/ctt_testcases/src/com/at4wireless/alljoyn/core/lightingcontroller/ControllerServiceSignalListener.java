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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alljoyn.bus.Variant;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving controllerServiceSignal events.
 * The class that is interested in processing a controllerServiceSignal
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addControllerServiceSignalListener<code> method. When
 * the controllerServiceSignal event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ControllerServiceSignalEvent
 */
public class ControllerServiceSignalListener
{
    
    /** The Constant TAG. */
    private static final String TAG     = "SignalListener";
    
    /** The Constant logger. */
    private static final Logger logger  = LoggerFactory.getLogger(TAG);

    /**
     * The Enum Signals.
     */
    private enum Signals {
        
        /** The lighting reset. */
        LIGHTING_RESET,
        
        /** The lamp name changed. */
        LAMP_NAME_CHANGED,
        
        /** The lamp state changed. */
        LAMP_STATE_CHANGED,
        
        /** The lamps found. */
        LAMPS_FOUND,
        
        /** The lamps lost. */
        LAMPS_LOST,
        
        /** The lamp groups name changed. */
        LAMP_GROUPS_NAME_CHANGED,
        
        /** The lamp groups created. */
        LAMP_GROUPS_CREATED,
        
        /** The lamp groups updated. */
        LAMP_GROUPS_UPDATED,
        
        /** The lamp groups deleted. */
        LAMP_GROUPS_DELETED,
        
        /** The default lamp state changed. */
        DEFAULT_LAMP_STATE_CHANGED,
        
        /** The presets name changed. */
        PRESETS_NAME_CHANGED,
        
        /** The presets created. */
        PRESETS_CREATED,
        
        /** The presets updated. */
        PRESETS_UPDATED,
        
        /** The presets deleted. */
        PRESETS_DELETED,
        
        /** The scenes name changed. */
        SCENES_NAME_CHANGED,
        
        /** The scenes created. */
        SCENES_CREATED,
        
        /** The scenes updated. */
        SCENES_UPDATED,
        
        /** The scenes deleted. */
        SCENES_DELETED,
        
        /** The scenes applied. */
        SCENES_APPLIED,
        
        /** The master scenes name changed. */
        MASTER_SCENES_NAME_CHANGED,
        
        /** The master scenes created. */
        MASTER_SCENES_CREATED,
        
        /** The master scenes updated. */
        MASTER_SCENES_UPDATED,
        
        /** The master scenes deleted. */
        MASTER_SCENES_DELETED,
        
        /** The master scenes applied. */
        MASTER_SCENES_APPLIED,
        
        /** The blob changed. */
        BLOB_CHANGED
    }

    /** The payload. */
    private String[] payload;
    
    /** The signals received. */
    private Set<Signals> signalsReceived;

    /**
     * Instantiates a new controller service signal listener.
     */
    public ControllerServiceSignalListener()
    {
        signalsReceived = new HashSet<Signals>();
        payload = null;
    }

    /**
     * Reset.
     */
    public void reset()
    {
        signalsReceived.clear();
        payload = null;
    }

    // Return the payload of the LAST signal received
    /**
     * Gets the signal payload.
     *
     * @return the signal payload
     */
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

    /**
     * Did lighting reset.
     *
     * @return true, if successful
     */
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

    /**
     * Did lamp name changed.
     *
     * @return true, if successful
     */
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

    /**
     * Did lamp state changed.
     *
     * @return true, if successful
     */
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

    /**
     * Did lamps found.
     *
     * @return true, if successful
     */
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

    /**
     * Did lamps lost.
     *
     * @return true, if successful
     */
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

    /**
     * Did lamp groups name changed.
     *
     * @return true, if successful
     */
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

    /**
     * Invoked when did lamp groups is created.
     *
     * @return true, if did lamp groups created
     */
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

    /**
     * Invoked when did lamp groups update occurs.
     *
     * @return true, if did lamp groups updated
     */
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

    /**
     * Did lamp groups deleted.
     *
     * @return true, if successful
     */
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

    /**
     * Did default lamp state changed.
     *
     * @return true, if successful
     */
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

    /**
     * Did presets name changed.
     *
     * @return true, if successful
     */
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

    /**
     * Invoked when did presets is created.
     *
     * @return true, if did presets created
     */
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

    /**
     * Invoked when did presets update occurs.
     *
     * @return true, if did presets updated
     */
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

    /**
     * Did presets deleted.
     *
     * @return true, if successful
     */
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

    /**
     * Did scenes name changed.
     *
     * @return true, if successful
     */
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

    /**
     * Invoked when did scenes is created.
     *
     * @return true, if did scenes created
     */
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

    /**
     * Invoked when did scenes update occurs.
     *
     * @return true, if did scenes updated
     */
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

    /**
     * Did scenes deleted.
     *
     * @return true, if successful
     */
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

    /**
     * Did scenes applied.
     *
     * @return true, if successful
     */
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

    /**
     * Did master scenes name changed.
     *
     * @return true, if successful
     */
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

    /**
     * Invoked when did master scenes is created.
     *
     * @return true, if did master scenes created
     */
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

    /**
     * Invoked when did master scenes update occurs.
     *
     * @return true, if did master scenes updated
     */
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

    /**
     * Did master scenes deleted.
     *
     * @return true, if successful
     */
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

    /**
     * Did master scenes applied.
     *
     * @return true, if successful
     */
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

    /**
     * Did blob changed.
     *
     * @return true, if successful
     */
    public boolean didBlobChanged()
    {
        return signalsReceived.contains(Signals.BLOB_CHANGED);
    }
}