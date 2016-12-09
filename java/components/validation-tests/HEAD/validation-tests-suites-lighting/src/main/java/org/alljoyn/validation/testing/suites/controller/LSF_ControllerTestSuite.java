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
package org.alljoyn.validation.testing.suites.controller;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.lang.Thread;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;

import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.services.ServiceAvailabilityHandler;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.IntrospectionXmlParser;
import org.alljoyn.validation.testing.utils.IntrospectionXmlFile;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.testing.suites.controller.util.ControllerInterfaceValidator;
import org.alljoyn.validation.testing.suites.controller.util.ControllerServiceHelper;

import org.alljoyn.validation.testing.suites.controller.ControllerServiceSignalListener;

import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceSignalHandler;

import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampBusInterface.*;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampSignalHandler;

import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampGroupBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampGroupBusInterface.*;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampGroupBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceLampGroupSignalHandler;

import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServicePresetBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServicePresetBusInterface.*;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServicePresetBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServicePresetSignalHandler;

import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceSceneBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceSceneBusInterface.*;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceSceneBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceSceneSignalHandler;

import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceMasterSceneBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceMasterSceneBusInterface.*;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceMasterSceneBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.ControllerServiceMasterSceneSignalHandler;

import org.alljoyn.validation.testing.suites.controller.alljoyn.LeaderElectionBusInterface;
import org.alljoyn.validation.testing.suites.controller.alljoyn.LeaderElectionBusInterface.*;
import org.alljoyn.validation.testing.suites.controller.alljoyn.LeaderElectionBusObject;
import org.alljoyn.validation.testing.suites.controller.alljoyn.LeaderElectionSignalHandler;

@ValidationSuite(name = "LSF_Controller-v1")
public class LSF_ControllerTestSuite extends ValidationBaseTestCase
{
    // Logging constants
    private static final String TAG     = "LSF_ControllerTestSuite";
    private static final Logger logger  = LoggerFactory.getLogger(TAG);

    // AJ testing constants
    private static final String BUS_APPLICATION_NAME            = "ControllerTestSuite";
    private static final long ANNOUNCEMENT_TIMEOUT_IN_SECONDS   = 30;
    private static final long SESSION_CLOSE_TIMEOUT_IN_SECONDS  = 5;

    // Controller service constants
    private static final String CONTROLLER_BUS_OBJECT_PATH          = "/org/allseen/LSF/ControllerService";
    private static final String LEADER_ELECTION_BUS_OBJECT_PATH     = "/org/allseen/LeaderElectionAndStateSync";
    private static final String CONTROLLERSERVICE_INTERFACE_NAME    = "org.allseen.LSF.ControllerService";
    private static final String LAMP_INTERFACE_NAME                 = "org.allseen.LSF.ControllerService.Lamp";
    private static final String LAMPGROUP_INTERFACE_NAME            = "org.allseen.LSF.ControllerService.LampGroup";
    private static final String PRESET_INTERFACE_NAME               = "org.allseen.LSF.ControllerService.Preset";
    private static final String SCENE_INTERFACE_NAME                = "org.allseen.LSF.ControllerService.Scene";
    private static final String MASTERSCENE_INTERFACE_NAME          = "org.allseen.LSF.ControllerService.MasterScene";
    private static final String LEADER_ELECTION_INTERFACE_NAME      = "org.allseen.LeaderElectionAndStateSync";

    // Ivars
    private ControllerServiceHelper serviceHelper;
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;
    private ControllerServiceSignalListener signalListener;

    // Objects for org.allseen.LSF.ControllerService
    private ControllerServiceBusObject controllerServiceBusObject;
    private ControllerServiceSignalHandler controllerServiceSignalHandler;
    private ControllerServiceBusInterface controllerIface;

    // Object for org.allseen.LSF.ControllerService.Lamp
    private ControllerServiceLampBusObject lampBusObject;
    private ControllerServiceLampSignalHandler lampSignalHandler;
    private ControllerServiceLampBusInterface lampIface;

    // Object for org.allseen.LSF.ControllerService.LampGroup
    private ControllerServiceLampGroupBusObject lampGroupBusObject;
    private ControllerServiceLampGroupSignalHandler lampGroupSignalHandler;
    private ControllerServiceLampGroupBusInterface lampGroupIface;

    // Object for org.allseen.LSF.ControllerService.Preset
    private ControllerServicePresetBusObject presetBusObject;
    private ControllerServicePresetSignalHandler presetSignalHandler;
    private ControllerServicePresetBusInterface presetIface;

    // Object for org.allseen.LSF.ControllerService.Scene
    private ControllerServiceSceneBusObject sceneBusObject;
    private ControllerServiceSceneSignalHandler sceneSignalHandler;
    private ControllerServiceSceneBusInterface sceneIface;

    // Object for org.allseen.LSF.ControllerService.MasterScene
    private ControllerServiceMasterSceneBusObject masterSceneBusObject;
    private ControllerServiceMasterSceneSignalHandler masterSceneSignalHandler;
    private ControllerServiceMasterSceneBusInterface masterSceneIface;

    // Object for org.allseen.LeaderElectionAndStateSync
    private LeaderElectionBusObject leaderElectionBusObject;
    private LeaderElectionSignalHandler leaderElectionSignalHandler;
    private LeaderElectionBusInterface leaderElectionIface;

    /**************************************************************************
     * Initialization / Deintialization
     **************************************************************************/

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        logger.debug("setUp started");

        try
        {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            dutAppId = appUnderTestDetails.getAppId();

            logger.debug("Running LSF_Controller test case against Device ID: " + dutDeviceId);
            logger.debug("Running LSF_Controller test case against App ID: " + dutAppId);

            initServiceHelper();
            initProxyBusObjects();

            logger.debug("setUp finished");
        }
        catch (Exception e)
        {
            logger.error("setUp thown an exception", e);
            releaseServiceHelper();
            throw e;
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        controllerIface.LightingResetControllerService(); // clean up Controller
		releaseServiceHelper();
    }

