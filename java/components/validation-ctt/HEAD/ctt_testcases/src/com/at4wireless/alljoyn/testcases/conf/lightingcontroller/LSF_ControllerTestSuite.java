/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.testcases.conf.lightingcontroller;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerInterfaceValidator;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceDataSetBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceDataSetBusInterface.GetLampDataSetValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceDataSetBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceHelper;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetAllLampIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampDetailsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampFaultsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampManufacturerValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampParametersFieldValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampParametersValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampStateFieldValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampStateValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.GetLampSupportedLanguagesValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.PulseLampWithStateValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.SetLampNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.TransitionLampStateFieldValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface.TransitionLampStateValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.CreateLampGroupValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.GetAllLampGroupIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.GetLampGroupNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.GetLampGroupValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.SetLampGroupNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.TransitionLampGroupStateValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.UpdateLampGroupValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusInterface.Values;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampGroupSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface.GetAllMasterSceneIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface.GetMasterSceneNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface.GetMasterSceneValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface.MasterSceneValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface.GetAllPresetIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface.GetDefaultLampStateValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface.GetPresetNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface.GetPresetValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusInterface.PresetValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePresetSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectBusInterface.GetAllPulseEffectIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectBusInterface.GetPulseEffectNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectBusInterface.GetPulseEffectValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectBusInterface.PulseEffectValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServicePulseEffectSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSWSEBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSWSEBusInterface.GetSceneWithSceneElementsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSWSEBusInterface.SceneWithSceneElementsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSWSEBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateScenePulselampsLampGroupsWithPreset;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateScenePulselampsLampGroupsWithState;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateSceneTransitionlampsLampGroupsToPreset;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateSceneTransitionlampsLampGroupsToState;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.GetAllSceneIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.GetSceneNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.GetSceneValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.SceneValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementBusInterface.GetAllSceneElementIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementBusInterface.GetSceneElementNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementBusInterface.GetSceneElementValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementBusInterface.SceneElementValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneElementSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSignalListener;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectBusInterface.GetAllTransitionEffectIDsValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectBusInterface.GetTransitionEffectNameValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectBusInterface.GetTransitionEffectValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectBusInterface.TransitionEffectValues;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceTransitionEffectSignalHandler;
import com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface;
import com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface.BlobValues;
import com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusInterface.ChecksumAndTimestampValues;
import com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionBusObject;
import com.at4wireless.alljoyn.core.lightingcontroller.LeaderElectionSignalHandler;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

public class LSF_ControllerTestSuite
{
	// Logging constants
	private static final Logger logger = new WindowsLoggerImpl(LSF_ControllerTestSuite.class.getSimpleName());

	// AJ testing constants
	private static final String BUS_APPLICATION_NAME = "ControllerTestSuite";
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 30;
	private long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

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
	private static final String TRANSITION_EFFECT_INTERFACE_NAME            = "org.allseen.LSF.ControllerService.TransitionEffect";
    private static final String PULSE_EFFECT_INTERFACE_NAME                 = "org.allseen.LSF.ControllerService.PulseEffect";
    private static final String SCENE_WITH_SCENE_ELEMENTS_INTERFACE_NAME    = "org.allseen.LSF.ControllerService.SceneWithSceneElements";
    private static final String SCENE_ELEMENT_INTERFACE_NAME                = "org.allseen.LSF.ControllerService.SceneElement";
    private static final String DATA_SET_INTERFACE_NAME                     = "org.allseen.LSF.ControllerService.DataSet";
	
	// Ivars
    private ControllerServiceHelper serviceHelper;
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    //private AppUnderTestDetails appUnderTestDetails; //[AT4] Not needed
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
    
    // Object for org.allseen.LSF.ControllerService.TransitionEffect
    private ControllerServiceTransitionEffectBusObject transitionEffectBusObject;
    private ControllerServiceTransitionEffectSignalHandler transitionEffectSignalHandler;
    private ControllerServiceTransitionEffectBusInterface transitionEffectIface;

    // Object for org.allseen.LSF.ControllerService.PulseEffect
    private ControllerServicePulseEffectBusObject pulseEffectBusObject;
    private ControllerServicePulseEffectSignalHandler pulseEffectSignalHandler;
    private ControllerServicePulseEffectBusInterface pulseEffectIface;

    // Object for org.allseen.LSF.ControllerService.SceneElement
    private ControllerServiceSceneElementBusObject sceneElementBusObject;
    private ControllerServiceSceneElementSignalHandler sceneElementSignalHandler;
    private ControllerServiceSceneElementBusInterface sceneElementIface;

    // Object for org.allseen.LSF.ControllerService.SceneWithSceneElements
    private ControllerServiceSWSEBusObject swseBusObject;
    private ControllerServiceSWSEBusInterface swseIface;

    //Object for org.allseen.LSF.ControllerService.DataSet
    private ControllerServiceDataSetBusObject dataSetBusObject;
    private ControllerServiceDataSetBusInterface dataSetIface;
    
	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass	stores the final verdict of the test case
	 * ics	map that stores ICS values	
	 * ixit	map that stores IXIT values
	 * 
	 * */
	boolean pass = true;
	boolean inconc = false;
	private Ics icsList;
	private Ixit ixitList;

