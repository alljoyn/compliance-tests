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
package org.alljoyn.validation.simulator;

public class DUTSimulatorErrorCodes
{
    public enum ErrorName
    {
        INVALID_VALUE("org.alljoyn.Error.InvalidValue"), FEATURE_NOT_AVAILABLE("org.alljoyn.Error.FeatureNotAvailable"), MAX_SIZE_EXCEEDED("org.alljoyn.Error.MaxSizeExceeded"), LANGUAGE_NOT_SUPPORTED(
                "org.alljoyn.Error.LanguageNotSupported"), UPDATE_NOT_ALLOWED("org.alljoyn.Error.UpdateNotAllowed"), METHOD_NOT_ALLOWED("org.alljoyn.Error.MethodNotAllowed");

        private String errorName;

        private ErrorName(String errorName)
        {
            this.errorName = errorName;
        }

        public String getErrorName()
        {
            return errorName;
        }

    }

    public enum ErrorMessage
    {
        INVALID_VALUE("Invalid value"), FEATURE_NOT_AVAILABLE("Feature not available"), MAX_SIZE_EXCEEDED("Maximum size exceeded"), LANGUAGE_NOT_SUPPORTED(
                "The language specified is not supported"), UPDATE_NOT_ALLOWED("Update Not Allowed"), METHOD_NOT_ALLOWED("Method Not Allowed");

        private String errorMessage;

        private ErrorMessage(String errorMessage)
        {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }

    }

}