    private void initServiceHelper() throws BusException, Exception
    {
        releaseServiceHelper();
        serviceHelper = new ControllerServiceHelper(new AndroidLogger());
        serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);
        signalListener = new ControllerServiceSignalListener(); // callback class for all signal handlers

        // create bus objects and signal handlers
        controllerServiceBusObject = new ControllerServiceBusObject();
        controllerServiceSignalHandler = new ControllerServiceSignalHandler();
        controllerServiceSignalHandler.setUpdateListener(signalListener);

        lampBusObject = new ControllerServiceLampBusObject();
        lampSignalHandler = new ControllerServiceLampSignalHandler();
        lampSignalHandler.setUpdateListener(signalListener);

        lampGroupBusObject = new ControllerServiceLampGroupBusObject();
        lampGroupSignalHandler = new ControllerServiceLampGroupSignalHandler();
        lampGroupSignalHandler.setUpdateListener(signalListener);

        presetBusObject = new ControllerServicePresetBusObject();
        presetSignalHandler = new ControllerServicePresetSignalHandler();
        presetSignalHandler.setUpdateListener(signalListener);

        sceneBusObject = new ControllerServiceSceneBusObject();
        sceneSignalHandler = new ControllerServiceSceneSignalHandler();
        sceneSignalHandler.setUpdateListener(signalListener);

        masterSceneBusObject = new ControllerServiceMasterSceneBusObject();
        masterSceneSignalHandler = new ControllerServiceMasterSceneSignalHandler();
        masterSceneSignalHandler.setUpdateListener(signalListener);

        leaderElectionBusObject = new LeaderElectionBusObject();
        leaderElectionSignalHandler = new LeaderElectionSignalHandler();
        leaderElectionSignalHandler.setUpdateListener(signalListener);

        // register bus objects and signal handlers with serviceHelper
        serviceHelper.registerBusObject(controllerServiceBusObject, CONTROLLER_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(controllerServiceSignalHandler);

        serviceHelper.registerBusObject(lampBusObject, CONTROLLER_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(lampSignalHandler);

        serviceHelper.registerBusObject(lampGroupBusObject, CONTROLLER_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(lampGroupSignalHandler);

        serviceHelper.registerBusObject(presetBusObject, CONTROLLER_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(presetSignalHandler);

        serviceHelper.registerBusObject(sceneBusObject, CONTROLLER_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(sceneSignalHandler);

        serviceHelper.registerBusObject(masterSceneBusObject, CONTROLLER_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(masterSceneSignalHandler);

        serviceHelper.registerBusObject(leaderElectionBusObject, LEADER_ELECTION_BUS_OBJECT_PATH);
        serviceHelper.registerSignalHandler(leaderElectionSignalHandler);

        deviceAboutAnnouncement = waitForNextDeviceAnnouncement();
        connectControllerService(deviceAboutAnnouncement);
    }

    private void initProxyBusObjects()
    {
        ProxyBusObject proxy = null;

        // org.allseen.LSF.ControllerService
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceBusInterface.class });
        controllerIface =proxy.getInterface(ControllerServiceBusInterface.class);

        // org.allseen.LSF.ControllerService.Lamp
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceLampBusInterface.class });
        lampIface = proxy.getInterface(ControllerServiceLampBusInterface.class);

        // org.allseen.LSF.ControllerService.LampGroup
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceLampGroupBusInterface.class });
        lampGroupIface = proxy.getInterface(ControllerServiceLampGroupBusInterface.class);