	public LSF_ControllerTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
	{
		this.icsList = icsList;
		this.ixitList = ixitList;

		ANNOUNCEMENT_TIMEOUT_IN_SECONDS   = gpList.GPCO_AnnouncementTimeout;
		SESSION_CLOSE_TIMEOUT_IN_SECONDS  = gpList.GPLC_SessionClose;

		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			inconc = true;
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		setUp();
		
		try
		{
			logger.info("Running testcase: %s", testCase);
	
			if (testCase.equals("LSF_Controller-v1-01"))
			{
				testLSF_Controller_v1_01_StandardizedInterfacesMatchDefinitions();
			}
			else if (testCase.equals("LSF_Controller-v1-02"))
			{
				testLSF_Controller_v1_02_VersionField();
			}
			else if (testCase.equals("LSF_Controller-v1-03"))
			{
				testLSF_Controller_v1_03_LightingReset();
			}
			else if (testCase.equals("LSF_Controller-v1-04"))
			{
				testLSF_Controller_v1_04_LampInfo();
			}
			else if (testCase.equals("LSF_Controller-v1-05"))
			{
				testLSF_Controller_v1_05_LampName();
			}
			else if (testCase.equals("LSF_Controller-v1-06"))
			{
				testLSF_Controller_v1_06_LampDetails();
			}
			else if (testCase.equals("LSF_Controller-v1-07"))
			{
				testLSF_Controller_v1_07_LampParameters();
			}
			else if (testCase.equals("LSF_Controller-v1-08"))
			{
				testLSF_Controller_v1_08_LampStateFields();
			}
			else if (testCase.equals("LSF_Controller-v1-09"))
			{
				testLSF_Controller_v1_09_LampStateTransition();
			}
			else if (testCase.equals("LSF_Controller-v1-10"))
			{
				testLSF_Controller_v1_10_LampStatePulse();
			}
			else if (testCase.equals("LSF_Controller-v1-11"))
			{
				testLSF_Controller_v1_11_LampStatePresets();
			}
			else if (testCase.equals("LSF_Controller-v1-12"))
			{
				testLSF_Controller_v1_12_LampReset();
			}
			else if (testCase.equals("LSF_Controller-v1-13"))
			{
				testLSF_Controller_v1_13_LampFaults();
			}
			else if (testCase.equals("LSF_Controller-v1-14"))
			{
				testLSF_Controller_v1_14_LampGroupCRUD();
			}
			else if (testCase.equals("LSF_Controller-v1-15"))
			{
				testLSF_Controller_v1_15_LampGroupName();
			}
			else if (testCase.equals("LSF_Controller-v1-16"))
			{
				testLSF_Controller_v1_16_LampGroupStateTransition();
			}
			else if (testCase.equals("LSF_Controller-v1-17"))
			{
				testLSF_Controller_v1_17_LampGroupStatePulse();
			}
			else if (testCase.equals("LSF_Controller-v1-18"))
			{
				testLSF_Controller_v1_18_LampGroupReset();
			}
			else if (testCase.equals("LSF_Controller-v1-19"))
			{
				testLSF_Controller_v1_19_LampGroupStatePresets();
			}
			else if (testCase.equals("LSF_Controller-v1-20"))
			{
				testLSF_Controller_v1_20_DefaultLampState();
			}
			else if (testCase.equals("LSF_Controller-v1-21"))
			{
				testLSF_Controller_v1_21_PresetCRUD();
			}
			else if (testCase.equals("LSF_Controller-v1-22"))
			{
				testLSF_Controller_v1_22_PresetNameChange();
			}
			else if (testCase.equals("LSF_Controller-v1-23"))
			{
				testLSF_Controller_v1_23_SceneCreate();
			}
			else if (testCase.equals("LSF_Controller-v1-24"))
			{
				testLSF_Controller_v1_24_SceneUpdateDelete();
			}
			else if (testCase.equals("LSF_Controller-v1-25"))
			{
				testLSF_Controller_v1_25_SceneApply();
			}
			else if (testCase.equals("LSF_Controller-v1-26"))
			{
				testLSF_Controller_v1_26_SceneNameChanged();
			}
			else if (testCase.equals("LSF_Controller-v1-27"))
			{
				testLSF_Controller_v1_27_MasterSceneCreate();
			}
			else if (testCase.equals("LSF_Controller-v1-28"))
			{
				testLSF_Controller_v1_28_MasterSceneUpdateDelete();
			}
			else if (testCase.equals("LSF_Controller-v1-29"))
			{
				testLSF_Controller_v1_29_MasterSceneApply();
			}
			else if (testCase.equals("LSF_Controller-v1-30"))
			{
				testLSF_Controller_v1_30_MasterSceneNameChanged();
			}
			else if (testCase.equals("LSF_Controller-v1-31"))
			{
				testLSF_Controller_v1_31_LeaderElectionBlobs();
			}
			else if (testCase.equals("LSF_Controller-v1-32"))
			{
				testLSF_Controller_v1_32_LeaderElectionBlobChanged();
			}
			else if (testCase.equals("LSF_Controller-v1-33"))
			{
				testLSF_Controller_v1_33_LeaderElectionOverthrow();
			}
			else if (testCase.equals("LSF_Controller-v1-34"))
			{
				testLSF_Controller_v1_34_TransitionEffectCreateWithLampState();
			}
			else if (testCase.equals("LSF_Controller-v1-35"))
			{
				testLSF_Controller_v1_35_TransitionEffectCreateWithPreset();
			}
			else if (testCase.equals("LSF_Controller-v1-36"))
			{
				testLSF_Controller_v1_36_TransitionEffectUpdateDelete();
			}
			else if (testCase.equals("LSF_Controller-v1-37"))
			{
				testLSF_Controller_v1_37_TransitionEffectName();
			}
			else if (testCase.equals("LSF_Controller-v1-38"))
			{
				testLSF_Controller_v1_38_TransitionEffectApplyLamp();
			}
			else if (testCase.equals("LSF_Controller-v1-39"))
			{
				testLSF_Controller_v1_39_TransitionEffectApplyLampGroup();
			}
			else if (testCase.equals("LSF_Controller-v1-40"))
			{
				testLSF_Controller_v1_40_PulseEffectCreateWithLampState();
			}
			else if (testCase.equals("LSF_Controller-v1-41"))
			{
				testLSF_Controller_v1_41_PulseEffectCreateWithPreset();
			}
			else if (testCase.equals("LSF_Controller-v1-42"))
			{
				testLSF_Controller_v1_42_PulseEffectUpdateDelete();
			}
			else if (testCase.equals("LSF_Controller-v1-43"))
			{
				testLSF_Controller_v1_43_PulseEffectName();
			}
			else if (testCase.equals("LSF_Controller-v1-44"))
			{
				testLSF_Controller_v1_44_PulseEffectApplyLamp();
			}
			else if (testCase.equals("LSF_Controller-v1-45"))
			{
				testLSF_Controller_v1_45_PulseEffectApplyLampGroup();
			}
			else if (testCase.equals("LSF_Controller-v1-46"))
			{
				testLSF_Controller_v1_46_SceneElementCreate();
			}
			else if (testCase.equals("LSF_Controller-v1-47"))
			{
				testLSF_Controller_v1_47_SceneElementUpdateDelete();
			}
			else if (testCase.equals("LSF_Controller-v1-48"))
			{
				testLSF_Controller_v1_48_SceneElementName();
			}
			else if (testCase.equals("LSF_Controller-v1-49"))
			{
				testLSF_Controller_v1_49_SceneElementApply();
			}
			else if (testCase.equals("LSF_Controller-v1-50"))
			{
				testLSF_Controller_v1_50_SceneWithSceneElementsCreate();
			}
			else if (testCase.equals("LSF_Controller-v1-51"))
			{
				testLSF_Controller_v1_51_SceneWithSceneElementsUpdateDelete();
			}
			else if (testCase.equals("LSF_Controller-v1-52"))
			{
				testLSF_Controller_v1_52_SceneWithSceneElementsApply();
			}
			else if (testCase.equals("LSF_Controller-v1-53"))
			{
				testLSF_Controller_v1_53_LampDataSetGet();
			}
			else
			{
				fail("Test Case not valid");
			}
		}
		catch (Exception exception)
		{
			logger.error("Exception executing Test Case: %s", exception.getMessage()); //[AT4]
			
			try 
			{
				tearDown();
			} 
			catch (Exception newException) 
			{
				logger.error("Exception releasing resources: %s", newException.getMessage());
			}
			
			throw exception;
		}
		
		tearDown();
	}
	
