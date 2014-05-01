/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator.controlpanel;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.ui.PropertyWidgetEnum;
import org.alljoyn.ioe.controlpanelservice.ui.PropertyWidgetHintsType;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetRangeConstraintAJ;

public class PropertySecuredBusObject implements PropertyControlSecured, BusObject
{
    private static final int STATE = 0;
    private static final int MAXIMUM_PROPERTY_VALUE = 9;
    private static final int MINIMUM_PROPERTY_VALUE = 0;
    private static final int PROPERTY_VALUE = 5;
    private static final String UNIT_OF_MEASURE = "secured property unit of measure";
    private static final int BACKGROUND_COLOR = 13434879;
    private static final String LABEL = "secured property label";

    @Override
    public void MetadataChanged() throws BusException
    {
    }

    @Override
    public void ValueChanged(Variant arg0) throws BusException
    {
    }

    @Override
    public Map<Short, Variant> getOptParams() throws BusException
    {
        Map<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put(PropertyWidgetEnum.LABEL.ID, new Variant(LABEL));
        parameters.put(PropertyWidgetEnum.BG_COLOR.ID, new Variant(BACKGROUND_COLOR, "u"));
        short[] shortArray = new short[]
        { PropertyWidgetHintsType.NUMERIC_KEYPAD.ID };
        parameters.put(PropertyWidgetEnum.HINTS.ID, new Variant(shortArray, "aq"));
        parameters.put(PropertyWidgetEnum.UNIT_OF_MEASURE.ID, new Variant(UNIT_OF_MEASURE));
        parameters.put(PropertyWidgetEnum.RANGE.ID, new Variant(getPropertyWidgetRangeConstraint()));

        return parameters;
    }

    @Override
    public int getStates() throws BusException
    {
        return STATE;
    }

    @Override
    public Variant getValue() throws BusException
    {
        return new Variant(PROPERTY_VALUE);
    }

    @Override
    public short getVersion() throws BusException
    {
        return VERSION;
    }

    @Override
    public void setValue(Variant arg0) throws BusException
    {
    }

    private PropertyWidgetRangeConstraintAJ getPropertyWidgetRangeConstraint()
    {
        PropertyWidgetRangeConstraintAJ propertyWidgetRangeConstraint = new PropertyWidgetRangeConstraintAJ();
        propertyWidgetRangeConstraint.min = new Variant(MINIMUM_PROPERTY_VALUE);
        propertyWidgetRangeConstraint.max = new Variant(MAXIMUM_PROPERTY_VALUE);
        propertyWidgetRangeConstraint.increment = new Variant(1);

        return propertyWidgetRangeConstraint;
    }
}