        // org.allseen.LSF.ControllerService.Preset
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServicePresetBusInterface.class });
        presetIface = proxy.getInterface(ControllerServicePresetBusInterface.class);

        // org.allseen.LSF.ControllerService.Scene
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceSceneBusInterface.class });
        sceneIface = proxy.getInterface(ControllerServiceSceneBusInterface.class);

        // org.allseen.LSF.ControllerService.Scene
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceMasterSceneBusInterface.class });
        masterSceneIface = proxy.getInterface(ControllerServiceMasterSceneBusInterface.class);

        // org.allseen.LeaderElectionAndStateSync
        proxy = serviceHelper.getProxyBusObject(LEADER_ELECTION_BUS_OBJECT_PATH,
            new Class[] { LeaderElectionBusInterface.class });
        leaderElectionIface = proxy.getInterface(LeaderElectionBusInterface.class);
    }

    private void releaseServiceHelper()
    {
        try
        {
            if (serviceHelper != null)
            {
                serviceHelper.release();
                waitForSessionToClose();
                serviceHelper = null;
            }

            if (signalListener != null)
            {
                signalListener.reset();
                signalListener = null;
            }

            controllerServiceBusObject = null;
            controllerServiceSignalHandler = null;
        }
        catch (Exception e)
        {
            logger.error("Exception releasing resources", e);
        }
    }

    private AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
    {
        logger.info("Waiting for About annoucement");
        return serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true);
    }

    private void connectControllerService(AboutAnnouncementDetails aboutAnnouncement) throws Exception
    {
        ServiceAvailabilityHandler serviceAvailabilityHandler = new ServiceAvailabilityHandler();
        serviceHelper.connect(aboutAnnouncement);
    }

    private void waitForSessionToClose() throws Exception
    {
        logger.info("Waiting for session to close");
        serviceHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    /**************************************************************************
     *  Test Cases
     **************************************************************************/

    @ValidationTest(name = "LSF_Controller-v1-01")
    public void testLSF_Controller_v1_01_StandardizedInterfacesMatchDefinitions() throws Exception
    {
        verifyInterfacesFromAnnouncement();
        verifyLeaderElectionAndStateSyncInterface();
    }

    @ValidationTest(name = "LSF_Controller-v1-02")
    public void testLSF_Controller_v1_02_VersionField() throws Exception
    {
        try
        {
            // check version properties from all interfaces do not cause BusException
            int version = -1;

            version = controllerIface.getVersion();
            logger.info(CONTROLLERSERVICE_INTERFACE_NAME + ", Version:" + version);

            version = lampIface.getVersion();
            logger.info(LAMP_INTERFACE_NAME + ", Version: " + version);

            version = lampGroupIface.getVersion();
            logger.info(LAMPGROUP_INTERFACE_NAME + " Version: " + version);

            version = presetIface.getVersion();
            logger.info(PRESET_INTERFACE_NAME + " Version: " + version);

            version = sceneIface.getVersion();
            logger.info(SCENE_INTERFACE_NAME + " Version: " + version);

            version = masterSceneIface.getVersion();
            logger.info(MASTERSCENE_INTERFACE_NAME + " Version: " + version);

            // verify version from method on ControllerService interface
            version = controllerIface.GetControllerServiceVersion();
            logger.info(LEADER_ELECTION_INTERFACE_NAME + " MethodCall-Version: " + version);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to get version");
            e.printStackTrace();
            fail("Exception caught while trying to get version");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-03")
    public void testLSF_Controller_v1_03_LightingReset() throws Exception
    {
        try
        {
            int reset = controllerIface.LightingResetControllerService();
            logger.info("Reset status code: " + reset);
            Thread.sleep(500); // wait before checking that signal is received
            boolean signalReceived = signalListener.didLightingReset();
            assertTrue("Signal for LightingReset was not received", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to call LightingReset");
            e.printStackTrace();
            fail("Exception caught while trying to call LightingReset");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-04")
    public void testLSF_Controller_v1_04_LampInfo() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // GetLampSupportedLanguages
            GetLampSupportedLanguagesValues supportedLangsValues = lampIface.GetLampSupportedLanguages(lampID);
            assertEquals("Error occurred in getting supported languages", 0, supportedLangsValues.responseCode);
            for (String lang : supportedLangsValues.supportedLanguages)
            {
                logger.info("Lamp supported language: " + lang);
            }

            // GetLampManufacturer
            GetLampManufacturerValues manufacturerValues = lampIface.GetLampManufacturer(lampID, "en");
            assertEquals("Error occurred in getting LampManufacturer", 0, manufacturerValues.responseCode);
            logger.info("Lamp manufacturer: " + manufacturerValues.manufacturer);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while querying lamp info");
            e.printStackTrace();
            fail("Exception caught while querying lamp info");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-05")
    public void testLSF_Controller_v1_05_LampName() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // set lamp name
            String lampName = "ControllerTestLamp-" + new Random().nextInt(1337);
            SetLampNameValues setNameValues = lampIface.SetLampName(lampID, lampName, "en");

            // verify lamp name changed
            GetLampNameValues getNameValues = lampIface.GetLampName(lampID, "en");
            logger.info("Lamp name is " + getNameValues.lampName);
            assertEquals(lampName, getNameValues.lampName);

            Thread.sleep(5000); // wait before checking signal is received
            boolean signalReceived = signalListener.didLampNameChanged();
            assertTrue("Signal for LampNameChanged was not received", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to get/set name");
            e.printStackTrace();
            fail("Exception caught while trying to get/set name");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-06")
    public void testLSF_Controller_v1_06_LampDetails() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // GetLampDetails
            GetLampDetailsValues detailsValues = lampIface.GetLampDetails(lampID);
            assertEquals("Error occurred in getting lamp details", 0, detailsValues.responseCode);
            Type type = null;
            for (String key : detailsValues.lampDetails.keySet())
            {
                logger.info(key + " : " + detailsValues.lampDetails.get(key).getObject(type).toString());
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to get/set name");
            e.printStackTrace();
            fail("Exception caught while trying to get/set name");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-07")
    public void testLSF_Controller_v1_07_LampParameters() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // get all lamp parameters
            GetLampParametersValues paramValues = lampIface.GetLampParameters(lampID);
            assertEquals("Error occurred in getting lamp parameters", 0, paramValues.responseCode);
            Type type = null;
            for (String key : paramValues.lampParameters.keySet())
            {
                logger.info("Checking " + key + " parameter");
                String value = paramValues.lampParameters.get(key).getObject(type).toString();

                // get field value
                GetLampParametersFieldValues paramFieldValues = lampIface.GetLampParametersField(lampID, key);
                assertEquals("Error occurred in getting " + key + " lamp parameter", 0, paramFieldValues.responseCode);
                String fieldValue = paramFieldValues.lampParameterFieldValue.getObject(type).toString();
                // verify against value from map
                assertEquals(value, fieldValue);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to get lamp parameters");
            e.printStackTrace();
            fail("Exception caught while trying to get lamp parameters");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-08")
    public void testLSF_Controller_v1_08_LampStateFields() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // get all lamp parameters
            GetLampStateValues stateValues = lampIface.GetLampState(lampID);
            assertEquals("Error occurred in getting lamp state", 0, stateValues.responseCode);
            Type type = null;
            for (String key : stateValues.lampState.keySet())
            {
                logger.info("Checking " + key + " state");
                String value = stateValues.lampState.get(key).getObject(type).toString();
                logger.info("Value: " + value);
                // get field value
                GetLampStateFieldValues stateFieldValues = lampIface.GetLampStateField(lampID, key);
                assertEquals("Error occurred in getting " + key + " lamp state", 0, stateFieldValues.responseCode);
                String fieldValue = stateFieldValues.lampStateFieldValue.getObject(type).toString();
                // verify against value from map
                assertEquals(value, fieldValue);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to get lamp parameters");
            e.printStackTrace();
            fail("Exception caught while trying to get lamp parameters");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-09")
    public void testLSF_Controller_v1_09_LampStateTransition() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            logger.info("Testing TransitionLampState()");
            // create new lamp state
            Map<String, Variant> lampState = newLampState(true, 147483648L, 739688812, 2061584302, 384286547);

            // change lamp state
            TransitionLampStateValues transitionValues = lampIface.TransitionLampState(lampID, lampState, 0);
            assertEquals("Error occurred in TransitionLampState", 0, transitionValues.responseCode);
            Thread.sleep(1000); // wait before checking signal is received
            boolean signalReceived = signalListener.didLampStateChanged();
            assertTrue("Signal for LampStateChanged was not received", signalReceived);
            signalListener.reset();

            logger.info("Testing TransitionFieldValues()");
            // change a single lamp field
            lampState.put("OnOff", new Variant(false, "b"));
            TransitionLampStateFieldValues transitionFieldValues =
                lampIface.TransitionLampStateField(lampID, "OnOff", lampState.get("OnOff"), 0);
            assertEquals("Error occurred in TransitionLampStateField", 0, transitionFieldValues.responseCode);
            Thread.sleep(1000); // wait before checking signal is received
            signalReceived = signalListener.didLampStateChanged();
            assertTrue("Signal for LampStateChanged was not received", signalReceived);

            logger.info("Verify state change is persistent");
            GetLampStateValues lampStateValues = lampIface.GetLampState(lampID);
            Type type = null;
            for (String key : lampState.keySet())
            {
                logger.info("Checking " + key);
                assertEquals("Unexpected lamp state value for " + key,
                            lampState.get(key).getObject(type).toString(),
                            lampStateValues.lampState.get(key).getObject(type).toString());
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to transition lamp state");
            e.printStackTrace();
            fail("Exception caught while trying to transition lamp state");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-10")
    public void testLSF_Controller_v1_10_LampStatePulse() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // toState and fromState using arbitrary colors
            Map<String, Variant> toState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            Map<String, Variant> fromState = newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // do pulse
            PulseLampWithStateValues stateValues = lampIface.PulseLampWithState(lampID, fromState, toState, 1000, 500, 5);
            assertEquals("Error occurred in PulseLampWithState", 0, stateValues.responseCode);
            Thread.sleep(5000); // allow pulse to finish
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to PulseLampWithState");
            e.printStackTrace();
            fail("Exception caught while trying to PulseLampWithState");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-11")
    public void testLSF_Controller_v1_11_LampStatePresets() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // toState and fromState using arbitrary colors
            Map<String, Variant> toState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            Map<String, Variant> fromState = newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // create 2 presets
            PresetValues createResponse = presetIface.CreatePreset(toState, "presetA", "en");
            String toPresetID = createResponse.presetID;
            createResponse = presetIface.CreatePreset(fromState, "presetB", "en");
            String fromPresetID = createResponse.presetID;

            lampIface.TransitionLampStateToPreset(lampID, toPresetID, 0);
            lampIface.PulseLampWithPreset(lampID, fromPresetID, toPresetID, 1000, 500, 5);
            Thread.sleep(5000);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Pulse/Transition state with preset");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-12")
    public void testLSF_Controller_v1_12_LampReset() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();

            // reset all and get state
            lampIface.ResetLampState(lampID);
            GetLampStateValues resetStateValues = lampIface.GetLampState(lampID);

            // set state
            Map<String, Variant> lampState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            lampIface.TransitionLampState(lampID, lampState, 0);

            // reset 1 field and get state
            String fieldName = "Brightness";
            lampIface.ResetLampStateField(lampID, fieldName);
            GetLampStateValues newStateValues = lampIface.GetLampState(lampID);

            // consistency check
            Type type = null;
            assertEquals(resetStateValues.lampState.get(fieldName).getObject(type).toString(),
                newStateValues.lampState.get(fieldName).getObject(type).toString());
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to ResetLampState");
            e.printStackTrace();
            fail("Exception caught while trying to ResetLampState");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-13")
    public void testLSF_Controller_v1_13_LampFaults() throws Exception
    {
        // make sure you can call GetLamp faults and ClearLamp faults without
        // any bus exceptions
        try
        {
            String lampID = getConnectedLamp();

            // get faults
            GetLampFaultsValues faultValues = lampIface.GetLampFaults(lampID);
            for (int fault : faultValues.lampFaults)
            {
                logger.info("clearing fault " + fault);
                lampIface.ClearLampFault(lampID, fault);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get/Clear LampFaults");
            e.printStackTrace();
            fail("Exception caught while trying to Get/Clear LampFaults");
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-14")
    public void testLSF_Controller_v1_14_LampGroupCRUD() throws Exception
    {
        try
        {
            logger.info("Test CreateLampGroup");
            // create group
            String lampID = getConnectedLamp();
            String[] lampIDs = { lampID };
            String[] lampGroupIDs = new String[0]; // intentionally empty
            String lampGroupName = "ControllerTestGroup" + new Random().nextInt(31337);
            CreateLampGroupValues createResponse = lampGroupIface.CreateLampGroup(lampIDs, lampGroupIDs, lampGroupName, "en");
            // get group
            String groupID = createResponse.lampGroupID;
            GetLampGroupValues getResponse = lampGroupIface.GetLampGroup(groupID);
            assertEquals("Incorrect number of lamps in group", lampIDs.length, getResponse.lampIDs.length);
            assertEquals("Expected lamp not found in the group", lampID, getResponse.lampIDs[0]);

            // signal check -- LampGroupsCreated
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didLampGroupsCreated();
            assertTrue("Did not receive signal LampGroupsCreated", signalReceived);

            logger.info("Test UpdateLampGroup");
            // update by removing lamp in group
            String[] newLampIDs = { lampID, lampID };
            UpdateLampGroupValues updateResponse = lampGroupIface.UpdateLampGroup(groupID, newLampIDs, lampGroupIDs);
            // there should still only be 1 lamp as we updated with duplicate lampIDs
            getResponse = lampGroupIface.GetLampGroup(groupID);
            assertEquals("Incorrect number of lamps in group", 1, getResponse.lampIDs.length);

            // signal check -- LampGroupsUpdated
            Thread.sleep(500); // wait for signal
            signalReceived = signalListener.didLampGroupsUpdated();
            assertTrue("Did not receive signal LampGroupsUpdated", signalReceived);

            logger.info("Test DeleteLampGroup()");
            GetAllLampGroupIDsValues getAllResponse = lampGroupIface.GetAllLampGroupIDs();
            int numGroupsPrior = getAllResponse.lampGroupIDs.length;
            // delete
            Values deleteResponse = lampGroupIface.DeleteLampGroup(groupID);
            // get all groups == numGroupsPrior - 1
            getAllResponse = lampGroupIface.GetAllLampGroupIDs();
            assertEquals("Incorrect number of groups", numGroupsPrior - 1, getAllResponse.lampGroupIDs.length);

            // signal check -- LampGroupsDeleted
            Thread.sleep(500); // wait for signal
            signalReceived = signalListener.didLampGroupsDeleted();
            assertTrue("Did not receive signal LampGroupsDeleted", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while testing LampGroup methods");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-15")
    public void testLSF_Controller_v1_15_LampGroupName() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();
            String groupID = createGroup();

            // set new name
            String newLampGroupName = lampGroupIface.GetLampGroupName(groupID, "en") + "X";
            SetLampGroupNameValues setNameResponse = lampGroupIface.SetLampGroupName(groupID, newLampGroupName, "en");

            // signal check -- LampGroupsNameChanged
            Thread.sleep(500);
            boolean signalReceived = signalListener.didLampGroupsNameChanged();
            assertTrue("Did not receive signal LamGroupsNameChanged", signalReceived);

            // get name and verify
            GetLampGroupNameValues getNameResponse = lampGroupIface.GetLampGroupName(groupID, "en");
            assertEquals(newLampGroupName, getNameResponse.lampGroupName);
        }
        catch (Exception e)
        {
            logger.info("Exveption caught while testing Set/GetLampGroupName");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-16")
    public void testLSF_Controller_v1_16_LampGroupStateTransition() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();
            String groupID = createGroup();

            logger.info("Testing TransitionLampGroupState()");
            // create new lamp state
            Map<String, Variant> lampState = newLampState(true, 147483648L, 739688812, 2061584302, 384286547);

            // change lamp state
            TransitionLampGroupStateValues transitionResponse = lampGroupIface.TransitionLampGroupState(groupID, lampState, 0);

            logger.info("Testing TransitionLampGroupFieldValues()");
            // change a single field
            lampState.put("OnOff", new Variant(false, "b"));
            lampGroupIface.TransitionLampGroupStateField(groupID, "OnOff", lampState.get("OnOff"), 0);

            // check lamp to see if state change occurred
            logger.info("Verify state change occured");
            GetLampStateValues lampStateValues = lampIface.GetLampState(lampID);
            Type type = null;
            for (String key : lampState.keySet())
            {
                logger.info("Checking " + key);
                assertEquals("Unexpected lamp state value for " + key,
                lampState.get(key).getObject(type).toString(),
                lampStateValues.lampState.get(key).getObject(type).toString());
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to transition lamp group state");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-17")
    public void testLSF_Controller_v1_17_LampGroupStatePulse() throws Exception
    {
        try
        {
            String groupID = createGroup();

            // toState and fromState using arbitrary colors
            Map<String, Variant> toState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            Map<String, Variant> fromState = newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // do pulse
            Values pulseResponse = lampGroupIface.PulseLampGroupWithState(groupID, fromState, toState, 1000, 500, 5);
            assertEquals("Error occurred in PulseLampGroupWithState", 0, pulseResponse.responseCode);
            Thread.sleep(5000); // allow pulse to finish
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to PulseLampGroupWithState");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-18")
    public void testLSF_Controller_v1_18_LampGroupReset() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();
            String groupID = createGroup();

            // reset all and get state
            lampGroupIface.ResetLampGroupState(groupID);
            // get state of a lamp that is part of the group
            GetLampStateValues resetStateValues = lampIface.GetLampState(lampID);

            // set state
            Map<String, Variant> lampState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            lampGroupIface.TransitionLampGroupState(lampID, lampState, 0);

            // reset 1 field and get state
            String fieldName = "Brightness";
            lampGroupIface.ResetLampGroupStateField(groupID, fieldName);
            GetLampStateValues newStateValues = lampIface.GetLampState(lampID);

            // consistency check
            Type type = null;
            assertEquals(resetStateValues.lampState.get(fieldName).getObject(type).toString(),
            newStateValues.lampState.get(fieldName).getObject(type).toString());
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to ResetLampGroupState");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-19")
    public void testLSF_Controller_v1_19_LampGroupStatePresets() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();
            String groupID = createGroup();

            // toState and fromState using arbitrary colors
            Map<String, Variant> toState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            Map<String, Variant> fromState = newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // create 2 presets
            PresetValues createResponse = presetIface.CreatePreset(toState, "presetA", "en");
            String toPresetID = createResponse.presetID;
            createResponse = presetIface.CreatePreset(fromState, "presetB", "en");
            String fromPresetID = createResponse.presetID;

            lampGroupIface.TransitionLampGroupStateToPreset(groupID, toPresetID, 0);
            lampGroupIface.PulseLampGroupWithPreset(groupID, fromPresetID, toPresetID, 1000, 500, 5);
            Thread.sleep(5000);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Pulse/Transition state with preset");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-20")
    public void testLSF_Controller_v1_20_DefaultLampState() throws Exception
    {
        try
        {
            // set default lamp state
            Map<String, Variant> defaultState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            presetIface.SetDefaultLampState(defaultState);

            // signal check -- DefaultLampStateChanged
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didDefaultLampStateChanged();
            assertTrue("Did not receive signal DefaultLampStateChanged", signalReceived);

            // get default lamp == newDefaultState
            GetDefaultLampStateValues getDefaultStateResponse = presetIface.GetDefaultLampState();
            Type type = null;
            for (String key : defaultState.keySet())
            {
                logger.debug("Comparing key " + key);
                String defaultStateVal = defaultState.get(key).getObject(type).toString();
                String responseStateVal = getDefaultStateResponse.lampState.get(key).getObject(type).toString();
                assertEquals(defaultStateVal, responseStateVal);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to modify default lamp state");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-21")
    public void testLSF_Controller_v1_21_PresetCRUD() throws Exception
    {
        try
        {
            // create preset
            Map<String, Variant> presetState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            String presetName = "ControllerTest Preset";
            PresetValues createResponse = presetIface.CreatePreset(presetState, presetName, "en");
            String presetID = createResponse.presetID;

            // signal check -- PresetsCreated
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didPresetsCreated();
            assertTrue("Did not receive signal PresetsCreated", signalReceived);

            // get preset
            GetPresetValues getResponse = presetIface.GetPreset(presetID);
            Type type = null;
            for (String key : presetState.keySet())
            {
                logger.debug("Compare key " + key);
                String expectedStateVal = presetState.get(key).getObject(type).toString();
                String stateVal = getResponse.lampState.get(key).getObject(type).toString();
                assertEquals(expectedStateVal, stateVal);
            }

            // update preset
            presetState.put("OnOff", new Variant(false, "b")); // change preset, then send back to controller
            presetIface.UpdatePreset(presetID, presetState);

            // signal check -- PresetsUpdated
            Thread.sleep(500); // wait for signal
            signalReceived = signalListener.didPresetsUpdated();
            assertTrue("Did not receive signal PresetsUpdated", signalReceived);

            // get all presets == 1
            GetAllPresetIDsValues getAllResponse = presetIface.GetAllPresetIDs();
            assertEquals(1, getAllResponse.presetIDs.length);

            // delete preset
            presetIface.DeletePreset(presetID);

            // signal check -- PresetsDeleted
            Thread.sleep(500); // wait for signal
            signalReceived = signalListener.didPresetsDeleted();
            assertTrue("Did not receive signal PresetsDeleted", signalReceived);

            // get all presets == 0
            getAllResponse = presetIface.GetAllPresetIDs();
            assertEquals(0, getAllResponse.presetIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to running preset CRUD operations");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-22")
    public void testLSF_Controller_v1_22_PresetNameChange() throws Exception
    {
        try
        {
            // create preset
            Map<String, Variant> presetState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            String presetName = "ControllerTest Preset";
            PresetValues createResponse = presetIface.CreatePreset(presetState, presetName, "en");
            String presetID = createResponse.presetID;

            // set name (change to new name)
            String newPresetName = presetName + new Random().nextInt(31337);
            presetIface.SetPresetName(presetID, newPresetName, "en");

            // signal check -- PresetsNameChanged
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didPresetsNameChanged();
            assertTrue("Did not receive signal PresetsNameChanged", signalReceived);

            // get name == new name
            GetPresetNameValues getNameResponse = presetIface.GetPresetName(presetID, "en");
            assertEquals(newPresetName, getNameResponse.presetName);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Set/GetPresetNameChange");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-23")
    public void testLSF_Controller_v1_23_SceneCreate() throws Exception
    {
        try
        {
            String transitionSceneID = createTransitionScene();
            String pulseSceneID = createPulseScene();

            // signal check -- ScenesCreated
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didScenesCreated();
            assertTrue("Did not receive signal ScenesCreated", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Create Scenes");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-24")
    public void testLSF_Controller_v1_24_SceneUpdateDelete() throws Exception
    {
        try
        {
            String pulseSceneID = createPulseScene();
            GetSceneValues getSceneResponse = sceneIface.GetScene(pulseSceneID);

            // change scene element
            getSceneResponse.pulselampsLampGroupsWithState[0].fromState =
                newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // do update
            sceneIface.UpdateScene(pulseSceneID, getSceneResponse.transitionlampsLampGroupsToState,
                getSceneResponse.transitionlampsLampGroupsToPreset, getSceneResponse.pulselampsLampGroupsWithState,
                getSceneResponse.pulselampsLampGroupsWithPreset);
            // signal check -- ScenesUpdated
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didScenesUpdated();
            assertTrue("Did not receive signal ScenesUpdated", signalReceived);

            // do delete
            sceneIface.DeleteScene(pulseSceneID);
            // signal check -- ScenesDeleted
            Thread.sleep(500); // wait for signal
            signalReceived = signalListener.didScenesDeleted();
            assertTrue("Did not receive signal ScenesDeleted", signalReceived);

            // get all scenes == 0
            GetAllSceneIDsValues getAllScenesResponse = sceneIface.GetAllSceneIDs();
            assertEquals(0, getAllScenesResponse.sceneIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Update or Delete Scenes");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-25")
    public void testLSF_Controller_v1_25_SceneApply() throws Exception
    {
        try
        {
            String pulseSceneID = createPulseScene();
            sceneIface.ApplyScene(pulseSceneID);
            Thread.sleep(5000); // allow for scene to finish

            // signal check -- ScenesApplied
            boolean signalReceived = signalListener.didScenesApplied();
            assertTrue("Did not receive signal ScenesApplied", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply Scene");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-26")
    public void testLSF_Controller_v1_26_SceneNameChanged() throws Exception
    {
        try
        {
            String pulseSceneID = createPulseScene();
            String sceneName = "ControllerPulse" + new Random().nextInt(31337);

            sceneIface.SetSceneName(pulseSceneID, sceneName, "en");

            // signal check -- ScenesNameChanged
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didScenesNameChanged();
            assertTrue("Did not receive signal ScenesNameChanged", signalReceived);

            GetSceneNameValues getNameResponse = sceneIface.GetSceneName(pulseSceneID, "en");

            assertEquals(sceneName, getNameResponse.sceneName);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get/SetSceneName");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-27")
    public void testLSF_Controller_v1_27_MasterSceneCreate() throws Exception
    {
        try
        {
            String masterSceneID = createMasterScene();

            // signal check -- MasterScenesCreated
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didMasterScenesCreated();
            assertTrue("Did not receive signal MasterScenesCreated", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create MasterScene");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-28")
    public void testLSF_Controller_v1_28_MasterSceneUpdateDelete() throws Exception
    {
        try
        {
            String masterSceneID = createMasterScene();
            GetMasterSceneValues getResponse = masterSceneIface.GetMasterScene(masterSceneID);

            // do update
            String transitionSceneID = createTransitionScene();
            String[] newScenes = { transitionSceneID };
            masterSceneIface.UpdateMasterScene(masterSceneID, newScenes);
            // signal check -- MasterScenesUpdated
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didMasterScenesUpdated();
            assertTrue("Did not receive signal MasterScenesUpdated", signalReceived);

            // do delete
            masterSceneIface.DeleteMasterScene(masterSceneID);
            // signal check -- MasterScenesDeleted
            Thread.sleep(500); // wait for signal
            signalReceived = signalListener.didMasterScenesDeleted();
            assertTrue("Did not receive signal MasterScenesDeleted", signalReceived);

            // get all master scenes == 0
            GetAllMasterSceneIDsValues getAllResponse = masterSceneIface.GetAllMasterSceneIDs();
            assertEquals(0, getAllResponse.masterSceneIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Update or Delete a MasterScene");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-29")
    public void testLSF_Controller_v1_29_MasterSceneApply() throws Exception
    {
        try
        {
            String masterSceneID = createMasterScene();
            masterSceneIface.ApplyMasterScene(masterSceneID);
            Thread.sleep(5000); // allow for scene to finish

            // signal check -- MasterScenesApplied
            boolean signalReceived = signalListener.didMasterScenesApplied();
            assertTrue("Did not receive signal MasterScenesApplied", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply MasterScene");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-30")
    public void testLSF_Controller_v1_30_MasterSceneNameChanged() throws Exception
    {
        try
        {
            String masterSceneID = createMasterScene();
            String masterSceneName = "ControllerMasterScene" + new Random().nextInt(31337);

            masterSceneIface.SetMasterSceneName(masterSceneID, masterSceneName, "en");

            // signal check -- MasterScenesNameChanged
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didMasterScenesNameChanged();
            assertTrue("Did not receive signal MasterScenesNameChanged", signalReceived);

            GetMasterSceneNameValues getNameResponse = masterSceneIface.GetMasterSceneName(masterSceneID, "en");

            assertEquals(masterSceneName, getNameResponse.masterSceneName);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get/SetMasterSceneName");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-31")
    public void testLSF_Controller_v1_31_LeaderElectionBlobs() throws Exception
    {
        try
        {
            ChecksumAndTimestampValues[] checksumTimestampResponse =
                leaderElectionIface.GetChecksumAndModificationTimestamp();
            for (ChecksumAndTimestampValues val : checksumTimestampResponse)
            {
                logger.debug("blobType: " + val.blobType);
                BlobValues blobResponse = leaderElectionIface.GetBlob(val.blobType);
                assertEquals(val.checksum, blobResponse.checksum);
                logger.debug("timestamp: " + blobResponse.timestamp);
                logger.debug("blob: " + blobResponse.blob);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to GetBlob data");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-32")
    public void testLSF_Controller_v1_32_LeaderElectionBlobChanged() throws Exception
    {
        try
        {
            createPulseScene();
            // signal check -- ScenesNameChanged
            Thread.sleep(500); // wait for signal
            boolean signalReceived = signalListener.didBlobChanged();
            assertTrue("Did not receive signal BlobChanged", signalReceived);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while testing BlobChanged");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @ValidationTest(name = "LSF_Controller-v1-33")
    public void testLSF_Controller_v1_33_LeaderElectionOverthrow() throws Exception
    {
        try
        {
            boolean overthrown = leaderElectionIface.Overthrow();
            logger.debug("overthrown:" + overthrown);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to do LeaderElection Overthrow");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**************************************************************************
     * Utility Methods
     **************************************************************************/

    // Used in LSF_Controller-v1-01
    private void verifyInterfacesFromAnnouncement() throws Exception
    {
        Set<String> interfacesSeen = new HashSet<String>();
        ControllerInterfaceValidator ciValidator = ControllerInterfaceValidator.getValidator();
        BusIntrospector introspector = serviceHelper.getBusIntrospector();

        // Validate interfaces from about announcement
        for (BusObjectDescription description : deviceAboutAnnouncement.getObjectDescriptions())
        {
            if (description.getPath().equals(CONTROLLER_BUS_OBJECT_PATH))
            {
                for (String iface : description.interfaces)
                {
                    logger.debug("Attempting to validate " + iface);
                    interfacesSeen.add(iface);
                    List<InterfaceDetail> ifaceDetails = introspector.getInterfacesExposedOnBusBasedOnName(iface);
                    ValidationResult result = ciValidator.validate(ifaceDetails);
                    if (!result.isValid())
                    {
                        fail(result.getFailureReason());
                    }
                    else
                    {
                        logger.debug("Interface " + iface + " is valid");
                    }
                }
            }
        }

        // Ensure all expected interfaces were found that were supposed to be
        List<String> expectedInterfaces = Arrays.asList(CONTROLLERSERVICE_INTERFACE_NAME, LAMP_INTERFACE_NAME,
                                                        LAMPGROUP_INTERFACE_NAME, PRESET_INTERFACE_NAME,
                                                        SCENE_INTERFACE_NAME, MASTERSCENE_INTERFACE_NAME);
        for (String iface : expectedInterfaces)
        {
            if (!interfacesSeen.contains(iface))
            {
                fail(String.format("Failed to find interface %s", iface));
            }
        }
    }

    // Used in LSF_Controller-v1-01
    private void verifyLeaderElectionAndStateSyncInterface() throws Exception
    {
        // Validate LeaderElection interface which is not announced
        BusIntrospector introspector = serviceHelper.getBusIntrospector();
        List<InterfaceDetail> ifaceDetails = introspector.getInterfacesExposedOnBusBasedOnName(LEADER_ELECTION_INTERFACE_NAME);

        if (ifaceDetails.size() == 0)
        {
            fail("Could not find the interface " + LEADER_ELECTION_INTERFACE_NAME);
        }

        ControllerInterfaceValidator ciValidator = ControllerInterfaceValidator.getValidator();
        ValidationResult result = ciValidator.validate(ifaceDetails);
        if (!result.isValid())
        {
            fail(result.getFailureReason());
        }
    }

    // Fetch any one lamp that is connected to the controller service
    private String getConnectedLamp() throws BusException
    {
        GetAllLampIDsValues values = lampIface.GetAllLampIDs();
        assertTrue("There must be at least 1 lamp connected to controller", values.lampIDs.length > 0);
        String lampID = values.lampIDs[0];
        return lampID;
    }

    // Create a lamp state map based on the input args
    private Map<String,Variant> newLampState(boolean onOff, long brightness, int hue, int saturation, int colorTemp)
    {
        Map<String, Variant> lampState = new HashMap<String, Variant>();
        lampState.put("OnOff", new Variant(onOff, "b"));
        lampState.put("Brightness", new Variant((int) brightness, "u"));
        lampState.put("Hue", new Variant(hue, "u"));
        lampState.put("Saturation", new Variant(saturation, "u"));
        lampState.put("ColorTemp", new Variant(colorTemp, "u"));

        return lampState;
    }

    // Create a group with the first lamp seen
    private String createGroup() throws Exception
    {
        // create group
        String lampID = getConnectedLamp();
        String[] lampIDs = { lampID };
        String[] lampGroupIDs = new String[0]; // intentionally empty
        String lampGroupName = "ControllerTestGroup" + new Random().nextInt(31337);
        CreateLampGroupValues createResponse = lampGroupIface.CreateLampGroup(lampIDs, lampGroupIDs, lampGroupName, "en");
        String groupID = createResponse.lampGroupID;
        return groupID;
    }

    // Create a scene with a single transition effect element.
    private String createTransitionScene() throws Exception
    {
        String lampID = getConnectedLamp();
        String lampIDs[] = { lampID };

        // create transition effect
        CreateSceneTransitionlampsLampGroupsToState transitionToState =
            new CreateSceneTransitionlampsLampGroupsToState();
        transitionToState.lampIDs = lampIDs;
        transitionToState.lampGroupIDs = new String[0];
        transitionToState.lampState = newLampState(true, 2147483648L, 1574821342, 2061584302, 384286547);
        transitionToState.transitionPeriod = 5000;

        // initialize args
        CreateSceneTransitionlampsLampGroupsToState[] transitionToStateArr = { transitionToState };
        CreateSceneTransitionlampsLampGroupsToPreset[] transitionToPresetArr = new CreateSceneTransitionlampsLampGroupsToPreset[0];
        CreateScenePulselampsLampGroupsWithState[] pulseWithStateArr = new CreateScenePulselampsLampGroupsWithState[0];
        CreateScenePulselampsLampGroupsWithPreset[] pulseWithPresetArr = new CreateScenePulselampsLampGroupsWithPreset[0];

        SceneValues createResponse = sceneIface.CreateScene(transitionToStateArr, transitionToPresetArr,
            pulseWithStateArr, pulseWithPresetArr, "ControllerTransitionScene", "en");

        return createResponse.sceneID;
    }

    // Create a scene with a single pulse effect element.
    private String createPulseScene() throws Exception
    {
        String lampID = getConnectedLamp();
        String lampIDs[] = { lampID };

        // create pulse effect
        CreateScenePulselampsLampGroupsWithState pulseWithState = new CreateScenePulselampsLampGroupsWithState();
        pulseWithState.lampIDs = lampIDs;
        pulseWithState.lampGroupIDs = new String[0];
        pulseWithState.toState = newLampState(true, 2147483648L, 1574821342, 2061584302, 384286547);
        pulseWithState.fromState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
        pulseWithState.period = 1000;
        pulseWithState.duration = 500;
        pulseWithState.numPulses = 5;

        // initialize args
        CreateSceneTransitionlampsLampGroupsToState[] transitionToStateArr = new CreateSceneTransitionlampsLampGroupsToState[0];
        CreateSceneTransitionlampsLampGroupsToPreset[] transitionToPresetArr = new CreateSceneTransitionlampsLampGroupsToPreset[0];
        CreateScenePulselampsLampGroupsWithState[] pulseWithStateArr = { pulseWithState };
        CreateScenePulselampsLampGroupsWithPreset[] pulseWithPresetArr = new CreateScenePulselampsLampGroupsWithPreset[0];

        SceneValues createResponse = sceneIface.CreateScene(transitionToStateArr, transitionToPresetArr,
            pulseWithStateArr, pulseWithPresetArr, "ControllerPulseScene", "en");

        return createResponse.sceneID;
    }

    // Create a master scene which contains a single scene within it
    private String createMasterScene() throws Exception
    {
        String pulseSceneID = createPulseScene();
        String[] scenes = { pulseSceneID };

        MasterSceneValues createResponse =
            masterSceneIface.CreateMasterScene(scenes, "ControllerTestMS", "en");

        return createResponse.masterSceneID;
    }
}