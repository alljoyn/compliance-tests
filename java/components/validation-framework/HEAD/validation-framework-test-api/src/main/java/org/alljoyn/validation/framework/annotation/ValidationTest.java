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
package org.alljoyn.validation.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method needs to be annotated with {@code ValidationTest} annotation to
 * indicate that its a test case method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidationTest
{
    /**
     * Specify <code>name</code> to provide a mapping to the defined test case
     * name. For e.g. {@code About-v1-01}
     */
    String name();

    /**
     * Optionally specify {@code timeout} in milliseconds to provide a mapping
     * to the defined test case timeout.
     */
    long timeout() default -1;

    /**
     * Optionally specify {@code order} to provide a mapping to the defined test
     * case order.
     * 
     * This allows for modifying the order of test case execution. The test
     * cases are executed within a suite based on sorting by the order and test
     * case name. If the order has a default value of 0, the sorting is just
     * based on the test case name.
     */
    int order() default 0;
}