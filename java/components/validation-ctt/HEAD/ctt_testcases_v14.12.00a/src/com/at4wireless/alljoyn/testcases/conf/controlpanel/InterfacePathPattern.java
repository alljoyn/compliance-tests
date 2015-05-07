package com.at4wireless.alljoyn.testcases.conf.controlpanel;

public enum InterfacePathPattern
{
    ControlPanel("/ControlPanel/[^/]+/[^/]+"), HttpControl("/ControlPanel/[^/]+/HTTPControl");

    private String value;

    InterfacePathPattern(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