    /**************************************************************************
     * Initialization / Deinitialization
     **************************************************************************/

	private void setUp() throws Exception
	{
		//super.setUp();
		logger.raw("====================================================");
		logger.info("setUp started");

		try
		{
			//appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
			//dutDeviceId = appUnderTestDetails.getDeviceId();
			dutDeviceId = ixitList.IXITCO_DeviceId;
			//dutAppId = appUnderTestDetails.getAppId();
			dutAppId = ixitList.IXITCO_AppId;

			logger.info("Running LSF_Controller test case against Device ID: " + dutDeviceId);
			logger.info("Running LSF_Controller test case against App ID: " + dutAppId);

			initServiceHelper();
			initProxyBusObjects();

			logger.info("setUp finished");
		}
		catch (Exception e)
		{
			logger.error("setUp thrown an exception: ", e);
			//releaseServiceHelper();
			tearDown();
			throw e;
		}
		logger.raw("====================================================");
	} 
	
	protected void tearDown() throws Exception
	{
		logger.raw("====================================================");
		logger.info("test tearDown started");
		controllerIface.LightingResetControllerService(); // clean up Controller
		releaseServiceHelper();
		logger.info("test tearDown done");
		logger.raw("====================================================");
	}

	private void initServiceHelper() throws BusException, Exception
	{
		releaseServiceHelper();
		serviceHelper = new ControllerServiceHelper();
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
		
		transitionEffectBusObject = new ControllerServiceTransitionEffectBusObject();
        transitionEffectSignalHandler = new ControllerServiceTransitionEffectSignalHandler();
        transitionEffectSignalHandler.setUpdateListener(signalListener);

        pulseEffectBusObject = new ControllerServicePulseEffectBusObject();
        pulseEffectSignalHandler = new ControllerServicePulseEffectSignalHandler();
        pulseEffectSignalHandler.setUpdateListener(signalListener);

        sceneElementBusObject = new ControllerServiceSceneElementBusObject();
        sceneElementSignalHandler = new ControllerServiceSceneElementSignalHandler();
        sceneElementSignalHandler.setUpdateListener(signalListener);

        swseBusObject = new ControllerServiceSWSEBusObject();

        dataSetBusObject = new ControllerServiceDataSetBusObject();

		// register bus objects and signal handlers with serviceHelper
		serviceHelper.registerBusObject(controllerServiceBusObject, CONTROLLER_BUS_OBJECT_PATH);
		serviceHelper.registerSignalHandler(controllerServiceSignalHandler);

		serviceHelper.registerBusObject(lampBusObject, CONTROLLER_BUS_OBJECT_PATH + "/Lamp");
		serviceHelper.registerSignalHandler(lampSignalHandler);

		serviceHelper.registerBusObject(lampGroupBusObject, CONTROLLER_BUS_OBJECT_PATH + "/LampGroup");
		serviceHelper.registerSignalHandler(lampGroupSignalHandler);

		serviceHelper.registerBusObject(presetBusObject, CONTROLLER_BUS_OBJECT_PATH + "/Preset");
		serviceHelper.registerSignalHandler(presetSignalHandler);

		serviceHelper.registerBusObject(sceneBusObject, CONTROLLER_BUS_OBJECT_PATH + "/Scene");
		serviceHelper.registerSignalHandler(sceneSignalHandler);

		serviceHelper.registerBusObject(masterSceneBusObject, CONTROLLER_BUS_OBJECT_PATH + "/MasterScene");
		serviceHelper.registerSignalHandler(masterSceneSignalHandler);

		serviceHelper.registerBusObject(leaderElectionBusObject, LEADER_ELECTION_BUS_OBJECT_PATH + "/LeaderElection");
		serviceHelper.registerSignalHandler(leaderElectionSignalHandler);
		
		serviceHelper.registerBusObject(transitionEffectBusObject, CONTROLLER_BUS_OBJECT_PATH + "/TransitionEffect");
        serviceHelper.registerSignalHandler(transitionEffectSignalHandler);

        serviceHelper.registerBusObject(pulseEffectBusObject, CONTROLLER_BUS_OBJECT_PATH + "/PulseEffect");
        serviceHelper.registerSignalHandler(pulseEffectSignalHandler);

        serviceHelper.registerBusObject(sceneElementBusObject, CONTROLLER_BUS_OBJECT_PATH + "/SceneElement");
        serviceHelper.registerSignalHandler(sceneElementSignalHandler);

        serviceHelper.registerBusObject(swseBusObject, CONTROLLER_BUS_OBJECT_PATH + "/SceneWithSceneElements");

        serviceHelper.registerBusObject(dataSetBusObject, CONTROLLER_BUS_OBJECT_PATH + "/DataSet");

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
		
		// org.allseen.LSF.ControllerService.TransitionEffect
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceTransitionEffectBusInterface.class });
        transitionEffectIface = proxy.getInterface(ControllerServiceTransitionEffectBusInterface.class);

        // org.allseen.LSF.ControllerService.PulseEffect
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServicePulseEffectBusInterface.class });
        pulseEffectIface = proxy.getInterface(ControllerServicePulseEffectBusInterface.class);

        // org.alseen.LSF.ControllerService.SceneElement
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceSceneElementBusInterface.class });
        sceneElementIface = proxy.getInterface(ControllerServiceSceneElementBusInterface.class);

        // org.allseen.LSF.ControllerService.SceneWithSceneElements
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceSWSEBusInterface.class });
        swseIface = proxy.getInterface(ControllerServiceSWSEBusInterface.class);

        // org.allseen.LSF.ControllerService.DataSet
        proxy = serviceHelper.getProxyBusObject(CONTROLLER_BUS_OBJECT_PATH,
            new Class[] { ControllerServiceDataSetBusInterface.class });
        dataSetIface = proxy.getInterface(ControllerServiceDataSetBusInterface.class);
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
		//ServiceAvailabilityHandler serviceAvailabilityHandler = new ServiceAvailabilityHandler();
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

	public void testLSF_Controller_v1_01_StandardizedInterfacesMatchDefinitions() throws Exception
	{
		verifyInterfacesFromAnnouncement();
		verifyLeaderElectionAndStateSyncInterface();
	}

	public void testLSF_Controller_v1_02_VersionField() throws Exception
	{
		try
		{
			// check version properties from all interfaces do not cause BusException
			int version = -1;

			version = controllerIface.getVersion();
			logger.info(CONTROLLERSERVICE_INTERFACE_NAME + ", Version:" + version);
			assertEquals("The controller Service Version does not match IXIT", version,
					ixitList.IXITLC_ControllerServiceVersion);
			
			version = lampIface.getVersion();
			logger.info(LAMP_INTERFACE_NAME + ", Version: " + version);
			assertEquals("The controller Service Lamp Version does not match IXIT", version,
					ixitList.IXITLC_ControllerServiceLampVersion);

			version = lampGroupIface.getVersion();
			logger.info(LAMPGROUP_INTERFACE_NAME + " Version: " + version);
			assertEquals("The controller Service Lamp Group Version does not match IXIT", version,
					ixitList.IXITLC_ControllerServiceLampGroupVersion);

			version = presetIface.getVersion();
			logger.info(PRESET_INTERFACE_NAME + " Version: " + version);
			assertEquals("The controller Service Preset Version does not match IXIT", version,
					ixitList.IXITLC_ControllerServicePresetVersion);

			version = sceneIface.getVersion();
			logger.info(SCENE_INTERFACE_NAME + " Version: " + version);
			assertEquals("The controller Service Scene Version does not match IXIT", version,
					ixitList.IXITLC_ControllerServiceSceneVersion);

			version = masterSceneIface.getVersion();
			logger.info(MASTERSCENE_INTERFACE_NAME + " Version: " + version);
			assertEquals("The controller Service Master Scene Version does not match IXIT", version,
					ixitList.IXITLC_ControllerServiceMasterSceneVersion);

			// verify version from method on ControllerService interface
			version = controllerIface.GetControllerServiceVersion();
			logger.info(LEADER_ELECTION_INTERFACE_NAME + " MethodCall-Version: " + version);
			assertEquals("The controller Service Leader Election Version does not match", version,
					ixitList.IXITLC_LeaderElectionAndStateSyncVersion);

		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to get version");
			e.printStackTrace();
			fail("Exception caught while trying to get version");
		}
	}

	public void testLSF_Controller_v1_03_LightingReset() throws Exception
	{
		try
		{
			int reset = controllerIface.LightingResetControllerService();
			logger.info("Reset status code: " + reset);
			Thread.sleep(500); // wait before checking that signal is received //[AT4] Should this 500 be a GP?
			boolean signalReceived = signalListener.didLightingReset();
			logger.info("Checking if LightingReset signal was received");
			assertTrue("Signal for LightingReset was not received", signalReceived);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to call LightingReset");
			e.printStackTrace();
			fail("Exception caught while trying to call LightingReset");
		}
	}

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
			logger.info("Checking if lamp names match");
			assertEquals("LampNames are not the same", lampName, getNameValues.lampName);

			Thread.sleep(5000); // wait before checking signal is received
			boolean signalReceived = signalListener.didLampNameChanged();
			logger.info("Checking if LampNameChanged signal was received");
			assertTrue("Signal for LampNameChanged was not received", signalReceived);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to get/set name");
			e.printStackTrace();
			fail("Exception caught while trying to get/set name");
		}
	}

	public void testLSF_Controller_v1_06_LampDetails() throws Exception
	{
		try
		{
			String lampID = getConnectedLamp();

			// GetLampDetails
			GetLampDetailsValues detailsValues = lampIface.GetLampDetails(lampID);
			//logger.info("Checking if an error occurred while getting lamp details");
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
				logger.info("Checking if parameters match");
				assertEquals("The lamp Parameters are not the same",value, fieldValue);
			}
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to get lamp parameters");
			e.printStackTrace();
			fail("Exception caught while trying to get lamp parameters");
		}
	}

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
				logger.info("Checking if fields match");
				assertEquals("The lamp State Field Value are not the same",value, fieldValue);
			}
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to get lamp parameters");
			e.printStackTrace();
			fail("Exception caught while trying to get lamp parameters");
		}
	}

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
			logger.info("Checking if LampStateChanged signal was received");
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
			assertEquals("The values are not the same after reset",resetStateValues.lampState.get(fieldName).getObject(type).toString(),
					newStateValues.lampState.get(fieldName).getObject(type).toString());
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to ResetLampState");
			e.printStackTrace();
			fail("Exception caught while trying to ResetLampState");
		}
	}

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
			assertEquals("The LampGroupName has not been changed",newLampGroupName, getNameResponse.lampGroupName);
		}
		catch (Exception e)
		{
			logger.info("Exveption caught while testing Set/GetLampGroupName");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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
			assertEquals("",resetStateValues.lampState.get(fieldName).getObject(type).toString(),
					newStateValues.lampState.get(fieldName).getObject(type).toString());
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to ResetLampGroupState");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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
				logger.info("Comparing key " + key);
				String defaultStateVal = defaultState.get(key).getObject(type).toString();
				String responseStateVal = getDefaultStateResponse.lampState.get(key).getObject(type).toString();
				assertEquals("",defaultStateVal, responseStateVal);
			}
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to modify default lamp state");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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
				logger.info("Compare key " + key);
				String expectedStateVal = presetState.get(key).getObject(type).toString();
				String stateVal = getResponse.lampState.get(key).getObject(type).toString();
				assertEquals("The state values are not the same",expectedStateVal, stateVal);
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
			assertEquals("The presets are not 1",1, getAllResponse.presetIDs.length);

			// delete preset
			presetIface.DeletePreset(presetID);

			// signal check -- PresetsDeleted
			Thread.sleep(500); // wait for signal
			signalReceived = signalListener.didPresetsDeleted();
			assertTrue("Did not receive signal PresetsDeleted", signalReceived);

			// get all presets == 0
			getAllResponse = presetIface.GetAllPresetIDs();
			assertEquals("The presets are not 0",0, getAllResponse.presetIDs.length);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to running preset CRUD operations");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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
			assertEquals("PresetName has not benn changed",newPresetName, getNameResponse.presetName);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to Set/GetPresetNameChange");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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
			assertEquals("Scene Update has not been deleted ",0, getAllScenesResponse.sceneIDs.length);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to Update or Delete Scenes");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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

			assertEquals("Scene Name has not been changed", sceneName, getNameResponse.sceneName);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to Get/SetSceneName");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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
			assertEquals("Master Scene Update has not been deleted",0, getAllResponse.masterSceneIDs.length);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to Update or Delete a MasterScene");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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

			assertEquals("Master Scene Name has not been changed", masterSceneName, getNameResponse.masterSceneName);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to Get/SetMasterSceneName");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testLSF_Controller_v1_31_LeaderElectionBlobs() throws Exception
	{
		try
		{
			ChecksumAndTimestampValues[] checksumTimestampResponse =
					leaderElectionIface.GetChecksumAndModificationTimestamp();
			for (ChecksumAndTimestampValues val : checksumTimestampResponse)
			{
				logger.info("blobType: " + val.blobType);
				BlobValues blobResponse = leaderElectionIface.GetBlob(val.blobType);
				assertEquals("Checksum returned from calling GetBlob() is not consistent "
						+ "with that returned by GetChecksumAndModificationTimestamp()", val.checksum, blobResponse.checksum);
				logger.info("timestamp: " + blobResponse.timestamp);
				logger.info("blob: " + blobResponse.blob);
			}
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to GetBlob data");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

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

	public void testLSF_Controller_v1_33_LeaderElectionOverthrow() throws Exception
	{
		try
		{
			boolean overthrown = leaderElectionIface.Overthrow();
			logger.info("overthrown:" + overthrown);
		}
		catch (Exception e)
		{
			logger.info("Exception caught while trying to do LeaderElection Overthrow");
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
    public void testLSF_Controller_v1_34_TransitionEffectCreateWithLampState() throws Exception
    {
        try
        {
            // create TransitionEffect using LampState
            Map<String, Variant> lampState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            TransitionEffectValues createResponse = transitionEffectIface.CreateTransitionEffect(lampState, new String(), 5000, "ControllerTransitionEffect", "en");

            String transitionEffectID = createResponse.transitionEffectID;

            // signal check -- TransitionEffectsCreated
            Thread.sleep(500);
            assertTrue("Did not receive signal TransitionEffectsCreated", signalListener.didTransitionEffectsCreated());

            // get TransitionEffect
            GetTransitionEffectValues getResponse = transitionEffectIface.GetTransitionEffect(transitionEffectID);
            Type type = null;
            for (String key : lampState.keySet())
            {
                logger.debug("Compare key " + key);
                String expectedStateVal = lampState.get(key).getObject(type).toString();
                String stateVal = getResponse.lampState.get(key).getObject(type).toString();
                assertEquals("", expectedStateVal, stateVal);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create Transition Effect with LampState");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_35_TransitionEffectCreateWithPreset() throws Exception
    {
        try
        {
            // create preset
            Map<String, Variant> presetState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
            String presetName = "ControllerTest Preset";
            PresetValues createPresetResponse = presetIface.CreatePreset(presetState, presetName, "en");
            String presetID = createPresetResponse.presetID;

            TransitionEffectValues createResponse = transitionEffectIface.CreateTransitionEffect(new HashMap<String, Variant>(), presetID, 5000, "ControllerTransitionEffect", "en");

            String transitionEffectID = createResponse.transitionEffectID;

            // signal check -- TransitionEffectCreated
            Thread.sleep(500);
            assertTrue("Did not receive signal TransitionEffectsCreated", signalListener.didTransitionEffectsCreated());

            // get TransitionEffect
            GetTransitionEffectValues getResponse = transitionEffectIface.GetTransitionEffect(transitionEffectID);
            assertEquals("", presetID, getResponse.presetID);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create Transition Effect with Preset");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_36_TransitionEffectUpdateDelete() throws Exception
    {
        try
        {
            String transitionEffectID = createTransitionEffect();
            GetTransitionEffectValues getTransitionEffectResponse = transitionEffectIface.GetTransitionEffect(transitionEffectID);

            // change the TransitionEffect
            getTransitionEffectResponse.lampState = newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // do update
            transitionEffectIface.UpdateTransitionEffect(transitionEffectID, getTransitionEffectResponse.lampState, new String(), 5000);
            // signal check -- TransitionEffectsUpdated
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal TransitionEffectsUpdated", signalListener.didTransitionEffectsUpdated());

            // do delete
            transitionEffectIface.DeleteTransitionEffect(transitionEffectID);
            // signal check -- TransitionEffectsDeleted
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal TransitionEffectsDeleted", signalListener.didTransitionEffectsDeleted());

            // get all TransitionEffect == 0
            GetAllTransitionEffectIDsValues getAllTransitionEffectsResponse = transitionEffectIface.GetAllTransitionEffectIDs();
            assertEquals("", 0, getAllTransitionEffectsResponse.transitionEffectIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Update or Delete TransitionEffects");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_37_TransitionEffectName() throws Exception
    {
        try
        {
            String transitionEffectID = createTransitionEffect();
            String transitionEffectName = "ControllerTransitionEffect" + new Random().nextInt(31337);

            transitionEffectIface.SetTransitionEffectName(transitionEffectID, transitionEffectName, "en");

            // signal check -- TransitionEffectsNameChanged
            Thread.sleep(500);
            assertTrue("Did not receive signal TransitionEffectsNameChanged", signalListener.didTransitionEffectsNameChanged());

            GetTransitionEffectNameValues getNameResponse = transitionEffectIface.GetTransitionEffectName(transitionEffectID, "en");
            assertEquals("", transitionEffectName, getNameResponse.transitionEffectName);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get/SetTransitionEffectName");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_38_TransitionEffectApplyLamp() throws Exception
    {
        try
        {
            String transitionEffectID = createTransitionEffect();
            String[] lampIDs = { getConnectedLamp() };

            transitionEffectIface.ApplyTransitionEffectOnLamps(transitionEffectID, lampIDs);
            Thread.sleep(5000); // allow for scene to finish
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply TransitionEffect on Lamp");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_39_TransitionEffectApplyLampGroup() throws Exception
    {
        try
        {
            String transitionEffectID = createTransitionEffect();
            String[] groupIDs = { createGroup() };

            transitionEffectIface.ApplyTransitionEffectOnLampGroups(transitionEffectID, groupIDs);
            Thread.sleep(5000); // allow for scene to finish
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply TransitionEffect on LampGroup");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_40_PulseEffectCreateWithLampState() throws Exception
    {
        try
        {
            // create PulseEffect using LampState
            Map<String, Variant> toState = newLampState(true, 2147483648L, 1574821342, 2061584302, 384286547);
            Map<String, Variant> fromState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);

            int period = 1000;
            int duration = 500;
            int numPulses = 5;

            PulseEffectValues createResponse = pulseEffectIface.CreatePulseEffect(toState, period, duration, numPulses, fromState, new String(), new String(), "ControllerPulseEffect", "en");
            String pulseEffectID = createResponse.pulseEffectID;

            // signal check -- PulseEffectsCreated
            Thread.sleep(500);
            assertTrue("Did not receive signal PulseEffectsCreated", signalListener.didPulseEffectsCreated());

            // get PulseEffect
            GetPulseEffectValues getResponse = pulseEffectIface.GetPulseEffect(pulseEffectID);
            Type type = null;

            // test fromState
            for (String key : fromState.keySet())
            {
                logger.debug("Compare key " + key);
                String expectedStateVal = fromState.get(key).getObject(type).toString();
                String stateVal = getResponse.fromLampState.get(key).getObject(type).toString();
                assertEquals("", expectedStateVal, stateVal);
            }

            // test toState
            for (String key : toState.keySet())
            {
                logger.debug("Compare key " + key);
                String expectedStateVal = toState.get(key).getObject(type).toString();
                String stateVal = getResponse.toLampState.get(key).getObject(type).toString();
                assertEquals("", expectedStateVal, stateVal);
            }
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create Pulse Effect with LampState");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_41_PulseEffectCreateWithPreset() throws Exception
    {
        try
        {
            // create preset
            Map<String, Variant> presetToState = newLampState(true, 2147483648L, 1574821342, 2061584302, 384286547);
            Map<String, Variant> presetFromState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);

            String toPresetName = "ControllerTest toPreset";
            PresetValues createPresetResponse = presetIface.CreatePreset(presetToState, toPresetName, "en");
            String toPresetID = createPresetResponse.presetID;

            String fromPresetName = "ControllerTest fromPreset";
            createPresetResponse = presetIface.CreatePreset(presetFromState, fromPresetName, "en");
            String fromPresetID = createPresetResponse.presetID;

            int period = 1000;
            int duration = 500;
            int numPulses = 5;

            PulseEffectValues createResponse = pulseEffectIface.CreatePulseEffect(new HashMap<String, Variant>(), period, duration, numPulses,
                new HashMap<String, Variant>(), toPresetID, fromPresetID, "ControllerPulseEffect", "en");

            String pulseEffectID = createResponse.pulseEffectID;

            // signal check -- PulseEffectsCreated
            Thread.sleep(500);
            assertTrue("Did not receive signal PulseEffectsCreated", signalListener.didPulseEffectsCreated());

            // get PulseEffect
            GetPulseEffectValues getResponse = pulseEffectIface.GetPulseEffect(pulseEffectID);
            assertEquals("", toPresetID, getResponse.toPresetID);
            assertEquals("", fromPresetID, getResponse.fromPresetID);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create Pulse Effect with Preset");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_42_PulseEffectUpdateDelete() throws Exception
    {
        try
        {
            String pulseEffectID = createPulseEffect();
            GetPulseEffectValues getPulseEffectResponse = pulseEffectIface.GetPulseEffect(pulseEffectID);

            // change the PulseEffect
            getPulseEffectResponse.toLampState = newLampState(false, 2147483648L, 1574821342, 2061584302, 384286547);
            getPulseEffectResponse.fromLampState = newLampState(false, 2147483648L, 739688812, 2061584302, 384286547);

            // do update
            pulseEffectIface.UpdatePulseEffect(pulseEffectID, getPulseEffectResponse.toLampState, getPulseEffectResponse.pulsePeriod,
                getPulseEffectResponse.pulseDuration, getPulseEffectResponse.numPulses, getPulseEffectResponse.fromLampState,
                getPulseEffectResponse.toPresetID, getPulseEffectResponse.fromPresetID);

            // signal check -- PulseEffectsUpdated
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal PulseEffectsUpdated", signalListener.didPulseEffectsUpdated());

            // do delete
            pulseEffectIface.DeletePulseEffect(pulseEffectID);

            // signal check -- PulseEffectsDeleted
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal PulseEffectsDeleted", signalListener.didPulseEffectsDeleted());

            // get all PulseEffecr == 0
            GetAllPulseEffectIDsValues getAllPulseEffectsResponse = pulseEffectIface.GetAllPulseEffectIDs();
            assertEquals("", 0, getAllPulseEffectsResponse.pulseEffectIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Update or Delete PulseEffects");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_43_PulseEffectName() throws Exception
    {
        try
        {
            String pulseEffectID = createPulseEffect();
            String pulseEffectName = "ControllerPulseEffect" + new Random().nextInt(31337);

            pulseEffectIface.SetPulseEffectName(pulseEffectID, pulseEffectName, "en");

            // signal check -- PulseEffectsNameChanged
            Thread.sleep(500);
            assertTrue("Did not receive signal PulseEffectsNameChanged", signalListener.didPulseEffectsNameChanged());

            GetPulseEffectNameValues getNameResponse = pulseEffectIface.GetPulseEffectName(pulseEffectID, "en");
            assertEquals("", pulseEffectName, getNameResponse.pulseEffectName);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get/SetPulseEffectName");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_44_PulseEffectApplyLamp() throws Exception
    {
        try
        {
            String pulseEffectID = createPulseEffect();
            String[] lampIDs = { getConnectedLamp() };

            pulseEffectIface.ApplyPulseEffectOnLamps(pulseEffectID, lampIDs);
            Thread.sleep(5000); // allow for scene to finish
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply PulseEffect on Lamp");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_45_PulseEffectApplyLampGroup() throws Exception
    {
        try
        {
            String pulseEffectID = createPulseEffect();
            String[] groupIDs = { createGroup() };
            pulseEffectIface.ApplyPulseEffectOnLampGroups(pulseEffectID, groupIDs);
            Thread.sleep(5000); // allow for scene to finish
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply PulseEffect on LampGroup");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_46_SceneElementCreate() throws Exception
    {
        try
        {
            // create SceneElement
            String effectID = createPulseEffect();
            String[] lampIDs = { getConnectedLamp() };

            SceneElementValues createResponse = sceneElementIface.CreateSceneElement(lampIDs, new String[] {}, effectID, "ControllerTestSceneElement", "en");
            String sceneElementID = createResponse.sceneElementID;

            // signal check -- SceneElementsCreated
            Thread.sleep(500);
            assertTrue("Did not receive signal SceneElementsCreated", signalListener.didSceneElementsCreated());

            // get SceneElement
            GetSceneElementValues getResponse = sceneElementIface.GetSceneElement(sceneElementID);

            assertEquals("", effectID, getResponse.effectID);
            assertEquals("", 1, getResponse.lampList.length);
            assertEquals("", lampIDs[0], getResponse.lampList[0]);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create Scene Element");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_47_SceneElementUpdateDelete() throws Exception
    {
        try
        {
            String sceneElementID = createSceneElement();
            GetSceneElementValues getSceneElementResponse = sceneElementIface.GetSceneElement(sceneElementID);

            // change the SceneElement effect
            getSceneElementResponse.effectID = createTransitionEffect();

            // do update
            sceneElementIface.UpdateSceneElement(sceneElementID, getSceneElementResponse.lampList, getSceneElementResponse.lampGroupList,
                getSceneElementResponse.effectID);

            // signal check -- SceneElementsUpdated
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal SceneElementsUpdated", signalListener.didSceneElementsUpdated());

            // do delete
            sceneElementIface.DeleteSceneElement(sceneElementID);

            // signal check -- SceneElementsDeleted
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal SceneElementsDeleted", signalListener.didSceneElementsDeleted());

            // get all SceneElement == 0
            GetAllSceneElementIDsValues getAllSceneElementResponse = sceneElementIface.GetAllSceneElementIDs();
            assertEquals("", 0, getAllSceneElementResponse.sceneElementIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Update or Delete SceneElements");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_48_SceneElementName() throws Exception
    {
        try
        {
            String sceneElementID = createSceneElement();
            String sceneElementName = "ControllerSceneElement" + new Random().nextInt(31337);

            sceneElementIface.SetSceneElementName(sceneElementID, sceneElementName, "en");

            Thread.sleep(500);
            assertTrue("Did not receive signal SceneElementsNameChanged", signalListener.didSceneElementsNameChanged());

            GetSceneElementNameValues getNameResponse = sceneElementIface.GetSceneElementName(sceneElementID, "en");
            assertEquals("", sceneElementName, getNameResponse.sceneElementName);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get/SetSceneElementName");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_49_SceneElementApply() throws Exception
    {
        try
        {
            String sceneElementID = createSceneElement();

            sceneElementIface.ApplySceneElement(sceneElementID);
            Thread.sleep(5000); // allow for scene to finish

            // signal check -- SceneElementsApplied
            assertTrue("Did not receive signal SceneElementsApplied", signalListener.didSceneElementsApplied());
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply SceneElement");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_50_SceneWithSceneElementsCreate() throws Exception
    {
        try
        {
            // create SceneElement
            String sceneElementID = createSceneElement();

            SceneWithSceneElementsValues createResponse = swseIface.CreateSceneWithSceneElements(new String[] { sceneElementID }, "ControllerTestSWSE", "en");
            String sceneID = createResponse.sceneWithSceneElementsID;

            // signal check -- ScenesCreated
            Thread.sleep(500);
            assertTrue("Did not receive signal ScenesCreated", signalListener.didScenesCreated());

            GetSceneWithSceneElementsValues getResponse = swseIface.GetSceneWithSceneElements(sceneID);

            assertEquals("", 1, getResponse.sceneElementIDs.length);
            assertEquals("", sceneElementID, getResponse.sceneElementIDs[0]);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to create SceneWithSceneElements");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_51_SceneWithSceneElementsUpdateDelete() throws Exception
    {
        try
        {
            String sceneID = createSceneWithSceneElements();
            GetSceneWithSceneElementsValues getSWSERespose = swseIface.GetSceneWithSceneElements(sceneID);

            // change the SceneWithSceneElements
            getSWSERespose.sceneElementIDs = new String[] { createSceneElement() };

            // do update
            swseIface.UpdateSceneWithSceneElements(sceneID, getSWSERespose.sceneElementIDs);

            // signal check -- ScenesUpdated
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal ScenesUpdated", signalListener.didScenesUpdated());

            // do delete
            sceneIface.DeleteScene(sceneID);

            // signal check -- ScenesDeleted
            Thread.sleep(500); // wait for signal
            assertTrue("Did not receive signal ScenesDeleted", signalListener.didScenesDeleted());

            GetAllSceneIDsValues getAllScenesResponse = sceneIface.GetAllSceneIDs();
            assertEquals("", 0, getAllScenesResponse.sceneIDs.length);
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Update or Delete SceneWithSceneElements");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_52_SceneWithSceneElementsApply() throws Exception
    {
        try
        {
            String sceneID = createSceneWithSceneElements();

            sceneIface.ApplyScene(sceneID);
            Thread.sleep(5000); // allow for scene to finish

            // signal check -- ScenesApplied
            assertTrue("Did not receive signal ScenesApplied", signalListener.didScenesApplied());
        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Apply SceneWithSceneElements");
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testLSF_Controller_v1_53_LampDataSetGet() throws Exception
    {
        try
        {
            String lampID = getConnectedLamp();
            GetLampDataSetValues dataSetResponse = dataSetIface.GetLampDataSet(lampID, "en");

            // check LampName
            logger.info("Verifying LampName");
            GetLampNameValues lampNameResponse = lampIface.GetLampName(lampID, "en");
            assertEquals("", lampNameResponse.lampName, dataSetResponse.lampName);


            // check LampDetails
            logger.info("Verifying LampDetails");
            GetLampDetailsValues lampDetailsResponse = lampIface.GetLampDetails(lampID);
            Type type = null;
            for (String key : dataSetResponse.lampDetails.keySet())
            {
                logger.info(key + " : " + lampDetailsResponse.lampDetails.get(key).getObject(type).toString());
                assertEquals("", lampDetailsResponse.lampDetails.get(key).getObject(type).toString(),
                    dataSetResponse.lampDetails.get(key).getObject(type).toString());
            }

            // check LampState
            logger.info("Verifying LampState");
            GetLampStateValues lampStateResponse = lampIface.GetLampState(lampID);
            for (String key : dataSetResponse.lampState.keySet())
            {
                logger.info(key + " : " + lampStateResponse.lampState.get(key).getObject(type).toString());
                assertEquals("", lampStateResponse.lampState.get(key).getObject(type).toString(),
                    dataSetResponse.lampState.get(key).getObject(type).toString());
            }

            // check LampParameters
            logger.info("Verifying LampParameters");
            GetLampParametersValues lampParametersResponse = lampIface.GetLampParameters(lampID);
            for (String key : dataSetResponse.lampParameters.keySet())
            {
                logger.info(key + " : " + lampParametersResponse.lampParameters.get(key).getObject(type).toString());
                assertEquals("", lampParametersResponse.lampParameters.get(key).getObject(type).toString(),
                    dataSetResponse.lampParameters.get(key).getObject(type).toString());
            }

        }
        catch (Exception e)
        {
            logger.info("Exception caught while trying to Get LampDataSet");
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
		for (AboutObjectDescription description : deviceAboutAnnouncement.getObjectDescriptions())
		{
			if (description.path.equals(CONTROLLER_BUS_OBJECT_PATH))
			{
				for (String iface : description.interfaces)
				{
					logger.info("Attempting to validate " + iface);
					interfacesSeen.add(iface);
					List<InterfaceDetail> ifaceDetails = introspector.getInterfacesExposedOnBusBasedOnName(iface);
					ValidationResult result = ciValidator.validate(ifaceDetails);
					if (!result.isValid())
					{
						fail(result.getFailureReason());
					}
					else
					{
						logger.info("Interface " + iface + " is valid");
						logger.info("Partial Verdict: PASS");
					}
				}
			}
		}

		// Ensure all expected interfaces were found that were supposed to be
		List<String> expectedInterfaces = Arrays.asList(CONTROLLERSERVICE_INTERFACE_NAME, LAMP_INTERFACE_NAME,
														LAMPGROUP_INTERFACE_NAME, PRESET_INTERFACE_NAME,
														SCENE_INTERFACE_NAME, MASTERSCENE_INTERFACE_NAME,
														TRANSITION_EFFECT_INTERFACE_NAME, PULSE_EFFECT_INTERFACE_NAME,
                                                        SCENE_WITH_SCENE_ELEMENTS_INTERFACE_NAME,
                                                        SCENE_ELEMENT_INTERFACE_NAME, DATA_SET_INTERFACE_NAME);
		for (String iface : expectedInterfaces)
		{
			if (!interfacesSeen.contains(iface))
			{
				fail(String.format("Failed to find interface %s", iface));
				inconc = true;
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
			inconc = true; //[AT4]
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
	
	// Create a transition effect (v2)
    private String createTransitionEffect() throws Exception
    {
        Map<String, Variant> lampState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);
        TransitionEffectValues createResponse = transitionEffectIface.CreateTransitionEffect(lampState, "", 5000, "ControllerTransitionEffect", "en");

        return createResponse.transitionEffectID;
    }

    // Create a pulse effect (v2)
    private String createPulseEffect() throws Exception
    {
        // create PulseEffect using LampState
        Map<String, Variant> toState = newLampState(true, 2147483648L, 1574821342, 2061584302, 384286547);
        Map<String, Variant> fromState = newLampState(true, 2147483648L, 739688812, 2061584302, 384286547);

        int period = 1000;
        int duration = 500;
        int numPulses = 5;

        PulseEffectValues createResponse = pulseEffectIface.CreatePulseEffect(toState, period, duration, numPulses, fromState, new String(), new String(), "ControllerPulseEffect", "en");

        return createResponse.pulseEffectID;
    }

    // Create a scene element
    private String createSceneElement() throws Exception
    {
        String effectID = createPulseEffect();
        String[] lampIDs = { getConnectedLamp() };

        SceneElementValues createResponse = sceneElementIface.CreateSceneElement(lampIDs, new String[] {}, effectID, "ControllerTestSceneElement", "en");
        return createResponse.sceneElementID;
    }

    // Create scene with scene elements (scene v2)
    private String createSceneWithSceneElements() throws Exception
    {
        String sceneElementID = createSceneElement();

        SceneWithSceneElementsValues createResponse = swseIface.CreateSceneWithSceneElements(new String[] { sceneElementID }, "ControllerTestSWSE", "en");
        return createResponse.sceneWithSceneElementsID;
    }

	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * 
	 * */
	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second))
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertEquals(String errorMessage, int first, int second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	/**
	 * [AT4] Added methods to manage the verdict
	 * 
	 * fail
	 * getFinalVerdict
	 * 
	 *  */
	private void fail(String msg)
	{
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass = false;
	}

	public String getFinalVerdict()
	{
		if (inconc)
		{
			return "INCONC";
		}
		
		if (pass)
		{
			return "PASS";
		}
		else
		{
			return "FAIL";
		}
		
		//return inconc ? "INCONC" : (pass ? "PASS" : "FAIL");
	}
}