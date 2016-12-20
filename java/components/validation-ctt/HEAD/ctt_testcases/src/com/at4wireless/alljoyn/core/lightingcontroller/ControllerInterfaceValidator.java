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
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.interfacevalidator.SetValidator;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.IntrospectionXmlParser;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionBaseTag;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;


// TODO: Auto-generated Javadoc
/**
 * The Class ControllerInterfaceValidator.
 */
public class ControllerInterfaceValidator
{
    
    /** The Constant TAG. */
    private static final String TAG = "CIValidator";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    /** The Constant INTROSPECT_FILES. */
    private static final String[] INTROSPECT_FILES = {
        "introspection-xml/ControllerService.xml",
        "introspection-xml/LeaderElectionAndStateSync.xml"
    };

    /** The validator. */
    private static ControllerInterfaceValidator validator = null;

    /** The xml introspection nodes. */
    private List<IntrospectionNode> xmlIntrospectionNodes;

    /**
     * Instantiates a new controller interface validator.
     */
    private ControllerInterfaceValidator()
    {
        xmlIntrospectionNodes = buildIntrospectionNodes();
    }

    // Create introspection nodes from XML files. Use created nodes as master
    // copy to match DUT interfaces against.
    /**
     * Builds the introspection nodes.
     *
     * @return the list
     */
    private List<IntrospectionNode> buildIntrospectionNodes()
    {
        List<IntrospectionNode> nodes = new ArrayList<IntrospectionNode>();
        IntrospectionXmlParser parser = new IntrospectionXmlParser();
        for (String filename : INTROSPECT_FILES)
        {
            try
            {
                InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
                nodes.add(parser.parseXML(istream));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        for (IntrospectionNode node : nodes)
        {
            logger.debug("NODE:" + node.getName());
            for (IntrospectionInterface iface : node.getInterfaces())
            {
                logger.debug("IFACE:" + iface.getName());
            }
        }

        return nodes;
    }

    /**
     * Gets the validator.
     *
     * @return the validator
     */
    public static ControllerInterfaceValidator getValidator()
    {
        return (validator == null)? new ControllerInterfaceValidator() : validator;
    }

    /**
     * Validate.
     *
     * @param ifaceDetails the iface details
     * @return the validation result
     */
    public ValidationResult validate(List<InterfaceDetail> ifaceDetails)
    {
        // validate each InterfaceDetail in the List
        for (InterfaceDetail ifaceDetail : ifaceDetails)
        {
            ValidationResult result = validate(ifaceDetail);
            if (!result.isValid())
            {
                return result;
            }
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate.
     *
     * @param ifaceDetail the iface detail
     * @return the validation result
     */
    public ValidationResult validate(InterfaceDetail ifaceDetail)
    {
        // validate each IntrospectionInterface of the InterfaceDetail
        for (IntrospectionInterface iface : ifaceDetail.getIntrospectionInterfaces())
        {
            ValidationResult result = validateInterface(iface, ifaceDetail.getPath());
            if (!result.isValid())
            {
                return result;
            }
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate interface.
     *
     * @param iface the iface
     * @param path the path
     * @return the validation result
     */
    private ValidationResult validateInterface(IntrospectionInterface iface, String path)
    {
        // Fetch expected interface specs
        String ifaceName = iface.getName();
        InterfaceDetail expectedInterfaceDetail = getExpectedInterfaceByName(xmlIntrospectionNodes, ifaceName);
        if (expectedInterfaceDetail == null)
        {
            String failureReason = "Interface definition does not exist for " + ifaceName;
            return new ValidationResult(false, failureReason);
        }

        // Compare paths
        String expectedPath = expectedInterfaceDetail.getPath();
        if (expectedPath == null || !expectedPath.equals(path))
        {
            String failureReason = "Interface " + ifaceName + " found at invalid path";
            return new ValidationResult(false, failureReason);
        }

        // Compare interfaces
        IntrospectionInterface expectedIface = expectedInterfaceDetail.getIntrospectionInterfaces().get(0);
        ValidationResult result = interfaceComparator(expectedIface, iface);
        if (!result.isValid())
        {
            return result;
        }

        return new ValidationResult(true, "");
    }

    /**
     * Interface comparator.
     *
     * @param expected the expected
     * @param actual the actual
     * @return the validation result
     */
    private ValidationResult interfaceComparator(IntrospectionInterface expected, IntrospectionInterface actual)
    {
        // Collect results
        ValidationResult methodResult = compare(expected.getMethods(), actual.getMethods());
        ValidationResult propertyResult = compare(expected.getProperties(), actual.getProperties());
        ValidationResult signalResult = compare(expected.getSignals(), actual.getSignals());
        ValidationResult annotationResult = compare(expected.getAnnotations(), actual.getAnnotations());
        List<ValidationResult> results =
            Arrays.asList(methodResult, propertyResult, signalResult, annotationResult);

        // Process results for faliures
        boolean isValid = true;
        StringBuilder failureReason = new StringBuilder();
        for (ValidationResult result : results)
        {
            if (!result.isValid())
            {
                isValid = false;
                failureReason.append(result.getFailureReason());
                failureReason.append("\n");
            }
        }

        return new ValidationResult(isValid, failureReason.toString());
    }

    /**
     * Compare.
     *
     * @param expected the expected
     * @param actual the actual
     * @return the validation result
     */
    private ValidationResult compare(Set<? extends IntrospectionBaseTag> expected, Set<? extends IntrospectionBaseTag> actual)
    {
        // Ensure both parameters are HashSets for proper comparison when passed
        // to the SetValidator
        if (!(expected instanceof HashSet<?>))
        {
            expected = new HashSet<IntrospectionBaseTag>(expected);
        }

        if (!(actual instanceof HashSet<?>))
        {
            actual = new HashSet<IntrospectionBaseTag>(actual);
        }

        SetValidator sv = new SetValidator<IntrospectionBaseTag>();
        return sv.validate(expected, actual);
    }

    /**
     * Gets the expected interface by name.
     *
     * @param nodes the nodes
     * @param ifaceName the iface name
     * @return the expected interface by name
     */
    private InterfaceDetail getExpectedInterfaceByName(List<IntrospectionNode> nodes, String ifaceName)
    {
        for (IntrospectionNode node : nodes)
        {
            for (IntrospectionInterface iface : node.getInterfaces())
            {
                if (ifaceName.equals(iface.getName()))
                {
                    List<IntrospectionInterface> ifaces = new ArrayList<IntrospectionInterface>();
                    ifaces.add(iface);
                    return new InterfaceDetail(node.getName(), ifaces);
                }
            }
        }

        return null;
    }
}