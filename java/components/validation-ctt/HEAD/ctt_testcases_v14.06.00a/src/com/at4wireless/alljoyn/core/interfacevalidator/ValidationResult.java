package com.at4wireless.alljoyn.core.interfacevalidator;

public class ValidationResult
{
    private boolean valid;
    private String failureReason;

    public ValidationResult(boolean valid, String failureReason)
    {
        this.valid = valid;
        this.failureReason = failureReason;
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getFailureReason()
    {
        return failureReason;
    }
}
