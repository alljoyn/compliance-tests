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
package org.alljoyn.validation.testing.instrument;

import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationTest;

public class ExampleTestCase extends ValidationBaseTestCase
{
    @ValidationTest(name = "Example-v1-01")
    public void test01()
    {
    }

    public void test02()
    {
    }

    @ValidationTest(name = "Example-v1-03")
    @Disabled
    public void test03()
    {
    }

    @ValidationTest(name = "Example-v1-04")
    public void notTest04()
    {
    }

    @ValidationTest(name = "Example-v1-05")
    public void test05()
    {
    }

}