/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.core.interfacevalidator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.IntrospectionXmlParser;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionAnnotation;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionMethod;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionProperty;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionSignal;

public class InterfaceValidator
{
    private List<IntrospectionNode> introspectionNodesLoadedFromXmlFiles = new ArrayList<IntrospectionNode>();
    private IntrospectionXmlParser introspectionXmlParser;
    private static final Logger logger = new WindowsLoggerImpl(InterfaceValidator.class.getSimpleName());

    public InterfaceValidator()
    {
        
    } 

    public ValidationResult validate(InterfaceDetail interfaceDetail) throws Exception //[ASACOMP-48]
    {
        StringBuilder failureReasonBuilder = new StringBuilder();
        boolean valid = true;

        for (IntrospectionInterface standardizedIntrospectionInterface : interfaceDetail.getIntrospectionInterfaces())
        {
            ValidationResult validationResult = validateInterface(standardizedIntrospectionInterface, interfaceDetail.getPath());

            if (!validationResult.isValid())
            {
                valid = false;
                appendToFailureReason(failureReasonBuilder, validationResult.getFailureReason());
            }
        }

        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    public ValidationResult validate(List<InterfaceDetail> interfaceDetailList) throws Exception //[ASACOMP-48]
    {
        StringBuilder failureReasonBuilder = new StringBuilder();
        boolean valid = true;

        for (InterfaceDetail interfaceDetail : interfaceDetailList)
        {
            ValidationResult validationResult = validate(interfaceDetail);

            if (!validationResult.isValid())
            {
                valid = false;
                appendToFailureReason(failureReasonBuilder, validationResult.getFailureReason());
            }else{
            	
            	logger.info("Object found at "+interfaceDetail.getPath()+" is correct");
            }
        }
 
        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    List<String> getXmlFilesToBeLoaded()
    {
        return Arrays.asList(IntrospectionXmlFile.About.getValue(), IntrospectionXmlFile.Action.getValue(), IntrospectionXmlFile.Audio.getValue(),
                IntrospectionXmlFile.Config.getValue(), IntrospectionXmlFile.Container.getValue(),IntrospectionXmlFile.LightingController.getValue(), IntrospectionXmlFile.ControlPanel.getValue(),
                IntrospectionXmlFile.DeviceIcon.getValue(), IntrospectionXmlFile.Dialog.getValue(), 
                IntrospectionXmlFile.GWAgentCtrlAcl.getValue(),IntrospectionXmlFile.GWAgentCtrlAclMgmt.getValue(),IntrospectionXmlFile.GWAgentCtrlApp.getValue(),IntrospectionXmlFile.GWAgentCtrlAppMgmt.getValue(),
                IntrospectionXmlFile.HTTPControl.getValue(), IntrospectionXmlFile.LabelProperty.getValue(),
                IntrospectionXmlFile.Lighting.getValue(),IntrospectionXmlFile.LeaderElectionAndStateSync.getValue(),
                IntrospectionXmlFile.ListProperty.getValue(), IntrospectionXmlFile.Notification.getValue(),
                IntrospectionXmlFile.NotificationAction.getValue(), IntrospectionXmlFile.Onboarding.getValue(), IntrospectionXmlFile.Peer.getValue(),
                IntrospectionXmlFile.Property.getValue(), IntrospectionXmlFile.Introspectable.getValue(),
                IntrospectionXmlFile.GWAgentConnectorApp.getValue(), IntrospectionXmlFile.Application.getValue(), IntrospectionXmlFile.SecureApplication.getValue(),
                IntrospectionXmlFile.ClaimableApplication.getValue(), IntrospectionXmlFile.ManagedApplication.getValue());
    }

    private ValidationResult validateInterface(IntrospectionInterface standardizedIntrospectionInterface, String path) throws Exception
    {
        ValidationResult validationResult = null;
        InterfaceDetail interfaceDetail = getInterfaceDetail(getIntrospectionNodesLoadedFromXmlFiles(), standardizedIntrospectionInterface.getName());

        if (interfaceDetail == null)
        {
        	throw new Exception(String.format("Interface definition does not exist for: %s", standardizedIntrospectionInterface.getName())); //[ASACOMP-48]
            //logger.warn(String.format("Interface definition does not exist for: %s", standardizedIntrospectionInterface.getName())); //[ASACOMP-48]
            //validationResult = new ValidationResult(true, null); //[ASACOMP-48]
        }
        else
        {
            validationResult = compare(interfaceDetail, standardizedIntrospectionInterface, path);
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

            if (isVersionPropertyMissing(expectedIntrospectionInterface, propertyValidationResult))
            {
                logger.warn(String.format("Ignoring interface property match comparison: %s", propertyValidationResult.getFailureReason()));
                /*logger.addNote(String.format("Interface definition does not match for %s - %s", standardizedIntrospectionInterface.getName(),
                        propertyValidationResult.getFailureReason()));*/
                valid = true;
            }
            if (isUndefinedMethodPresentForConfigInterface(expectedIntrospectionInterface, methodValidationResult))
            {
                logger.warn(String.format("Ignoring interface method match comparison: %s", methodValidationResult.getFailureReason()));
                /*logger.addNote(String.format("Interface definition does not match for %s - %s", standardizedIntrospectionInterface.getName(),
                        methodValidationResult.getFailureReason()));*/
                valid = true;
            }
            if (isAnnotationMissingForPeerAuthenticationInterface(expectedIntrospectionInterface, annotationValidationResult))
            {
                logger.warn(String.format("Ignoring interface annotation match comparison: %s", annotationValidationResult.getFailureReason()));
                valid = true;
            }
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
            failureReasonBuilder.insert(0, "Interface definition does not match for " + standardizedIntrospectionInterface.getName());
        }

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
        if (introspectionNodesLoadedFromXmlFiles.isEmpty())
        {
            introspectionXmlParser = new IntrospectionXmlParser();
            buildIntrospectionNodesFromXmlFiles();
        }

        return introspectionNodesLoadedFromXmlFiles;
    }

    private void buildIntrospectionNodesFromXmlFiles()
    {
        for (String xmlFileToBeLoaded : getXmlFilesToBeLoaded())
        {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFileToBeLoaded);
            StringBuilder failureReasonBuilder = new StringBuilder();
            try
            {
                introspectionNodesLoadedFromXmlFiles.add(introspectionXmlParser.parseXML(inputStream));
            }
            catch (Exception exception)
            {
                logger.error("While loading xml " + xmlFileToBeLoaded + " exception caught: " + exception);
                appendToFailureReason(failureReasonBuilder, "While loading xml " + xmlFileToBeLoaded + " exception caught: " + exception);
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