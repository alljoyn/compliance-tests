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
package org.alljoyn.validation.testing.suites;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.IntrospectionXmlParser;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionAnnotation;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionMethod;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionProperty;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionSignal;
import org.alljoyn.validation.testing.utils.IntrospectionXmlFile;
import org.alljoyn.validation.testing.utils.SetValidator;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class LampInterfaceValidator
{
    private List<IntrospectionNode> introspectionNodesLoadedFromXmlFiles = new ArrayList<IntrospectionNode>();
    private IntrospectionXmlParser introspectionXmlParser;
    private ValidationTestContext validationTestContext;
    private static final String TAG = "InterfaceValidator";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    public LampInterfaceValidator(ValidationTestContext validationTestContext)
    {
        this.validationTestContext = validationTestContext;
    }

    public ValidationResult validate(InterfaceDetail interfaceDetail)
    {
        StringBuilder failureReasonBuilder = new StringBuilder();
        boolean valid = true;

        logger.debug(" before for loop in validate " );
        for (IntrospectionInterface standardizedIntrospectionInterface : interfaceDetail.getIntrospectionInterfaces())
        {
        	logger.debug(" in for loop " + interfaceDetail );
        	ValidationResult validationResult = validateInterface(standardizedIntrospectionInterface, interfaceDetail.getPath());

            if (!validationResult.isValid())
            {
                valid = false;
                appendToFailureReason(failureReasonBuilder, validationResult.getFailureReason());
            }
        }

        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    public ValidationResult validate(List<InterfaceDetail> interfaceDetailList)
    {
        StringBuilder failureReasonBuilder = new StringBuilder();
        boolean valid = true;

        logger.debug(" before for loop in validate list " );
        for (InterfaceDetail interfaceDetail : interfaceDetailList)
        {
        	logger.debug(" in for loop " + interfaceDetail );
            ValidationResult validationResult = validate(interfaceDetail);

            if (!validationResult.isValid())
            {
                valid = false;
                appendToFailureReason(failureReasonBuilder, validationResult.getFailureReason());
            }
        }

        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    List<String> getXmlFilesToBeLoaded()
    {
        return Arrays.asList("introspection-xml/Lamp.xml");
    }

    private ValidationResult validateInterface(IntrospectionInterface standardizedIntrospectionInterface, String path)
    {
        ValidationResult validationResult = null;
        InterfaceDetail interfaceDetail = getInterfaceDetail(getIntrospectionNodesLoadedFromXmlFiles(), standardizedIntrospectionInterface.getName());

        if (interfaceDetail == null)
        {
            StringBuilder failureReasonBuilder = new StringBuilder();
            appendToFailureReason(failureReasonBuilder, "Interface definition does not exist for ");
            appendToFailureReason(failureReasonBuilder, standardizedIntrospectionInterface.getName());
            validationResult = new ValidationResult(false, failureReasonBuilder.toString());
			logger.debug(" in validateInterface, null " + path );
        }
        else
        {
			validationResult = compare(interfaceDetail, standardizedIntrospectionInterface, path);
			logger.debug(" in validateInterface " + path );
        }

        return validationResult;
    }

    private ValidationResult compare(InterfaceDetail interfaceDetail, IntrospectionInterface standardizedIntrospectionInterface, String path)
    {
        boolean valid = true;
        StringBuilder failureReasonBuilder = new StringBuilder();
        IntrospectionInterface expectedIntrospectionInterface = interfaceDetail.getIntrospectionInterfaces().get(0);

        if (interfaceDetail.getPath() != null && !interfaceDetail.getPath().equals(path))
        {
            valid = false;
            appendToFailureReason(failureReasonBuilder, " - Interface ");
            appendToFailureReason(failureReasonBuilder, standardizedIntrospectionInterface.getName());
            appendToFailureReason(failureReasonBuilder, " found at invalid path ");
            appendToFailureReason(failureReasonBuilder, path);
        }

        ValidationResult methodValidationResult = new SetValidator<IntrospectionMethod>().validate(expectedIntrospectionInterface.getMethods(),
                standardizedIntrospectionInterface.getMethods());
        ValidationResult propertyValidationResult = new SetValidator<IntrospectionProperty>().validate(expectedIntrospectionInterface.getProperties(),
                standardizedIntrospectionInterface.getProperties());
        ValidationResult signalValidationResult = new SetValidator<IntrospectionSignal>().validate(expectedIntrospectionInterface.getSignals(),
                standardizedIntrospectionInterface.getSignals());
        ValidationResult annotationValidationResult = new SetValidator<IntrospectionAnnotation>().validate(expectedIntrospectionInterface.getAnnotations(),
                standardizedIntrospectionInterface.getAnnotations());

        if (!methodValidationResult.isValid() || !propertyValidationResult.isValid() || !signalValidationResult.isValid() || !annotationValidationResult.isValid())
        {
            valid = false;
			/* 
            if (isVersionPropertyMissing(expectedIntrospectionInterface, propertyValidationResult))
            {
                logger.error(String.format("Ignoring interface property match comparison: %s", propertyValidationResult.getFailureReason()));
                validationTestContext.addNote(String.format("Interface definition does not match for %s - %s", standardizedIntrospectionInterface.getName(),
                        propertyValidationResult.getFailureReason()));
                valid = true;
            }
            if (isUndefinedMethodPresentForConfigInterface(expectedIntrospectionInterface, methodValidationResult))
            {
                logger.error(String.format("Ignoring interface method match comparison: %s", methodValidationResult.getFailureReason()));
                validationTestContext.addNote(String.format("Interface definition does not match for %s - %s", standardizedIntrospectionInterface.getName(),
                        methodValidationResult.getFailureReason()));
                valid = true;
            }
            if (isAnnotationMissingForPeerAuthenticationInterface(expectedIntrospectionInterface, annotationValidationResult))
            {
                logger.error(String.format("Ignoring interface annotation match comparison: %s", annotationValidationResult.getFailureReason()));
                valid = true;
            }
			*/
            if (!valid)
            {
                appendToFailureReason(failureReasonBuilder, methodValidationResult.getFailureReason());
                appendToFailureReason(failureReasonBuilder, propertyValidationResult.getFailureReason());
                appendToFailureReason(failureReasonBuilder, signalValidationResult.getFailureReason());
                appendToFailureReason(failureReasonBuilder, annotationValidationResult.getFailureReason());
                appendToFailureReason(failureReasonBuilder, " ----------- ");
            }
        }

        if (!valid)
        {
		    logger.debug(" in compare, invalid " );
            failureReasonBuilder.insert(0, "Interface definition does not match for " + standardizedIntrospectionInterface.getName());
        }
		logger.debug(" in compare" );
        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    private boolean isVersionPropertyMissing(IntrospectionInterface expectedIntrospectionInterface, ValidationResult propertyValidationResult)
    {
        return !propertyValidationResult.isValid()
                && (expectedIntrospectionInterface.getName().equals("org.alljoyn.Config") || expectedIntrospectionInterface.getName().equals("org.alljoyn.Notification"))
                && propertyValidationResult.getFailureReason().equals(" - Missing Property [name=Version, type=q, access=read]");
    }

    private boolean isUndefinedMethodPresentForConfigInterface(IntrospectionInterface expectedIntrospectionInterface, ValidationResult methodValidationResult)
    {
        return !methodValidationResult.isValid() && expectedIntrospectionInterface.getName().equals("org.alljoyn.Config")
                && methodValidationResult.getFailureReason().equals(" - Undefined Method [name=getVersion, args=[[type=q]], annotations=[]]");
    }

    private boolean isAnnotationMissingForPeerAuthenticationInterface(IntrospectionInterface expectedIntrospectionInterface, ValidationResult annotationValidationResult)
    {
        return !annotationValidationResult.isValid() && expectedIntrospectionInterface.getName().equals("org.alljoyn.Bus.Peer.Authentication")
                && annotationValidationResult.getFailureReason().contains("Missing Annotation") && !annotationValidationResult.getFailureReason().contains("Undefined Annotation");
    }

    private List<IntrospectionNode> getIntrospectionNodesLoadedFromXmlFiles()
    {
    	logger.debug(" before checking xml is empty " );
        if (introspectionNodesLoadedFromXmlFiles.isEmpty())
        {
            introspectionXmlParser = new IntrospectionXmlParser();
            buildIntrospectionNodesFromXmlFiles();
        }

        return introspectionNodesLoadedFromXmlFiles;
    }

    private void buildIntrospectionNodesFromXmlFiles()
    {
    	logger.debug(" before loading xml " );
        for (String xmlFileToBeLoaded : getXmlFilesToBeLoaded())
        {
        	logger.debug(" loading xml " + xmlFileToBeLoaded);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFileToBeLoaded);

            try
            {
                introspectionNodesLoadedFromXmlFiles.add(introspectionXmlParser.parseXML(inputStream));
            }
            catch (Exception exception)
            {
                logger.error("While loading xml " + xmlFileToBeLoaded + " exception caught: " + exception);
            }
        }
    }

    private InterfaceDetail getInterfaceDetail(List<IntrospectionNode> introspectionNodes, String introspectionInterfaceName)
    {
        for (IntrospectionNode introspectionNode : introspectionNodes)
        {
            for (IntrospectionInterface introspectionInterface : introspectionNode.getInterfaces())
            {
                if (introspectionInterfaceName.equals(introspectionInterface.getName()))
                {
                    List<IntrospectionInterface> interfaces = new ArrayList<IntrospectionInterface>();
                    interfaces.add(introspectionInterface);

                    return new InterfaceDetail(introspectionNode.getName(), interfaces);
                }
            }
        }

        return null;
    }

    private void appendToFailureReason(StringBuilder failureReasonBuilder, String reason)
    {
        if (!reason.trim().isEmpty())
        {
            failureReasonBuilder.append(reason);
        }
    }
}