/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.suites.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Variant;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionSubNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.alljoyn.validation.testing.suites.BaseTestSuiteTest;
import org.alljoyn.validation.testing.suites.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.InterfaceValidator;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.audio.AudioSinkPlayStateChangedSignal;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.AudioSink;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.AudioSource;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Clock;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Configuration;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.ImageSink;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.ImageSource;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.MetadataSink;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.MetadataSource;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Port;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Stream;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Volume;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.VolumeRange;
import org.alljoyn.validation.testing.utils.audio.handlers.AudioSinkSignalHandler;
import org.alljoyn.validation.testing.utils.audio.handlers.OwnershipLostSignalHandler;
import org.alljoyn.validation.testing.utils.audio.handlers.VolumeControlSignalHandler;
import org.alljoyn.validation.testing.utils.bus.BusAttachmentMgr;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;
import org.xml.sax.SAXException;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AudioTestSuiteTest extends BaseTestSuiteTest
{
    private VolumeControlSignalHandler volumeControlSignalHandler;
    private static final int FIFO_SIZE = 2 * 16 * 1024;
    private static final short[] RATES = new short[]
    { -21436, -17536 };
    private static final String[] FORMATS = new String[]
    { "s16le" };
    private static final byte[] CHANNELS = new byte[]
    { 1, 2 };
    private static final String SUB_NODE_PATH = "sub_node_path";
    private static final String PATH = "path";
    private static final String STREAM_INTERFACE_NAME = "org.alljoyn.Stream";
    private static final String BUS_UNIQUE_NAME = "busName";
    private static final String METADATA_SOURCE_PATH = "/Player/Out/Metadata";
    private static final String IMAGE_SOURCE_PATH = "/Player/Out/Image";
    private static final String AUDIO_SOURCE_PATH = "/Player/Out/Audio";
    @Mock
    private BusAttachmentMgr mockBusAttachmentManager;
    @Mock
    private SignalEmitter mockSignalEmitter;
    @Mock
    private ValidationTestContext mockTestContext;
    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncement;
    @Mock
    protected ServiceHelper mockServiceHelper;
    @Mock
    private AboutClient mockAboutClient;
    @Mock
    private AboutIconClient mockAboutIconClient;
    @Mock
    private XmlBasedBusIntrospector mockIntrospector;
    @Mock
    private BusAttachment mockBusAttachment;
    @Mock
    private InterfaceValidator mockInterfaceValidator;
    @Mock
    private NodeDetail mockNodeDetail;
    @Mock
    private NodeDetail mockSubNodeDetail;
    @Mock
    private IntrospectionInterface mockIntrospectionInterface;
    @Mock
    private IntrospectionNode mockIntrospectionNode;
    @Mock
    private IntrospectionNode mockSubIntrospectionNode;
    @Mock
    private IntrospectionSubNode mockIntrospectionSubNode;
    @Mock
    private Stream mockStream;
    @Mock
    private AudioSource mockAudioSource;
    @Mock
    private AudioSink mockAudioSink;
    @Mock
    private ImageSource mockImageSource;
    @Mock
    private ImageSink mockImageSink;
    @Mock
    private MetadataSource mockMetadataSource;
    @Mock
    private MetadataSink mockMetadataSink;
    @Mock
    private Port mockPort;
    @Mock
    private Clock mockClock;
    @Mock
    private AudioSinkSignalHandler mockAudioSinkSignalHandler;
    @Mock
    private Volume mockVolume;

    private AudioTestSuite audioTestSuite;
    protected Exception thrownException;
    private String deviceId = "deviceId";
    private UUID appId = UUID.randomUUID();
    private AppUnderTestDetails appUnderTestDetails;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        when(mockTestContext.getTestObjectPath()).thenReturn(PATH);

        when(mockAboutAnnouncement.supportsInterface(AboutTransport.INTERFACE_NAME)).thenReturn(true);
        constructAudioTestSuite();
        setupMockServiceHelper();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
    }

    @Test
    public void testGetServiceHelper()
    {
        audioTestSuite = new AudioTestSuite();
        ServiceHelper serviceHelper = audioTestSuite.getServiceHelper();

        assertNotNull(serviceHelper);
        assertTrue(serviceHelper != audioTestSuite.getServiceHelper());
    }

    @Test
    public void testGetIntrospector() throws Exception
    {
        audioTestSuite = new AudioTestSuite()
        {
            @Override
            protected ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };
        audioTestSuite.setValidationTestContext(mockTestContext);
        audioTestSuite.setUp();
        BusIntrospector busIntrospector = audioTestSuite.getIntrospector();

        assertNotNull(busIntrospector);
        assertEquals(busIntrospector, audioTestSuite.getIntrospector());
    }

    @Test
    public void testGetSignalTimeout() throws Exception
    {
        audioTestSuite = new AudioTestSuite();
        assertEquals(30, audioTestSuite.getSignalTimeout());
    }

    @Test
    public void testGetAudioSinkSignalHandler()
    {
        audioTestSuite = new AudioTestSuite();
        AudioSinkSignalHandler audioSinkSignalHandler = audioTestSuite.getAudioSinkSignalHandler();

        assertNotNull(audioSinkSignalHandler);
        assertTrue(audioSinkSignalHandler != audioTestSuite.getAudioSinkSignalHandler());
    }

    @Test
    public void testFailsIfAudioStreamObjectPathNotSpecified() throws Exception
    {
        mockTestContext = Mockito.mock(ValidationTestContext.class);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);
        when(mockTestContext.getTestObjectPath()).thenReturn(null);
        when(mockAboutAnnouncement.supportsInterface(AboutTransport.INTERFACE_NAME)).thenReturn(true);
        constructAudioTestSuite();
        setupMockServiceHelper();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        executeTestMethodThrowsException(getTestWrapperFor_v1_01(), "Audio Stream object path not specified");
    }

    @Test
    public void testValidateStreamObjectsFailsWhenStreamInterfaceVersionDoesNotMatch() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockStream.getVersion()).thenReturn((short) 2);
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "Stream Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateStreamObjectsFailsIfStreamHasNoChildInterface() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockStream.getVersion()).thenReturn((short) 1);
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(),
                "The object implementing the Stream interface must have at least one child object implementing org.alljoyn.Stream.Port");
    }

    @Test
    public void testValidateStreamObjectsFailsIfStreamHasNoChildPortInterface() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockStream.getVersion()).thenReturn((short) 1);
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        Port port = Mockito.mock(Port.class);
        when(port.getVersion()).thenReturn((short) 2);
        when(mockIntrospector.getInterface("path/subnodePath", Port.class)).thenReturn(port);
        IntrospectionSubNode subNode = Mockito.mock(IntrospectionSubNode.class);
        when(subNode.getName()).thenReturn("subnodePath");
        List<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();
        subNodes.add(subNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(subNodes);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn")).thenReturn(false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(),
                "The object implementing the Stream interface must have at least one child object implementing org.alljoyn.Stream.Port");
    }

    @Test
    public void testValidateStreamObjectsFailsIfChildPortInterfaceVersionDoesNotMatch() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockStream.getVersion()).thenReturn((short) 1);
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        Port port = Mockito.mock(Port.class);
        when(port.getVersion()).thenReturn((short) 2);
        when(mockIntrospector.getInterface("path/subnodePath", Port.class)).thenReturn(port);
        IntrospectionSubNode subNode = Mockito.mock(IntrospectionSubNode.class);
        when(subNode.getName()).thenReturn("subnodePath");
        List<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();
        subNodes.add(subNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(subNodes);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port")).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "Port Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateStreamObjectsFailsIfChildPortInterfaceDirectionDoesNotMatch() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockStream.getVersion()).thenReturn((short) 1);
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        Port port = Mockito.mock(Port.class);
        when(port.getVersion()).thenReturn((short) 1);
        when(port.getDirection()).thenReturn((byte) 0);
        when(mockIntrospector.getInterface("path/subnodePath", Port.class)).thenReturn(port);
        IntrospectionSubNode subNode = Mockito.mock(IntrospectionSubNode.class);
        when(subNode.getName()).thenReturn("subnodePath");
        List<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();
        subNodes.add(subNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(subNodes);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port")).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "Port Interface direction does not match expected:<1> but was:<0>");
    }

    @Test
    public void testValidateStreamObjectsFailsIfNoMediaTypeRelatedPortInterfaceIsImplemented() throws Exception
    {
        setupMediaTypeRelatedPortInterfaceTest();
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port.AudioSink")).thenReturn(false);
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port.ImageSink")).thenReturn(false);
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port.Application.MetadataSink")).thenReturn(false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "Object implementing the Port interface must also implement one of the media-type specific port interfaces");
    }

    @Test
    public void testValidateStreamObjectsPassesIfAudioSinkInterfaceIsImplemented() throws Exception
    {
        setupMediaTypeRelatedPortInterfaceTest();
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port.AudioSink")).thenReturn(true);

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testValidateStreamObjectsPassesIfImageSinkInterfaceIsImplemented() throws Exception
    {
        setupMediaTypeRelatedPortInterfaceTest();
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port.ImageSink")).thenReturn(true);

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testValidateStreamObjectsPassesIfMetadataSinkInterfaceIsImplemented() throws Exception
    {
        setupMediaTypeRelatedPortInterfaceTest();
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port.Application.MetadataSink")).thenReturn(true);

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testOpenStreamObject() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        executeTestMethod(getTestWrapperFor_v1_02());
        verify(mockStream).Open();
    }

    @Test
    public void testOpenAndCloseStreamObject() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        executeTestMethod(getTestWrapperFor_v1_03());
        verify(mockStream).Open();
        verify(mockStream).Close();
    }

    @Test
    public void testCloseUnopenedStreamObjectFailsIfExceptionIsNotThrown() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Closing an unopened stream must throw exception");
        verify(mockStream).Open();
        verify(mockStream, times(2)).Close();
    }

    @Test
    public void testCloseUnopenedStreamObject() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        doNothing().doThrow(new ErrorReplyBusException("org.alljoyn.Bus.ErStatus", "0x0001")).when(mockStream).Close();

        executeTestMethod(getTestWrapperFor_v1_04());
        verify(mockStream).Open();
        verify(mockStream, times(2)).Close();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasNullCapabilities() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        when(mockPort.getCapabilities()).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "No capabilities found");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasEmptyCapabilities() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        when(mockPort.getCapabilities()).thenReturn(new Configuration[0]);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "No capabilities found");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasNoXRawCapability() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration[] configurations =
        { getConfiguration("audio/x-alac") };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "audio/x-raw media type capability not found");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingChannelsParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.remove("Channels");
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Channels parameter does not exist for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingFormatParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.remove("Format");
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Format parameter does not exist for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingRateParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.remove("Rate");
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Rate parameter does not exist for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingChannelsParameterFirstMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.put("Channels", new Variant(new byte[]
        { 0, 2 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory channel value 1 not found for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingChannelsParameterSecondMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.put("Channels", new Variant(new byte[]
        { 1, 0 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory channel value 2 not found for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingFormatParameterMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.put("Format", new Variant(new String[]
        { "format" }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory format value s16le not found for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingRateParameterFirstMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.put("Rate", new Variant(new short[]
        { 0, -17536 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory rate value -21436 not found for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXRawCapabilityButMissingRateParameterSecondMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.put("Rate", new Variant(new short[]
        { -21436, 0 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory rate value -17536 not found for media type audio/x-raw");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingChannelsParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.remove("Channels");
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Channels parameter does not exist for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingFormatParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.remove("Format");
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Format parameter does not exist for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingRateParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.remove("Rate");
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Rate parameter does not exist for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingChannelsParameterFirstMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.put("Channels", new Variant(new byte[]
        { 0, 2 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory channel value 1 not found for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingChannelsParameterSecondMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.put("Channels", new Variant(new byte[]
        { 1, 0 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory channel value 2 not found for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingFormatParameterMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.put("Format", new Variant(new String[]
        { "format" }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory format value s16le not found for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingRateParameterFirstMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.put("Rate", new Variant(new short[]
        { 0, -17536 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory rate value -21436 not found for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasXAlacCapabilityButMissingRateParameterSecondMandatoryValue() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-alac");
        configuration.parameters.put("Rate", new Variant(new short[]
        { -21436, 0 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Mandatory rate value -17536 not found for media type audio/x-alac");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasInvalidMediaTypeCapability() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration[] configurations =
        { getConfiguration("invalid/x-alac") };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "invalid/x-alac does not match expected media type pattern audio/*");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasValidCapabilityButInvalidChannelsParameterValueSignature() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/dummy");
        configuration.parameters.put("Channels", new Variant(new short[]
        { 0, 1 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "an signature for Channels parameter does not match expected signature ay for media type audio/dummy");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasValidCapabilityButInvalidFormatParameterValueSignature() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/dummy");
        configuration.parameters.put("Format", new Variant(new short[]
        { 0, 1 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "an signature for Format parameter does not match expected signature as for media type audio/dummy");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesFailsIfAudioSinkHasValidCapabilityButInvalidRateParameterValueSignature() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/dummy");
        configuration.parameters.put("Rate", new Variant(new byte[]
        { 0, 1 }));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "ay signature for Rate parameter does not match expected signature aq for media type audio/dummy");
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapability() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration[] configurations =
        { getConfiguration("audio/x-raw") };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapabilityWithNonMandatoryParameters() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/x-raw");
        configuration.parameters.put("key", new Variant("value"));
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapabilityAndXAlacCapability() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration[] configurations =
        { getConfiguration("audio/x-raw"), getConfiguration("audio/x-alac") };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapabilityAndAudioCapability() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration[] configurations =
        { getConfiguration("audio/x-raw"), getConfiguration("audio/dummy") };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapabilityAndAudioCapabilityWithMissingChannelsParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/dummy");
        configuration.parameters.remove("Channels");
        Configuration[] configurations =
        { getConfiguration("audio/x-raw"), configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapabilityAndAudioCapabilityWithMissingFormatParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/dummy");
        configuration.parameters.remove("Format");
        Configuration[] configurations =
        { getConfiguration("audio/x-raw"), configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyAudioSinkCapabilitiesPassesIfAudioSinkHasValidXRawCapabilityAndAudioCapabilityWithMissingRateParameter() throws Exception
    {
        setupDataForVerifyAudioSinkTest();
        Configuration configuration = getConfiguration("audio/dummy");
        configuration.parameters.remove("Rate");
        Configuration[] configurations =
        { getConfiguration("audio/x-raw"), configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyImageSinkCapabilitiesAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_06());
        verify(mockTestContext).addNote("Stream does not support ImageSink!");
    }

    @Test
    public void testVerifyImageSinkCapabilitiesAddsNoteIfNoValidImageSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, ImageSink.class)).thenReturn(mockImageSink);
        when(mockImageSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_06());
        verify(mockTestContext).addNote("Stream does not support ImageSink!");
    }

    @Test
    public void testVerifyImageSinkCapabilitiesFailsIfImageSinkHasNullCapabilities() throws Exception
    {
        setupDataForVerifyImageSinkCapabilitiesTest();
        when(mockPort.getCapabilities()).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "No capabilities found");
    }

    @Test
    public void testVerifyImageSinkCapabilitiesFailsIfImageSinkHasEmptyCapabilities() throws Exception
    {
        setupDataForVerifyImageSinkCapabilitiesTest();
        when(mockPort.getCapabilities()).thenReturn(new Configuration[0]);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "No capabilities found");
    }

    @Test
    public void testVerifyImageSinkCapabilitiesFailsIfImageSinkHasInvalidCapability() throws Exception
    {
        setupDataForVerifyImageSinkCapabilitiesTest();
        Configuration configuration = new Configuration();
        configuration.mediaType = "invalid/image";
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "invalid/image does not match expected media type pattern image/*");
    }

    @Test
    public void testVerifyImageSinkCapabilitiesPassesIfImageSinkHasValidCapability() throws Exception
    {
        setupDataForVerifyImageSinkCapabilitiesTest();
        Configuration configuration = new Configuration();
        configuration.mediaType = "image/dummy";
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_06());
        verify(mockStream).Open();
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_07());
        verify(mockTestContext).addNote("Stream does not support ApplicationMetadataSink!");
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesAddsNoteIfNoValidMetadataSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, MetadataSink.class)).thenReturn(mockMetadataSink);
        when(mockImageSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_07());
        verify(mockTestContext).addNote("Stream does not support ApplicationMetadataSink!");
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesFailsIfMetadataSinkHasNullCapabilities() throws Exception
    {
        setupDataForVerifyMetadataSinkCapabilitiesTest();
        when(mockPort.getCapabilities()).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "No capabilities found");
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesFailsIfMetadataSinkHasEmptyCapabilities() throws Exception
    {
        setupDataForVerifyMetadataSinkCapabilitiesTest();
        when(mockPort.getCapabilities()).thenReturn(new Configuration[0]);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "No capabilities found");
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesFailsIfMetadataSinkHasMoreThanOneCapability() throws Exception
    {
        setupDataForVerifyMetadataSinkCapabilitiesTest();
        when(mockPort.getCapabilities()).thenReturn(new Configuration[2]);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Capabilities length does not match for MetadataSink object expected:<1> but was:<2>");
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesFailsIfMetadataSinkHasInvalidCapability() throws Exception
    {
        setupDataForVerifyMetadataSinkCapabilitiesTest();
        Configuration configuration = new Configuration();
        configuration.mediaType = "invalid/metadata";
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Media type does not match expected:<[application/x-]metadata> but was:<[invalid/]metadata>");
    }

    @Test
    public void testVerifyMetadataSinkCapabilitiesPassesIfMetadataSinkHasValidCapability() throws Exception
    {
        setupDataForVerifyMetadataSinkCapabilitiesTest();
        Configuration configuration = new Configuration();
        configuration.mediaType = "application/x-metadata";
        Configuration[] configurations =
        { configuration };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_07());
        verify(mockStream).Open();
    }

    @Test
    public void testConfigureAudioSinkPortAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_08());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testConfigureAudioSinkPortAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_08());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testConfigureAudioSinkPort() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        executeTestMethod(getTestWrapperFor_v1_08());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testConfigureAudioSinkPortWithInvalidConfigurationAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_09());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testConfigureAudioSinkPortWithInvalidConfigurationAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_09());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testConfigureAudioSinkPortWithInvalidConfigurationFailsIfExceptionIsNotThrown() throws Exception
    {
        answerConnectWithInvalidConfiguration();
        setupDataForVerifyAudioSinkTest();

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "Connecting with invalid configuration must throw exception");
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testConfigureAudioSinkPortWithInvalidConfiguration() throws Exception
    {
        doThrow(new ErrorReplyBusException("")).when(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        setupDataForVerifyAudioSinkTest();

        executeTestMethod(getTestWrapperFor_v1_09());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testConfigureAudioSinkPortTwiceAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testConfigureAudioSinkPortTwiceAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testConfigureAudioSinkPortTwiceFailsIfExceptionIsNotThrown() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Connecting twice to audio sink must throw exception");
        verify(mockPort, times(2)).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testConfigureAudioSinkPortTwice() throws Exception
    {
        doNothing().doThrow(new ErrorReplyBusException("error")).when(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        setupDataForVerifyAudioSinkTest();

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort, times(2)).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testCheckOwnershipLostSignalAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_11());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testCheckOwnershipLostSignalAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_11());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testCheckOwnershipLostSignalFailsIfSignalIsNotReceived() throws Exception
    {
        setupAudioSuiteTestForOwnershipLostSignalTest();
        audioTestSuite.setValidationTestContext(mockTestContext);
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.AudioSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_11(), "Did not receive expected OwnershipLost signal");
        verify(mockStream, times(2)).Open();
        verify(mockServiceHelper).registerSignalHandler(anyObject());
        verify(mockServiceHelper).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testCheckOwnershipLostSignal() throws Exception
    {
        setupAudioSuiteTestForOwnershipLostSignalTest();
        answerRegisterOwnershipLostSignalHandler();
        audioTestSuite.setValidationTestContext(mockTestContext);
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.AudioSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);

        executeTestMethod(getTestWrapperFor_v1_11());
        verify(mockStream, times(2)).Open();
        verify(mockServiceHelper).registerSignalHandler(anyObject());
        verify(mockServiceHelper).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPlaybackAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_12());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testPlaybackAudioSinkAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_12());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testPlaybackAudioSinkFailsIfPlayStateChangedSignalForPlayingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_12(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPlaybackAudioSinkFailsIfFifoPositionChangedSignalIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);
        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_12(), "Timed out waiting for FifoPositionChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPlaybackAudioSinkFailsIfPlayStateChangedSignalForIdleIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_12(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPlaybackAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 0);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethod(getTestWrapperFor_v1_12());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPlaybackAudioSinkWhenFifoSizeIsLessThanDataSize() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 0);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(16 * 1024 - 100);

        executeTestMethod(getTestWrapperFor_v1_12());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(2)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPauseAudioSinkPlaybackAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_13());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testPauseAudioSinkPlaybackAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_13());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testPauseAudioSinkPlaybackFailsIfPlayStateChangedSignalForPlayingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_13(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink, times(0)).Pause(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPauseAudioSinkPlaybackFailsIfPlayStateChangedSignalForPausingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_13(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testPauseAudioSinkPlayback() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 2);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethod(getTestWrapperFor_v1_13());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPausedAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_14());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testFlushPausedAudioSinkAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_14());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testFlushPausedAudioSinkFailsIfPlayStateChangedSignalForPlayingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink, times(0)).Pause(0);
        verify(mockAudioSink, times(0)).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPausedAudioSinkFailsIfPlayStateChangedSignalForPausingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockAudioSink, times(0)).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPausedAudioSinkFailsIfFifoPositionChangedSignalIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);
        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 2);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(), "Timed out waiting for FifoPositionChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockAudioSink).Flush(0);
        verify(mockAudioSinkSignalHandler).clearFifoPositionChangedSignalQueue();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPausedAudioSinkFailsIfPlayStateChangedSignalForFlushingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 2);
        AudioSinkPlayStateChangedSignal thirdPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal,
                thirdPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockAudioSink).Flush(0);
        verify(mockAudioSinkSignalHandler).clearFifoPositionChangedSignalQueue();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPausedAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 2);
        AudioSinkPlayStateChangedSignal thirdPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 2, (byte) 0);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal,
                thirdPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethod(getTestWrapperFor_v1_14());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockAudioSink).Flush(0);
        verify(mockAudioSinkSignalHandler).clearFifoPositionChangedSignalQueue();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPlayingAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_15());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testFlushPlayingAudioSinkAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_15());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testFlushPlayingAudioSinkFailsIfPlayStateChangedSignalForPlayingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_15(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink, times(0)).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPlayingAudioSinkFailsIfFifoPositionChangedSignalIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);
        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_15(), "Timed out waiting for FifoPositionChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPlayingAudioSinkFailsIfPlayStateChangedSignalForIdleIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_15(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushPlayingAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 0);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethod(getTestWrapperFor_v1_15());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyPausedAudioSinkRemainsPausedAfterSendingDataAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_16());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyPausedAudioSinkRemainsPausedAfterSendingDataAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_16());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyPausedAudioSinkRemainsPausedAfterSendingDataFailsIfPlayStateChangedSignalForPausingIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_16(), "Timed out waiting for PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyPausedAudioSinkRemainsPausedAfterSendingDataFailsIfPlayStateChangedSignalIsReceivedAfterSendingData() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 2);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 1, (byte) 2);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_16(), "Received unexpected PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyPausedAudioSinkRemainsPausedAfterSendingData() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 2);
        AudioSinkPlayStateChangedSignal secondPlayStateChangedSignal = null;
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal, secondPlayStateChangedSignal);
        when(mockAudioSink.getFifoSize()).thenReturn(FIFO_SIZE);

        executeTestMethod(getTestWrapperFor_v1_16());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(2)).getFifoSize();
        verify(mockAudioSink).Pause(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(3)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyPlayingEmptyAudioSinkRemainsIdleAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_17());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyPlayingEmptyAudioSinkRemainsIdleAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_17());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyPlayingEmptyAudioSinkRemainsIdleFailsIfPlayStateChangedSignalIsReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        AudioSinkPlayStateChangedSignal firstPlayStateChangedSignal = new AudioSinkPlayStateChangedSignal((byte) 0, (byte) 2);
        when(mockAudioSinkSignalHandler.waitForNextPlayStateChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(firstPlayStateChangedSignal);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_17(), "Received unexpected PlayStateChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(1)).getFifoSize();
        verify(mockAudioSink).Play();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyPlayingEmptyAudioSinkRemainsIdle() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        executeTestMethod(getTestWrapperFor_v1_17());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(1)).getFifoSize();
        verify(mockAudioSink).Play();
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushIdleAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_18());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testFlushIdleAudioSinkAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_18());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testFlushIdleAudioSinkFailsIfFifoPositionChangedSignalIsNotReceived() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_18(), "Timed out waiting for FifoPositionChanged signal");
        verify(mockStream).Open();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(1)).getFifoSize();
        verify(mockAudioSink).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testFlushIdleAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();

        when(mockAudioSinkSignalHandler.waitForNextFifoPositionChangedSignal(anyLong(), (TimeUnit) anyObject())).thenReturn(0);
        executeTestMethod(getTestWrapperFor_v1_18());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockAudioSink).getFifoPosition();
        verify(mockAudioSink, times(1)).getFifoSize();
        verify(mockAudioSink).Flush(0);
        verify(mockServiceHelper).registerSignalHandler(mockAudioSinkSignalHandler);
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testSendDataToImageSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_19());
        verify(mockTestContext).addNote("Stream does not support ImageSink!");
    }

    @Test
    public void testSendDataToImageSinkAddsNoteIfNoValidImageSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, ImageSink.class)).thenReturn(mockImageSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_19());
        verify(mockTestContext).addNote("Stream does not support ImageSink!");
    }

    @Test
    public void testSendDataToImageSink() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, ImageSink.class)).thenReturn(mockImageSink);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.ImageSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);

        Configuration mockConfiguartion = Mockito.mock(Configuration.class);
        Configuration[] configurations =
        { mockConfiguartion };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_19());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(BUS_UNIQUE_NAME, IMAGE_SOURCE_PATH, mockConfiguartion);
        verify(mockImageSource).Data((byte[]) anyObject());
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testSendDataToMetadataSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_20());
        verify(mockTestContext).addNote("Stream does not support MetadataSink!");
    }

    @Test
    public void testSendDataToMetadataSinkAddsNoteIfNoValidImageSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, MetadataSink.class)).thenReturn(mockMetadataSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_20());
        verify(mockTestContext).addNote("Stream does not support MetadataSink!");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendDataToMetadataSink() throws Exception
    {
        answerMetadataSourceDataSignal();
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, MetadataSink.class)).thenReturn(mockMetadataSink);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.Application.MetadataSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);

        Configuration mockConfiguartion = Mockito.mock(Configuration.class);
        Configuration[] configurations =
        { mockConfiguartion };
        when(mockPort.getCapabilities()).thenReturn(configurations);

        executeTestMethod(getTestWrapperFor_v1_20());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(BUS_UNIQUE_NAME, METADATA_SOURCE_PATH, mockConfiguartion);
        verify(mockMetadataSource).Data((Map<String, Variant>) anyObject());
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyAudioSinkCanBeMutedUnmutedAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_21());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyAudioSinkCanBeMutedUnmuted() throws Exception
    {
        answerConnectWithValidConfiguration();

        when(mockVolume.getMute()).thenReturn(true, false, false, true);
        answerRegisterVolumeControlSignalHandler();
        answerSetMuteVolumeProperty();
        setupDataForVerifyVolumeTest();

        executeTestMethod(getTestWrapperFor_v1_21());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockServiceHelper).registerSignalHandler(anyObject());
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testVerifyVolumeCanBeSetOnAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_22());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyVolumeCanBeSetOnAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();

        VolumeRange volumeRange = createVolumeRange((short) 0, (short) -100, (short) 50);

        when(mockVolume.getVolumeRange()).thenReturn(volumeRange);

        short volume = 0;
        short newVolume = -50;
        when(mockVolume.getVolume()).thenReturn(volume, newVolume, volume);
        answerRegisterVolumeControlSignalHandler();
        answerSetVolumeProperty();
        setupDataForVerifyVolumeTest();

        executeTestMethod(getTestWrapperFor_v1_22());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockServiceHelper).registerSignalHandler(anyObject());
        verify(mockServiceHelper, times(1)).registerBusObject((BusObject) anyObject(), anyString());
    }

    @Test
    public void testGetNewVolumePropertyValue()
    {
        VolumeRange volumeRange = createVolumeRange((short) 400, (short) -200, (short) 100);

        short volumeProperty = 100;
        short newVolume = audioTestSuite.getNewVolumePropertyValue(volumeProperty, volumeRange);
        assertEquals(200, newVolume);
    }

    @Test
    public void testGetNewVolumePropertyValueLowRange()
    {
        VolumeRange volumeRange = createVolumeRange((short) 400, (short) -200, (short) 100);

        short volumeProperty = -200;
        short newVolume = audioTestSuite.getNewVolumePropertyValue(volumeProperty, volumeRange);
        assertEquals(-100, newVolume);
    }

    @Test
    public void testGetNewVolumePropertyValueHighRange()
    {
        VolumeRange volumeRange = createVolumeRange((short) 400, (short) -200, (short) 100);

        short volumeProperty = 400;
        short newVolume = audioTestSuite.getNewVolumePropertyValue(volumeProperty, volumeRange);
        assertEquals(300, newVolume);
    }

    @Test
    public void testGetNewVolumePropertyValueShortMaxValue()
    {
        VolumeRange volumeRange = createVolumeRange(Short.MAX_VALUE, (short) -200, (short) 100);

        short volumeProperty = Short.MAX_VALUE;
        short newVolume = audioTestSuite.getNewVolumePropertyValue(volumeProperty, volumeRange);
        assertEquals(32667, newVolume);
    }

    @Test
    public void testGetNewVolumePropertyValueShortMinValue()
    {
        VolumeRange volumeRange = createVolumeRange((short) 400, Short.MIN_VALUE, (short) 100);

        short volumeProperty = Short.MIN_VALUE;
        short newVolume = audioTestSuite.getNewVolumePropertyValue(volumeProperty, volumeRange);
        assertEquals(-32668, newVolume);
    }

    @Test
    public void testGetNewVolumePropertyValueHighLowEqual()
    {
        VolumeRange volumeRange = createVolumeRange((short) 400, (short) 400, (short) 100);

        short volumeProperty = 400;
        short newVolume = audioTestSuite.getNewVolumePropertyValue(volumeProperty, volumeRange);
        assertEquals(400, newVolume);
    }

    @Test
    public void testVerifySetInvalidVolumeOnAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_23());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifySetInvalidVolumeOnAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();

        VolumeRange volumeRange = createVolumeRange((short) 0, (short) -100, (short) 50);

        when(mockVolume.getVolumeRange()).thenReturn(volumeRange);

        answerSetVolumePropertyThrowException();
        setupDataForVerifyVolumeTest();

        executeTestMethod(getTestWrapperFor_v1_23());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testVerifyIndependenceOfMuteAndVolumeOnAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_24());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testVerifyIndependenceOfMuteAndVolumeOnAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();

        VolumeRange volumeRange = createVolumeRange((short) 500, (short) -100, (short) 50);
        when(mockVolume.getVolumeRange()).thenReturn(volumeRange);

        short volume = 200;
        short oneMoreThanLow = -50;
        when(mockVolume.getVolume()).thenReturn(volume, volume, volume, oneMoreThanLow, oneMoreThanLow);
        when(mockVolume.getMute()).thenReturn(true, false, true);

        setupDataForVerifyVolumeTest();

        executeTestMethod(getTestWrapperFor_v1_24());
        verify(mockVolume, times(2)).setVolume(Mockito.anyShort());
        verify(mockVolume, times(3)).setMute(Mockito.anyBoolean());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @Test
    public void testGetMidRangeVolumeProperty()
    {
        VolumeRange volumeRange = createVolumeRange((short) 20, (short) 10, (short) 3);
        assertEquals(13, audioTestSuite.getMidRangeVolumeProperty(volumeRange));

        volumeRange.high = 20;
        volumeRange.low = 10;
        volumeRange.step = 2;
        assertEquals(14, audioTestSuite.getMidRangeVolumeProperty(volumeRange));
    }

    @Test
    public void testSynchronizeClocksOnAudioSinkAddsNoteIfNoSubNodeExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(new ArrayList<IntrospectionSubNode>());

        executeTestMethod(getTestWrapperFor_v1_25());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testSynchronizeClocksOnAudioSinkAddsNoteIfNoValidAudioSinkExists() throws Exception
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockAudioSink.getVersion()).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_25());
        verify(mockTestContext).addNote("Stream does not support AudioSink!");
    }

    @Test
    public void testSynchronizeClocksOnAudioSinkClockGetVersionException() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        doThrow(new BusException()).when(mockClock).getVersion();
        when(mockIntrospector.getInterface(PATH, Clock.class)).thenReturn(null);

        executeTestMethod(getTestWrapperFor_v1_25());
        verify(mockClock, times(0)).SetTime(anyLong());
        verify(mockClock, times(0)).AdjustTime(anyLong());
        verify(mockTestContext).addNote("Stream does not support Clock!");
    }

    @Test
    public void testSynchronizeClocksOnAudioSink() throws Exception
    {
        answerConnectWithValidConfiguration();
        setupDataForVerifyAudioSinkTest();
        when(mockClock.getVersion()).thenReturn((short) 1);

        executeTestMethod(getTestWrapperFor_v1_25());
        verify(mockStream).Open();
        verify(mockStream).Close();
        verify(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
        verify(mockClock, times(1)).getVersion();
        verify(mockClock, times(1)).SetTime(anyLong());
        verify(mockClock, times(1)).AdjustTime(anyLong());
    }

    private void setupAudioSuiteTestForOwnershipLostSignalTest()
    {
        audioTestSuite = new AudioTestSuite()
        {
            @Override
            protected ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }

            @Override
            long getSignalTimeout()
            {
                return 0;
            }

            @Override
            BusAttachmentMgr createBusAttachmentManager() throws BusException
            {
                when(mockBusAttachmentManager.getBusAttachment()).thenReturn(mockBusAttachment);
                return mockBusAttachmentManager;
            }

            @Override
            AboutClient createAboutClient(BusAttachment busAttachment) throws BusException
            {
                return mockAboutClient;
            }

            @Override
            BusIntrospector getIntrospector(BusAttachment busAttachment, AboutClient newAboutClient)
            {
                return mockIntrospector;
            }
        };
    }

    @SuppressWarnings(
    { "rawtypes" })
    private void answerRegisterVolumeControlSignalHandler() throws BusException
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                volumeControlSignalHandler = (VolumeControlSignalHandler) objects[0];

                return null;
            }
        }).when(mockServiceHelper).registerSignalHandler(anyObject());
    }

    @SuppressWarnings(
    { "rawtypes" })
    private void answerSetMuteVolumeProperty() throws BusException
    {

        doAnswer(new Answer()
        {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                Boolean newMute = (Boolean) objects[0];

                volumeControlSignalHandler.MuteChanged(newMute);

                return null;
            }
        }).when(mockVolume).setMute(Mockito.anyBoolean());
    }

    private void answerSetVolumePropertyThrowException() throws BusException
    {
        doThrow(new BusException("ER_BUS_REPLY_IS_ERROR_MESSAGE")).when(mockVolume).setVolume(Mockito.anyShort());
    }

    @SuppressWarnings(
    { "rawtypes" })
    private void answerSetVolumeProperty() throws BusException
    {

        doAnswer(new Answer()
        {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                Short newVolume = (Short) objects[0];

                volumeControlSignalHandler.VolumeChanged(newVolume);

                return null;
            }
        }).when(mockVolume).setVolume(Mockito.anyShort());

    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    private void answerMetadataSourceDataSignal() throws BusException
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                Map<String, Variant> dictionary = (Map<String, Variant>) objects[0];
                assertEquals("item name", dictionary.get("Name").getObject(String.class));
                assertEquals("album title", dictionary.get("Album").getObject(String.class));

                return null;
            }
        }).when(mockMetadataSource).Data((Map<String, Variant>) anyObject());
    }

    @SuppressWarnings(
    { "rawtypes" })
    private void answerRegisterOwnershipLostSignalHandler() throws BusException
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                OwnershipLostSignalHandler ownershipLostSignalHandler = (OwnershipLostSignalHandler) objects[0];
                CountDownLatch countDownLatch = ownershipLostSignalHandler.getCountDownLatch();
                assertEquals(1, countDownLatch.getCount());
                countDownLatch.countDown();

                return null;
            }
        }).when(mockServiceHelper).registerSignalHandler(anyObject());
    }

    @SuppressWarnings(
    { "rawtypes" })
    private void answerConnectWithInvalidConfiguration() throws BusException
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                assertEquals(BUS_UNIQUE_NAME, (String) objects[0]);
                assertEquals(AUDIO_SOURCE_PATH, (String) objects[1]);

                Configuration configuration = (Configuration) objects[2];
                assertEquals("audio/x-unknown", configuration.mediaType);
                assertTrue(configuration.parameters.isEmpty());

                return null;
            }
        }).when(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    @SuppressWarnings(
    { "rawtypes" })
    private void answerConnectWithValidConfiguration() throws BusException
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                assertEquals(BUS_UNIQUE_NAME, (String) objects[0]);
                assertEquals(AUDIO_SOURCE_PATH, (String) objects[1]);

                Configuration configuration = (Configuration) objects[2];
                assertEquals("audio/x-raw", configuration.mediaType);

                Map<String, Variant> parameters = configuration.parameters;
                assertEquals((byte) 1, (byte) ((Variant) parameters.get("Channels")).getObject(byte.class));
                assertEquals("s16le", ((Variant) parameters.get("Format")).getObject(String.class));
                assertEquals((short) -21436, (short) ((Variant) parameters.get("Rate")).getObject(short.class));

                return null;
            }
        }).when(mockPort).Connect(anyString(), anyString(), (Configuration) anyObject());
    }

    private VolumeRange createVolumeRange(short high, short low, short step)
    {
        VolumeRange volumeRange = new VolumeRange();
        volumeRange.high = high;
        volumeRange.low = low;
        volumeRange.step = step;
        return volumeRange;
    }

    private void setupDataForVerifyVolumeTest() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.AudioSink")).thenReturn(true);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Control.Volume")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Volume.class)).thenReturn(mockVolume);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);
        when(mockIntrospector.getInterface(PATH, Clock.class)).thenReturn(mockClock);
    }

    private void setupDataForVerifyAudioSinkTest() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.AudioSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, AudioSink.class)).thenReturn(mockAudioSink);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);
        when(mockIntrospector.getInterface(PATH, Clock.class)).thenReturn(mockClock);
    }

    private void setupDataForVerifyImageSinkCapabilitiesTest() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.ImageSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);
    }

    private void setupDataForVerifyMetadataSinkCapabilitiesTest() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);
        when(mockIntrospectionSubNode.getName()).thenReturn(SUB_NODE_PATH);
        List<IntrospectionSubNode> mockIntrospectionSubNodes = new ArrayList<IntrospectionSubNode>();
        mockIntrospectionSubNodes.add(mockIntrospectionSubNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(mockIntrospectionSubNodes);
        when(mockIntrospector.isInterfacePresent(PATH + "/" + SUB_NODE_PATH, "org.alljoyn.Stream.Port.Application.MetadataSink")).thenReturn(true);
        when(mockIntrospector.getInterface(PATH + "/" + SUB_NODE_PATH, Port.class)).thenReturn(mockPort);
    }

    private Configuration getConfiguration(String mediaType)
    {
        Map<String, Variant> parameters = new HashMap<String, Variant>();
        parameters.put("Channels", new Variant(CHANNELS));
        parameters.put("Format", new Variant(FORMATS));
        parameters.put("Rate", new Variant(RATES, "aq"));

        Configuration configuration = new Configuration();
        configuration.mediaType = mediaType;
        configuration.parameters = parameters;

        return configuration;
    }

    private void setupMediaTypeRelatedPortInterfaceTest() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        setupStreamInterfaceDetailListExposedOnBus();
        when(mockStream.getVersion()).thenReturn((short) 1);
        when(mockIntrospector.getInterface(PATH, Stream.class)).thenReturn(mockStream);

        Port port = Mockito.mock(Port.class);
        when(port.getVersion()).thenReturn((short) 1);
        when(port.getDirection()).thenReturn((byte) 1);
        when(mockIntrospector.getInterface("path/subnodePath", Port.class)).thenReturn(port);
        IntrospectionSubNode subNode = Mockito.mock(IntrospectionSubNode.class);
        when(subNode.getName()).thenReturn("subnodePath");
        ArrayList<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();
        subNodes.add(subNode);
        when(mockIntrospectionNode.getSubNodes()).thenReturn(subNodes);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockIntrospector.isInterfacePresent("path/subnodePath", "org.alljoyn.Stream.Port")).thenReturn(true);
    }

    private void setupStreamInterfaceDetailListExposedOnBus() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        InterfaceDetail streamInterfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(streamInterfaceDetail.getPath()).thenReturn(PATH);
        ArrayList<InterfaceDetail> streamInterfaceDetailListExposedOnBus = new ArrayList<InterfaceDetail>();
        streamInterfaceDetailListExposedOnBus.add(streamInterfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(STREAM_INTERFACE_NAME)).thenReturn(streamInterfaceDetailListExposedOnBus);
    }

    private void setupMockServiceHelper() throws Exception
    {
        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(mockAboutAnnouncement);
        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncement)).thenReturn(mockAboutClient);
        when(mockServiceHelper.getBusIntrospector(mockAboutClient)).thenReturn(mockIntrospector);
        when(mockServiceHelper.getBusUniqueName()).thenReturn(BUS_UNIQUE_NAME);
    }

    private void constructAudioTestSuite()
    {
        audioTestSuite = new AudioTestSuite()
        {
            @Override
            protected ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }

            @Override
            SignalEmitter createSignalEmitter(BusObject busObject)
            {
                when(mockSignalEmitter.getInterface(AudioSource.class)).thenReturn(mockAudioSource);
                when(mockSignalEmitter.getInterface(ImageSource.class)).thenReturn(mockImageSource);
                when(mockSignalEmitter.getInterface(MetadataSource.class)).thenReturn(mockMetadataSource);

                return mockSignalEmitter;
            }

            @Override
            long getSignalTimeout()
            {
                return 5;
            }

            @Override
            AudioSinkSignalHandler getAudioSinkSignalHandler()
            {
                return mockAudioSinkSignalHandler;
            }
        };

        audioTestSuite.setValidationTestContext(mockTestContext);
    }

    protected TestWrapper getTestWrapperFor_v1_01()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_01_ValidateStreamObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_02()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_02_OpenStreamObject();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_03()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_03_OpenAndCloseStreamObject();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_04()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_04_CloseUnopenedStreamObject();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_05()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_05_VerifyAudioSinkCapabilities();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_06()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_06_VerifyImageSinkCapabilities();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_07()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_07_VerifyApplicationMetadataCapabilities();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_08()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_08_ConfigureAudioSinkPort();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_09()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_09_ConfigureAudioSinkPortWithInvalidConfiguration();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_10()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_10_ConfigureAudioSinkPortTwice();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_11()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_11_CheckOwnershipLostSignal();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_12()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_12_PlaybackAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_13()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_13_PauseAudioSinkPlayback();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_14()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_14_FlushPausedAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_15()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_15_FlushPlayingAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_16()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_16_VerifyPausedAudioSinkRemainsPausedAfterSendingData();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_17()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_17_VerifyPlayingEmptyAudioSinkRemainsIdle();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_18()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_18_FlushIdleAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_19()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_19_SendDataToImageSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_20()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_20_SendDataToMetadataSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_21()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_21_VerifyAudioSinkCanBeMutedUnmuted();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_22()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_22_VerifyVolumeCanBeSetOnAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_23()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_23_SetInvalidVolumeOnAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_24()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_24_VerifyIndependenceOfMuteAndVolumeOnAudioSink();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_25()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                audioTestSuite.testAudio_v1_25_SynchronizeClocksOnAudioSink();
            }
        };
    }

    @Override
    protected void executeTestMethod(TestWrapper testWrapper) throws Exception
    {
        audioTestSuite.setUp();

        try
        {
            testWrapper.executeTestMethod();
        }
        finally
        {
            audioTestSuite.tearDown();
        }
    }
}