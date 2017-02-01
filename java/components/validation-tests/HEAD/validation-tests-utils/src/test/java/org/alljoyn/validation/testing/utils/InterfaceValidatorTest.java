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
package org.alljoyn.validation.testing.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionAnnotation;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionArg;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionMethod;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionProperty;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionSignal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.xml.sax.SAXException;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class InterfaceValidatorTest
{
    private static final String INVALID_PATH = "invalid path";
    private static final String ABOUT_INTROSPECTION_XML_FILE = "About.xml";
    private static final String CONFIG_INTROSPECTION_XML_FILE = "Config.xml";
    private static final String PEER_INTROSPECTION_XML_FILE = "Peer.xml";
    private static final String PATH = "/About";
    private static final String INTERFACE_NAME = "org.alljoyn.About";
    private static final String INVALID_INTERFACE_NAME = "org.alljoyn.Undefined";
    private static final String SIGNAL_NAME = "Announce";
    private static final String PROPERTY_NAME = "Version";
    private static final String PROPERTY_TYPE = "q";
    private static final String PROPERTY_ACCESS = "read";
    private static final String ANNOTATION_NAME = "org.alljoyn.Bus.Secure";
    private static final String ANNOTATION_VALUE = "true";
    private static final String METHOD_NAME = "GetObjectDescription";
    private static final String NEW_METHOD_NAME = "GetData";
    private static final String NEW_SIGNAL_NAME = "Notify";
    private static final String NEW_ANNOTATION_NAME = "org.alljoyn.Bus.Unsecure";
    private static final String NEW_PROPERTY_TYPE = "a(ss)";
    private static final String CONFIG_INTERFACE_NAME = "org.alljoyn.Config";
    private static final String PEER_AUTHENTICATION_INTERFACE_NAME = "org.alljoyn.Bus.Peer.Authentication";
    private InterfaceValidator interfaceValidator;
    private InterfaceDetail interfaceDetail;
    private IntrospectionAnnotation annotation;
    private IntrospectionMethod method;
    private IntrospectionProperty property;
    private IntrospectionSignal signal;
    private Set<IntrospectionAnnotation> annotations;
    private Set<IntrospectionMethod> methods;
    private Set<IntrospectionProperty> properties;
    private Set<IntrospectionSignal> signals;
    private List<IntrospectionInterface> interfaceList;
    private IntrospectionInterface introspectionInterface;
    private ValidationTestContext mockValidationTestContext;

    @Before
    public void setup() throws IOException, ParserConfigurationException, SAXException
    {
        createAnnotations();
        createMethods();
        createProperties();
        createSignals();
        createInterfaces();
        createInterfaceDetail();
        mockValidationTestContext = mock(ValidationTestContext.class);

        interfaceValidator = new InterfaceValidator(mockValidationTestContext)
        {
            @Override
            List<String> getXmlFilesToBeLoaded()
            {
                List<String> xmlFiles = new ArrayList<String>();
                xmlFiles.add(ABOUT_INTROSPECTION_XML_FILE);
                xmlFiles.add(CONFIG_INTROSPECTION_XML_FILE);
                xmlFiles.add(PEER_INTROSPECTION_XML_FILE);

                return xmlFiles;
            }
        };
    }

    @Test
    public void loadingInvalidXmlFileThrowsExceptionWhichIsCaught()
    {
        interfaceValidator = new InterfaceValidator(mockValidationTestContext)
        {
            @Override
            List<String> getXmlFilesToBeLoaded()
            {
                List<String> xmlFiles = new ArrayList<String>();
                xmlFiles.add("invalid.xml");

                return xmlFiles;
            }
        };

        interfaceValidator.validate(interfaceDetail);
    }

    @Test
    public void validIfInterfaceDetailListIsEmpty()
    {
        ValidationResult validationResult = interfaceValidator.validate(new ArrayList<InterfaceDetail>());

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void validOnlyIfEveryInterfaceDetailIsValid()
    {
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetailList);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void invalidIfAnyInterfaceDetailIsInvalid()
    {
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        interfaceDetailList.add(new InterfaceDetail(INVALID_PATH, interfaceList));
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetailList);

        assertFalse(validationResult.isValid());
        assertEquals("Interface definition does not match for " + INTERFACE_NAME + " - Interface " + INTERFACE_NAME + " found at invalid path " + INVALID_PATH,
                validationResult.getFailureReason());
    }

    @Test
    public void validIfNoInterfaceFound()
    {
        when(interfaceDetail.getIntrospectionInterfaces()).thenReturn(new ArrayList<IntrospectionInterface>());
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void validIfInterfacesMatchesDefinition()
    {
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void invalidIfInterfaceDefinitionNotFound()
    {
        when(introspectionInterface.getName()).thenReturn(INVALID_INTERFACE_NAME);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void invalidIfInterfaceFoundAtInvalidPath()
    {
        when(interfaceDetail.getPath()).thenReturn(INVALID_PATH);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertEquals("Interface definition does not match for " + INTERFACE_NAME + " - Interface " + INTERFACE_NAME + " found at invalid path " + INVALID_PATH,
                validationResult.getFailureReason());
    }

    @Test
    public void validIfInterfacePathDefinitionNotFound()
    {
        when(introspectionInterface.getName()).thenReturn(CONFIG_INTERFACE_NAME);
        when(introspectionInterface.getAnnotations()).thenReturn(Collections.<IntrospectionAnnotation> emptySet());
        when(introspectionInterface.getMethods()).thenReturn(Collections.<IntrospectionMethod> emptySet());
        when(introspectionInterface.getProperties()).thenReturn(Collections.<IntrospectionProperty> emptySet());
        when(introspectionInterface.getSignals()).thenReturn(Collections.<IntrospectionSignal> emptySet());
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void invalidIfInterfaceHasExtraMethod()
    {
        IntrospectionMethod extraMethod = new IntrospectionMethod();
        extraMethod.setName(NEW_METHOD_NAME);
        methods.add(extraMethod);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Method"));
        assertTrue(validationResult.getFailureReason().contains(NEW_METHOD_NAME));
    }

    @Test
    public void invalidIfInterfaceIsMissingMethod()
    {
        methods.remove(method);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Method"));
        assertTrue(validationResult.getFailureReason().contains(METHOD_NAME));
    }

    @Test
    public void invalidIfInterfaceMethodIsNotSame()
    {
        method.setName(NEW_METHOD_NAME);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Method"));
        assertTrue(validationResult.getFailureReason().contains(NEW_METHOD_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Method"));
        assertTrue(validationResult.getFailureReason().contains(METHOD_NAME));
    }

    @Test
    public void invalidIfInterfaceHasExtraSignal()
    {
        IntrospectionSignal extraSignal = new IntrospectionSignal();
        extraSignal.setName(NEW_SIGNAL_NAME);
        signals.add(extraSignal);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Signal"));
        assertTrue(validationResult.getFailureReason().contains(NEW_SIGNAL_NAME));
    }

    @Test
    public void invalidIfInterfaceIsMissingSignal()
    {
        signals.remove(signal);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Signal"));
        assertTrue(validationResult.getFailureReason().contains(SIGNAL_NAME));
    }

    @Test
    public void invalidIfInterfaceSignalIsNotSame()
    {
        signal.setName(NEW_SIGNAL_NAME);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Signal"));
        assertTrue(validationResult.getFailureReason().contains(NEW_SIGNAL_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Signal"));
        assertTrue(validationResult.getFailureReason().contains(SIGNAL_NAME));
    }

    @Test
    public void invalidIfInterfaceHasExtraAnnotation()
    {
        IntrospectionAnnotation extraAnnotation = new IntrospectionAnnotation();
        extraAnnotation.setName(NEW_ANNOTATION_NAME);
        annotations.add(extraAnnotation);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Annotation"));
        assertTrue(validationResult.getFailureReason().contains(NEW_ANNOTATION_NAME));
    }

    @Test
    public void invalidIfInterfaceIsMissingAnnotation()
    {
        annotations.remove(annotation);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Annotation"));
        assertTrue(validationResult.getFailureReason().contains(ANNOTATION_NAME));
    }

    @Test
    public void invalidIfInterfaceAnnotationIsNotSame()
    {
        annotation.setName(NEW_ANNOTATION_NAME);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Annotation"));
        assertTrue(validationResult.getFailureReason().contains(NEW_ANNOTATION_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Annotation"));
        assertTrue(validationResult.getFailureReason().contains(ANNOTATION_NAME));
    }

    @Test
    public void invalidIfInterfaceHasExtraProperty()
    {
        IntrospectionProperty extraProperty = new IntrospectionProperty();
        extraProperty.setName(NEW_PROPERTY_TYPE);
        properties.add(extraProperty);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Property"));
        assertTrue(validationResult.getFailureReason().contains(NEW_PROPERTY_TYPE));
    }

    @Test
    public void invalidIfInterfaceIsMissingProperty()
    {
        properties.remove(property);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Missing Property"));
        assertTrue(validationResult.getFailureReason().contains(PROPERTY_TYPE));
    }

    @Test
    public void invalidIfInterfacePropertyIsNotSame()
    {
        property.setName(NEW_PROPERTY_TYPE);
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Interface definition does not match for " + INTERFACE_NAME));
        assertTrue(validationResult.getFailureReason().contains("Undefined Property"));
        assertTrue(validationResult.getFailureReason().contains(NEW_PROPERTY_TYPE));
        assertTrue(validationResult.getFailureReason().contains("Missing Property"));
        assertTrue(validationResult.getFailureReason().contains(PROPERTY_TYPE));
    }

    @Test
    public void validIfAnnotationIsMissingForPeerAuthenticationInterface()
    {
        when(introspectionInterface.getName()).thenReturn(PEER_AUTHENTICATION_INTERFACE_NAME);
        when(introspectionInterface.getAnnotations()).thenReturn(Collections.<IntrospectionAnnotation> emptySet());
        when(introspectionInterface.getMethods()).thenReturn(Collections.<IntrospectionMethod> emptySet());
        when(introspectionInterface.getProperties()).thenReturn(Collections.<IntrospectionProperty> emptySet());
        when(introspectionInterface.getSignals()).thenReturn(Collections.<IntrospectionSignal> emptySet());
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void validIfVersionPropertyIsMissingForConfigInterface()
    {
        when(introspectionInterface.getName()).thenReturn(CONFIG_INTERFACE_NAME);
        when(introspectionInterface.getAnnotations()).thenReturn(Collections.<IntrospectionAnnotation> emptySet());
        when(introspectionInterface.getMethods()).thenReturn(Collections.<IntrospectionMethod> emptySet());
        when(introspectionInterface.getProperties()).thenReturn(Collections.<IntrospectionProperty> emptySet());
        when(introspectionInterface.getSignals()).thenReturn(Collections.<IntrospectionSignal> emptySet());
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
        verify(mockValidationTestContext).addNote(anyString());
    }

    @Test
    public void validIfGetVersionMethodIsPresentForConfigInterface()
    {
        IntrospectionArg introspectionArg = new IntrospectionArg();
        introspectionArg.setType("q");
        IntrospectionMethod introspectionMethod = new IntrospectionMethod();
        introspectionMethod.setName("getVersion");
        introspectionMethod.getArgs().add(introspectionArg);
        Set<IntrospectionMethod> introspectionMethods = new HashSet<IntrospectionMethod>();
        introspectionMethods.add(introspectionMethod);

        IntrospectionProperty introspectionProperty = new IntrospectionProperty();
        introspectionProperty.setName("Version");
        introspectionProperty.setType("q");
        introspectionProperty.setAccess("read");
        Set<IntrospectionProperty> introspectionProperties = new HashSet<IntrospectionProperty>();
        introspectionProperties.add(introspectionProperty);

        when(introspectionInterface.getName()).thenReturn(CONFIG_INTERFACE_NAME);
        when(introspectionInterface.getAnnotations()).thenReturn(Collections.<IntrospectionAnnotation> emptySet());
        when(introspectionInterface.getMethods()).thenReturn(introspectionMethods);
        when(introspectionInterface.getProperties()).thenReturn(introspectionProperties);
        when(introspectionInterface.getSignals()).thenReturn(Collections.<IntrospectionSignal> emptySet());
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
        verify(mockValidationTestContext).addNote(anyString());
    }

    @Test
    public void validIfVersionPropertyIsMissingForNotificationInterface()
    {
        when(introspectionInterface.getName()).thenReturn("org.alljoyn.Notification");
        when(introspectionInterface.getAnnotations()).thenReturn(Collections.<IntrospectionAnnotation> emptySet());
        when(introspectionInterface.getMethods()).thenReturn(Collections.<IntrospectionMethod> emptySet());
        when(introspectionInterface.getProperties()).thenReturn(Collections.<IntrospectionProperty> emptySet());
        when(introspectionInterface.getSignals()).thenReturn(Collections.<IntrospectionSignal> emptySet());
        ValidationResult validationResult = interfaceValidator.validate(interfaceDetail);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
        verify(mockValidationTestContext).addNote(anyString());
    }

    private void createInterfaceDetail()
    {
        interfaceDetail = mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(PATH);
        when(interfaceDetail.getIntrospectionInterfaces()).thenReturn(interfaceList);
    }

    private void createInterfaces()
    {
        introspectionInterface = mock(IntrospectionInterface.class);
        when(introspectionInterface.getName()).thenReturn(INTERFACE_NAME);
        when(introspectionInterface.getAnnotations()).thenReturn(annotations);
        when(introspectionInterface.getMethods()).thenReturn(methods);
        when(introspectionInterface.getProperties()).thenReturn(properties);
        when(introspectionInterface.getSignals()).thenReturn(signals);
        interfaceList = new ArrayList<IntrospectionInterface>();
        interfaceList.add(introspectionInterface);
    }

    private void createSignals()
    {
        signal = new IntrospectionSignal();
        signal.setName(SIGNAL_NAME);
        signals = new HashSet<IntrospectionSignal>();
        signals.add(signal);
    }

    private void createProperties()
    {
        property = new IntrospectionProperty();
        property.setName(PROPERTY_NAME);
        property.setType(PROPERTY_TYPE);
        property.setAccess(PROPERTY_ACCESS);
        properties = new HashSet<IntrospectionProperty>();
        properties.add(property);
    }

    private void createMethods()
    {
        method = new IntrospectionMethod();
        method.setName(METHOD_NAME);
        methods = new HashSet<IntrospectionMethod>();
        methods.add(method);
    }

    private void createAnnotations()
    {
        annotation = new IntrospectionAnnotation();
        annotation.setName(ANNOTATION_NAME);
        annotation.setValue(ANNOTATION_VALUE);
        annotations = new HashSet<IntrospectionAnnotation>();
        annotations.add(annotation);
    }
}