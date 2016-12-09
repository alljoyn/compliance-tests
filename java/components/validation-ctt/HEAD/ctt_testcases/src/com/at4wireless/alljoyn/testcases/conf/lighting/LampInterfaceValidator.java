/*
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
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
package com.at4wireless.alljoyn.testcases.conf.lighting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.SetValidator;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.IntrospectionXmlParser;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionAnnotation;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionMethod;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionProperty;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionSignal;



// TODO: Auto-generated Javadoc
/**
 * The Class LampInterfaceValidator.
 */
public class LampInterfaceValidator
{
    
    /** The introspection nodes loaded from xml files. */
    private List<IntrospectionNode> introspectionNodesLoadedFromXmlFiles = new ArrayList<IntrospectionNode>();
    
    /** The introspection xml parser. */
    private IntrospectionXmlParser introspectionXmlParser;
    //private ValidationTestContext validationTestContext;
    /** The Constant TAG. */
    private static final String TAG = "InterfaceValidator";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

    /**
     * Instantiates a new lamp interface validator.
     */
    public LampInterfaceValidator()
    {
       
    }

    /**
     * Validate.
     *
     * @param interfaceDetail the interface detail
     * @return the validation result
     */
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

    /**
     * Validate.
     *
     * @param interfaceDetailList the interface detail list
     * @return the validation result
     */
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

    /**
     * Gets the xml files to be loaded.
     *
     * @return the xml files to be loaded
     */
    List<String> getXmlFilesToBeLoaded()
    {
        return Arrays.asList("introspection-xml/Lamp.xml");
    }

    /**
     * Validate interface.
     *
     * @param standardizedIntrospectionInterface the standardized introspection interface
     * @param path the path
     * @return the validation result
     */
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

    /**
     * Compare.
     *
     * @param interfaceDetail the interface detail
     * @param standardizedIntrospectionInterface the standardized introspection interface
     * @param path the path
     * @return the validation result
     */
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

    /**
     * Checks if is version property missing.
     *
     * @param expectedIntrospectionInterface the expected introspection interface
     * @param propertyValidationResult the property validation result
     * @return true, if is version property missing
     */
    private boolean isVersionPropertyMissing(IntrospectionInterface expectedIntrospectionInterface, ValidationResult propertyValidationResult)
    {
        return !propertyValidationResult.isValid()
                && (expectedIntrospectionInterface.getName().equals("org.alljoyn.Config") || expectedIntrospectionInterface.getName().equals("org.alljoyn.Notification"))
                && propertyValidationResult.getFailureReason().equals(" - Missing Property [name=Version, type=q, access=read]");
    }

    /**
     * Checks if is undefined method present for config interface.
     *
     * @param expectedIntrospectionInterface the expected introspection interface
     * @param methodValidationResult the method validation result
     * @return true, if is undefined method present for config interface
     */
    private boolean isUndefinedMethodPresentForConfigInterface(IntrospectionInterface expectedIntrospectionInterface, ValidationResult methodValidationResult)
    {
        return !methodValidationResult.isValid() && expectedIntrospectionInterface.getName().equals("org.alljoyn.Config")
                && methodValidationResult.getFailureReason().equals(" - Undefined Method [name=getVersion, args=[[type=q]], annotations=[]]");
    }

    /**
     * Checks if is annotation missing for peer authentication interface.
     *
     * @param expectedIntrospectionInterface the expected introspection interface
     * @param annotationValidationResult the annotation validation result
     * @return true, if is annotation missing for peer authentication interface
     */
    private boolean isAnnotationMissingForPeerAuthenticationInterface(IntrospectionInterface expectedIntrospectionInterface, ValidationResult annotationValidationResult)
    {
        return !annotationValidationResult.isValid() && expectedIntrospectionInterface.getName().equals("org.alljoyn.Bus.Peer.Authentication")
                && annotationValidationResult.getFailureReason().contains("Missing Annotation") && !annotationValidationResult.getFailureReason().contains("Undefined Annotation");
    }

    /**
     * Gets the introspection nodes loaded from xml files.
     *
     * @return the introspection nodes loaded from xml files
     */
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

    /**
     * Builds the introspection nodes from xml files.
     */
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

    /**
     * Gets the interface detail.
     *
     * @param introspectionNodes the introspection nodes
     * @param introspectionInterfaceName the introspection interface name
     * @return the interface detail
     */
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

    /**
     * Append to failure reason.
     *
     * @param failureReasonBuilder the failure reason builder
     * @param reason the reason
     */
    private void appendToFailureReason(StringBuilder failureReasonBuilder, String reason)
    {
        if (!reason.trim().isEmpty())
        {
            failureReasonBuilder.append(reason);
        }
    }
}