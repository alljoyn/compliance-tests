/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */

package org.alljoyn.gatewaycontroller.sdk;

import java.util.Comparator;

import org.alljoyn.gatewaycontroller.sdk.RuleObjectDescription.RuleObjectPath;

// TODO: Auto-generated Javadoc
/**
 * Allows to compare {@link RuleObjectDescription} according to there
 * {@link RuleObjectPath}. The algorithm performs lexicographical comparison
 * of the object paths with the condition that for equal object paths the object
 * path that is not defined as prefix is less than the object path that is
 * prefix.
 */
class RuleObjectDescriptionComparator implements Comparator<RuleObjectDescription> {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(RuleObjectDescription lhs, RuleObjectDescription rhs) {

        String lhsOP = lhs.getObjectPath().getPath();
        String rhsOP = rhs.getObjectPath().getPath();

        int compRes = rhsOP.compareTo(lhsOP);
        if (compRes == 0) {

            if (lhs.getObjectPath().isPrefix()) {
                return 1;
            } else {
                return -1;
            }
        }

        return compRes;
    }

}